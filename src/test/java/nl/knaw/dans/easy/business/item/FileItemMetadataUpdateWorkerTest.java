package nl.knaw.dans.easy.business.item;

import static org.junit.Assert.*;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.business.md.amd.ElementnameUpdateStrategy;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.xml.AdditionalContent;
import nl.knaw.dans.easy.xml.AdditionalMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileItemMetadataUpdateWorkerTest
{
    
    private static final String ADDITIONAL_CONTENT_ID = "addi";
    private static UnitOfWork MOCK_UOW;
    private static Dataset MOCK_DATASET;
    private static FileStoreAccess MOCK_FSACCES;
    private static FileItem MOCK_FILE_ITEM;
    
    @BeforeClass
    public static void beforeClass()
    {
        MOCK_UOW = EasyMock.createMock(UnitOfWork.class);
        MOCK_DATASET = EasyMock.createMock(Dataset.class);
        MOCK_FSACCES = EasyMock.createMock(FileStoreAccess.class);
        MOCK_FILE_ITEM = EasyMock.createMock(FileItem.class);
        new Data().setFileStoreAccess(MOCK_FSACCES);
    }
    
    @Test
    public void workUpdateMetadata() throws Exception
    {
        AdditionalMetadataUpdateStrategy strategy = new ElementnameUpdateStrategy(ADDITIONAL_CONTENT_ID);
        FileItemMetadataUpdateWorker worker = new FileItemMetadataUpdateWorker(MOCK_UOW, strategy);
        String path = "the/path/to/file.txt";
        ResourceMetadataList rmdl = createResourceMetadataList(
                AccessCategory.ANONYMOUS_ACCESS, 
                AccessCategory.GROUP_ACCESS, path, 
                new String[]{"element0", "value 0"},
                new String[]{"element1", "value 1"},
                new String[]{"element2", "value 2"},
                new String[]{"element3", "value 3"});
        
        FileItemVO fileItemVO = new FileItemVO();
        fileItemVO.setSid("fileItemId");
        AdditionalMetadata originalAmd = new AdditionalMetadata();
        
        EasyMock.reset(MOCK_UOW, MOCK_DATASET, MOCK_FSACCES, MOCK_FILE_ITEM);
        MOCK_UOW.attach(MOCK_DATASET);
        EasyMock.expect(MOCK_DATASET.getStoreId()).andReturn("datasetId").anyTimes();
        EasyMock.expect(MOCK_FSACCES.findFileByPath("datasetId", path)).andReturn(fileItemVO);
        EasyMock.expect(MOCK_UOW.retrieveObject("fileItemId")).andReturn(MOCK_FILE_ITEM);
        EasyMock.expect(MOCK_FILE_ITEM.getDatasetId()).andReturn("datasetId");
        MOCK_FILE_ITEM.setVisibleTo(VisibleTo.ANONYMOUS);
        MOCK_FILE_ITEM.setAccessibleTo(AccessibleTo.RESTRICTED_GROUP);
        EasyMock.expect(MOCK_FILE_ITEM.getAdditionalMetadata()).andReturn(originalAmd);
        MOCK_UOW.commit();
        MOCK_UOW.close();
        
        EasyMock.replay(MOCK_UOW, MOCK_DATASET, MOCK_FSACCES, MOCK_FILE_ITEM);
        
        worker.workUpdateMetadata(MOCK_DATASET, rmdl);
        
        EasyMock.verify(MOCK_UOW, MOCK_DATASET, MOCK_FSACCES, MOCK_FILE_ITEM);
        
        AdditionalContent content = originalAmd.getAdditionalContent(ADDITIONAL_CONTENT_ID);
        assertNotNull(content);
        
    }
    
    protected ResourceMetadataList createResourceMetadataList(
            AccessCategory discover, AccessCategory read, String path, String[]... values)
    {
        ResourceMetadataList rmdl = new ResourceMetadataList();
        ResourceMetadata rmd = new ResourceMetadata(path);
        rmd.setCategoryDiscover(discover);
        rmd.setCategoryRead(read);
        rmd.setAdditionalMetadata(createAMD(values));
        rmdl.addResourceMetadata(rmd);
        return rmdl;
    }
    
    protected AdditionalMetadata createAMD(String[]... values)
    {
        AdditionalMetadata amd = new AdditionalMetadata();
        Element content = new DefaultElement("myContent");
        
        for (int i = 0; i < values.length; i++)
        {
            Element e = new DefaultElement(values[i][0]);
            e.setText(values[i][1]);
            content.add(e);
        }
        
        AdditionalContent ac = new AdditionalContent(ADDITIONAL_CONTENT_ID, "this is content", content);
        amd.addAdditionalContent(ac);
        return amd;
    }

}
