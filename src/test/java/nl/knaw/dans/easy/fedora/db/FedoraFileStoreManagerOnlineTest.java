package nl.knaw.dans.easy.fedora.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.db.DbLocalConfig;
import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class FedoraFileStoreManagerOnlineTest
{
    
    private static FedoraFileStoreManager fsManager;
    private static FedoraFileStoreAccess fsAccess;
    private static int fileItemIdCount = 1;
    private static int folderItemIdCount = 1;
    
    
    @BeforeClass
    public static void beforeClass() throws StoreAccessException
    {
        // using properties from src/test/resources/test.properties
        DbLocalConfig localConfig = new DbLocalConfig(
                Tester.getString("filestore.test.db.username"), 
                Tester.getString("filestore.test.db.password"), 
                Tester.getString("filestore.test.db.connectionUrl"), 
                Tester.getString("filestore.test.db.hbnDriverClass"), 
                Tester.getString("filestore.test.db.hbnDialect"));
        DbUtil.setLocalConfig(localConfig);
        
        fsManager = new FedoraFileStoreManager();
        fsAccess = new FedoraFileStoreAccess();
    }
    
    
    @Test
    @Ignore("Cannot be run in batch test, because this test uses the test db.")
    public void ingestUpdatePurgeFileItem() throws Exception
    {
        FileItem fileItem = createFileItem();
        fsManager.onIngestFileItem(fileItem);
        verifyPersistence(fileItem);
        
        fileItem.setLabel("new_file_name.docx");
        fileItem.getFileItemMetadata().setPath("new/filepath");
        fsManager.onUpdateFileItem(fileItem);
        verifyPersistence(fileItem);
        
        fsManager.onPurgeFileItem(fileItem);
        assertNull(fsAccess.findFileById(fileItem.getDmoStoreId()));
    }
    
    @Test
    @Ignore("Cannot be run in batch test, because this test uses the test db.")
    public void ingestUpdatPurgeFolderItem() throws Exception
    {
        FolderItem folderItem = createFolderItem();
        fsManager.onIngestFolderItem(folderItem);
        verifyPersistence(folderItem);
        
        FileItem fileItem = createFileItem();
        fileItem.setVisibleTo(VisibleTo.RESTRICTED_REQUEST);
        folderItem.addFileOrFolder(fileItem);
        fsManager.onUpdateFolderItem(folderItem);
        verifyPersistence(folderItem);
        
        fsManager.onPurgeFolderItem(folderItem);
        assertNull(fsAccess.findFolderById(folderItem.getDmoStoreId()));
    }

    private void verifyPersistence(FileItem fileItem) throws StoreAccessException
    {
        FileItemVO fivo = fsAccess.findFileById(fileItem.getDmoStoreId());
        assertNotNull(fivo);
        assertTrue(new FileItemVO(fileItem).equals(fivo));
        assertEquals(new FileItemVO(fileItem), fivo);
    }
    
    private void verifyPersistence(FolderItem folderItem) throws StoreAccessException
    {
        FolderItemVO fovo = fsAccess.findFolderById(folderItem.getDmoStoreId());
        assertNotNull(fovo);
        
        FolderItemVO original = new FolderItemVO(folderItem);
        
        assertEquals(original.getSid(), fovo.getSid());
        assertEquals(original.getParentSid(), fovo.getParentSid());
        assertEquals(original.getDatasetSid(), fovo.getDatasetSid());
        assertEquals(original.getName(), fovo.getName());
        assertEquals(original.getPath(), fovo.getPath()); 
        
        Set<FolderItemAccessibleTo> origAccess = original.getAccessibleToList();
        Set<FolderItemAccessibleTo> fovoAccess = fovo.getAccessibleToList();
        assertEquals(origAccess.size(), fovoAccess.size());
        
        Set<FolderItemVisibleTo> origVis = original.getVisibleToList();
        Set<FolderItemVisibleTo> fovoVis = fovo.getVisibleToList();
        assertEquals(origVis.size(), fovoVis.size());
        
        Set<FolderItemCreatorRole> origCreatorRoles = original.getCreatorRoles();
        Set<FolderItemCreatorRole> fovoCreatorRoles = fovo.getCreatorRoles();
        assertEquals(origCreatorRoles.size(), fovoCreatorRoles.size());
    }

    private FileItem createFileItem() throws IOException
    {
        int id = fileItemIdCount++;
        FileItem fileItem = new FileItemImpl("easy-file:" + id);
        fileItem.getFileItemMetadata().setParentDmoStoreId(new DmoStoreId("easy-folder:1000"));
        fileItem.setDatasetId(new DmoStoreId("easy-dataset:2000"));
        fileItem.setLabel("file_nr" + id + ".txt");
        fileItem.setFile(new File("src/test/resources/test.properties"));
        
        fileItem.getFileItemMetadata().setPath("foo/bar/bla");
        fileItem.setCreatorRole(CreatorRole.DEPOSITOR);
        fileItem.setVisibleTo(VisibleTo.KNOWN);
        fileItem.setAccessibleTo(AccessibleTo.RESTRICTED_GROUP);
        return fileItem;
    }
    
    private FolderItem createFolderItem() throws IOException, DomainException
    {
        // DatasetItemContainerMetadata extends DatasetItemMetadata. yaba!
        int id = folderItemIdCount++;
        FolderItem folderItem = new FolderItemImpl("easy-folder:" + id);
        folderItem.getDatasetItemContainerMetadata().setParentDmoStoreId(new DmoStoreId("easy-folder:1001"));
        folderItem.setDatasetId(new DmoStoreId("easy-dataset:2001"));
        folderItem.setLabel("folder_nr" + id);
        // a path set as "path" will return from db as "path/"
        folderItem.getDatasetItemContainerMetadata().setPath("bla/bar/foo/");
        folderItem.addFileOrFolder(createFileItem());
        
        FileItem fileItem2 = createFileItem();
        fileItem2.setVisibleTo(VisibleTo.ANONYMOUS);
        folderItem.addFileOrFolder(fileItem2);
        
        folderItem.addFileOrFolder(createFileItem());
        return folderItem;
    }
    

}
