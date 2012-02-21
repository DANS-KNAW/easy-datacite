package nl.knaw.dans.easy.fedora.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.data.store.StoreException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.fedora.store.AbstractOnlineTest;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class FedoraFileStoreAccessOnlineTest extends AbstractOnlineTest
{
    
    private static FedoraFileStoreAccess fsAccess;
    
    @BeforeClass
    public static void beforeClass()
    {
        fsAccess = new FedoraFileStoreAccess(getFedora(), getDbLocalConfig());
    }
    
    @Ignore("Local variables")
    @Test
    public void findById() throws StoreAccessException
    {
        String fileId = "easy-file:2071";
        FileItemVO fivo = fsAccess.findFileById(new DmoStoreId(fileId));
        assertEquals(fileId, fivo.getSid());
    }
    
    @Ignore("Local variables")
    @Test
    public void findByIdList() throws StoreAccessException
    {
        List<DmoStoreId> sids = Arrays.asList(new DmoStoreId[] {new DmoStoreId("easy-file:207"), new DmoStoreId("easy-file:2173"), new DmoStoreId("easy-file:3695")});
        
        List<FileItemVO> fivos = fsAccess.findFilesById(sids);
        assertEquals(2, fivos.size());
        assertEquals("easy-file:2173", fivos.get(0).getSid());
        assertEquals("easy-file:3695", fivos.get(1).getSid());
    }
    
    @Ignore("Local variables")
    @Test 
    public void findFileByPath() throws Exception
    {
        String datasetId = "easy-dataset:10";
        String relativePath = "original/img/00000087.002";
        FileItemVO fivo = fsAccess.findFileByPath(new DmoStoreId(datasetId), relativePath);
        assertEquals(relativePath, fivo.getPath());
        assertEquals(datasetId, fivo.getDatasetSid());
    }
    
    @Ignore("Local variables")
    @Test
    public void findFolderByPath() throws Exception
    {
        String datasetId = "easy-dataset:17";
        String relativePath = "original/prc/";
        FolderItemVO fovo = fsAccess.findFolderByPath(new DmoStoreId(datasetId), relativePath);
        assertEquals(relativePath, fovo.getPath());
        assertEquals(datasetId, fovo.getDatasetSid());
        
        relativePath = "original/prc";
        fovo = fsAccess.findFolderByPath(new DmoStoreId(datasetId), relativePath);
        assertEquals(relativePath + "/", fovo.getPath());
        assertEquals(datasetId, fovo.getDatasetSid());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void findByIdEmptyList() throws StoreAccessException
    {
        List<DmoStoreId> sids = Arrays.asList(new DmoStoreId[] {});
        
        fsAccess.findFilesById(sids);
    }
    
    @Ignore("Local variables")
    @Test
    public void canItFindDatasetChildren() throws StoreAccessException
    {
        List<ItemVO> datasetKids = fsAccess.getFilesAndFolders(new DmoStoreId("easy-dataset:721"), -1, -1, null, null);
        for (ItemVO kid : datasetKids)
        {
            System.out.println(kid.getName());
        }
        
        List<FileItemVO> kids = fsAccess.getFiles(new DmoStoreId("easy-dataset:1"), -1, -1, null, null);
        for (ItemVO kid : kids)
        {
            System.out.println(kid.getName());
        }
    }
    
    @Ignore("Local variables")
    @Test
    public void getFileNames() throws StoreAccessException
    {
        List<String> filenames = fsAccess.getFilenames(new DmoStoreId("easy-dataset:201"), true);
        for (String s : filenames)
        {
            System.err.println(s);
        }
    }
    
    @Test
    public void getDatasetId() throws Exception
    {
        String datasetId = fsAccess.getDatasetId(new DmoStoreId("easy-folder:1"));
        System.err.println(datasetId);
    }
    
    @Test
    public void getDatasetIdWithNoneExistingItemId() throws Exception
    {
        String datasetId = fsAccess.getDatasetId(new DmoStoreId("easy-folder:0"));
        assertNull(datasetId);
    }
    
    @Test(expected = StoreException.class)
    public void getDatasetIdWithWrongParameter() throws Exception
    {
        fsAccess.getDatasetId(new DmoStoreId("easy-dataset:1"));
    }

}
