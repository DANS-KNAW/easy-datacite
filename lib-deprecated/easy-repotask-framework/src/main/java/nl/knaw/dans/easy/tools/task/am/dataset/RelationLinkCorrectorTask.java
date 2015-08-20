package nl.knaw.dans.easy.tools.task.am.dataset;

import java.net.URI;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdRelation;
import nl.knaw.dans.pf.language.emd.types.Relation;

// Internal links in emd.relations are not corrected to persistent identifier yet.
// This is the format we want to tackle:
// <eas:relation>
// <eas:subject-title>WoON2009, release 1.2 - WoonOnderzoek Nederland voor overheid en
// universiteiten</eas:subject-title>
// <eas:subject-link>http://persistent-identifier.nl/?identifier=urn:nbn:nl:ui:13-1ck-ner</eas:subject-link>
// </eas:relation>
// <eas:relation>
// <eas:subject-title>WoON2009, release 1.2 - WoonOnderzoek Nederland voor overige
// partijen</eas:subject-title>
// <eas:subject-link>http://easy.dans.knaw.nl/dms?command=AIP_info&amp;aipId=twips.dans.knaw.nl--8513817369253725916-1270566839781&amp;windowStyle=default&amp;windowContext=default</eas:subject-link>
// </eas:relation>
//
// dc- and term- relations have a BasicIdentifier, which means there is no link-field.
// only easRelations are of type Relation, with a subjectLink.
public class RelationLinkCorrectorTask extends AbstractDatasetTask {

    private boolean datasetAffected;
    private String currentDatasetId;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);

        if (hasTaskStamp(joint)) {
            return; // already did this one
        }

        datasetAffected = false;

        Dataset dataset = joint.getDataset();
        currentDatasetId = dataset.getStoreId();
        EasyMetadata emd = dataset.getEasyMetadata();
        EmdRelation emdRelation = emd.getEmdRelation();

        Map<String, List<Relation>> relationMap = emdRelation.getRelationMap();
        for (String relationName : relationMap.keySet()) {
            List<Relation> relations = relationMap.get(relationName);
            processRelations(relations);
        }

        if (datasetAffected) {
            joint.setCycleSubjectDirty(true);
            setTaskStamp(joint);
        }

    }

    private void processRelations(List<Relation> relations) throws FatalTaskException {
        for (Relation relation : relations) {
            URI subjectLink = relation.getSubjectLink();
            if (subjectLink != null) {
                processSubjectLink(relation);
            }
        }
    }

    private void processSubjectLink(Relation relation) throws FatalTaskException {
        URI subjectLink = relation.getSubjectLink();
        relation.setSubjectLink(processURI(subjectLink));
    }

    protected URI processURI(URI uri) throws FatalTaskException {
        String uriString = uri.toString();
        String aipId;
        boolean candidate = uriString.contains("dms?command=AIP_info") && uriString.contains("aipId=");
        if (!candidate) {
            return uri;
        } else {
            aipId = findAipId(uriString);
        }

        IdMap idMap = null;
        try {
            idMap = Data.getMigrationRepo().getMostRecentByAipId(aipId);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
        if (idMap == null) {
            RL.info(new Event(getTaskName(), "TargetPID not (yet) found", currentDatasetId, "no idMap", aipId));
            return uri;
        }

        String pid = idMap.getPersistentIdentifier();
        if (pid == null) {
            // No PID in idMap means that the link is to a dataset that has no pid. F.i. because it has
            // not been submitted.
            RL.info(new Event(getTaskName(), "No PID in idMap", currentDatasetId, "no pid", aipId));
            return uri;
        }

        // link to persistent-identifier
        String newHref = "http://persistent-identifier.nl/?identifier=" + pid;
        URI newUri = URI.create(newHref);

        datasetAffected = true;

        RL.info(new Event(getTaskName(), "Resolved subjectLink", currentDatasetId, uriString, newHref));
        return newUri;
    }

    protected String findAipId(String uriString) {
        String aipId;
        int index = uriString.indexOf("aipId=");
        String rawAipId = uriString.substring(index + 6);
        int i = rawAipId.indexOf('&');
        if (i < 0) {
            aipId = rawAipId;
        } else {
            aipId = rawAipId.substring(0, i);
        }
        return aipId;
    }

}
