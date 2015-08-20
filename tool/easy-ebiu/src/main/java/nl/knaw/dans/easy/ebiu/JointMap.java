package nl.knaw.dans.easy.ebiu;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileOntology.MetadataFormat;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.xml.ResourceMetadataList;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Objects to pass along a chain of {@link Task}s.
 */
public class JointMap {

    public static final String CURRENT_DIRECTORY = "currentDirectory";
    public static final String EASY_METADATA = "easyMetadata";
    public static final String ADMINISTRATIVE_METADATA = "administrative-metadata";
    public static final String RESOURCE_METADATA_LIST = "resource-metadata-list";
    public static final String ADDITIONAL_METADATAFILES = "additional-metadatafiles";
    public static final String DATASET = "dataset";
    public static final String DASET_STATE = "datasetState";

    public static final String JUMPOFFDMO = "jumpoffDmo";
    public static final String JUMPOFF_DOM4J_DOCUMENT = "jumpoff-dom4j-document";

    private static final Logger logger = LoggerFactory.getLogger(JointMap.class);

    private boolean fitForSave = true;
    private boolean fitForDraft = true;
    private boolean fitForSubmit = true;
    private boolean fitForPublication = true;

    private boolean cycleSubjectDirty;
    private boolean cycleAbborted;
    private boolean cycleProcessingCompleted;

    private final Map<String, Object> objects = new HashMap<String, Object>();

    private EasyUser easyUser;

    public void printObjects(Appendable appendable) throws IOException {
        appendable.append("\n#OBJECTS IN (" + this + ")\n");
        Set<Entry<String, Object>> entrySet = objects.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            appendable.append(entry.getKey());
            appendable.append("=");
            Object obj = entry.getValue();
            appendable.append(obj == null ? "null" : obj.toString());
            appendable.append("\n");
        }
        appendable.append("fitForSave=" + isFitForSave()).append("\n").append("fitForDraft=" + isFitForDraft()).append("\n")
                .append("fitForSubmit=" + isFitForSubmit()).append("\n").append("fitForPublication=" + isFitForPublication()).append("\n")
                .append("cycleSubjectDirty=" + isCycleSubjectDirty()).append("\n").append("cycleAbborted=" + cycleAbborted).append("\n")
                .append("cycleProcessingCompleted=" + cycleProcessingCompleted).append("\n");
    }

    public boolean isFitForSave() {
        return fitForSave;
    }

    public void setFitForSave(boolean fitForSave) {
        this.fitForSave &= fitForSave;
    }

    public boolean isFitForDraft() {
        return isFitForSave() && fitForDraft;
    }

    public void setFitForDraft(boolean fitForDraft) {
        this.fitForDraft &= fitForDraft;
    }

    public boolean isFitForSubmit() {
        return isFitForDraft() && fitForSubmit;
    }

    public void setFitForSubmit(boolean fitForSubmit) {
        this.fitForSubmit &= fitForSubmit;
    }

    public boolean isFitForPublication() {
        return isFitForSubmit() && fitForPublication;
    }

    public void setFitForPublication(boolean fitForPublication) {
        this.fitForPublication &= fitForPublication;
    }

    public boolean isCycleSubjectDirty() {
        return cycleSubjectDirty;
    }

    public void setCycleSubjectDirty(boolean cycleSubjectDirty) {
        this.cycleSubjectDirty = cycleSubjectDirty;
    }

    public boolean isCycleAbborted() {
        return cycleAbborted;
    }

    public void setCycleAbborted(boolean cycleAbborted) {
        this.cycleAbborted = cycleAbborted;
    }

    public boolean isCycleProcessingCompleted() {
        return cycleProcessingCompleted;
    }

    public void setCycleProcessingCompleted(boolean cycleProcessingCompleted) {
        this.cycleProcessingCompleted = cycleProcessingCompleted;
    }

    public void put(String key, Object obj) {
        objects.put(key, obj);
    }

    public Object get(String key) {
        Object value = objects.get(key);
        if (value == null) {
            logger.warn("=========> Object '" + key + "' is null <=========");
        }
        return value;
    }

    public Object remove(String key) {
        return objects.remove(key);
    }

    public void clearCycleState() {
        objects.clear();

        fitForSave = true;
        fitForDraft = true;
        fitForSubmit = true;
        fitForPublication = true;

        cycleSubjectDirty = false;
        cycleAbborted = false;
        cycleProcessingCompleted = false;
    }

    public void setEasyUser(EasyUser easyUser) {
        this.easyUser = easyUser;
    }

    public EasyUser getEasyUser() {
        return easyUser;
    }

    public void setCurrentDirectory(File currentDirectory) {
        put(CURRENT_DIRECTORY, currentDirectory);
    }

    public File getCurrentDirectory() {
        return (File) get(CURRENT_DIRECTORY);
    }

    public void setEasyMetadata(EasyMetadata easyMetadata) {
        put(EASY_METADATA, easyMetadata);
    }

    public EasyMetadata getEasyMetadata() {
        return (EasyMetadata) get(EASY_METADATA);
    }

    public void setAdministrativeMetadata(AdministrativeMetadata administrativeMetadata) {
        put(ADMINISTRATIVE_METADATA, administrativeMetadata);
    }

    public AdministrativeMetadata getAdministrativeMetadata() {
        return (AdministrativeMetadata) get(ADMINISTRATIVE_METADATA);
    }

    public ResourceMetadataList getResourceMetadataList() {
        return (ResourceMetadataList) get(RESOURCE_METADATA_LIST);
    }

    public void setResourceMetadataList(ResourceMetadataList rml) {
        put(RESOURCE_METADATA_LIST, rml);
    }

    public HashMap<MetadataFormat, String> getAdditionalMetadataFiles() {
        return (HashMap<MetadataFormat, String>) get(ADDITIONAL_METADATAFILES);
    }

    public void setAdditionalMetadataFiles(HashMap<MetadataFormat, String> mf) {
        put(ADDITIONAL_METADATAFILES, mf);
    }

    public void setDataset(Dataset dataset) {
        put(DATASET, dataset);
    }

    public Dataset getDataset() {
        return (Dataset) get(DATASET);
    }

    public boolean hasDataset() {
        return objects.containsKey(DATASET);
    }

    public void setOriginalState(DatasetState datasetState) {
        put(DASET_STATE, datasetState);
    }

    public DatasetState getOriginalState() {
        return (DatasetState) get(DASET_STATE);
    }

    public void setJumpoffDmo(JumpoffDmo jumpoffDmo) {
        put(JUMPOFFDMO, jumpoffDmo);
    }

    public JumpoffDmo getJumpoffDmo() {
        return (JumpoffDmo) get(JUMPOFFDMO);
    }

    public void setJumpoffDom4jDocument(Document document) {
        put(JUMPOFF_DOM4J_DOCUMENT, document);
    }

    public Document getJumpoffDom4jDocument() {
        return (Document) get(JUMPOFF_DOM4J_DOCUMENT);
    }
}
