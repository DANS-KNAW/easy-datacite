package nl.knaw.dans.easy.web.view.dataset;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.model.Dataset;

import org.dom4j.Element;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class UploadFileMetadataProcessTest
{
    private static final String    IMAGE_FILE = "HEED-08-008.jpg";
    private static final String    APPL_FILE  = "AARDEWERK.csv";
    private static final String    APPL_XML   = "filemetadata.xml";
    private static final String    IMAGES_XML = "filemetadata_images.xml";

    private static Dataset         dataset;
    private static FileStoreAccess fileStoreAccess;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
        ClassPathHacker.addFile("../easy-webui/src/main/java");

        fileStoreAccess = EasyMock.createMock(FileStoreAccess.class);
        dataset = EasyMock.createMock(Dataset.class);

        new Data().setFileStoreAccess(fileStoreAccess);
    }

    private void prepare() throws Exception
    {
        final String sid = "easy-dataset:123";
        final String[] fileNames = {"folder1/filemetadata_images.xml", APPL_FILE};

        EasyMock.expect(fileStoreAccess.getFilenames(sid, true)).andReturn(Arrays.asList(fileNames)).anyTimes();
        EasyMock.expect(dataset.getPreferredTitle()).andReturn("A mocked dataset").anyTimes();
        EasyMock.expect(dataset.getStoreId()).andReturn(sid).anyTimes();
    }

    @Test @Ignore
    public void processImages() throws Exception
    {
        prepare();
        new UploadFileMetadataProcess().processUploadedFile(getFile(IMAGES_XML), dataset);
    }

    @Test @Ignore
    public void processAppls() throws Exception
    {
        prepare();
        new UploadFileMetadataProcess().processUploadedFile(getFile(APPL_XML), dataset);
    }

    @Test(expected = UploadPostProcessException.class)
    public void mixed1() throws Exception
    {
        getFileMap("mixed1.xml");
    }

    @Test(expected = UploadPostProcessException.class)
    public void mixed2() throws Exception
    {
        getFileMap("mixed2.xml");
    }

    @Test
    public void imagesMetadata() throws Exception
    {
        final String actual = getFileMap(IMAGES_XML).get(IMAGE_FILE).asXML();
        assertThat(actual, containsString("overzicht vlak van zuid naar noord"));
    }

    @Test
    public void metadata() throws Exception
    {
        final String actual = getFileMap(APPL_XML).get(APPL_FILE).asXML();
        assertThat(actual, containsString("Specialistentabel Aardewerkdeterminatie"));
    }

    private Map<String, Element> getFileMap(final String fileName) throws UploadPostProcessException
    {
        return UploadFileMetadataProcess.buildFileMap(getFile(fileName));
    }

    private File getFile(final String fileName)
    {
        return new File(this.getClass().getResource(fileName).getFile());
    }
}
