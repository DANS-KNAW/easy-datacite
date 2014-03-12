package nl.knaw.dans.pf.language.emd.types;

import java.io.Serializable;

public class ApplicationSpecific implements Serializable
{

    public enum MetadataFormat
    {
        UNSPECIFIED, SOCIOLOGY, HISTORY, ARCHAEOLOGY, LIFESCIENCE, LANGUAGE_LITERATURE
    }
    
    public enum PakbonStatus {
        NOT_IMPORTED, IMPORTED;
    }

    private static final long serialVersionUID = -7645674090791579101L;

    private MetadataFormat metadataFormat = MetadataFormat.UNSPECIFIED;
    private PakbonStatus pakbonStatus = PakbonStatus.NOT_IMPORTED;

    public MetadataFormat getMetadataFormat()
    {
        return metadataFormat;
    }

    public void setMetadataFormat(MetadataFormat metadataFormat)
    {
        this.metadataFormat = metadataFormat;
    }

    public void setPakbonStatus(PakbonStatus status)
    {
        if(metadataFormat.equals(MetadataFormat.ARCHAEOLOGY)) {
            pakbonStatus = status;
        }
    }

    public PakbonStatus getPakbonStatus()
    {
        return pakbonStatus;
    }

    public static MetadataFormat formatForName(String name)
    {
        return MetadataFormat.valueOf(name.toUpperCase());
    }

}
