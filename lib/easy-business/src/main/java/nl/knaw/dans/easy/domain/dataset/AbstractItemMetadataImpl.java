package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.DatasetItemMetadata;

public abstract class AbstractItemMetadataImpl<T extends DatasetItemMetadata> extends AbstractTimestampedJiBXObject<T> implements DatasetItemMetadata {

    private static final long serialVersionUID = -8632210202402645510L;

    private DmoStoreId dmoStoreId;
    private String name;
    private String path;
    private DmoStoreId parentDmoStoreId;
    private DmoStoreId datasetDmoStoreId;

    private boolean versionable;

    protected AbstractItemMetadataImpl() {

    }

    public AbstractItemMetadataImpl(DmoStoreId dmoStoreId) {
        this.dmoStoreId = dmoStoreId;
    }

    public DmoStoreId getDmoStoreId() {
        return dmoStoreId;
    }

    public void setDmoStoreId(DmoStoreId sid) {
        evaluateDirty(sid, this.dmoStoreId);
        this.dmoStoreId = sid;
    }

    public DmoStoreId getParentDmoStoreId() {
        return parentDmoStoreId;
    }

    public void setParentDmoStoreId(DmoStoreId parentSid) {
        evaluateDirty(parentSid, this.parentDmoStoreId);
        this.parentDmoStoreId = parentSid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        evaluateDirty(path, this.path);
        this.path = path;
    }

    public DmoStoreId getDatasetDmoStoreId() {
        return datasetDmoStoreId;
    }

    public void setDatasetDmoStoreId(DmoStoreId datasetId) {
        evaluateDirty(datasetId, this.datasetDmoStoreId);
        this.datasetDmoStoreId = datasetId;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        evaluateDirty(name, this.name);
        this.name = name;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    /*
     * IMPORTANT NOTE These JiBX methods must be kept around as long as there are FILE_ITEM_METADTA datastreams that contain sids, parentSids or datastreamSids.
     * The current code does not use or save those in the FILE_ITEM_METADATA anymore. After we have removed them from all existing FILE_ITEM_METADATA
     * datastreams, we can proceed to remove below methods from this class and corresponding jibx bindings from ItemMetadata-binding.xml.
     */

    // methods for JiBX-serialization
    protected void setSid(String sid) {}

    // methods for JiBX-serialization
    protected String getSid() {
        return null;
    }

    // methods for JiBX-serialization
    protected void setParentSid(String parentSid) {}

    // methods for JiBX-serialization
    protected String getParentSid() {
        return null;
    }

    // methods for JiBX-serialization
    protected void setDatasetSid(String datasetSid) {}

    // methods for JiBX-serialization
    protected String getDatasetSid() {
        return null;
    }

}
