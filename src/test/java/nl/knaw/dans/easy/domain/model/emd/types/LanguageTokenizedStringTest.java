package nl.knaw.dans.easy.domain.model.emd.types;

import java.util.Locale;
import java.util.MissingResourceException;

import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.emd.types.InvalidLanguageTokenException;
import nl.knaw.dans.easy.domain.model.emd.types.LanguageTokenizedString;

import org.junit.Assert;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class LanguageTokenizedStringTest
{

    /**
     * Test the regex used for language token.
     */
    @Test
    public void testIsValidLanguageToken()
    {
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken(null));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken(""));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("a"));

        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("ab"));
        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("abc"));

        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("abcd"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("abc-"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("abc-a"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("ab-"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("ab-a"));

        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("ab-ab"));
        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("ab-abc"));
        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("abc-ab"));
        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("abc-abc"));

        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("ab-abcdefgh"));
        Assert.assertTrue(LanguageTokenizedString.isValidLanguageToken("abc-abcdefgh"));

        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("ab-abcdefghi"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("abc-abcdefghi"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("abcd-ab"));
        Assert.assertFalse(LanguageTokenizedString.isValidLanguageToken("abcd-abcdefgh"));
    }

    /**
     * Test setting the language token with a string.
     *
     * @throws InvalidLanguageTokenException but shouldn't
     */
    @Test
    public void testSetLanguageString() throws InvalidLanguageTokenException
    {
        BasicString bs = new BasicString();

        String string = "";
        bs.setLanguage(string);
        Assert.assertEquals(string, bs.getLanguage());

        string = null;
        bs.setLanguage(string);
        Assert.assertNull(bs.getLanguage());

        string = "nl";
        bs.setLanguage(string);
        Assert.assertEquals(string, bs.getLanguage());

        string = "nld";
        bs.setLanguage(string);
        Assert.assertEquals(string, bs.getLanguage());

        string = "nl-NL";
        bs.setLanguage(string);
        Assert.assertEquals(string, bs.getLanguage());

        string = "nld-NL";
        bs.setLanguage(string);
        Assert.assertEquals(string, bs.getLanguage());

        string = "nld-NLD";
        bs.setLanguage(string);
        Assert.assertEquals(string, bs.getLanguage());
    }

    /**
     * Test setting the language token with a valid Locale.
     *
     * @throws InvalidLanguageTokenException but shouldn't
     */
    @Test
    public void testSetValidLanguageByLocale() throws InvalidLanguageTokenException
    {
        BasicString bs = new BasicString();

        Locale locale = new Locale("nl");
        bs.setLanguage(locale);
        Assert.assertEquals("nld", bs.getLanguage());

        locale = new Locale("nl", "NL");
        bs.setLanguage(locale);
        Assert.assertEquals("nld-NLD", bs.getLanguage());

        locale = new Locale("nl", "nl");
        bs.setLanguage(locale);
        Assert.assertEquals("nld-NLD", bs.getLanguage());
    }

    /**
     * Test setting the language token with an invalid Locale.
     *
     */
    @Test
    public void testSetInvalidLanguageByLocale()
    {
        BasicString bs = new BasicString();

        Locale locale = new Locale("");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() == null);
        }

        locale = new Locale("nld");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() == null);
        }

        locale = new Locale("nl", "NLD");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() == null);
        }

        locale = new Locale("nld", "NLD");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() == null);
        }

        locale = new Locale("", "NL");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() == null);
        }

        locale = new Locale("nld");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() == null);
        }

        locale = new Locale("oo");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() instanceof MissingResourceException);
        }

        locale = new Locale("nl", "OO");
        try
        {
            bs.setLanguage(locale);
            Assert.fail("expected exception");
        }
        catch (InvalidLanguageTokenException e)
        {
            Assert.assertTrue(e.getCause() instanceof MissingResourceException);
        }

    }

    /**
     * Test constructor.
     *
     * @throws InvalidLanguageTokenException but shouldn't
     */
    @Test
    public void testLanguageConstructors() throws InvalidLanguageTokenException
    {
        LanguageTokenizedString bs = new BasicString("foo", "bar");
        Assert.assertEquals("foo", bs.getValue());
        Assert.assertEquals("bar", bs.getLanguage());

        BasicString bas = new BasicString("foo", "bar", "bas");
        Assert.assertEquals("foo", bas.getValue());
        Assert.assertEquals("bar", bas.getLanguage());
        Assert.assertEquals("bas", bas.getScheme());
    }

    /**
     * Test constructor.
     *
     * @throws InvalidLanguageTokenException but shouldn't
     */
    @Test
    public void testLocaleConstructors() throws InvalidLanguageTokenException
    {
        LanguageTokenizedString bs = new BasicString("foo", new Locale("nl"));
        Assert.assertEquals("foo", bs.getValue());
        Assert.assertEquals("nld", bs.getLanguage());

        BasicString bas = new BasicString("foo", new Locale("nl"), "bas");
        Assert.assertEquals("foo", bas.getValue());
        Assert.assertEquals("nld", bas.getLanguage());
        Assert.assertEquals("bas", bas.getScheme());
    }

    @Test
    public void testToString()
    {
        LanguageTokenizedString bs = new BasicString();
        Assert.assertEquals("", bs.toString());
    }

}
