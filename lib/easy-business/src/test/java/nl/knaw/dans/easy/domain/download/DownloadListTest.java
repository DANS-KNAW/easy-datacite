package nl.knaw.dans.easy.domain.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.download.DownloadList.Level;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadListTest {

    private static final Logger logger = LoggerFactory.getLogger(DownloadListTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void testMarshalAndUnmarshal() throws XMLException {
        DownloadList list = new DownloadList();
        list.add(DownloadRecordTest.createRecord());
        list.add(DownloadRecordTest.createRecord());

        if (verbose)
            logger.debug("\n" + list.asXMLString(4) + "\n");

        byte[] objectXML = list.asObjectXML();

        DownloadList list2 = (DownloadList) JiBXObjectFactory.unmarshal(DownloadList.class, objectXML);
        assertEquals(list.asXMLString(), list2.asXMLString());
    }

    @Test
    public void acceptsAll() {
        DateTime startDate = new DateTime("2010-03-04");
        DownloadList list = new DownloadList(DownloadList.TYPE_ALL, null, startDate);

        assertTrue(list.accepts(new DateTime("2010-12-31")));
        assertTrue(list.accepts(new DateTime("2011-12-31")));
        printPeriod(list);
    }

    private void printPeriod(DownloadList list) {
        if (verbose)
            logger.debug(list.printPeriod());
    }

    @Test
    public void acceptsYear() {
        DateTime startDate = new DateTime("2010-03-04");
        DownloadList list = new DownloadList(DownloadList.TYPE_YEAR, null, startDate);

        assertTrue(list.accepts(new DateTime("2010-12-31")));
        assertFalse(list.accepts(new DateTime("2011-01-01")));
        printPeriod(list);
    }

    @Test
    public void acceptsMonth() {
        DateTime startDate = new DateTime("2010-03-04");
        DownloadList list = new DownloadList(DownloadList.TYPE_MONTH, null, startDate);

        assertTrue(list.accepts(new DateTime("2010-03-31")));
        assertFalse(list.accepts(new DateTime("2010-04-01")));
        printPeriod(list);
    }

    @Test
    public void acceptsWeek() {
        DateTime startDate = new DateTime("2010-12-31");
        DownloadList list = new DownloadList(DownloadList.TYPE_WEEK, null, startDate);

        assertTrue(list.accepts(new DateTime("2010-12-31")));
        assertTrue(list.accepts(new DateTime("2011-01-01")));
        printPeriod(list);

        startDate = new DateTime("2011-01-01");
        list = new DownloadList(DownloadList.TYPE_WEEK, null, startDate);

        assertTrue(list.accepts(new DateTime("2010-12-31")));
        assertTrue(list.accepts(new DateTime("2011-01-01")));
        printPeriod(list);
    }

    @Test
    public void addDownloadLevelFileItem() throws XMLSerializationException {
        DateTime startDate = new DateTime("2010-03-04");
        DownloadList list = new DownloadList(DownloadList.TYPE_ALL, Level.FILE_ITEM, startDate);
        list.addDownload(getFileItemVO(), getUser(), new DateTime());
        list.addDownload(getFileItemVO(), getUser(), new DateTime());

        if (verbose)
            logger.debug("\n" + list.asXMLString(4) + "\n");
    }

    @Test
    public void addDownloadLevelDataset() throws XMLSerializationException {
        DateTime startDate = new DateTime("2010-03-04");
        DownloadList list = new DownloadList(DownloadList.TYPE_YEAR, Level.DATASET, startDate);
        DateTime downloadTime = new DateTime("2010-04-05");

        list.addDownload(getFileItemVO(), getUser(), downloadTime);
        list.addDownload(getFileItemVO(), getUser(), downloadTime);

        List<ItemVO> downloadedItemVOs = new ArrayList<ItemVO>();
        downloadedItemVOs.add(getFileItemVO());
        downloadedItemVOs.add(getFileItemVO());
        downloadTime = new DateTime("2010-04-06");
        list.addDownload(downloadedItemVOs, getUser(), downloadTime);

        assertEquals(3, list.getDownloadCount());
        assertEquals(4, list.getRecords().size());

        if (verbose)
            logger.debug("\n" + list.asXMLString(4) + "\n");

        Throwable error = null;
        try {
            list.addDownload(getFileItemVO(), getUser(), new DateTime("2011-01-01"));
        }
        catch (Exception e) {
            error = e;
            if (verbose)
                logger.debug(e.getMessage());
        }
        assertNotNull(error);
    }

    @Test
    public void addDownloadLevelGlobal() throws XMLSerializationException {
        DateTime startDate = new DateTime("2010-03-04");
        DownloadList list = new DownloadList(DownloadList.TYPE_MONTH, Level.STORE, startDate);
        DateTime downloadTime = new DateTime("2010-03-05");
        list.addDownload(getFileItemVO(), getUser(), downloadTime);
        list.addDownload(getFileItemVO(), getUser(), downloadTime);

        if (verbose)
            logger.debug("\n" + list.asXMLString(4) + "\n");

        Throwable error = null;
        try {
            list.addDownload(getFileItemVO(), getUser(), new DateTime("2010-04-01"));
        }
        catch (Exception e) {
            error = e;
            if (verbose)
                logger.debug(e.getMessage());
        }
        assertNotNull(error);
    }

    private FileItemVO getFileItemVO() {
        FileItemVO fivo = new FileItemVO();
        fivo.setDatasetSid("easy-dataset:456");
        fivo.setMimetype("text/html");
        fivo.setPath("foo/bar/nice.html");
        fivo.setSid("easy-file:123456");
        fivo.setSize(10000);
        return fivo;
    }

    private User getUser() {
        User user = new EasyUserImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getId() {
                return "jan_klaassen";
            }

        };
        return user;
    }

}
