package nl.knaw.dans.easy.domain.download;

import static org.junit.Assert.*;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadRecordTest
{

    private static final Logger logger = LoggerFactory.getLogger(DownloadRecordTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void testMarshalAndUnmarshal() throws XMLException
    {
        DownloadRecord record = createRecord();

        if (verbose)
            logger.debug("\n" + record.asXMLString(4) + "\n");

        byte[] objectXML = record.asObjectXML();

        DownloadRecord record2 = (DownloadRecord) JiBXObjectFactory.unmarshal(DownloadRecord.class, objectXML);
        assertEquals(record.asXMLString(), record2.asXMLString());
    }

    public static DownloadRecord createRecord()
    {
        DownloadRecord record = new DownloadRecord("easy-file:123");
        record.setDatasetId("easy-dataset:123");
        record.setPath("abc/def/nice.jpg");
        record.setDownloaderId("esther");
        record.setDownloadTime(new DateTime());
        return record;
    }

}
