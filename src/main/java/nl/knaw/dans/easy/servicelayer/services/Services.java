package nl.knaw.dans.easy.servicelayer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Services
{
    private static DatasetService datasetService;
    private static ItemService itemService;
    private static DepositService depositService;
    private static UserService userService;
    private static MigrationService migrationService;
    private static FederativeUserService federativeUserService;
    private static SearchService searchService;
    private static DisciplineCollectionService disciplineService;
    private static CollectionService collectionService;
    private static JumpoffService jumpoffService;
    private static Logger logger = LoggerFactory.getLogger(Services.class);

    public Services() throws IllegalStateException
    {
        logger.debug("Created " + this);
    }

    public static DepositService getDepositService()
    {
        return depositService;
    }

    public static DatasetService getDatasetService()
    {
        return datasetService;
    }

    public static ItemService getItemService()
    {
        return itemService;
    }

    public static UserService getUserService()
    {
        return userService;
    }

    public static MigrationService getMigrationService()
    {
        return migrationService;
    }

    public static FederativeUserService getFederativeUserService()
    {
        return federativeUserService;
    }

    public static SearchService getSearchService()
    {
        return searchService;
    }

    public static DisciplineCollectionService getDisciplineService()
    {
        return disciplineService;
    }

    public static CollectionService getCollectionService()
    {
        return collectionService;
    }

    public static JumpoffService getJumpoffService()
    {
        return jumpoffService;
    }

    public void setDepositService(DepositService depositService) throws IllegalStateException
    {
        Services.depositService = depositService;
        logger.debug("Injected dependency depositService: " + depositService);
    }

    public void setDatasetService(DatasetService datasetService) throws IllegalStateException
    {
        Services.datasetService = datasetService;
        logger.debug("Injected dependency datasetService: " + datasetService);
    }

    public void setItemService(ItemService itemService) throws IllegalStateException
    {
        Services.itemService = itemService;
        logger.debug("Injected dependency itemService: " + itemService);
    }

    public void setUserService(UserService userService) throws IllegalStateException
    {
        Services.userService = userService;
        logger.debug("Injected dependency userService: " + userService);
    }

    public void setMigrationService(MigrationService migrationService) throws IllegalStateException
    {
        Services.migrationService = migrationService;
        logger.debug("Injected dependency migrationService: " + migrationService);
    }

    public void setFederativeUserService(FederativeUserService federativeUserService)
    {
        Services.federativeUserService = federativeUserService;
        logger.debug("Injected dependency federativeUserService: " + federativeUserService);
    }

    public void setDisciplineService(DisciplineCollectionService disciplineService)
    {
        Services.disciplineService = disciplineService;
        logger.debug("Injected dependency disciplineCollectionService: " + disciplineService);
    }

    public void setCollectionService(CollectionService collectionService)
    {
        Services.collectionService = collectionService;
        logger.debug("injected dependency collectionService: " + collectionService);
    }

    public void setJumpoffService(JumpoffService jumpoffService)
    {
        Services.jumpoffService = jumpoffService;
        logger.debug("Injected dependency jumpoffService: " + jumpoffService);
    }

    public void setSearchService(SearchService searchService)
    {
        Services.searchService = searchService;
    }
}
