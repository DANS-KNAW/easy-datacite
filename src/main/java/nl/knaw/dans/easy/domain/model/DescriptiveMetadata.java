package nl.knaw.dans.easy.domain.model;

import java.net.URI;
import java.util.List;

import org.dom4j.Element;

import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;

public interface DescriptiveMetadata extends MetadataUnit
{
    
public static final String UNIT_ID = "DMD";
    
    public static final String UNIT_LABEL = "Descriptive meata data";
    
    public static final String UNIT_FORMAT = "http://dans.knaw.nl/easy/descriptive-md";
    
    public static final URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);
    
    public Element getContent();
    
    public List<KeyValuePair> getProperties();
}
