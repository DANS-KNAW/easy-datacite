package nl.knaw.dans.common.lang.collect;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nl.knaw.dans.common.lang.progress.ProgressListener;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvCollectorTest {

    public static final String TEST_FILE_NAME = "test-files/collect/csvcollector-test.csv";
    public static final String TEST_FILE_PATH = "src/test/resources/";

    private static final Logger logger = LoggerFactory.getLogger(CsvCollectorTest.class);

    @Test
    public void collect() throws Exception {
        CsvCollector collector = new CsvCollector(TEST_FILE_PATH + TEST_FILE_NAME);
        collector.addProgressListeners(new MyProgressListener());
        List<List<String>> entries = collector.collect();

        assertEquals(12, entries.size());

        List<String> line0 = entries.get(0);
        assertEquals(3, line0.size());
        assertEquals("urn:nbn:nl:ui:13-km4l-t5", line0.get(1));
        assertEquals("foo", line0.get(2));

        List<String> line7 = entries.get(7);
        assertEquals(4, line7.size());
        assertEquals("easy-dataset:46218", line7.get(0));
        assertEquals("bar", line7.get(3));
    }

    class MyProgressListener implements ProgressListener {

        @Override
        public void onStartProcess(String processId) {
            logger.debug("onStartProcess " + processId);
        }

        @Override
        public void updateProgress(int percentage) {
            logger.debug("updateProgress " + percentage);
        }

        @Override
        public void onEndProcess(String processId) {
            logger.debug("onEndProcess " + processId);
        }

    }

}
