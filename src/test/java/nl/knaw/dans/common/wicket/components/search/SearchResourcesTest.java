package nl.knaw.dans.common.wicket.components.search;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;

import nl.knaw.dans.common.wicket.WicketCommonTestBase;

import org.junit.Ignore;
import org.junit.Test;

public class SearchResourcesTest extends WicketCommonTestBase
{
    private static final long serialVersionUID = -1685338433015730235L;

    @Ignore("test in error ")
    @Test
    public void testResourceReferences() throws IllegalArgumentException, IllegalAccessException
    {
        Field[] fields = SearchResources.class.getDeclaredFields();
        BaseSearchPanel sp = new BaseSearchPanel("test");
        for (Field field : fields)
        {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(String.class))
            {
                String key = (String) field.get(null);
                assertNotNull(sp.getString(key));
            }
        }
    }

}
