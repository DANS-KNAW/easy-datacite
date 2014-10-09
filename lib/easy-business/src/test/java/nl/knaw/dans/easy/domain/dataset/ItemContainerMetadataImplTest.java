package nl.knaw.dans.easy.domain.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

import org.easymock.EasyMock;
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

        icmd.setName("folder1");
        assertEquals("folder1", icmd.getPath());

        FileItemImpl fi = new FileItemImpl("dummy-file:1");
        fi.setCreatorRole(CreatorRole.ARCHIVIST);

        fi = new FileItemImpl("dummy-file:2");
        fi.setCreatorRole(CreatorRole.DEPOSITOR);

        if (verbose)
            logger.debug("\n" + icmd.asXMLString(4) + "\n");
    }

    @Test
    public void dirtyChecking() throws RepositoryException, DomainException {
        if (verbose)
            Tester.printClassAndFieldHierarchy(ItemContainerMetadataImpl.class);

        // fields affected:

        // - name:java.lang.String
        // - parentSid:java.lang.String
        // - datasetId:java.lang.String
        // versionable:boolean --> leave it for the time being
        FileStoreAccess fileStoreAccess = createMock(FileStoreAccess.class);
        expect(fileStoreAccess.hasMember(isA(DmoStoreId.class), EasyMock.eq(FileItemVO.class))).andStubReturn(true);
        replayAll();

        new Data().setFileStoreAccess(fileStoreAccess);

        FolderItemImpl folder = new FolderItemImpl("foo:123");
        DatasetItemContainerMetadata icmd = folder.getDatasetItemContainerMetadata();
        assertTrue(icmd.isDirty());

        @SuppressWarnings("unchecked")
        AbstractItemMetadataImpl<DatasetItemContainerMetadata> itemMetadataImpl = (AbstractItemMetadataImpl<DatasetItemContainerMetadata>) icmd;
        itemMetadataImpl.setName("bar");

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
        assertFalse(icmd.isDirty());
        assertTrue(fi.isDirty());
        assertTrue(fi.getFileItemMetadata().isDirty());
        icmd.setDirty(true);
        assertTrue(icmd.isDirty());
        icmd.setDirty(false);

        fi.setAccessibleTo(AccessibleTo.ANONYMOUS);
        assertFalse(icmd.isDirty());

        fi.setCreatorRole(CreatorRole.ARCHIVIST);
        assertFalse(icmd.isDirty());

        fi.setVisibleTo(VisibleTo.KNOWN);
        assertFalse(icmd.isDirty());

        resetAll();
    }
}
