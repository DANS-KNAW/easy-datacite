package nl.knaw.dans.easy.data;

import java.io.File;

import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.easy.data.collections.DmoCollectionsAccess;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.data.search.DatasetSearch;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.data.userrepo.GroupRepo;
import nl.knaw.dans.easy.data.userrepo.RepoAccessDelegatorImpl;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data is a sentient android who serves as the second officer and chief operations officer aboard the
 * starships USS Enterprise-D and USS Enterprise-E. As a Data Access Point Broker, Data is the central
 * point for persisted knowledge. Calling the constructor (or any of an instances setter calls) during
 * application runtime will lead to an IllegelStateException. So the normal use of this class is to treat
 * it as a static class to get a Data Access Point implementation:
 * 
 * <pre>
 * UserRepo userRepository = Data.getUserRepo();
 * </pre>
 * 
 * @author ecco Jan 25, 2009
 */
public class Data
{
    private static EasyUserRepo userRepo;
    private static GroupRepo groupRepo;
    private static MigrationRepo migrationRepo;
    private static FederativeUserRepo federativeUserRepo;
    private static EasyStore easyStore;
    private static FileStoreAccess fileStoreAccess;
    private static DatasetSearch datasetSearch;
    private static SearchEngine searchEngine;
    private static DmoCollectionsAccess dmoCollectionAccess;

    private static int downloadLimit; // max. size of download in Mb
    private static int maxNumberOfFiles;
    private static File zipFileDir;

    private static Logger logger = LoggerFactory.getLogger(Data.class);

    /**
     * Constructs new Data - called by the application context. Sets a {@link RepoAccessDelegatorImpl} on
     * {@link RepoAccess}.
     * 
     * @throws IllegalStateException
     *         if the constructor was called during application runtime
     */
    public Data() throws IllegalStateException
    {
        // set RepoAccessDelegator on RepoAccess.
        RepoAccess.setDelegator(new RepoAccessDelegatorImpl());
    }

    /**
     * Get the Data Access Point for {@link EasyUser}s.
     * 
     * @return the UserRepo
     */
    public static EasyUserRepo getUserRepo()
    {
        if (userRepo == null)
        {
            throw new DataConfigurationException("No userRepo set. Make sure the application context is properly configured.");
        }
        return userRepo;
    }

    /**
     * Get the Data Access Point for {@link Group}s.
     * 
     * @return the GroupRepo
     */
    public static GroupRepo getGroupRepo()
    {
        if (groupRepo == null)
        {
            throw new DataConfigurationException("No groupRepo set. Make sure the application context is properly configured.");
        }
        return groupRepo;
    }

    public static MigrationRepo getMigrationRepo()
    {
        if (migrationRepo == null)
        {
            throw new DataConfigurationException("No MigrationRepo set. Make sure the application context is properly configured.");
        }
        return migrationRepo;
    }

    public static FederativeUserRepo getFederativeUserRepo()
    {
        if (federativeUserRepo == null)
        {
            throw new DataConfigurationException("No FederativeUserRepo set. Make sure the application context is properly configured.");
        }
        return federativeUserRepo;
    }

    public static EasyStore getEasyStore()
    {
        if (easyStore == null)
        {
            throw new DataConfigurationException("No easyStore set. Make sure the application context is properly configured.");
        }
        return easyStore;
    }

    public static FileStoreAccess getFileStoreAccess()
    {
        if (fileStoreAccess == null)
        {
            throw new DataConfigurationException("No fileStoreAccess set. Make sure the application context is properly configured.");
        }
        return fileStoreAccess;
    }

    public static DatasetSearch getDatasetSearch()
    {
        if (datasetSearch == null)
        {
            throw new DataConfigurationException("No datasetSearch set. Make sure the application context is properly configured.");
        }
        return datasetSearch;
    }

