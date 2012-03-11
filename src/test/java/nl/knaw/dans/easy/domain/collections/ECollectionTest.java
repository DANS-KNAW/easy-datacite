package nl.knaw.dans.easy.domain.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import nl.knaw.dans.common.lang.repo.DmoNamespace;

import org.junit.Test;

public class ECollectionTest
{
    
    @Test
    public void iterator()
    {
        Iterator<ECollection> iter = ECollection.iterator();
        
        assertTrue(iter.hasNext());
        assertEquals(ECollection.EasyCollection, iter.next());
        
        assertTrue(iter.hasNext());
        assertEquals(ECollection.EasyInterestArea, iter.next());
        
//        assertTrue(iter.hasNext());
//        assertEquals(ECollection.EasyDiscipline, iter.next());
        
        assertTrue(iter.hasNext());
        assertEquals(ECollection.EasyResearchArea, iter.next());
        
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void isECollection()
    {
        String[] validNs = { "easy-collection", "easy-interest-area", "easy-research-area" };
        String[] inValidNs = { "easy-dataset", "easy-file" };
        
        for (String s : validNs)
        {
            assertTrue(ECollection.isECollection(new DmoNamespace(s)));
        }
        
        for (String s : inValidNs)
        {
            assertFalse(ECollection.isECollection(new DmoNamespace(s)));
        }
    }
}
