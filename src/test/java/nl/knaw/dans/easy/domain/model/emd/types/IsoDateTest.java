package nl.knaw.dans.easy.domain.model.emd.types;

import nl.knaw.dans.easy.domain.model.emd.types.InvalidDateStringException;
import nl.knaw.dans.easy.domain.model.emd.types.IsoDate;

import org.junit.Assert;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class IsoDateTest
{

    @Test
    public void testConstructor()
    {
        IsoDate edate = new IsoDate();
        //System.out.println(edate.toString());
        //System.out.println(edate.getDateTimeValue());
        Assert.assertEquals(IsoDate.Format.DAY, edate.getFormat());

        edate.setValueAsString("2008-08-05T13:17:24.898+0200");
        Assert.assertEquals("2008-08-05T13:17:24.898+0200", edate.toString());
        Assert.assertEquals(edate.toString(), edate.getValueAsString());
        Assert.assertEquals(IsoDate.Format.MILLISECOND, edate.getFormat());

        // can it cope with this?
        edate.setValueAsString("2008-08-05T13:17:24.898");
        Assert.assertEquals(IsoDate.Format.MILLISECOND, edate.getFormat());
        edate.setValueAsString("2008-08-05T12");
        Assert.assertEquals(IsoDate.Format.HOUR, edate.getFormat());
        // obviously

        edate.setValueAsString("2010-03-09");
        Assert.assertEquals(IsoDate.Format.DAY, edate.getFormat());
        Assert.assertEquals("2010-03-09", edate.toString());

        edate.setValueAsString("2010-02");
        Assert.assertEquals(IsoDate.Format.MONTH, edate.getFormat());
        Assert.assertEquals("2010-02", edate.toString());

        edate.setValueAsString("2010");
        Assert.assertEquals(IsoDate.Format.YEAR, edate.getFormat());
        Assert.assertEquals("2010", edate.toString());

        try
        {
            edate.setValueAsString("2001-");
            Assert.fail("expected exception");
        }
        catch (InvalidDateStringException e)
        {
            Assert.assertEquals("2010", edate.toString());
            Assert.assertEquals(IsoDate.Format.YEAR, edate.getFormat());
        }

        try
        {
            edate.setValueAsString("");
            Assert.fail("expected exception");
        }
        catch (InvalidDateStringException e)
        {
            Assert.assertEquals("2010", edate.toString());
            Assert.assertEquals(IsoDate.Format.YEAR, edate.getFormat());
        }

        try
        {
            String test = null;
            edate.setValueAsString(test);
            Assert.fail("expected exception");
        }
        catch (InvalidDateStringException e)
        {
            Assert.assertEquals("2010", edate.toString());
            Assert.assertEquals(IsoDate.Format.YEAR, edate.getFormat());
            //e.printStackTrace();
        }
    }

}
