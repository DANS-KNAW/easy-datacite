package nl.knaw.dans.easy.domain.model.emd.types;

import java.util.Locale;

import nl.knaw.dans.easy.domain.model.emd.types.BasicDate;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants.DateScheme;

import org.junit.Assert;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class BasicDateTest
{

    @Test
    public void testConstructors()
    {
        BasicDate bd = new BasicDate();
        Assert.assertNull(bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd = new BasicDate("foo");
        Assert.assertEquals("foo", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd = new BasicDate("maart '97", new Locale("nl", "NL"));
        Assert.assertEquals("maart '97", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd = new BasicDate("mei '68", "nld-NLD");
        Assert.assertEquals("mei '68", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd = new BasicDate("foo 2008", new Locale("en", "US"), DateScheme.Period);
        Assert.assertEquals("foo 2008", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd = new BasicDate("bar 2008", "en-US", DateScheme.Period);
        Assert.assertEquals("bar 2008", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd = new BasicDate("2008", new Locale("en", "US"), DateScheme.W3CDTF);
        Assert.assertEquals("2008", bd.getValue());
        Assert.assertTrue(bd.getDateTime().toString().startsWith("2008"));

        bd = new BasicDate("2007-10", "en-US", DateScheme.W3CDTF);
        Assert.assertEquals("2007-10", bd.getValue());
        Assert.assertTrue(bd.getDateTime().toString().startsWith("2007-10"));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalState()
    {
        new BasicDate("foo 2008", new Locale("en", "US"), DateScheme.W3CDTF);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalState2()
    {
        BasicDate bad = new BasicDate("foo 2008", new Locale("en", "US"));
        bad.setScheme(DateScheme.W3CDTF);
    }

    @Test
    public void testIllegalState3()
    {
        BasicDate bad = new BasicDate("2008", new Locale("en", "US"), DateScheme.W3CDTF);
        Assert.assertEquals(DateScheme.W3CDTF, bad.getScheme());
        bad.setValue("foo");
        Assert.assertNull(bad.getScheme());
    }

    @Test
    public void testSetValueAndScheme()
    {
        BasicDate bd = new BasicDate();

        bd.setValue("foo");
        Assert.assertEquals("foo", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd.setScheme(DateScheme.Period);
        bd.setValue("bar");
        Assert.assertEquals("bar", bd.getValue());
        Assert.assertNull(bd.getDateTime());
        Assert.assertEquals(DateScheme.Period, bd.getScheme());

        bd.setValue("2008-08-04");
        Assert.assertEquals(DateScheme.Period, bd.getScheme());
        Assert.assertEquals("2008-08-04", bd.getValue());
        Assert.assertTrue(bd.getDateTime().toString().startsWith("2008-08-04"));

        bd.setValue(null);
        Assert.assertEquals(DateScheme.Period, bd.getScheme());
        Assert.assertNull(bd.getDateTime());
        Assert.assertNull(bd.getValue());

        bd.setValue("not valid");
        Assert.assertEquals(DateScheme.Period, bd.getScheme());
        Assert.assertEquals("not valid", bd.getValue());
        Assert.assertNull(bd.getDateTime());

        bd.setScheme(null);
        bd.setValue("2008-08-04");
        Assert.assertEquals(DateScheme.W3CDTF, bd.getScheme());
        Assert.assertEquals("2008-08-04", bd.getValue());
        Assert.assertTrue(bd.getDateTime().toString().startsWith("2008-08-04"));

        bd.setScheme(DateScheme.Period);
        bd.setValue("");
        Assert.assertEquals("", bd.getValue());
        Assert.assertEquals(DateScheme.Period, bd.getScheme());
        Assert.assertNull(bd.getDateTime());

        try
        {
            bd.setScheme(DateScheme.W3CDTF);
            Assert.fail("expected exception");
        }
        catch (IllegalStateException e)
        {
            Assert.assertEquals(DateScheme.Period, bd.getScheme());
            //e.printStackTrace();
        }
    }

    @Test
    public void testIsIsoDateString()
    {
        Assert.assertFalse(BasicDate.isISODateString(null));
        Assert.assertFalse(BasicDate.isISODateString(""));
        Assert.assertFalse(BasicDate.isISODateString("foo"));
        Assert.assertFalse(BasicDate.isISODateString(" 2008"));
        Assert.assertTrue(BasicDate.isISODateString("2008"));
    }

}
