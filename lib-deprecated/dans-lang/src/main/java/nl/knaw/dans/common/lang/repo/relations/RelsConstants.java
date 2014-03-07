package nl.knaw.dans.common.lang.repo.relations;

public class RelsConstants
{
    public static final DansOntologyNamespace DANS_NS = new DansOntologyNamespace();

    public static final String RDF_LITERAL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";

    public static final String OAI_ITEM_ID = "http://www.openarchives.org/OAI/2.0/itemID";

    public static final String OAI_SET_SPEC = "http://www.openarchives.org/OAI/2.0/setSpec";

    public static final String OAI_SET_NAME = "http://www.openarchives.org/OAI/2.0/setName";

    public static final String FEDORA_URI = "info:fedora/";

    public static final String FM_HAS_MODEL = "info:fedora/fedora-system:def/model#hasModel";

    public static final String RELS_EXT_IS_MEMBER_OF = "info:fedora/fedora-system:def/relations-external#isMemberOf";

    public static String getObjectURI(String storeId)
    {
        if (storeId.startsWith(FEDORA_URI))
        {
            return storeId;
        }
        return FEDORA_URI + storeId;
    }

    public static String stripFedoraUri(Object object)
    {
        if (object == null)
        {
            return null;
        }
        String objectURI = object.toString();
        if (!objectURI.startsWith(FEDORA_URI))
        {
            return objectURI;
        }
        return objectURI.substring(FEDORA_URI.length());
    }

}
