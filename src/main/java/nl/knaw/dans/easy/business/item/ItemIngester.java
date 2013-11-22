package nl.knaw.dans.easy.business.item;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.AbstractWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: If an existing FolderItem with name 'X' during an update is replaced by a file (not a directory)
 * with name 'X' an exception will be thrown.
 * 
 * @author ecco Oct 26, 2009
 */
public class ItemIngester extends AbstractWorker
{

    private static final Logger logger = LoggerFactory.getLogger(ItemIngester.class);

    public static final String[] SKIPPED_FILENAMES = {"Thumbs.db", "__MACOSX", ".DS_Store"};

    public static final List<String> SKIPPED_FILENAMES_LIST = Arrays.asList(SKIPPED_FILENAMES);

    public static final String DEPOSITOR_FOLDER_NAME = "original";

    // CHANGE THIS!!!
    public static final String VIDEO_EXTENSION = ".xml";

    private final ItemIngesterDelegator delegator;

    private final Dataset dataset;

    private final EasyUser sessionUser;

    private final CreatorRole creatorRole;

    private FileFilter ingestFilter;

    /**
     * @param dataset
     *        the dataset that is augmented
     * @param sessionUser
     *        the user that initiates the ingest
     */
    protected ItemIngester(Dataset dataset, EasyUser sessionUser, ItemIngesterDelegator delegator)
    {
        this(new EasyUnitOfWork(sessionUser), dataset, sessionUser, delegator);
    }

    protected ItemIngester(UnitOfWork uow, Dataset dataset, EasyUser sessionUser, ItemIngesterDelegator delegator)
    {
        super(uow);
        this.dataset = dataset;
        this.sessionUser = sessionUser;
        creatorRole = sessionUser.getCreatorRole();
        if (delegator == null)
        {
            this.delegator = new DefaultDelegator(dataset);
        }
        else
        {
            this.delegator = delegator;
        }
    }

    /*
     * used by tools-easy-import
     */
    protected void addDirectoryContents(DatasetItemContainer parentContainer, File rootFile) throws ServiceException
    {
        workAddDirectoryContents(parentContainer, rootFile, new IngestFilter());
    }

    protected void workAddDirectoryContents(DatasetItemContainer parentContainer, File rootFile, FileFilter ingestFilter) throws ServiceException
    {
        this.ingestFilter = ingestFilter;
        try
        {
            getUnitOfWork().attach(dataset);
            getUnitOfWork().attach(parentContainer);
            if (logger.isDebugEnabled())
            {
                logger.debug("Ingesting for " + dataset.getStoreId() + " in parentContainer " + parentContainer.getStoreId() + ". rootFile="
                        + rootFile.getName() + " ingestFilter=" + ingestFilter);

            }

            if (!rootFile.isDirectory())
            {
                throw new IllegalArgumentException("The given file is not a directory: " + rootFile.getPath());
            }

            Map<String, String> members = collectMembers(parentContainer.getDmoStoreId());

            // Files and folders submitted by depositor go into folder 'original'.
            if (parentContainer instanceof Dataset && CreatorRole.DEPOSITOR.equals(creatorRole))
            {
                String storeId = members.get(DEPOSITOR_FOLDER_NAME);
                if (storeId == null)
                {
                    FolderItem original = (FolderItem) AbstractDmoFactory.newDmo(FolderItem.NAMESPACE);
                    getUnitOfWork().attach(original);

                    original.setLabel(DEPOSITOR_FOLDER_NAME);
                    original.setOwnerId(sessionUser.getId());
                    original.setDatasetId(dataset.getDmoStoreId());
                    original.setParent(parentContainer);
                    parentContainer = original;
                }
                else
                {
                    FolderItem original = (FolderItem) getUnitOfWork().retrieveObject(new DmoStoreId(storeId));
                    parentContainer = original;
                }
                members = collectMembers(parentContainer.getDmoStoreId());
            }
            // End files and folders submitted by depositor go into folder 'original'.

            for (File file : rootFile.listFiles(ingestFilter))
            {
                addFile(parentContainer, file, members);
            }
            getUnitOfWork().commit();
        }
        catch (UnitOfWorkInterruptException e)
        {
            // rollBack(e.getMessage());
            throw new UnsupportedOperationException("Rollback not implemented");
        }
        catch (RepositoryException e)
        {
            throw new ServiceException("Exception while processing unit of work: ", e);
        }
        catch (IOException e)
        {
            throw new ServiceException("Exception while processing files: ", e);
        }
        catch (DomainException e)
        {
            throw new ServiceException("Exception while processing files: ", e);
        }
        finally
        {
            getUnitOfWork().close();
        }
    }

    // Base entrance for the process. Decides whether to ingest or update an item.
    private void addFile(DatasetItemContainer parentContainer, File file, Map<String, String> members) throws RepositoryException, IOException,
            UnitOfWorkInterruptException, DomainException
    {
        String storeId = members.get(file.getName());
        if (storeId == null)
        {
            ingestFile(parentContainer, file);
        }
        else
        {
            updateFile(new DmoStoreId(storeId), file);
        }
    }

