package nl.knaw.dans.easy.sword;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** TODO should this class be part of some some common library? */
public class FederativeAuthentication
{

    private final String userId;
    private final String password;

    /** TODO This secret key should not be in the code but read from an external file! */
    static final String  FEDERATIVE_LOGIN_SECRET = "d49bcb3d-ffb6-4748-aef4-8ca6319f3afb";

    static Logger        log                     = LoggerFactory.getLogger(FederativeAuthentication.class);

    public FederativeAuthentication(final String userId, final String password)
    {
        this.userId = userId;
        this.password = password;
    }

    public boolean canBeTraditionalAccount()
    {
        // TODO reuse some check from easy-business or whatever
        return userId.matches("[a-zA-Z0-9]{5,}");
    }

    public String getUserId()
    {
        final int HASH_LENGTH = 40; // This depends on hash algorithm and
                                    // conversion to string
        String fedUserId = null;

        // get last bytes containing the hash
        // the string before it is the federativeUserId
        if (password.length() > HASH_LENGTH)
        {
            final int hashPos = password.length() - HASH_LENGTH;
            final String givenFedUserId = password.substring(0, hashPos);
            final String givenHashString = password.substring(hashPos);
            log.debug("input id: " + givenFedUserId + ", hash: " + givenHashString);

            // calculate the hash with the secret key
            final String hash = calculateHash(givenFedUserId, FEDERATIVE_LOGIN_SECRET);
            if (0 == hash.compareTo(givenHashString))
            {
                fedUserId = givenFedUserId;
            }
            else
            {
                log.info("Hash is not correct: " + givenHashString);
                // TODO fail if hash is not OK!
            }
        }
        else
        {
            log.info("Token is too small: " + Integer.toString(password.length()));
            // error; token is too small
        }

        return fedUserId;
    }

    // TODO create a HashUtils or HashCalculator class
    static String calculateHash(final String message, final String key)
    {
        final String HASH_ALGORITHM = "SHA-1";
        String hash = "";
        final String messagePlusKey = message + key;
        try
        {
            final MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            messageDigest.update(messagePlusKey.getBytes("UTF-8"));
            final byte bytes[] = messageDigest.digest();
            log.debug("Binary hash length=" + Integer.toString(bytes.length));
            hash = new String(convertToHexString(bytes));
            log.debug("String hash length=" + Integer.toString(hash.length()));
        }
        catch (final NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return hash;
    }

    // TODO add to a HashUtils or StringUtils class
    static String convertToHexString(final byte[] bytes)
    {
        if (bytes == null)
            return null;

        final StringBuffer hexString = new StringBuffer(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++)
        {
            // convert the nibbles, because toHexString does not prepend zero
            hexString.append(Integer.toHexString((0xF0 & bytes[i]) >> 4));
            hexString.append(Integer.toHexString(0x0F & bytes[i]));
        }
        return hexString.toString();
    }

}
