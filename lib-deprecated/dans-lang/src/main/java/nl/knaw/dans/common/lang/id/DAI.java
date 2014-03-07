package nl.knaw.dans.common.lang.id;

import java.net.URI;

/**
 * A Digital Author Id (DAI) is composed of 8 or 9 digits and a mod 11 checksum character.
 * 
 * @see http://en.wikipedia.org/wiki/MSI_Barcode#Mod_11_Check_Digit
 * @see http://wiki.surf.nl/display/standards/DAI#DAI-Syntax
 * 
 * @author henkb
 *
 */
public class DAI
{

    public static final int MAX_DAI_LENGTH = 10;

    public static final int MIN_DAI_LENGTH = 9;

    public static final String DAI_NAMESPACE = "info:eu-repo/dai/nl/";

    protected static final int FACTOR_START = 2;

    /**
     * Maximum value of factor used under IBM-mode digest.
     */
    protected static final int MAX_IBM = 7;

    /**
     * Maximum value of factor used under NCR-mode digest.
     */
    protected static final int MAX_NCR = 9;

    private final String identifier;

    public DAI(String identifier) throws IllegalArgumentException
    {
        if (!isValid(identifier))
        {
            throw new IllegalArgumentException(explain(identifier));
        }
        this.identifier = identifier;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public URI getURI()
    {
        return URI.create(DAI_NAMESPACE + identifier);
    }

    public static String explain(String dai)
    {
        if (dai == null)
        {
            return "<null> is not a valid DAI.";
        }
        if (dai.length() > MAX_DAI_LENGTH)
        {
            return "A DAI has a maximum of " + MAX_DAI_LENGTH + " characters.";
        }
        if (dai.length() < MIN_DAI_LENGTH)
        {
            return "A DAI has a minimum of " + MIN_DAI_LENGTH + " characters.";
        }
        String[] daiCompound = devide(dai);
        for (int i = 0; i < daiCompound[0].length(); i++)
        {
            if (!Character.isDigit(daiCompound[0].charAt(i)))
            {
                return "Non-digit character found in DAI at position " + (i + 1) + ".";
            }
        }
        if (!isValid(dai))
        {
            return "Checksum-invalid DAI.";
        }
        return "Valid DAI.";
    }

    public static boolean isValid(String dai)
    {
        if (dai == null || dai.length() > MAX_DAI_LENGTH || dai.length() < MIN_DAI_LENGTH)
        {
            return false;
        }
        String[] daiCompound = devide(dai);
        for (int i = 0; i < daiCompound[0].length(); i++)
        {
            if (!Character.isDigit(daiCompound[0].charAt(i)))
            {
                return false;
            }
        }
        char checksum = digest(daiCompound[0], MAX_NCR);
        return daiCompound[1].equalsIgnoreCase(String.valueOf(checksum));
    }

    /**
     * Digests a message and returns the mod 11 checksum character for the given mode.
     * DAI uses {@value #MAX_NCR}.
     * @param message message to digest
     * @param modeMax either {@link #MAX_IBM} or {@link #MAX_NCR}
     * @return checksum
     */
    protected static char digest(String message, int modeMax)
    {
        String reverse = new StringBuilder(message).reverse().toString();
        int f = FACTOR_START;
        int w = 0;
        for (int i = 0; i < reverse.length(); i++)
        {
            char cx = reverse.charAt(i);
            int x = cx - 48;
            w += f * x;
            f++;
            if (f > modeMax)
                f = FACTOR_START;
        }
        int mod = (w % 11);
        if (mod == 0)
        {
            return '0';
        }
        int c = 11 - mod;
        if (c == 10)
        {
            return 'X';
        }
        else
        {
            return (char) (c + 48);
        }
    }

    protected static String create(String message, int modeMax)
    {
        return message + String.valueOf(digest(message, modeMax));
    }

    protected static String[] devide(String dai) throws IllegalArgumentException
    {
        if (dai == null || dai.length() > MAX_DAI_LENGTH || dai.length() < MIN_DAI_LENGTH)
        {
            throw new IllegalArgumentException(dai == null ? "<null> argument" : "invalid length: " + dai.length());
        }
        String[] daiCompound = new String[2];
        daiCompound[0] = dai.substring(0, dai.length() - 1);
        daiCompound[1] = dai.substring(dai.length() - 1);
        return daiCompound;
    }

}
