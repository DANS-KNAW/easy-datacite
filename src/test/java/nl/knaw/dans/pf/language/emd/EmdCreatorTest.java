package nl.knaw.dans.pf.language.emd;

import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.Author;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.junit.Assert;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class EmdCreatorTest
{
    @Test
    public void testToStringWithSeparator()
    {
        EmdCreator emdc = new EmdCreator();
        Assert.assertEquals("", emdc.toString("foo"));

        emdc.getDcCreator().add(new BasicString("Frank Zappa"));
        Assert.assertEquals("Frank Zappa", emdc.toString("foo"));

        emdc.getDcCreator().add(new BasicString("Bob Dylan"));
        Assert.assertEquals("Frank ZappafooBob Dylan", emdc.toString("foo"));

        emdc.getEasCreator().add(new Author());
        Assert.assertEquals("Frank ZappafooBob Dylanfoo", emdc.toString("foo"));

        emdc.getEasCreator().add(new Author("Dr.", "PHD", "van", "Vliet"));
        Assert.assertEquals("Frank ZappafooBob DylanfoofooVliet, Dr. PHD van", emdc.toString("foo"));
        Assert.assertEquals("Frank Zappa; Bob Dylan; ; Vliet, Dr. PHD van", emdc.toString("; "));

        Assert.assertEquals("Frank Zappa;Bob Dylan;;Vliet, Dr. PHD van", emdc.toString());
    }

    @Test
    public void testIsEmpty()
    {
        List<?> list = new LinkedList<Object>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertTrue(list.add(null));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1, list.size());

        EmdCreator emdc = new EmdCreator();
        Assert.assertTrue(emdc.isEmpty());
        emdc.getDcCreator().add(null);
        Assert.assertFalse(emdc.isEmpty());
    }

}
