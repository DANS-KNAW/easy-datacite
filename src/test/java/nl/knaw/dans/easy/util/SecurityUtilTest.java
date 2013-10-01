/**
 *
 */
package nl.knaw.dans.easy.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Security utils for Easy.
 * 
 * @author Herman Suijs
 */
public class SecurityUtilTest
{

    // ecco: CHECKSTYLE: OFF

    /**
     * Test get random.
     */
    @Test
    public void testGetRandom()
    {
        String string1 = SecurityUtil.getRandomString();
        Assert.assertTrue(string1.length() == SecurityUtil.GENERATED_RANDOM_STRING_LENGTH);
        String string2 = SecurityUtil.getRandomString();
        Assert.assertTrue(string2.length() == SecurityUtil.GENERATED_RANDOM_STRING_LENGTH);
        Assert.assertNotSame(string2, string1);
    }

    /**
     * Test generating a hashcode.
     */
    @Test
    public void testGenerateHashCode()
    {
        String string1 = SecurityUtil.getRandomString();

        Assert.assertEquals(SecurityUtil.generateHashCode(string1), SecurityUtil.generateHashCode(string1));

        String string2 = SecurityUtil.getRandomString();

        Assert.assertNotSame(SecurityUtil.generateHashCode(string2), SecurityUtil.generateHashCode(string1));
    }

    /**
     * Test generating a hashcode string.
     */
    @Test
    public void testGenerateHashCodeString()
    {
        String string1 = SecurityUtil.getRandomString();

        Assert.assertEquals(SecurityUtil.generateHashCodeString(string1), SecurityUtil.generateHashCodeString(string1));

        String string2 = SecurityUtil.getRandomString();

        Assert.assertNotSame(SecurityUtil.generateHashCodeString(string1), SecurityUtil.generateHashCodeString(string2));
    }

    @Test
    public void testNullString()
    {
        final String randomString = SecurityUtil.getRandomString();
        @SuppressWarnings("unused")
        final String token = Integer.valueOf(SecurityUtil.generateHashCode("foo", null, "bar", randomString)).toString();
        // System.out.println(token);
    }
}
