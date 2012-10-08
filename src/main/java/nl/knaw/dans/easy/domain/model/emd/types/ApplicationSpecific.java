package nl.knaw.dans.easy.domain.model.emd.types;

import java.io.Serializable;

public class ApplicationSpecific implements Serializable
{
    
    public enum MetadataFormat
    {
        UNSPECIFIED,
        SOCIOLOGY,
        HISTORY,
        ARCHAEOLOGY,
        LIFESCIENCE,
        LANGUAGE_LITERATURE
    }

    /**
     * 
     */
    private static final long serialVersionUID = -7645674090791579101L;
    
    private MetadataFormat metadataFormat = MetadataFormat.UNSPECIFIED;
    
    /**
     * Constructs a new ApplicationSpecific.
     */
    public ApplicationSpecific()
    {
        super();
    }

    public MetadataFormat getMetadataFormat()
    {
        return metadataFormat;
    }

    public void setMetadataFormat(MetadataFormat metadataFormat)
    {
        this.metadataFormat = metadataFormat;
    }
    
    public static MetadataFormat formatForName(String name)
    {
        return MetadataFormat.valueOf(name.toUpperCase());
    }

}