    // ingest FileItems and FolderITems
    private void ingestFile(DatasetItemContainer parentContainer, File file) throws RepositoryException, IOException, UnitOfWorkInterruptException,
            DomainException
    {
        if (file.isDirectory())
        {
            FolderItem kidFolder = (FolderItem) AbstractDmoFactory.newDmo(FolderItem.NAMESPACE);
            getUnitOfWork().attach(kidFolder);

            kidFolder.setLabel(file.getName());
            kidFolder.setOwnerId(sessionUser.getId());
            kidFolder.setDatasetId(dataset.getDmoStoreId());
            // set parent before iterating kids. see DatasetItem setParent/ getPath
            kidFolder.setParent(parentContainer);

            for (File kid : file.listFiles(ingestFilter))
            {
                ingestFile(kidFolder, kid);
            }

        }
        else
        {
            FileItem kidFile = (FileItem) AbstractDmoFactory.newDmo(FileItem.NAMESPACE);
            getUnitOfWork().attach(kidFile);

            kidFile.setFile(file);
            kidFile.setCreatorRole(creatorRole);
            // CHANGE THIS!!!
            if (file.getName().endsWith(VIDEO_EXTENSION))
            {
                kidFile.setStreamingUrl("http://www.koe.com/video/" + file.getName());
            }
            kidFile.setDatasetId(dataset.getDmoStoreId());
            kidFile.setOwnerId(sessionUser.getId());
            // order of next statements is of importance for migration
            kidFile.setParent(parentContainer);
            setFileRights(kidFile);
            addAdditionalMetadata(kidFile);
            addAdditionalRDF(kidFile);

            getUnitOfWork().saveAndDetach(kidFile);
        }
    }

    protected void setFileRights(FileItem fileItem)
    {
        delegator.setFileRights(fileItem);
    }

    protected void addAdditionalMetadata(FileItem fileItem)
    {
        delegator.addAdditionalMetadata(fileItem);
    }

    protected void addAdditionalRDF(FileItem fileItem)
    {
        delegator.addAdditionalRDF(fileItem);
    }

    // update FileItems and FolderItems
    private void updateFile(DmoStoreId storeId, File file) throws RepositoryException, IOException, UnitOfWorkInterruptException, DomainException
    {
        if (file.isDirectory())
        {
            if (!storeId.isInNamespace(FolderItem.NAMESPACE))
            {
                throw new RepositoryException("Cannot update " + storeId + " because it is not a FolderItem.");
            }
            Map<String, String> members = collectMembers(storeId);
            FolderItem currentFolder = (FolderItem) getUnitOfWork().retrieveObject(storeId);
            for (File kid : file.listFiles(ingestFilter))
            {
                addFile(currentFolder, kid, members);
            }
        }
        else
        {
            if (!storeId.isInNamespace(FileItem.NAMESPACE))
            {
                throw new RepositoryException("Cannot update " + storeId + " because it is not a FileItem.");
            }
            FileItem currentFile = (FileItem) getUnitOfWork().retrieveObject(storeId);
            currentFile.setFile(file);
            currentFile.setCreatorRole(creatorRole);
            currentFile.setOwnerId(sessionUser.getId());
            setFileRights(currentFile);
            addAdditionalMetadata(currentFile);
            addAdditionalRDF(currentFile);

            getUnitOfWork().saveAndDetach(currentFile);
        }
    }

    private Map<String, String> collectMembers(DmoStoreId parentId) throws RepositoryException
    {
        Map<String, String> members = new HashMap<String, String>();
        List<ItemVO> items = Data.getFileStoreAccess().getFilesAndFolders(parentId, -1, -1, null, null);
        for (ItemVO item : items)
        {
            members.put(item.getName(), item.getSid());
        }

        return members;
    }

    public static class ListFilter implements FileFilter
    {

        private final List<File> filesToIngest;

        public ListFilter(List<File> filesToIngest)
        {
            this.filesToIngest = filesToIngest;
        }

        public boolean accept(File file)
        {
            boolean accept = filesToIngest.contains(file) && !SKIPPED_FILENAMES_LIST.contains(file.getName());

            return accept;
        }

    }

    public static class IngestFilter implements FileFilter
    {

        public boolean accept(File file)
        {
            boolean accept = !SKIPPED_FILENAMES_LIST.contains(file.getName());

            return accept;
        }

    }

    public static class DefaultDelegator implements ItemIngesterDelegator
    {

        // TODO refactor enum VisibleTo to AccessCategory;
        private final VisibleTo visibleToOnIngest;
        private final AccessibleTo accesibleTonIngest;

        public DefaultDelegator(Dataset dataset)
        {
            AccessCategory accessCategory = dataset.getAccessCategory();
            visibleToOnIngest = VisibleTo.ANONYMOUS; // all files are visible, unless an archivist
                                                     // decides differently.
            accesibleTonIngest = AccessibleTo.translate(accessCategory);
        }

        @Override
        public void setFileRights(FileItem fileItem)
        {
            setDiscoverRights(fileItem);
            setReadRights(fileItem);
        }

        protected void setDiscoverRights(FileItem fileItem)
        {
            fileItem.setVisibleTo(visibleToOnIngest);
        }

        protected void setReadRights(FileItem fileItem)
        {
            fileItem.setAccessibleTo(accesibleTonIngest);
        }

        @Override
        public void addAdditionalMetadata(FileItem fileItem)
        {
            // no additional metadata in default behavior.
        }

        @Override
        public void addAdditionalRDF(FileItem fileItem)
        {
            // no additional RDF in default behavior.

        }

    }

}
