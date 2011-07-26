package nl.knaw.dans.easy.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.easy.util.PropertyList;

public class AdditionalMetadata extends AbstractJiBXObject<AdditionalMetadata>
{

    private static final long serialVersionUID = 2731810508223291282L;
    
    private PropertyList propertyList;
    private Map<String, AdditionalContent> additionalContentMap = new LinkedHashMap<String, AdditionalContent>();
    
    public PropertyList getPropertryList()
    {
        if (propertyList == null)
        {
            propertyList = new PropertyList();
        }
        return propertyList;
    }

    public void setPropertryList(PropertyList propertyList)
    {
        this.propertyList = propertyList;
    }
    
    public AdditionalContent getAdditionalContent(String id)
    {
        return additionalContentMap.get(id);
    }
    
    public void addAdditionalContent(AdditionalContent addContent)
    {
        String id = addContent.getId();
        if (additionalContentMap.containsKey(id))
        {
            throw new IllegalStateException("Violation of unique constraint adding AdditionalContent with id '" + id + "'");
        }
        additionalContentMap.put(id, addContent);
    }
    
    public void setAdditionalContentList(List<AdditionalContent> addContentList)
    {
        for (AdditionalContent addContent : addContentList)
        {
            addAdditionalContent(addContent);
        }
    }
    
    public List<AdditionalContent> getAdditionalContentlist()
    {
        return new ArrayList<AdditionalContent>(additionalContentMap.values());
    }

}
