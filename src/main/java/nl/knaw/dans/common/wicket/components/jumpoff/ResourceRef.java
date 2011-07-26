package nl.knaw.dans.common.wicket.components.jumpoff;

import java.io.Serializable;

import org.apache.wicket.Application;

import nl.knaw.dans.common.lang.repo.UnitMetadata;

public class ResourceRef implements Serializable
{

    public static final String CONTEXT_PATH = "/ui";

    private static final long serialVersionUID = -3112109550712445609L;
    
    private final String containerId;
    private final String unitId;
    private final String filename;
    private final String mimeType;
    private boolean referenced;
    private final String resourceAlias;
    
    public ResourceRef(String containerId, UnitMetadata unitMetadata, String resourceAlias)
    {
        this.containerId = containerId;
        unitId = unitMetadata.getId();
        filename = unitMetadata.getLabel();
        mimeType = unitMetadata.getMimeType();
        this.resourceAlias = resourceAlias;
    }
    
    public String getHref()
    {
        String alias = resourceAlias == null ? Application.class.getName() : resourceAlias;
        return CONTEXT_PATH + "/resources/" + alias + "/content?sid=" + containerId + "&did=" + unitId;
    }

    public String getContainerId()
    {
        return containerId;
    }

    public String getUnitId()
    {
        return unitId;
    }

    public String getFilename()
    {
        return filename;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public boolean isReferenced()
    {
        return referenced;
    }

    public void setReferenced(boolean referenced)
    {
        this.referenced = referenced;
    }

    public String getResourceAlias()
    {
        return resourceAlias;
    }

}
