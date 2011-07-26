package nl.knaw.dans.easy.domain.deposit.discipline;

import static org.junit.Assert.*;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;

import org.junit.BeforeClass;
import org.junit.Test;

public class RecursiveListCacheTest
{
    
    public static final String ID_SUBJECT = RecursiveList.LID_ARCHAEOLOGY_DC_SUBJECT;
    public static final String ID_TEMPORAL = RecursiveList.LID_ARCHAEOLOGY_DCTERMS_TEMPORAL;
    
    @BeforeClass
    public static void beforeClass() throws ServiceException
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
    }
    
    @Test
    public void getRecursiveList() throws Exception
    {
        RecursiveList subjectList = RecursiveListCache.getInstance().getList(ID_SUBJECT);
        assertEquals(ID_SUBJECT, subjectList.getListId());
        
        RecursiveList temporalList = RecursiveListCache.getInstance().getList(ID_TEMPORAL);
        assertEquals(ID_TEMPORAL, temporalList.getListId());
    }
    
//    @Test
//    public void removeOrdinals() throws Exception
//    {
//        RecursiveList subjectList = RecursiveListCache.getInstance().getList(ID_SUBJECT);
//        for (RecursiveEntry entry : subjectList.getChildren())
//        {
//            removeOrdinals(entry);
//        }
//        System.out.println(subjectList.asXMLString(4));
//    }
//    
//    private void removeOrdinals(RecursiveEntry entry)
//    {
//        entry.setOrdinal(0);
//        for (RecursiveEntry e : entry.getChildren())
//        {
//            removeOrdinals(e);
//        }
//    }

}
