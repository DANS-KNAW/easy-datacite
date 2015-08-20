package nl.knaw.dans.easy.tools.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractSidSetTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.types.Relation;

/**
 * https://drivenbydata.onjira.com/browse/EASY-98 first point : Check for direct links to datasets, extended with a general link checker.
 */
public class RelatedLinkCheckerTask extends AbstractSidSetTask {
    @Override
    public void run(final JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        final DatasetImpl dataset = (DatasetImpl) joint.getDataset();
        for (final Relation relation : dataset.getEasyMetadata().getEmdRelation().getEasRelation()) {
            if (relation.getSubjectTitle() == null //
                    || relation.getSubjectTitle().getValue() == null //
                    || relation.getSubjectTitle().getValue().trim().length() == 0)
                warn("link without title for " + relation.getSubjectLink());
            if (relation.getSubjectLink() == null || relation.getSubjectLink().toString().trim().length() == 0)
                warn("link without href for " + relation.getSubjectTitle());
            else {
                final DmoStoreId directLinkId = extractIdOfDirectDatasetLink(relation);
                if (directLinkId == null) {
                    follow(relation.getSubjectLink());
                    final String host = relation.getSubjectLink().getHost();
                    if (host != null && host.matches("[0-9.]+"))
                        warn("machinelink " + relation.getSubjectLink());
                } else {
                    relation.setSubjectLink(toPeristentHref(relation, directLinkId));
                    joint.setCycleSubjectDirty(true);
                }
            }
        }
    }

    private DmoStoreId extractIdOfDirectDatasetLink(final Relation relation) {
        if (relation.getSubjectLink() == null)
            return null;
        final String[] path = relation.getSubjectLink().getPath().split("/");
        if (path.length < 4)
            return null;
        else if (!"datasets".equals(path[1]))
            return null;
        else if (!"id".equals(path[2]))
            return null;
        return new DmoStoreId(path[3]);
    }

    private URI toPeristentHref(final Relation relation, final DmoStoreId dmoStoreId) throws FatalTaskException {
        try {
            final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(dmoStoreId);
            final URI uri = new URI("http://persistent-identifier.nl/?identifier=" + new URI(dataset.getPersistentIdentifier()));
            warn("relation to " + relation.getSubjectLink(), "to be replaced by " + uri);
            return uri;
        }
        catch (final ObjectNotInStoreException e) {
            warn(relation.getSubjectLink() + " points to a not found dataset " + dmoStoreId);
        }
        catch (final URISyntaxException e) {
            warn(relation.getSubjectLink() + " points to a dataset with an invalid persistent identifier");
        }
        catch (final RepositoryException e) {
            error(e.getMessage());
            throw new FatalTaskException(e, this);
        }
        return null;
    }

    private void follow(final URI subjectLink) {
        try {
            final URL subjectUrl = subjectLink.toURL();
            final URLConnection urlConnection = subjectUrl.openConnection();
            urlConnection.connect();
            info("relation to " + subjectLink, //
                    "connects to " + urlConnection.getURL(),//
                    "redirects to " + follow(subjectUrl, urlConnection));
        }
        catch (final IllegalArgumentException e) {
            warn("relation to invalid URL " + subjectLink, e.getMessage());
        }
        catch (final MalformedURLException e) {
            warn("relation to malformed " + subjectLink, e.getMessage());
        }
        catch (final IOException e) {
            warn("relation to inaccessible " + subjectLink, e.getMessage());
        }
    }

    private URL follow(final URL subjectLink, final URLConnection urlConnection) {
        try {
            urlConnection.getInputStream();
            return urlConnection.getURL();
        }
        catch (final IOException e) {
            warn("relation to " + subjectLink, "connects to inaccessible " + urlConnection.getURL(), e.getMessage());
        }
        return null;
    }

    private void warn(final String... messages) {
        Application.getReporter().warn(new Event(getTaskName(), messages));
    }

    private void info(final String... messages) {
        Application.getReporter().info(new Event(getTaskName(), messages));
    }

    private void error(final String... messages) {
        Application.getReporter().error(new Event(getTaskName(), messages));
    }
}
