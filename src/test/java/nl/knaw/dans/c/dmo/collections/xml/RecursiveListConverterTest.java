package nl.knaw.dans.c.dmo.collections.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.c.dmo.collections.core.AbstractDmoCollectionsTest;
import nl.knaw.dans.c.dmo.collections.core.MockRootCreator;
import nl.knaw.dans.common.lang.repo.bean.RecursiveEntry;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

import org.junit.BeforeClass;
import org.junit.Test;

public class RecursiveListConverterTest extends AbstractDmoCollectionsTest
{
    
    @BeforeClass
    public static void beforeClass()
    {
        initializeWithNoSecurity();
    }
    
    @Test
    public void convert() throws Exception
    {
        DmoCollection dmoRoot = MockRootCreator.createRoot("jib-col", 2, 2);
        RecursiveList recursiveList = RecursiveListConverter.convert(dmoRoot);
        
        //System.err.println(recursiveList.asXMLString(4));
        RecursiveEntry lastEntry = recursiveList.get("jib-col:14");
        assertEquals("Shortname of jib-col:14", lastEntry.getShortname());
        assertEquals("Label of jib-col:14", lastEntry.getName());
        assertEquals(14, lastEntry.getOrdinal());
    }
    
    // create a recursive list for solr facetted search 
    @Test
    public void createRecursiveList() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/easy-collections.xml");
        DmoCollection easyRoot = JiBXCollectionConverter.convert(url, false);
        RecursiveList easyRL = RecursiveListConverter.convert(easyRoot);
        File file = new File("src/test/resources/test-files/easy-collections.xml");
        file.delete();
        easyRL.serializeTo(file, 4);
    }

}
