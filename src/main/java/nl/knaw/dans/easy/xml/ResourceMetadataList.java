package nl.knaw.dans.easy.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class ResourceMetadataList extends AbstractJiBXObject<ResourceMetadataList>
{

    private static final long serialVersionUID = 4665139159743935829L;

    private Map<String, ResourceMetadata> resourceMetadataMap = new LinkedHashMap<String, ResourceMetadata>();

    public void setResourceMetadataAsList(List<ResourceMetadata> resourceMetadataList)
    {
        resourceMetadataMap.clear();
        for (ResourceMetadata fmd : resourceMetadataList)
        {
            resourceMetadataMap.put(fmd.getIdentifier(), fmd);
        }
    }

    public List<ResourceMetadata> getResourceMetadataAsList()
    {
        return new ArrayList<ResourceMetadata>(resourceMetadataMap.values());
    }

    public ResourceMetadata getResourceMetadata(String pathOrSid)
    {
        return resourceMetadataMap.get(pathOrSid);
    }

    public void addResourceMetadata(ResourceMetadata fmd)
    {
        resourceMetadataMap.put(fmd.getIdentifier(), fmd);
    }

}
