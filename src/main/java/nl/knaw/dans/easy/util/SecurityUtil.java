/**
 * 
 */
package nl.knaw.dans.easy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Utility methods for security purposes.
 * 
 * @author Herman Suijs
 */
public final class SecurityUtil
{
    /**
     * Hashing algorithm used.
     */
    private static final String HASHING_ALGORITHM = "SHA1";

    /**
     * Length of the generated random string.
     */
    public static final int GENERATED_RANDOM_STRING_LENGTH = 20;

    /**
     * Default constructor.
     */
    private SecurityUtil()
    {
        // Do not instantiate.
    }

    /**
     * Create a new object and return its representation as a random string.
     * 
     * @return random string.
     */
    public static String getRandomString()
    {
        String randomString = RandomStringUtils.random(GENERATED_RANDOM_STRING_LENGTH);
        return randomString;
    }

    /**
     * Create hashCode.
     * 
     * @param strings memberStrings for the hashCode.
     * @return integer hash
     */
    public static int generateHashCode(final String... strings)
    {
        HashCodeBuilder builder = new HashCodeBuilder(12345, 54321);
        for (String memberString : strings)
        {
            builder.append(memberString);
        }
        return builder.toHashCode();
    }

    /**
     * Create hashCode string.
     * 
     * @param strings memberStrings for the hashCode
     * @return String hash
     */
    public static String generateHashCodeString(final String... strings)
    {
        MessageDigest messageDigest;
        String returnValue = null;
        try
        {
            messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            StringBuilder completeHashingString = new StringBuilder();
            for (String hashString : strings)
            {
                completeHashingString.append(hashString);
            }
            messageDigest.update(completeHashingString.toString().getBytes());
            returnValue = new String(messageDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            // use HashCodeBuilder to generate a fallback hashcode.
            returnValue = Integer.valueOf(generateHashCode(strings)).toString();
        }

        return returnValue;
    }

}
