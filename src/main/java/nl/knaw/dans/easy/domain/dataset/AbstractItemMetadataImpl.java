package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.easy.domain.model.DatasetItemMetadata;

public abstract class AbstractItemMetadataImpl<T extends DatasetItemMetadata> extends AbstractTimestampedJiBXObject<T> implements DatasetItemMetadata
{

    private static final long serialVersionUID = -8632210202402645510L;
    
    private String sid;
    private String name;
    private String path;
    private String parentSid;
    private String datasetId;
    
    private boolean versionable;
    
    protected AbstractItemMetadataImpl()
    {
        
    }
    
    public AbstractItemMetadataImpl(String sid)
    {
        this.sid = sid;
    }
    
    public String getSid()
    {
        return sid;
    }
    
    public void setSid(String sid)
    {
        evaluateDirty(sid, this.sid);
        this.sid = sid;
    }
    
    public String getParentSid()
    {
        return parentSid;
    }
    
    public void setParentSid(String parentSid)
    {
        evaluateDirty(parentSid, this.parentSid);
        this.parentSid = parentSid;
    }
    
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getDatasetId()
    {
        return datasetId;
    }

    public void setDatasetId(String datasetId)
    {
        evaluateDirty(datasetId, this.datasetId);
        this.datasetId = datasetId;
    }

    public String getName()
    {
        return name;
    }
    
    protected void setName(String name)
    {
        evaluateDirty(name, this.name);
        this.name = name;
        if (path == null) path = name;
    }

    public boolean isVersionable()
    {
        return versionable;
    }

    public void setVersionable(boolean versionable)
    {
        this.versionable = versionable;
    }

}
