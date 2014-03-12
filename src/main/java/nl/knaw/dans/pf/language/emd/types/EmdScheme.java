package nl.knaw.dans.pf.language.emd.types;

public enum EmdScheme
{
    ARCHAEOLOGY_DC_IDENTIFIER /* choicelist + */, ARCHAEOLOGY_DC_SUBJECT /* choicelist + recursivelist */, ARCHAEOLOGY_DCTERMS_ACCESSRIGHTS, ARCHAEOLOGY_DCTERMS_DATE /* choicelist + */, ARCHAEOLOGY_DCTERMS_TEMPORAL /* choicelist + recursivelist */, ARCHAEOLOGY_EAS_SPATIAL/*EN?*//* choicelist + */, COMMON_DC_FORMAT /* choicelist + */, COMMON_DC_LANGUAGE /* choicelist + */, COMMON_DC_TYPE /* choicelist + */, COMMON_DCTERMS_ACCESSRIGHTS /* choicelist + */, COMMON_DCTERMS_AUDIENCE /* choicelist + */, COMMON_DCTERMS_DATE /* choicelist + */, COMMON_DCTERMS_RELATION /* choicelist + */, LIFESCIENCE_DC_SUBJECT /* choicelist + */, LIFESCIENCE_EAS_SPATIAL /* choicelist + */, ;
    public String getId()
    {
        return name().toLowerCase().replaceAll("_", ".");
    }
}
