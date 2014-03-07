package nl.knaw.dans.common.lang.id;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.junit.Test;

public class DAITest
{

    private static final String VSOI_DUMP = "src/test/resources/test-files/dai/vsoi-dump.csv";

    /**
     * A DAI consists of 8 digits + a checksum character. Test the values in the test file VSOI_DUMP.
     * 
     * @throws Exception
     */
    @Test
    public void testVSOIDump() throws Exception
    {
        readVSOIDump(new VSOIDumpListener()
        {
            @Override
            public void onDAI(int row, String dai)
            {
                assertEquals("@row " + row, 9, dai.length());
            }

            @Override
            public void onDAIMessage(int row, String daiMessage)
            {
                assertEquals("@row " + row, 8, daiMessage.length());
                for (int i = 0; i < 8; i++)
                {
                    assertTrue("@row " + row, Character.isDigit(daiMessage.charAt(i)));
                }
            }

            @Override
            public void onLength(int row, int length)
            {
                assertEquals(9, length);
            }
        });
    }

    @Test
    public void digestVSOI() throws Exception
    {
        readVSOIDump(new VSOIDumpListener()
        {
            char currentChecksum;
            int currentRow;

            @Override
            public void onDAIMessage(int row, String daiMessage)
            {
                currentRow = row;
                currentChecksum = DAI.digest(daiMessage, DAI.MAX_NCR);
            }

            @Override
            public void onChecksum(int row, char checksum)
            {
                assertEquals(row, currentRow);
                boolean compare = String.valueOf(checksum).equalsIgnoreCase(String.valueOf(currentChecksum));
                assertTrue("@row " + row + " expected " + String.valueOf(checksum) + " actual " + String.valueOf(currentChecksum), compare);
            }
        });
    }

    @Test
    public void digest()
    {
        // digest method is not restricted to DAI-formats.
        char cs = DAI.digest("Data Archiving and Networked Services", DAI.MAX_NCR);
        assertEquals('4', cs);
    }

    @Test
    public void devide()
    {
        String dai = "123456789";
        String[] daiCompound = DAI.devide(dai);
        assertEquals("12345678", daiCompound[0]);
        assertEquals("9", daiCompound[1]);

        dai = "123456789x";
        daiCompound = DAI.devide(dai);
        assertEquals("123456789", daiCompound[0]);
        assertEquals("x", daiCompound[1]);
    }

    @Test
    public void explain()
    {
        assertEquals("<null> is not a valid DAI.", DAI.explain(null));
        assertEquals("A DAI has a minimum of 9 characters.", DAI.explain("1"));
        assertEquals("A DAI has a maximum of 10 characters.", DAI.explain("1234567890X"));
        assertEquals("Non-digit character found in DAI at position 3.", DAI.explain("12a456789"));

        assertEquals("Checksum-invalid DAI.", DAI.explain("12345678x"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("12345678X"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("12345678a"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("12345678A"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456780"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456781"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456782"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456783"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456784"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456785"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456786"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456787"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("123456788"));
        assertEquals("Valid DAI.", DAI.explain("123456789"));

        assertEquals("Checksum-invalid DAI.", DAI.explain("1234567896"));
        assertEquals("Valid DAI.", DAI.explain("1234567897"));

        assertEquals("Checksum-invalid DAI.", DAI.explain("5234567890"));
        assertEquals("Checksum-invalid DAI.", DAI.explain("523456789a"));
        assertEquals("Valid DAI.", DAI.explain("523456789x"));
        assertEquals("Valid DAI.", DAI.explain("523456789X"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidArgumentConstructor()
    {
        new DAI("123456788");
    }

    @Test
    public void ValidArgumentConstructor()
    {
        DAI dai = new DAI("123456789");
        assertEquals("123456789", dai.getIdentifier());
        assertEquals(URI.create("info:eu-repo/dai/nl/123456789"), dai.getURI());
    }

    public static class VSOIDumpListener
    {
        public void onDAI(int row, String dai)
        {
        }

        public void onDAIMessage(int row, String daiMessage)
        {
        }

        public void onChecksum(int row, char checksum)
        {
        }

        public void onLength(int row, int length)
        {
        }
    }

    private void readVSOIDump(VSOIDumpListener listener) throws IOException
    {
        RandomAccessFile vsoiDump = null;
        try
        {
            vsoiDump = new RandomAccessFile(VSOI_DUMP, "r");
            vsoiDump.readLine(); // first line contains column headings
            String line;
            int row = 1;
            while ((line = vsoiDump.readLine()) != null)
            {
                String[] split = line.split(",");
                listener.onDAI(row, split[0]);
                listener.onDAIMessage(row, split[1]);
                listener.onChecksum(row, split[2].charAt(0));
                listener.onLength(row, Integer.parseInt(split[3]));
                row++;
            }
        }
        finally
        {
            vsoiDump.close();
        }

    }

}
