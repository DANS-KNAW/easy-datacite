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

    private static boolean LOCKED;
    private static Logger logger = LoggerFactory.getLogger(Services.class);
    private static final String ILLEGAL_METHOD_CALL = "Illegal method call: Setter methods in the Services class should not be called.";

    public Services() throws IllegalStateException
    {
        if (LOCKED) // CGLIB will call the constructor a second time.
        {
            final String msg = "Illegal constructor call: The Services class cannot be instantiated.";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        logger.debug("Created " + this);
    }

    /**
     * Lock Services. Can be called by a BeanPostProcessor to prevent further changes. All constructor calls and setter
     * methods will throw an IllegalStateException afterwards.
     */
    public void lock()
    {
        LOCKED = true;
        logger.info(this + " has been locked.");
    }

    /**
     * Unlock Services.
     */
    public void unlock()
    {
        LOCKED = false;
        logger.debug(this + " has been unlocked.");
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
        checkLock();
        Services.depositService = depositService;
        logger.debug("Injected dependency depositService: " + depositService);
    }

    public void setDatasetService(DatasetService datasetService) throws IllegalStateException
    {
        checkLock();
        Services.datasetService = datasetService;
        logger.debug("Injected dependency datasetService: " + datasetService);
    }

    public void setItemService(ItemService itemService) throws IllegalStateException
    {
        checkLock();
        Services.itemService = itemService;
        logger.debug("Injected dependency itemService: " + itemService);
    }

    public void setUserService(UserService userService) throws IllegalStateException
    {
        checkLock();
        Services.userService = userService;
        logger.debug("Injected dependency userService: " + userService);
    }

    public void setMigrationService(MigrationService migrationService) throws IllegalStateException
    {
        checkLock();
        Services.migrationService = migrationService;
        logger.debug("Injected dependency migrationService: " + migrationService);
    }

    public void setFederativeUserService(FederativeUserService federativeUserService)
    {
        checkLock();
        Services.federativeUserService = federativeUserService;
        logger.debug("Injected dependency federativeUserService: " + federativeUserService);
    }

    public void setDisciplineService(DisciplineCollectionService disciplineService)
    {
        checkLock();
        Services.disciplineService = disciplineService;
        logger.debug("Injected dependency disciplineCollectionService: " + disciplineService);
    }

    public void setCollectionService(CollectionService collectionService)
    {
        checkLock();
        Services.collectionService = collectionService;
        logger.debug("injected dependency collectionService: " + collectionService);
    }

    public void setJumpoffService(JumpoffService jumpoffService)
    {
        checkLock();
        Services.jumpoffService = jumpoffService;
        logger.debug("Injected dependency jumpoffService: " + jumpoffService);
    }

    public void setSearchService(SearchService searchService)
    {
        Services.searchService = searchService;
    }

    private void checkLock()
    {
        if (LOCKED)
        {
            logger.debug(ILLEGAL_METHOD_CALL);
            throw new IllegalStateException(ILLEGAL_METHOD_CALL);
        }
    }

}
