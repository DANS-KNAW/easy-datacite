package nl.knaw.dans.common.lang.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Helper class that reads the entire contents of a text file into a string.
 */
public class TextFileReader
{
    private final File file;

    /**
     * Initializes this {@link TextFileReader} with a file. The read action is not performed until the
     * call to the {@link #read()} function.
     * 
     * @param file
     *        the file to use
     */
    public TextFileReader(final File file)
    {
        this.file = file;
    }

    /**
     * Returns the contents of the text file as a string.
     * 
     * @return the contents
     * @throws IOException
     */
    public String read() throws IOException
    {
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try
        {
            String line;
            String result = "";

            while ((line = reader.readLine()) != null)
            {
                result += String.format("%s%n", line);
            }

            return result;
        }
        finally
        {
            reader.close();
        }
    }
}
