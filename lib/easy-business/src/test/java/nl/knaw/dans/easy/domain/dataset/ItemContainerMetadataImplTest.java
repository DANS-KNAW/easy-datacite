package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemContainerMetadataImplTest {

    private static final Logger logger = LoggerFactory.getLogger(ItemContainerMetadataImplTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void serializeDeserializeFull() throws XMLException {
        DmoStoreId folderItemId = new DmoStoreId("foo:123");
        DmoStoreId datasetId = new DmoStoreId("dataset:123");

        ItemContainerMetadataImpl icmd = new ItemContainerMetadataImpl(folderItemId);
        icmd.setDatasetDmoStoreId(datasetId);

        icmd.setName("Testing item container metadata");
        icmd.setParentDmoStoreId(new DmoStoreId("folder:123"));

        if (verbose)
            logger.debug("\n" + icmd.asXMLString(4) + "\n");

        DatasetItemContainerMetadata icmd2 = (DatasetItemContainerMetadata) JiBXObjectFactory.unmarshal(ItemContainerMetadataImpl.class, icmd.asObjectXML());
        assertEquals(icmd.asXMLString(), icmd2.asXMLString());

        if (verbose)
            logger.debug("\n" + icmd2.asXMLString(4) + "\n");
    }

    @Test
    public void administration() throws XMLSerializationException, RepositoryException {
        ItemContainerMetadataImpl icmd = new ItemContainerMetadataImpl(new DmoStoreId("foo:123"));
        assertEquals(0, icmd.getChildFileCount());
        assertEquals(0, icmd.getTotalFileCount());
        assertEquals(0, icmd.getChildFolderCount());
        assertEquals(0, icmd.getTotalFolderCount());
        assertEquals(0, icmd.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, icmd.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        assertEquals(0, icmd.getCreatorRoles().size());

        icmd.setName("folder1");
        assertEquals("folder1", icmd.getPath());

        FileItemImpl fi = new FileItemImpl("dummy-file:1");
        fi.setCreatorRole(CreatorRole.ARCHIVIST);
        icmd.onChildAdded(fi);

        assertEquals(1, icmd.getChildFileCount());
        assertEquals(1, icmd.getTotalFileCount());
        assertEquals(1, icmd.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(0, icmd.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        List<CreatorRole> creatorRoles = icmd.getCreatorRoles();
        assertEquals(1, creatorRoles.size());
        assertEquals(CreatorRole.ARCHIVIST, creatorRoles.get(0));

        fi = new FileItemImpl("dummy-file:2");
        fi.setCreatorRole(CreatorRole.DEPOSITOR);
        icmd.onChildAdded(fi);

        assertEquals(2, icmd.getChildFileCount());
        assertEquals(2, icmd.getTotalFileCount());
        assertEquals(1, icmd.getCreatorRoleFileCount(CreatorRole.ARCHIVIST));
        assertEquals(1, icmd.getCreatorRoleFileCount(CreatorRole.DEPOSITOR));
        creatorRoles = icmd.getCreatorRoles();
        assertEquals(2, creatorRoles.size());

        if (verbose)
            logger.debug("\n" + icmd.asXMLString(4) + "\n");
    }

    @Test
    public void dirtyChecking() throws RepositoryException, DomainException {
        if (verbose)
            Tester.printClassAndFieldHierarchy(ItemContainerMetadataImpl.class);

        // fields affected:
        // totalFileCount:int
        // childFileCount:int
        // totalFolderCount:int
        // childFolderCount:int
        // creatorRoleArray:[I
        // visibleToArray:[I
        // accessibleToArray:[I

        // - name:java.lang.String
        // - parentSid:java.lang.String
        // - datasetId:java.lang.String
        // versionable:boolean --> leave it for the time being

        FolderItemImpl folder = new FolderItemImpl("foo:123");

        DatasetItemContainerMetadata icmd = folder.getDatasetItemContainerMetadata();
        assertTrue(icmd.isDirty());

        ((AbstractItemMetadataImpl<DatasetItemContainerMetadata>) icmd).setName("bar");
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);
        assertFalse(icmd.isDirty());

        icmd.setParentDmoStoreId(new DmoStoreId("foo:122"));
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);

        icmd.setDatasetDmoStoreId(new DmoStoreId("foo:121"));
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);

        FileItemImpl fi = new FileItemImpl("file:1");
        folder.addFileOrFolder(fi);
        assertTrue(icmd.isDirty());
        assertTrue(fi.isDirty());
        assertTrue(fi.getFileItemMetadata().isDirty());
        icmd.setDirty(false);
        assertFalse(icmd.isDirty());

        fi.setAccessibleTo(AccessibleTo.ANONYMOUS);
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);

        fi.setCreatorRole(CreatorRole.ARCHIVIST);
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);

        fi.setVisibleTo(VisibleTo.KNOWN);
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);
    }

}
