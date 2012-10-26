package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.easy.domain.model.Dataset;

public abstract class AbstractItemVO implements java.io.Serializable, ItemVO, Cloneable
{

    private static final long serialVersionUID = 4141978905361852147L;
    private String sid;
    private String parentSid;
    private String datasetSid;
    private String name;
    private String path;

    private AuthzStrategy authzStrategy;

    public AbstractItemVO()
    {
    }

    public AbstractItemVO(String pid, String parentSid, String datasetSid, String name)
    {
        this.sid = pid;
        this.parentSid = parentSid;
        this.datasetSid = datasetSid;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.knaw.dans.easy.domain.dataset.IItemVO#getSid()
     */
    public String getSid()
    {
        return this.sid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.knaw.dans.easy.domain.dataset.IItemVO#setSid(java.lang.String)
     */
    public void setSid(String pid)
    {
        this.sid = pid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.knaw.dans.easy.domain.dataset.IItemVO#getParentSid()
     */
    public String getParentSid()
    {
        return this.parentSid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.knaw.dans.easy.domain.dataset.IItemVO#setParentSid(java.lang.String)
     */
    public void setParentSid(String parentSid)
    {
        this.parentSid = parentSid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.knaw.dans.easy.domain.dataset.IItemVO#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.knaw.dans.easy.domain.dataset.IItemVO#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.knaw.dans.easy.domain.dataset.IItemVO#getDatasetSid()
     */
    public String getDatasetSid()
    {
        return datasetSid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.knaw.dans.easy.domain.dataset.IItemVO#setDatasetSid(java.lang.String)
     */
    public void setDatasetSid(String datasetSid)
    {
        this.datasetSid = datasetSid;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean belongsTo(Dataset dataset)
    {
        boolean belongsTo = false;
        if (dataset != null && getDatasetSid() != null)
        {
            belongsTo = getDatasetSid().equals(dataset.getStoreId());
        }
        return belongsTo;
    }

    @Override
    public AuthzStrategy getAuthzStrategy()
    {
        if (authzStrategy == null)
        {
            throw new IllegalStateException("No AuthzStrategy set on this ItemVO.");
        }
        return authzStrategy;
    }

    public void setAuthzStrategy(AuthzStrategy authzStrategy)
    {
        this.authzStrategy = authzStrategy;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datasetSid == null) ? 0 : datasetSid.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentSid == null) ? 0 : parentSid.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((sid == null) ? 0 : sid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractItemVO other = (AbstractItemVO) obj;
        if (datasetSid == null)
        {
            if (other.datasetSid != null)
                return false;
        }
        else if (!datasetSid.equals(other.datasetSid))
            return false;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (parentSid == null)
        {
            if (other.parentSid != null)
                return false;
        }
        else if (!parentSid.equals(other.parentSid))
            return false;
        if (path == null)
        {
            if (other.path != null)
                return false;
        }
        else if (!path.equals(other.path))
            return false;
        if (sid == null)
        {
            if (other.sid != null)
                return false;
        }
        else if (!sid.equals(other.sid))
            return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
