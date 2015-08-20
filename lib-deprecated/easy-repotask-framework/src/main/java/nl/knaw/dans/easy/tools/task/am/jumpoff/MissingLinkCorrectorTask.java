package nl.knaw.dans.easy.tools.task.am.jumpoff;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.bean.MarkupMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoRelations;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffFile;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;

public class MissingLinkCorrectorTask extends AbstractTask {

    // Things we need to tackle:

    // <img src="../customAIP/twips.dans.knaw.nl--8513817369253725916-1270566839781/logowoon.jpg "
    // alt="logo" />
    // starts with ../

    // href="customAIP/twips.dans.knaw.nl-807139707405450874-1226310466720/a00604_ore.rdf"
    // (is not "/customAIP")

    // <img
    // src=" /customAIP/twips.dans.knaw.nl--2352436691732177206-1223023130752/organisational_commitment.jpg "
    // <img
    // src="../customAIP/twips.dans.knaw.nl-4644886950577384590-1294238848626/logo-letters_SVNT_120.jpg "
    // watch the spaces!

    // https://easy.dans.knaw.nl/dms?command=show&windowStyle=default&windowContext=default&didAction=Search+Results&query=Kwaliteitskaart&search_twips.dans.knaw.nl--3877089603410664659-1167404201644=on&search_twips.dans.knaw.nl-4322691534929405083-1167404201739=on&search_twips.dans.knaw.nl--6017685496380663529-1167404201757=on&search_twips.dans.knaw.nl--8739414114196558923-1179232222081=on&search_twips.dans.knaw.nl--4667124701540888169-1179232222015=on&search_twips.dans.knaw.nl-9203215916833700147-1223996427623=on&search_mydatasets=on&search_incoming=on
    // cannot be tackled.

    // in 'onclick':
    // "window.open('/customAIP/twips.dans.knaw.nl-6802027138963377881-1234195166246/bestandwijzerTOP10vector.jpg',null,'status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes'); return false"

    private static final String CUSTOM_AIP = "customAIP/twips.dans.knaw.nl";

    private final String webLocation;

    private String datasetId;
    private String currentStoreId;
    private JumpoffDmo currentJumpoff;
    private boolean docChanged;

    // easystore.webLocation=/mnt/sara1022/easystore/runningbackup/easystore/web/
    public MissingLinkCorrectorTask(String webLocation) throws FatalTaskException {
        this.webLocation = webLocation;
        if (!new File(webLocation).exists()) {
            throw new FatalTaskException("File not found: " + webLocation, this);
        }
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        docChanged = false;
        currentJumpoff = joint.getJumpoffDmo();
        currentStoreId = currentJumpoff.getStoreId();

        JumpoffDmoRelations relations = (JumpoffDmoRelations) currentJumpoff.getRelations();
        datasetId = relations.getObjectId();

        if (MarkupVersionID.TEXT_MU.equals(currentJumpoff.getJumpoffDmoMetadata().getDefaultMarkupVersionID())) {
            // RL.info(new Event(getTaskName(), "Not processing text", currentStoreId, datasetId));
            return;
        }

        Document markupDoc = joint.getJumpoffDom4jDocument();
        if (markupDoc == null) {
            RL.error(new Event(getTaskName(), "No markup", currentStoreId, datasetId));
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
        } else if ("script".equalsIgnoreCase(element.getName())) {
            processScriptTag(element);
        }
        @SuppressWarnings("unchecked")
        Iterator<Element> iter = element.elementIterator();
        while (iter.hasNext()) {
            Element child = iter.next();
            processElements(child);
        }

    }

    private void processScriptTag(Element element) {
        String scriptText = element.asXML();
        if (scriptText.contains(CUSTOM_AIP)) {
            RL.info(new Event(getTaskName(), "Found script with customAIP", currentStoreId, datasetId, "script", scriptText));
        }

    }

    private void processImageTag(Element element) throws FatalTaskException {
        Attribute linkAttr = element.attribute("src");
        if (linkAttr == null) {
            linkAttr = element.attribute("SRC");
        }
        if (linkAttr == null) {
            return;
        }

        processIfNeeded(linkAttr, "img");

    }

    private void processAnchorTag(Element element) throws FatalTaskException {
        Attribute linkAttr = element.attribute("href");
        if (linkAttr == null) {
            linkAttr = element.attribute("HREF");
        }
        if (linkAttr != null) {
            processIfNeeded(linkAttr, "anchor");
        }

        Attribute onClickAttr = element.attribute("onclick");
        if (onClickAttr == null) {
            onClickAttr = element.attribute("onClick");
        }
        if (onClickAttr != null) {
            processOnClickIfNeeded(onClickAttr);
        }

    }

