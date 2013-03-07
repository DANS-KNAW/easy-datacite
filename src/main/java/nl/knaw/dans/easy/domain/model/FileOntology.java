package nl.knaw.dans.easy.domain.model;

/**
 * Voorlopige (en zeer beperkte) ontologie voor files.
 * 
 * @author henk van den berg
 */
public class FileOntology
{

    public enum MetadataFormat
    {
        CMDI, DDM, DDI 
    }
    
    public static final String URI = "http://easy.dans.knaw.nl/ontologies/file#";
    public static final String PREFIX = "easy-file";

    private static final String IS_METADATA_ON = "isMetadataOn";

    private static final String HAS_METADATA_FORMAT = "hasMetadataFormat";

    private String text = URI;

    public String get()
    {
        return text;
    }

    public String iri()
    {
        return "<" + text + ">";
    }

    public FileOntology isMetadataOn()
    {
        text = URI + IS_METADATA_ON;
        return this;
    }

    public FileOntology hasMetadataFormat()
    {
        text = URI + HAS_METADATA_FORMAT;
        return this;
    }

}
