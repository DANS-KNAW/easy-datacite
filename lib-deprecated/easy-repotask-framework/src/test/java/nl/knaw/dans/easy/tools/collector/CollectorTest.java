package nl.knaw.dans.easy.tools.collector;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.collect.Collector;
import nl.knaw.dans.common.lang.collect.FileEntryCollector;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class CollectorTest {

    private static final String PATH = "src/test/resources/test-files/collector";

    private static FileStoreAccess MOCK_FS_ACCESS;

    @BeforeClass
    public static void beforeClass() {
        MOCK_FS_ACCESS = EasyMock.createMock(FileStoreAccess.class);
        new Data().setFileStoreAccess(MOCK_FS_ACCESS);
    }

    @Test
    public void collect1() throws Exception {
        Collector<Map<String, Set<String>>> collector = new DatasetIdCollectorDecorator(new FileEntryCollector(PATH));

        EasyMock.reset(MOCK_FS_ACCESS);
        EasyMock.expect(MOCK_FS_ACCESS.getDatasetId(new DmoStoreId("easy-file:1"))).andReturn("easy-dataset:1");
        EasyMock.expect(MOCK_FS_ACCESS.getDatasetId(new DmoStoreId("easy-folder:2"))).andReturn("easy-dataset:2");
        EasyMock.expect(MOCK_FS_ACCESS.getDatasetId(new DmoStoreId("easy-file:0"))).andReturn(null);

        EasyMock.replay(MOCK_FS_ACCESS);

        Map<String, Set<String>> map = collector.collect();
        assertEquals(3, map.size());
        assertEquals("easy-file:1", map.get("easy-dataset:1").iterator().next());
        assertEquals("easy-folder:2", map.get("easy-dataset:2").iterator().next());
        assertEquals("easy-dataset:3", map.get("easy-dataset:3").iterator().next());
        EasyMock.verify(MOCK_FS_ACCESS);
    }

}
