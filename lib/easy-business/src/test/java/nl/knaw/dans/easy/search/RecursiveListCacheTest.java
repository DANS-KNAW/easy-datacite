package nl.knaw.dans.easy.search;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;

import org.junit.Test;

public class RecursiveListCacheTest {

    public static final String ID_SUBJECT = RecursiveListCache.LID_ARCHAEOLOGY_DC_SUBJECT;
    public static final String ID_TEMPORAL = RecursiveListCache.LID_ARCHAEOLOGY_DCTERMS_TEMPORAL;

    @Test
    public void getRecursiveList() throws Exception {
        RecursiveList subjectList = RecursiveListCache.getInstance().getList(ID_SUBJECT);
        assertEquals(ID_SUBJECT, subjectList.getListId());

        RecursiveList temporalList = RecursiveListCache.getInstance().getList(ID_TEMPORAL);
        assertEquals(ID_TEMPORAL, temporalList.getListId());
    }
}
