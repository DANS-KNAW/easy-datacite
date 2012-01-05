package nl.knaw.dans.easy.xml;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.model.FileItem;

public class ResourceMetadata extends AbstractJiBXObject<ResourceMetadata>
{

    private static final long serialVersionUID = -6682322311713437593L;

    private String path;
    private String sid;
    private AccessCategory categoryDiscover;
    private AccessCategory categoryRead;
    private AccessCategory categoryWrite;
    private AccessCategory categoryDelete;
    
    private AdditionalMetadata additionalMetadata;
    
    public ResourceMetadata()
    {
        
    } 
    
    public ResourceMetadata(String identifier)
    {
        identifier = identifier.trim();
        if (identifier.startsWith(FileItem.NAMESPACE.getValue()))
        {
            this.sid = identifier;
        }
        else
        {
            this.path = identifier;
        }
    }
    
    public String getIdentifier()
    {
        return sid == null ? path : sid;
    }
    
    public void setPath(String path)
    {
        this.path = path == null ? null : path.trim();
    }

    public String getPath()
    {
        return path;
    }
    
    public void setSid(String sid)
    {
        this.sid = sid == null ? null : sid.trim();
    }

    public String getSid()
    {
        return sid;
    }

    public AccessCategory getCategoryDiscover()
    {
        return categoryDiscover;
    }

    public void setCategoryDiscover(AccessCategory categoryDiscover)
    {
        this.categoryDiscover = categoryDiscover;
    }


    public AccessCategory getCategoryRead()
    {
        return categoryRead;
    }

    public void setCategoryRead(AccessCategory categoryRead)
    {
        this.categoryRead = categoryRead;
    }


    public AccessCategory getCategoryWrite()
    {
        return categoryWrite;
    }


    public void setCategoryWrite(AccessCategory categoryWrite)
    {
        this.categoryWrite = categoryWrite;
    }

    public AccessCategory getCategoryDelete()
    {
        return categoryDelete;
    }

    public void setCategoryDelete(AccessCategory categoryDelete)
    {
        this.categoryDelete = categoryDelete;
    }
    
    public boolean hasAdditionalMetadata()
    {
        return additionalMetadata != null;
    }

    public AdditionalMetadata getAdditionalMetadata()
    {
        if (additionalMetadata == null)
        {
            additionalMetadata = new AdditionalMetadata();
        }
        return additionalMetadata;
    }

    public void setAdditionalMetadata(AdditionalMetadata additionalMetadata)
    {
        this.additionalMetadata = additionalMetadata;
    }

    
}