    public static SearchEngine getSearchEngine()
    {
        if (searchEngine == null)
        {
            throw new DataConfigurationException("No searchEngine set. Make sure the application context is properly configured.");
        }
        return searchEngine;
    }

    public static DmoCollectionsAccess getCollectionAccess()
    {
        if (dmoCollectionAccess == null)
        {
            throw new DataConfigurationException("No dmoCollectionAccess set. Make sure the application context is properly configured.");
        }
        return dmoCollectionAccess;
    }

    public static int getDownloadLimit()
    {
        return downloadLimit;
    }

    // Somehow the spring framework can get confused and wants a String parameter 
    // when easy is deployed on the server
    public void setDownloadLimit(String downloadLimit)
    {
        try
        {
            Data.downloadLimit = Integer.parseInt(downloadLimit);
        }
        catch (NumberFormatException e)
        {
            logger.error("not a valid number: " + downloadLimit);
            throw e;
        }

        logger.info("Download limit is set to " + Data.downloadLimit + " MB");
    }

    public static int getMaxNumberOfFiles()
    {
        return maxNumberOfFiles;
    }

    public void setMaxNumberOfFiles(String maxNumberOfFiles)
    {
        try
        {
            Data.maxNumberOfFiles = Integer.parseInt(maxNumberOfFiles);
        }
        catch (NumberFormatException e)
        {
            logger.error("not a valid number: " + maxNumberOfFiles);
            throw e;
        }

        logger.info("Max number of files is set to " + Data.maxNumberOfFiles);
    }

    public void setZipFileDir(File dir)
    {
        Data.zipFileDir = dir;
    }

    public static File getZipFileDir()
    {
        return zipFileDir;
    }

    // more DAP getters

    /**
     * Set UserDAP - called by application context.
     * 
     * @param userRepo
     *        the Data Access Point for {@link EasyUser}s
     * @throws IllegalStateException
     *         if the method was called during application runtime
     * @see reset()
     */
    public void setUserRepo(final EasyUserRepo userRepo) throws IllegalStateException
    {
        Data.userRepo = userRepo;
        logger.debug("Injected dependency userRepo: " + userRepo);
    }

    public void setGroupRepo(final GroupRepo groupRepo) throws IllegalStateException
    {
        Data.groupRepo = groupRepo;
        logger.debug("Injected dependency groupRepo: " + groupRepo);
    }

    public void setMigrationRepo(final MigrationRepo migrationRepo) throws IllegalStateException
    {
        Data.migrationRepo = migrationRepo;
        logger.debug("Injected dependency migrationRepo: " + migrationRepo);
    }

    public void setFederativeUserRepo(final FederativeUserRepo federativeUserRepo) throws IllegalStateException
    {
        Data.federativeUserRepo = federativeUserRepo;
        logger.debug("Injected dependency federativeUserRepo: " + federativeUserRepo);
    }

    public void setEasyStore(final EasyStore easyStore)
    {
        Data.easyStore = easyStore;
        logger.debug("Injected dependency easyStore: " + easyStore);
    }

    public void setFileStoreAccess(final FileStoreAccess fileStoreAccess) throws IllegalStateException
    {
        Data.fileStoreAccess = fileStoreAccess;
        logger.debug("Injected dependency fileStoreAccess: " + fileStoreAccess);
    }

    public void setDatasetSearch(final DatasetSearch datasetSearch) throws IllegalStateException
    {
        Data.datasetSearch = datasetSearch;
        logger.debug("Injected dependency datasetSearch: " + datasetSearch);
    }

    public void setSearchEngine(final SearchEngine searchEngine) throws IllegalStateException
    {
        Data.searchEngine = searchEngine;
        logger.debug("Injected dependency searchEngine: " + searchEngine);
    }

    public void setCollectionAccess(final DmoCollectionsAccess dmoCollectionAccess)
    {
        Data.dmoCollectionAccess = dmoCollectionAccess;
        logger.debug("Injected dependency easyCollections: " + dmoCollectionAccess);
    }
}
