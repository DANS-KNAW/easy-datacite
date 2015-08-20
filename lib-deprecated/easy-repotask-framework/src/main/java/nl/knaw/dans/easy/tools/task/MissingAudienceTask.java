package nl.knaw.dans.easy.tools.task;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.easy.tools.AbstractSidSetTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.batch.EasyMetadataCheck;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.task.am.dataset.AudienceCorrectorTask;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Tries to repair a missing audience (issue 98), repeats the meta data check that should have been done by batch ingestors, checks might have improved after
 * running the batch.
 */
public class MissingAudienceTask extends AbstractSidSetTask {
    private final EasyMetadataCheck easyMetadataCheck;

    public MissingAudienceTask() {
        // create batch task for checks that batch ingestors should have done
        easyMetadataCheck = new EasyMetadataCheck();
        easyMetadataCheck.setAllowDcCreator(true);
    }

    @Override
    public void run(final JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        final EasyMetadata emd = joint.getDataset().getEasyMetadata();

        // the main task, repair what the batch task used to overlook
        if (EasyMetadataCheck.hasNoAudience(emd)) {
            if (!MetadataFormat.ARCHAEOLOGY.equals(joint.getDataset().getMetadataFormat()))
                warn("no default audience to add", "metadataformat " + joint.getDataset().getMetadataFormat());
            else {
                final BasicString dcTermsAudience = new BasicString(AudienceCorrectorTask.DISCIPLINE_ID_ARCHAEOLOGY);
                dcTermsAudience.setSchemeId(AudienceCorrectorTask.CUSTOM_DISCIPLINES);
                emd.getEmdAudience().getTermsAudience().add(dcTermsAudience);
                warn("archaeolgy audience added because metadataformat is archaeology");
                joint.setCycleSubjectDirty(true);
            }
        }

        // execute batch task
        joint.setEasyMetadata(emd);
        easyMetadataCheck.run(joint);

        if (!joint.isFitForSubmit())
            warn("still not fit for submit");
    }

    private void warn(final String... messages) {
        Application.getReporter().warn(new Event(getTaskName(), messages));
    }
}
