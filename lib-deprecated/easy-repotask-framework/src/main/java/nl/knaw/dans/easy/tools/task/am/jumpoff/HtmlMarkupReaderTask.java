package nl.knaw.dans.easy.tools.task.am.jumpoff;

import java.io.ByteArrayInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoRelations;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class HtmlMarkupReaderTask extends AbstractTask {

    private String datasetId;
    private JumpoffDmo currentJumpoff;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        currentJumpoff = joint.getJumpoffDmo();

        JumpoffDmoRelations relations = (JumpoffDmoRelations) currentJumpoff.getRelations();
        datasetId = relations.getObjectId();

        if (MarkupVersionID.TEXT_MU.equals(currentJumpoff.getJumpoffDmoMetadata().getDefaultMarkupVersionID())) {
            RL.info(new Event(getTaskName(), "Not processing text", currentJumpoff.getStoreId(), datasetId));
            return;
        }

        Document markupDoc = readMarkup(joint);

        joint.setJumpoffDom4jDocument(markupDoc);
    }

    private Document readMarkup(JointMap joint) throws TaskException {
        Document markupDoc;
        SAXReader reader = new SAXReader();
        try {
            markupDoc = reader.read(new ByteArrayInputStream(currentJumpoff.getHtmlMarkup().getHtml().getBytes()));
        }
        catch (DocumentException e) {
            joint.setFitForSave(false);
            RL.error(new Event(getTaskName(), e, "Could not read markup", currentJumpoff.getStoreId(), datasetId));
            throw new TaskException("Could not read markup", e, this);
        }
        return markupDoc;
    }

}
