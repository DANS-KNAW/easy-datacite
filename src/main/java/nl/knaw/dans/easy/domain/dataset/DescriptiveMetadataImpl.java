package nl.knaw.dans.easy.domain.dataset;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.AbstractTimestampedObject;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;

import org.dom4j.Element;

public class DescriptiveMetadataImpl extends AbstractTimestampedObject implements DescriptiveMetadata
{
    private static final long serialVersionUID = 1575512236385029937L;
    
    private final Element content;
    
    public DescriptiveMetadataImpl(final Element content)
    {
        this.content = content;
    }
    
    @Override
    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    @Override
    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    @Override
    public String getUnitId()
    {
        return UNIT_ID;
    }

    @Override
    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

    @Override
    public boolean isVersionable()
    {
        return false;
    }

    @Override
    public void setVersionable(boolean versionable)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] asObjectXML() throws XMLSerializationException
    {
        return content.asXML().getBytes();
    }

    @Override
    public Element getContent()
    {
        return content;
    }
    
    @Override
    public List<KeyValuePair> getProperties()
    {
        List<KeyValuePair> properties = new ArrayList<KeyValuePair>();
        @SuppressWarnings("unchecked")
        List<Element> elements = content.elements();
        for (Element element : elements)
        {
            properties.add(new KeyValuePair(element.getName(), element.getText()));
        }
        return properties;
    }

}
