package nl.knaw.dans.easy.data;

import static org.junit.Assert.fail;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.util.TestHelper;

import org.junit.BeforeClass;
import org.junit.Test;

// ecco (Jan 25, 2009): CHECKSTYLE: OFF

public class DataTest extends TestHelper
{

    @BeforeClass
    public static void before()
    {
        before(DataTest.class);
    }

    @Test()
    public void testConstructor()
    {
        new Data();
        Data data = new Data();
        data.lock();
        // after locking Data, no constructor and setter calls allowed.
        try
        {
            new Data();
            fail("expected IllegalStateException for constructor.");
        }
        catch (IllegalStateException e)
        {
            // expected
        }

        try
        {
            data.setUserRepo(null);
            fail("expected IllegalStateException for setter method.");
        }
        catch (IllegalStateException e)
        {
            // expected
        }
        data.unlock();
    }

}
