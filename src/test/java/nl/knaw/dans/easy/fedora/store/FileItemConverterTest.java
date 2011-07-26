package nl.knaw.dans.easy.fedora.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileItemConverterTest
{
    private static final Logger logger = LoggerFactory.getLogger(FileItemConverterTest.class);
    
    private boolean             verbose = Tester.isVerbose();
    private FileItemConverter converter = new FileItemConverter();
    
    @Test
    public void testConversion() throws IOException, ResourceNotFoundException, ObjectSerializationException, XMLSerializationException, ObjectDeserializationException
    {
        FileItemImpl fi = new FileItemImpl("easy-file:1");
        fi.setFile(Tester.getFile("test-files/FileItemConverter/kubler.doc"));
        fi.setAccessibleTo(AccessibleTo.RESTRICTED_GROUP);
        fi.getBinaryUnits().get(0).setLocation("foo:location");
        
        DigitalObject dob = converter.serialize(fi);
        if (verbose)
            logger.debug("\n" + dob.asXMLString(4) + "\n");
        
        FileItemImpl fi2 = new FileItemImpl("easy-file:1");
        converter.deserialize(dob, fi2);
        FileItemMetadata convertedFim = fi2.getFileItemMetadata();
        
        assertFalse(convertedFim.isDirty());
        assertEquals(fi.getFileItemMetadata().asXMLString(), convertedFim.asXMLString());
    }
    

}
