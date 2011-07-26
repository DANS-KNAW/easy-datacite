package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FolderItemTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(FolderItemTest.class);
    
    private boolean verbose = Tester.isVerbose();
    
    @Test
    public void addChildEarlyBinding() throws XMLSerializationException, RepositoryException, DomainException
    {
        FolderItem fo1 = new FolderItemImpl("folder:1");
        	
        assertEquals(0, fo1.getChildFileCount());
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        FileItem fi1 = new FileItemImpl("file:1");
        fo1.addFileOrFolder(fi1);
        
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        fi1.setCreatorRole(CreatorRole.ARCHIVIST);
        fi1.setAccessibleTo(AccessibleTo.ANONYMOUS);
        
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(1, fo1.getTotalFileCount());
        assertEquals(0, fo1.getChildFolderCount());
        assertEquals(0, fo1.getTotalFolderCount());
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        assertEquals(1, fo1.getAccessibleToFileCount(AccessibleTo.ANONYMOUS));
        assertEquals(0, fo1.getAccessibleToFileCount(AccessibleTo.KNOWN));
        assertEquals(1, fo1.getDatasetItemContainerMetadata().getAccessibleToList().size());
        
        FolderItem fo2 = new FolderItemImpl("folder:2");
                
        FileItem fi2 = new FileItemImpl("file:2");
        fo2.addFileOrFolder(fi2);
        fo1.addFileOrFolder(fo2);
        
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(2, fo1.getTotalFileCount());
        assertEquals(1, fo1.getChildFolderCount());
        assertEquals(1, fo1.getTotalFolderCount());
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        fi2.setCreatorRole(CreatorRole.DEPOSITOR);
        fi2.setVisibleTo(VisibleTo.KNOWN);
        
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(2, fo1.getTotalFileCount());
        assertEquals(1, fo1.getChildFolderCount());
        assertEquals(1, fo1.getTotalFolderCount());
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        assertEquals(1, fo1.getVisibleToFileCount(VisibleTo.KNOWN));
        
        
        if (verbose)
            logger.debug("\n" + fo1.getDatasetItemContainerMetadata().asXMLString(4));
    }
    
    @Test
    public void addChildLateBindingAndRemove() throws XMLSerializationException, RepositoryException, DomainException
    {
    	FolderItem fo1 = new FolderItemImpl("folder:1");
    	
    	
        assertEquals(0, fo1.getChildFileCount());
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        FileItem fi1   = new FileItemImpl("file:1");
        FolderItem fo2 = new FolderItemImpl("folder:2");
        
      
        FileItem fi2 = new FileItemImpl("file:2");
        fi1.setCreatorRole(CreatorRole.ARCHIVIST);
        fi1.setVisibleTo(VisibleTo.NONE);
        fi2.setCreatorRole(CreatorRole.DEPOSITOR);
        fi2.setAccessibleTo(AccessibleTo.RESTRICTED_REQUEST);
        
        fo1.addFileOrFolder(fi1);
        fo2.addFileOrFolder(fi2);
        fo1.addFileOrFolder(fo2);
               
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(2, fo1.getTotalFileCount());
        assertEquals(1, fo1.getChildFolderCount());
        assertEquals(1, fo1.getTotalFolderCount());
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        logger.debug("\n" + fo1.getDatasetItemContainerMetadata().asXMLString(4));
        
        fo2.registerDeleted();
        assertFalse(fo2.isDeletable());
        
        fi2.registerDeleted();
        assertTrue(fo2.isDeletable());
        
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(1, fo1.getTotalFileCount());
        assertEquals(0, fo1.getChildFolderCount());
        assertEquals(0, fo1.getTotalFolderCount());
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        logger.debug("\n" + fo1.getDatasetItemContainerMetadata().asXMLString(4));
        
        
        
    }


    @Test
    public void setParentTest() throws XMLSerializationException, RepositoryException, DomainException
    {
    	FolderItem fo1 = new FolderItemImpl("folder:1");
    	FolderItem fo2 = new FolderItemImpl("folder:2");
    	   	
        assertEquals(0, fo1.getChildFileCount());
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, fo1.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        
        FileItem fi1   = new FileItemImpl("file:1");
        FileItem fi2   = new FileItemImpl("file:2");
        
        fi1.setCreatorRole(CreatorRole.ARCHIVIST);
        fi1.setVisibleTo(VisibleTo.NONE);
        
        
        fi1.setParent(fo1);

        assertEquals(1, fo1.getChildFileCount());
        assertEquals(1, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(1, fo1.getVisibleToFileCount(VisibleTo.NONE));
        
        fi2.setCreatorRole(CreatorRole.ARCHIVIST);
        fi2.setVisibleTo(VisibleTo.ANONYMOUS);
        
        fi2.setParent(fo2);
        fo2.setParent(fo1);

        assertEquals(2, fo1.getTotalFileCount());
        assertEquals(1, fo1.getChildFileCount());
        assertEquals(1, fo1.getChildFolderCount());
        assertEquals(2, fo1.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(1, fo1.getVisibleToFileCount(VisibleTo.NONE));
        assertEquals(1, fo1.getVisibleToFileCount(VisibleTo.ANONYMOUS));
    }
}
