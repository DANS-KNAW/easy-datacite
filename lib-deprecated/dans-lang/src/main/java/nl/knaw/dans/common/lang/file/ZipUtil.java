package nl.knaw.dans.common.lang.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtil
{

    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * Zip the given collection of <code>zipItems</code> to the file <code>zipFile</code>. The members of
     * the collection <code>zipItems</code> can be of type {@link ZipItem} and {@link ZipFolderItem}.
     * <p/>
     * A ZipFileItem has a 'path' denoting the file name and -optional- the file path within the created
     * zip file. Like so:
     * 
     * <pre>
     *    &quot;myFile.txt&quot;
     *    &quot;foo/bar/myFile.txt&quot;
     * </pre>
     * 
     * Above that, it must have an InputStream that will be written to the zipFile. After usage, the
     * InputStream will be closed by this method.
     * <p/>
     * A ZipFolderItem has a 'path' denoting the folder path. Mind that the last character in the 'path'
     * should be a forward slash (/), otherwise the last part of the path will be zipped as a (empty)
     * file.
     * 
     * <pre>
     *    &quot;myEmptyFolder/&quot;
     *    &quot;foo/bar/myEmptyFolder/&quot;
     * </pre>
     * 
     * @param zipFile
     *        File will be created by this method; the file path must exist.
     * @param zipItems
     *        a collection of ZipItems to be zipped.
     * @return the given zipFile
     * @throws IOException
     *         if the zipFile or one of the internally created and handled streams could not be opened,
     *         read, written or closed.
     */
    public static File zipFiles(File zipFile, Collection<ZipItem> zipItems) throws IOException
    {
        ZipOutputStream zipOut = null;
        logger.debug("Creating zip file with " + zipItems.size() + " entries");
        try
        {
            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            for (ZipItem zipItem : zipItems)
            {
                addZipEntry(zipOut, zipItem);
            }
        }
        finally
        {
            if (zipOut != null)
            {
                closeZipOutputStream(zipOut);
            }
        }
        return zipFile;

    }

    private static void closeZipOutputStream(ZipOutputStream zipOut) throws IOException
    {
        try
        {
            zipOut.close();
        }
        catch (IOException e)
        {
            logger.error("Could not close ZipOutputStream: ", e);
            throw e;
        }
    }

    public static void addZipEntry(ZipOutputStream zipOut, ZipItem zipItem) throws IOException
    {
        ZipEntry zipEntry = null;
        try
        {
            zipEntry = new ZipEntry(zipItem.getVirtualPath());
            zipOut.putNextEntry(zipEntry);
            if (zipItem.hasInputStream())
            {
                writeBytes(zipOut, ((ZipItem) zipItem).getInputStream());
            }
        }
        finally
        {
            if (zipEntry != null)
            {
                closeZipEntry(zipOut);
            }
        }
    }

    private static void closeZipEntry(ZipOutputStream zipOut) throws IOException
    {
        try
        {
            zipOut.closeEntry();
        }
        catch (IOException e)
        {
            logger.error("Could not close ZipEntry: ", e);
            throw e;
        }
    }

    private static void writeBytes(ZipOutputStream out, InputStream inStream) throws IOException
    {
        byte[] buf = new byte[8192];
        try
        {
            int read = inStream.read(buf);
            while (read != -1)
            {
                out.write(buf, 0, read);
                read = inStream.read(buf);
            }
        }
        finally
        {
            closeInputStream(inStream);
        }
    }

    private static void closeInputStream(InputStream inStream) throws IOException
    {
        try
        {
            inStream.close();
        }
        catch (IOException e)
        {
            logger.error("Could not close InputStream: ", e);
            throw e;
        }
    }

}
