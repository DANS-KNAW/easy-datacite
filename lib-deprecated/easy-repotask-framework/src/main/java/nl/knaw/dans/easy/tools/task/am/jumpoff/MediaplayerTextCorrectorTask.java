package nl.knaw.dans.easy.tools.task.am.jumpoff;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.bean.MarkupMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoRelations;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.jumpoff.twips.MediaPlayerConverter;

import org.joda.time.DateTime;

public class MediaplayerTextCorrectorTask extends AbstractTask {

    private final MediaPlayerConverter mediaPlayerConverter = new MediaPlayerConverter();

    private JumpoffDmo currentJumpoff;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        currentJumpoff = joint.getJumpoffDmo();

        if (MarkupVersionID.HTML_MU.equals(currentJumpoff.getJumpoffDmoMetadata().getDefaultMarkupVersionID())) {
            return;
        }

        JumpoffDmoRelations relations = (JumpoffDmoRelations) currentJumpoff.getRelations();
        String datasetId = relations.getObjectId();

        String content = currentJumpoff.getTextMarkup().getHtml();

        String newContent = mediaPlayerConverter.convertPaths(content);
        newContent = mediaPlayerConverter.convertDivs(newContent);
        newContent = mediaPlayerConverter.convertScripts(newContent);

        if (!content.equals(newContent)) {
            currentJumpoff.getTextMarkup().setHtml(newContent);
            joint.setCycleSubjectDirty(true);

            MarkupMetadata mmd = currentJumpoff.getJumpoffDmoMetadata().getTextMarkupMetadata();
            mmd.setLastEdited(new DateTime());
            mmd.setLastEditedBy(getTaskName());
        }

        if (newContent.contains("customAIP")) {
            RL.warn(new Event(getTaskName(), "'customAIP' in mp-text", currentJumpoff.getStoreId(), datasetId));
        }

    }

}
