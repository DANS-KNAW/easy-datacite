package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.xml.AdditionalContent;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileItemMetadataImplTest
{

    private static final Logger logger = LoggerFactory.getLogger(FileItemMetadataImplTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DmoStoreId fileItemId = new DmoStoreId("foo:123");
        FileItemMetadata fimd = new FileItemMetadataImpl(fileItemId);

        FileItemMetadata fimd2 = (FileItemMetadata) JiBXObjectFactory.unmarshal(FileItemMetadataImpl.class, fimd.asObjectXML());
        assertEquals(fimd.asXMLString(), fimd2.asXMLString());

        if (verbose)
            logger.debug("\n" + fimd.asXMLString(4) + "\n");
    }

    @Test
    public void serializeDeserializeFull() throws Exception
    {
        DmoStoreId fileItemId = new DmoStoreId("foo:123");
        DmoStoreId datasetId = new DmoStoreId("dataset:123");
        DmoStoreId folderId = new DmoStoreId("folder:123");
        FileItemMetadataImpl fimd = new FileItemMetadataImpl(fileItemId);

        fimd.setDatasetDmoStoreId(datasetId);
        fimd.setMimeType("foo/bar");
        fimd.setName("Testing the pasting");
        fimd.setParentDmoStoreId(folderId);
        fimd.setSize(123L);
        fimd.setCreatorRole(CreatorRole.ARCHIVIST);
        fimd.setAccessibleTo(AccessibleTo.ANONYMOUS);
        fimd.setVisibleTo(VisibleTo.ANONYMOUS);
        // COMMENTED OUT FOR RELEASE 2.8
        // fimd.setStreamingUrl("http://koe.com/");

        AdditionalMetadata addmd = fimd.getAdditionalMetadata();
        Element content = getContent("src/test/resources/test-files/add-content.xml");

        AdditionalContent addContent = new AdditionalContent("id1", content);
        addmd.addAdditionalContent(addContent);
        addmd.getPropertryList().addProperty("foo", "bar");
        addmd.getPropertryList().addProperty("bla bla", "rabarberplanten en -struiken");

        AdditionalContent addContent2 = new AdditionalContent("id2", content);
        addmd.addAdditionalContent(addContent2);
        addmd.getPropertryList().addProperty("foo", "bar");
        addmd.getPropertryList().addProperty("bla bla", "rabarberplanten en -struiken");

        if (verbose)
            logger.debug("\n" + fimd.asXMLString(4) + "\n");

        FileItemMetadata fimd2 = (FileItemMetadata) JiBXObjectFactory.unmarshal(FileItemMetadataImpl.class, fimd.asObjectXML());
        assertEquals(fimd.asXMLString(), fimd2.asXMLString());
    }

    private Element getContent(String filename) throws DocumentException
    {
        Dom4jReader reader = new Dom4jReader(filename);
        return (Element) reader.getNode("content");
    }

    @Test
    public void testDirty()
    {
        if (verbose)
            Tester.printClassAndFieldHierarchy(FileItemMetadataImpl.class);

        // fields affected by dirtyChecking:
        // - creatorRole:nl.knaw.dans.easy.domain.model.CreatorRole
        // - visibleTo:nl.knaw.dans.easy.domain.model.VisibleTo
        // - accessibleTo:nl.knaw.dans.easy.domain.model.AccessibleTo
        // - mimeType:java.lang.String
        // - size:long

        // sid:java.lang.String --> whole object will be ingested
        // - name:java.lang.String
        // - parentSid:java.lang.String
        // - datasetId:java.lang.String
        // versionable:boolean --> leave it for the time being

        DmoStoreId fileItemId = new DmoStoreId("foo:123");
        DmoStoreId datasetId = new DmoStoreId("dataset:123");
        DmoStoreId folderId = new DmoStoreId("folder:123");

        FileItemMetadataImpl fimd = new FileItemMetadataImpl(fileItemId);
        assertTrue(fimd.isDirty());

        fimd.setDatasetDmoStoreId(datasetId);
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setDatasetDmoStoreId(datasetId);
        assertFalse(fimd.isDirty());

        fimd.setMimeType("foo/bar");
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setMimeType("foo/bar");
        assertFalse(fimd.isDirty());
        fimd.setMimeType("changed");
        assertTrue(fimd.isDirty());

        fimd.setDirty(false);
        fimd.setName("Testing the pasting");
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setName("Testing the pasting");
        assertFalse(fimd.isDirty());
        fimd.setName("changed");
        assertTrue(fimd.isDirty());

        fimd.setDirty(false);
        fimd.setParentDmoStoreId(folderId);
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setParentDmoStoreId(folderId);
        assertFalse(fimd.isDirty());

        fimd.setSize(123L);
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setSize(123L);
        assertFalse(fimd.isDirty());

        fimd.setCreatorRole(CreatorRole.ARCHIVIST);
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setCreatorRole(CreatorRole.ARCHIVIST);
        assertFalse(fimd.isDirty());
        fimd.setCreatorRole(CreatorRole.DEPOSITOR);
        assertTrue(fimd.isDirty());

        fimd.setDirty(false);
        fimd.setAccessibleTo(AccessibleTo.ANONYMOUS);
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setAccessibleTo(AccessibleTo.ANONYMOUS);
        assertFalse(fimd.isDirty());

        fimd.setDirty(false);
        fimd.setVisibleTo(VisibleTo.ANONYMOUS);
        assertTrue(fimd.isDirty());
        fimd.setDirty(false);
        fimd.setVisibleTo(VisibleTo.ANONYMOUS);
        assertFalse(fimd.isDirty());

        // COMMENTED OUT FOR RELEASE 2.8
        // fimd.setDirty(false);
        // fimd.setStreamingUrl("http://koe.com/");
        // assertTrue(fimd.isDirty());
        // fimd.setDirty(false);
        // fimd.setStreamingUrl("http://koe.com/");
        // assertFalse(fimd.isDirty());
    }
}
