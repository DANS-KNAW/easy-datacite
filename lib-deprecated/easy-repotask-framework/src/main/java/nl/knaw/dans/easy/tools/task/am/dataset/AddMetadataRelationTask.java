package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.types.Relation;

/**
 * <p>
 * This Task adds a new EMD Relation to the metadata.
 * </p>
 * <p>
 * An example of what can be added to the metadata XML is:
 * </p>
 * 
 * <pre>
 * {@code	
 * 	<emd:relation>
 * 		<eas:relation eas:emphasis="true">
 * 		<eas:subject-title>DANS EASY</eas:subject-title>
 * 		<eas:subject-link>https://easy.dans.knaw.nl/</eas:subject-link>
 * 	</eas:relation>
 * </emd:relation>
 * }
 * </pre>
 * <p>
 * The relations should be configured in the Spring context-file add-metadata-relation-context.xml
 * </p>
 */
public class AddMetadataRelationTask extends AbstractTask {
    private Relation[] relations;

    public AddMetadataRelationTask(Relation... relations) {
        this.relations = relations;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        Dataset dataset = joint.getDataset();

        List<Relation> datasetRels = dataset.getEasyMetadata().getEmdRelation().getEasRelation();

        for (int i = 0; i < relations.length; i++) {
            if (datasetRels.add(relations[i])) {
                joint.setCycleSubjectDirty(true); // so we can use the SaveDatasetTask
                RL.info(new Event(getTaskName(), String.format("Added Relation[%s] to dataset[%s]", relations[i], dataset.getDmoStoreId().toString())));
            }
        }
    }
}
