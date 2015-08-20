package nl.knaw.dans.easy.tools.task.adhoc;

/**
 * Used by xslt-processing.
 * 
 * @author ecco Jan 22, 2010
 */
public class Properties {
    // private static final Logger logger = LoggerFactory.getLogger(Constants.class);

    private static String COMMENT = "New migration of Archeology metadata (archis OMN, audience)";
    private static String COLLECTION_ID;

    private Properties() {

    }

    public void setComment(String comment) {
        COMMENT = comment;
    }

    public static String getComment() {
        return COMMENT;
    }

    /**
     * Momentary collectionId of the resource collection being processed at the moment.
     * 
     * @return momentary collectionId
     */
    public static String getCollectionId() {
        return COLLECTION_ID;
    }

    public static void setCollectionId(String collectionId) {
        COLLECTION_ID = collectionId;
    }

}
