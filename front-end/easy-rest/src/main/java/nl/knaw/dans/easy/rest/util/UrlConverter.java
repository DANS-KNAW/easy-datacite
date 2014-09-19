package nl.knaw.dans.easy.rest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * An utility class for converting URL objects to a byte array.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class UrlConverter {

    /**
     * Throw an AssertionError if this class is instantiated.
     */
    protected UrlConverter() {
        throw new AssertionError("Instantiating utility class...");
    }

    /**
     * Given an URL and size (in bytes) this method will return the byte array.
     * 
     * @param url
     *        The URL that points to a file.
     * @param size
     *        The size of the file (in bytes).
     * @return An byte array that represents the file.
     * @throws IOException
     *         If something goes wrong while parsing the URL.
     */
    public static byte[] toByteArray(URL url, long size) throws IOException {
        InputStream input = url.openStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[Integer.parseInt("" + size)];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

}