    private void processOnClickIfNeeded(Attribute onClickAttr) throws FatalTaskException {
        String text = onClickAttr.getText().trim();
        if (text.contains(CUSTOM_AIP)) {
            RL.info(new Event(getTaskName(), "On click with customAIP", currentStoreId, datasetId, "anchor", text));
            processOnClick(onClickAttr);
        } else {
            RL.info(new Event(getTaskName(), "On click", currentStoreId, datasetId, "anchor", text));
        }
    }

    // "window.open('/customAIP/twips.dans.knaw.nl-6802027138963377881-1234195166246/bestandwijzerTOP10vector.jpg',null,'status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes'); return false"
    protected void processOnClick(Attribute onClickAttr) throws FatalTaskException {
        String text = onClickAttr.getText();
        String newText = processOnclickText(text);
        if (newText != null) {
            onClickAttr.setText(newText);
            docChanged = true;
            RL.info(new Event(getTaskName(), "Changed markup", currentStoreId, datasetId, "anchor script", text, newText));
        }
    }

    protected String processOnclickText(String text) throws FatalTaskException {
        StringBuilder pathBuilder = new StringBuilder();
        int apostrofCount = 0;
        int start = 0;
        int end = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\'') {
                apostrofCount++;
                if (apostrofCount == 1)
                    start = i + 1;
                if (apostrofCount == 2)
                    end = i;
            }
            if (apostrofCount == 1) {
                pathBuilder.append(c);
            }
        }

        String path = pathBuilder.toString().substring(1);
        String oldPath = locateResource(path.trim());

        String newHref = uploadFile("anchor script", text, oldPath);

        String newText = null;
        if (newHref != null) {
            newText = text.substring(0, start) + newHref + text.substring(end, text.length());
        }
        return newText;
    }

    protected void processIfNeeded(Attribute linkAttr, String elementName) throws FatalTaskException {
        String text = linkAttr.getText().trim();

        if (text.contains("customAIP")) {
            RL.info(new Event(getTaskName(), "Found internal link", currentStoreId, datasetId, elementName, text));
            processLinkAttr(linkAttr, elementName);
        }
    }

    private void processLinkAttr(Attribute linkAttr, String elementName) throws FatalTaskException {
        String text = linkAttr.getText().trim();
        String oldPath = locateResource(text);

        String newHref = uploadFile(elementName, text, oldPath);

        if (newHref != null) {
            linkAttr.setText(newHref);
            docChanged = true;
            RL.info(new Event(getTaskName(), "Changed markup", currentStoreId, datasetId, elementName, text, newHref));
        }
    }

    protected String uploadFile(String elementName, String originalText, String oldPath) throws FatalTaskException {
        String newHref = null;
        if (oldPath == null) {
            RL.warn(new Event(getTaskName(), "Unable to locate a linked resource", currentStoreId, datasetId, elementName, originalText));
            return newHref;
        }
        File file = new File(webLocation, oldPath);
        if (!file.exists()) {
            RL.warn(new Event(getTaskName(), "Unable to locate a file", currentStoreId, datasetId, elementName, originalText));
            return newHref;
        }

        JumpoffFile jumpoffFile = new JumpoffFile(file.getName().replaceAll(" ", "_"));

        String storeId = currentStoreId;
        try {
            jumpoffFile.setFile(file);

            Data.getEasyStore().addOrUpdateBinaryUnit(new DmoStoreId(storeId), jumpoffFile, "Processed by " + this.getClass().getSimpleName());
            newHref = "/resources/easy/content?sid=" + storeId + "&did=" + jumpoffFile.getUnitId();

        }
        catch (IOException e) {
            RL.error(new Event(getTaskName(), e, "Unable to save a file", currentStoreId, datasetId, elementName, originalText, newHref));
        }
        catch (RepositoryException e) {
            RL.error(new Event(getTaskName(), e, "Unable to save a file 2", currentStoreId, datasetId, elementName, originalText, newHref));
            throw new FatalTaskException("Unable to save a file 2", e, this);
        }
        return newHref;
    }

    protected String locateResource(String text) {
        String oldPath = null;
        if (text == null) {
            oldPath = null;
        } else if (text.startsWith("/customAIP")) {
            oldPath = text;
        } else if (text.startsWith("customAIP")) {
            oldPath = "/" + text;
        } else if (text.startsWith("../customAIP")) {
            oldPath = text.substring(2);
        } else if (text.startsWith("http://easy.dans.knaw.nl/customAIP")) {
            oldPath = text.substring(24);
        } else if (text.startsWith("https://easy.dans.knaw.nl/customAIP")) {
            oldPath = text.substring(25);
        } else {
            RL.warn(new Event(getTaskName(), "Illegible", currentStoreId, datasetId, text));
        }
        return oldPath;
    }

}
