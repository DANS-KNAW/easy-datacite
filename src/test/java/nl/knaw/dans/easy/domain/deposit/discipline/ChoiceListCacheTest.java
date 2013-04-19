package nl.knaw.dans.easy.domain.deposit.discipline;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import nl.knaw.dans.common.lang.AbstractCache;
import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.easy.util.TestHelper;

import org.jibx.runtime.JiBXException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ChoiceListCacheTest extends TestHelper
{

    @BeforeClass
    public static void beforeClass()
    {
        ClassPathHacker.addFile("../../app/easy-webui/src/main/resources");
        before(ChoiceListCacheTest.class);
    }

    //    @Test
    //    public void getProperties() throws IOException, ResourceNotFoundException
    //    {
    //        Properties props = ChoiceListCache.getInstance().getProperties("archaeology.eas.spatial");
    //        // props.list(System.out);
    //        assertEquals(3, props.size());
    //        assertEquals("lengte/breedte (graden)", props.getProperty("degrees"));
    //    }
    //    
    //    @Test
    //    public void getLocaleProperties() throws IOException, ResourceNotFoundException
    //    {
    //        Locale locale = Locale.US;
    //        Properties props = ChoiceListCache.getInstance().getProperties("archaeology.eas.spatial", locale);
    //        //props.list(System.out);
    //        assertEquals(3, props.size());
    //        assertEquals("longitude/latitude (degrees)", props.getProperty("degrees"));
    //    }

    // Will not work when you are offline because of
    // <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
    @Ignore
    @Test
    public void getChoiceList() throws CacheException, JiBXException
    {
        ChoiceList list = ChoiceListCache.getInstance().getList("archaeology.eas.spatial");
        //System.out.println(list.asXMLString(4));
        assertEquals(3, list.getChoices().size());
        assertEquals("lengte/breedte (graden)", list.getChoices().get(1).getValue());
    }

    // Will not work when you are offline because of
    // <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
    @Ignore
    @Test
    public void getLocaleChoiceList() throws CacheException, JiBXException
    {
        Locale locale = Locale.US;
        ChoiceList list = ChoiceListCache.getInstance().getList("archaeology.eas.spatial", locale);
        //System.out.println(list.asXMLString(4));
        assertEquals(3, list.getChoices().size());
        assertEquals("longitude/latitude (degrees)", list.getChoices().get(1).getValue());
    }

    @Test(expected = CacheException.class)
    public void getNonExsistingChoiceList() throws CacheException
    {
        ChoiceListCache.getInstance().getList("duizend.bommen.en.granaten");
    }

    @Ignore("Would take too long...")
    @Test
    public void testClean() throws CacheException
    {
        final AbstractCache<String, String> cache1 = new AbstractCache<String, String>(3L, 5L)
        {

            @Override
            protected String getObject(String key, Locale locale)
            {
                return new Date().toString();
            }

        };

        final AbstractCache<Date, Object> cache2 = new AbstractCache<Date, Object>(1L, 2L)
        {

            @Override
            protected Object getObject(Date key, Locale locale)
            {
                return new Date();
            }

        };

        Thread thread = new Thread();
        thread.start();
        while (true)
        {
            try
            {
                Thread.sleep(5000L);
                cache1.getCachedObject(new Date().toString(), null);
                cache2.getCachedObject(new Date(), null);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
