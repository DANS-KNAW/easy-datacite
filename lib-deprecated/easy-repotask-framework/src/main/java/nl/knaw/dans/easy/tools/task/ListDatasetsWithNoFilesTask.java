package nl.knaw.dans.easy.tools.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.fedora.rdf.FedoraURIReference;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.client.FedoraClient;

public class ListDatasetsWithNoFilesTask extends AbstractTask {
    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        Dataset d = joint.getDataset();
        String sid = d.getDmoStoreId().toString();
        String title = d.getEasyMetadata().getPreferredTitle();
        final String dmoObjectRef = FedoraURIReference.create(sid);
        final String query = String.format("select ?s from <#ri> where {" + "   ?s <%s> <%s> ."
                + "   ?s <fedora-model:hasModel> <info:fedora/easy-model:EDM1FILE> .}", RelsConstants.DANS_NS.IS_SUBORDINATE_TO.stringValue(), dmoObjectRef);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("lang", "sparql");
        params.put("query", query);

        final List<String> filePids = getFileListFromTuples(getTuples(params));

        if (filePids.size() == 0) {
            RL.info(new Event(getTaskName(), String.format("Dataset without files: %s - %s", sid, title)));
        } else {
            RL.info(new Event(getTaskName(), String.format("Dataset %s has %d files", sid, filePids.size())));
        }
    }

    private TupleIterator getTuples(final Map<String, String> params) throws FatalTaskException {
        final FedoraClient fc = getFedoraClient();
        try {
            return fc.getTuples(params);
        }
        catch (final IOException e) {
            throw new FatalTaskException(this);
        }
    }

    private FedoraClient getFedoraClient() throws FatalTaskException {
        try {
            return Application.getFedora().getRepository().getFedoraClient();
        }
        catch (final RepositoryException e) {
            throw new FatalTaskException(this);
        }
    }

    private List<String> getFileListFromTuples(final TupleIterator tuples) throws FatalTaskException {
        final List<String> result = new LinkedList<String>();

        try {
            while (tuples.hasNext()) {
                result.add(tuples.next().get("s").toString());
            }

            return result;
        }
        catch (final TrippiException e) {
            throw new FatalTaskException("Could not get file list from tuples", e, this);
        }
    }
}
