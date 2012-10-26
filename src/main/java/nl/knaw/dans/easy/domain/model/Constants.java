package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.repo.DmoStoreId;

public interface Constants
{

    /**
     * Content model for Datasets.
     */
    String CM_DATASET_1 = "easy-model:EDM1DATASET";

    /**
     * Content model for FileItems.
     */
    String CM_FILE_ITEM_1 = "easy-model:EDM1FILE";

    /**
     * Content model for FolderItems.
     */
    String CM_FOLDER_1 = "easy-model:EDM1FOLDER";

    /**
     * Content model for OAI items.
     */
    String CM_OAI_ITEM_1 = "easy-model:oai-item1";

    /**
     * Content model for OAI sets.
     */
    String CM_OAI_SET_1 = "easy-model:oai-set1";

    /**
     * The prefix for OAI identifiers. A complete OAI identifier complies to:
     * <pre>
     *   oai-identifier = scheme ":" namespace-identifier ":" local-identifier
     * <pre>
     * 
     * @see http://www.openarchives.org/OAI/2.0/guidelines-oai-identifier.htm
     */
    String OAI_IDENTIFIER_PREFIX = "oai:easy.dans.knaw.nl:";

    /**
     * 
     */
    String OAI_DRIVER_SET_ID = "easy-data:oai-driverset1";

    DmoStoreId OAI_DRIVER_SET_DMO_ID = new DmoStoreId(OAI_DRIVER_SET_ID);

}
