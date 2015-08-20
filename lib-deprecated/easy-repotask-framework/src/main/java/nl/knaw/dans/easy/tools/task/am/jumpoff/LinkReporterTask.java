package nl.knaw.dans.easy.tools.task.am.jumpoff;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

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

public class LinkReporterTask extends AbstractTask {

    private String datasetId;
    private JumpoffDmo currentJumpoff;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        currentJumpoff = joint.getJumpoffDmo();

        JumpoffDmoRelations relations = (JumpoffDmoRelations) currentJumpoff.getRelations();
        datasetId = relations.getObjectId();

        if (MarkupVersionID.TEXT_MU.equals(currentJumpoff.getJumpoffDmoMetadata().getDefaultMarkupVersionID())) {
            return;
        }

        Document markupDoc = joint.getJumpoffDom4jDocument();
        if (markupDoc == null) {
            RL.error(new Event(getTaskName(), "No markup", currentJumpoff.getStoreId(), datasetId));
            return;
        }

        processElements(markupDoc.getRootElement());

    }

    private void processElements(Element element) {
        String text = element.getText();
        String elementName = element.getName();
        processText(elementName, "text", text);

        @SuppressWarnings("unchecked")
        List<Attribute> attrs = element.attributes();
        for (Attribute attr : attrs) {
            text = attr.getText();
            processText(elementName, attr.getName(), text);
        }

        @SuppressWarnings("unchecked")
        Iterator<Element> iter = element.elementIterator();
        while (iter.hasNext()) {
            Element child = iter.next();
            processElements(child);
        }
    }

    private void processText(String elementName, String attributeName, String text) {
        if (text == null) {
            return;
        }
        if (text.contains("customAIP") || text.contains("http") || text.contains("dms")
                || ("a".equalsIgnoreCase(elementName) && "href".equalsIgnoreCase(attributeName))
                || ("img".equalsIgnoreCase(elementName) && "src".equalsIgnoreCase(attributeName)))
        {
            RL.info(new Event(getTaskName(), currentJumpoff.getStoreId(), datasetId, elementName, attributeName, text));
        }
    }

}
