package nl.knaw.dans.easy.tools.task.am.jumpoff;

import java.util.Iterator;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.bean.MarkupMetadata;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoRelations;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;

public class InternalLinkCorrectorTask extends AbstractTask {

    private String datasetId;
    private JumpoffDmo currentJumpoff;
    private boolean docChanged;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        docChanged = false;
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

        if (docChanged) {
            joint.setCycleSubjectDirty(true);
            MarkupMetadata mmd = currentJumpoff.getJumpoffDmoMetadata().getHtmlMarkupMetadata();
            mmd.setLastEdited(new DateTime());
            mmd.setLastEditedBy(getTaskName());
        }
    }

    private void processElements(Element element) throws FatalTaskException {
        if ("a".equalsIgnoreCase(element.getName())) {
            processAnchorTag(element);
        } else if ("img".equalsIgnoreCase(element.getName())) {
            processImageTag(element);
        }
        @SuppressWarnings("unchecked")
        Iterator<Element> iter = element.elementIterator();
        while (iter.hasNext()) {
            Element child = iter.next();
            processElements(child);
        }
    }

    private void processAnchorTag(Element element) throws FatalTaskException {
        Attribute linkAttr = element.attribute("href");
        if (linkAttr == null) {
            linkAttr = element.attribute("HREF");
        }
        if (linkAttr == null) {
            return;
        }

        String href = linkAttr.getText().trim();

        if (href.startsWith("/customAIP") || href.startsWith("customAIP") || href.startsWith("http://easy.dans.knaw.nl/customAIP")) {
            RL.warn(new Event(getTaskName(), "Unresolved anchor", currentJumpoff.getStoreId(), datasetId, href));
        }

        if (href.contains("dms?command=AIP_info&aipId=twips.dans.knaw.nl") || href.contains("dms?command=&aip_info&aipId=twips.dans.knaw.nl")) {
            processInternalLink(linkAttr, element);
        }
    }

    private void processInternalLink(Attribute linkAttr, Element element) throws FatalTaskException {
        String aipId;
        String href = linkAttr.getText();
        int index = href.indexOf("&aipId=");
        if (index < 0) {
            RL.warn(new Event(getTaskName(), "aipId not found", currentJumpoff.getStoreId(), datasetId, "no aipId", href));
            return;
        } else {
            aipId = href.substring(index + 7);
        }
        IdMap idMap = null;
        try {
            idMap = Data.getMigrationRepo().getMostRecentByAipId(aipId);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
        if (idMap == null) {
            RL.info(new Event(getTaskName(), "TargetPID not (yet) found", currentJumpoff.getStoreId(), datasetId, "no idMap", aipId));
            return;
        }

        String pid = idMap.getPersistentIdentifier();
        if (pid == null) {
            // No PID in idMap means that the link is to a dataset that has no pid. F.i. because it has
            // not been submitted.
            RL.info(new Event(getTaskName(), "No PID in idMap", currentJumpoff.getStoreId(), datasetId, "no pid", aipId));
            return;
        }

        // link to persistent-identifier
        String newHref = "http://persistent-identifier.nl/?identifier=" + pid;
        linkAttr.setText(newHref);
        docChanged = true;
        RL.info(new Event(getTaskName(), "Resolved anchor", currentJumpoff.getStoreId(), datasetId, href, newHref));
    }

    private void processImageTag(Element element) {
        Attribute linkAttr = element.attribute("src");
        if (linkAttr == null) {
            linkAttr = element.attribute("SRC");
        }
        if (linkAttr == null) {
            return;
        }

        String src = linkAttr.getText().trim();

        if (src.startsWith("/resources/easy/content")) {
            return;
        } else if (src.startsWith("/customAIP") || src.startsWith("customAIP") || src.startsWith("http://easy.dans.knaw.nl/customAIP")
                || src.startsWith("https://easy.dans.knaw.nl/customAIP"))
        {
            RL.warn(new Event(getTaskName(), "Unresolved img src 1", currentJumpoff.getStoreId(), datasetId, linkAttr.getText()));
        } else if (src.startsWith("http://easy.dans.knaw.nl/dms") || src.startsWith("https://easy.dans.knaw.nl/dms") || src.startsWith("/dms")) {
            RL.warn(new Event(getTaskName(), "Unresolved img src 2", currentJumpoff.getStoreId(), datasetId, linkAttr.getText()));
        } else if (src.startsWith("http://easy.dans.knaw.nl") || src.startsWith("https://easy.dans.knaw.nl") || src.startsWith("/")) {
            RL.warn(new Event(getTaskName(), "Unresolved img src 3", currentJumpoff.getStoreId(), datasetId, linkAttr.getText()));
        } else if (src.startsWith("http://") || src.startsWith("https://")) {
            RL.info(new Event(getTaskName(), "External img src", currentJumpoff.getStoreId(), datasetId, linkAttr.getText()));
        } else {
            RL.warn(new Event(getTaskName(), "Unresolved img src 4", currentJumpoff.getStoreId(), datasetId, linkAttr.getText()));
        }
    }

}
