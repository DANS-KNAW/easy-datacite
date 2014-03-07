package nl.knaw.dans.easy.domain.dataset;

import org.apache.commons.lang.StringUtils;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.DatasetItemMetadata;

public abstract class AbstractItemMetadataImpl<T extends DatasetItemMetadata> extends AbstractTimestampedJiBXObject<T> implements DatasetItemMetadata
{

    private static final long serialVersionUID = -8632210202402645510L;

    private DmoStoreId dmoStoreId;
    private String name;
    private String path;
    private DmoStoreId parentDmoStoreId;
    private DmoStoreId datasetDmoStoreId;

    private boolean versionable;

    protected AbstractItemMetadataImpl()
    {

    }

    public AbstractItemMetadataImpl(DmoStoreId dmoStoreId)
    {
        this.dmoStoreId = dmoStoreId;
    }

    public DmoStoreId getDmoStoreId()
    {
        return dmoStoreId;
    }

    public void setDmoStoreId(DmoStoreId sid)
    {
        evaluateDirty(sid, this.dmoStoreId);
        this.dmoStoreId = sid;
    }

    public DmoStoreId getParentDmoStoreId()
    {
        return parentDmoStoreId;
    }

    public void setParentDmoStoreId(DmoStoreId parentSid)
    {
        evaluateDirty(parentSid, this.parentDmoStoreId);
        this.parentDmoStoreId = parentSid;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public DmoStoreId getDatasetDmoStoreId()
    {
        return datasetDmoStoreId;
    }

    public void setDatasetDmoStoreId(DmoStoreId datasetId)
    {
        evaluateDirty(datasetId, this.datasetDmoStoreId);
        this.datasetDmoStoreId = datasetId;
    }

    public String getName()
    {
        return name;
    }

    protected void setName(String name)
    {
        evaluateDirty(name, this.name);
        this.name = name;
        if (path == null)
            path = name;
    }

    public boolean isVersionable()
    {
        return versionable;
    }

    public void setVersionable(boolean versionable)
    {
        this.versionable = versionable;
    }

    // methods for JiBX-serialization
    protected void setSid(String sid)
    {
        if (!StringUtils.isBlank(sid))
            dmoStoreId = new DmoStoreId(sid);
    }

    // methods for JiBX-serialization
    protected String getSid()
    {
        return dmoStoreId == null ? "" : dmoStoreId.getStoreId();
    }

    // methods for JiBX-serialization
    protected void setParentSid(String parentSid)
    {
        if (!StringUtils.isBlank(parentSid))
            this.parentDmoStoreId = new DmoStoreId(parentSid);
    }

    // methods for JiBX-serialization
    protected String getParentSid()
    {
        return parentDmoStoreId == null ? null : parentDmoStoreId.getStoreId();
    }

    // methods for JiBX-serialization
    protected void setDatasetSid(String datasetSid)
    {
        if (!StringUtils.isBlank(datasetSid))
            this.datasetDmoStoreId = new DmoStoreId(datasetSid);
    }

    // methods for JiBX-serialization
    protected String getDatasetSid()
    {
        return datasetDmoStoreId == null ? null : datasetDmoStoreId.getStoreId();
    }

}
