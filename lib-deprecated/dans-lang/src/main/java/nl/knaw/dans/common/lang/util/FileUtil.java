package nl.knaw.dans.common.lang.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.util.IOUtil;

public class FileUtil
{

    private static final String INVALID_PATH_REG_EX = "[:?*\"'<>|]*";
    private static final Pattern INVALID_PATH_PATTERN = Pattern.compile(INVALID_PATH_REG_EX);

    private static MimeTypeIdentifierFactory MIMETYPE_IDENTIFIER_FACTORY;

    /**
     * Copy the file <code>original</code> to the file <code>destination</code>.
     * 
     * @param original
     *        the original file
     * @param destination
     *        the destination file
     * @throws IOException
     *         for exceptions during IO
     */
    public static void copyFile(File original, File destination) throws IOException
    {
        if (!original.exists())
        {
            throw new FileNotFoundException("File not found: " + original.getName());
        }
        if (original.isHidden())
        {
            return;
        }
        if (original.equals(destination))
        {
            throw new IOException("Cannot copy " + original.getName() + " into itself.");
        }
        if (original.isDirectory() && destination.isFile())
        {
            throw new IOException("Cannot copy the contents of '" + original.getName() + "' to the file " + destination.getName());
        }

        File finalDestination;
        if (destination.isDirectory())
        {
            finalDestination = new File(destination, original.getName());
        }
        else
        {
            finalDestination = destination;
        }

        if (original.isDirectory())
        {
            finalDestination.mkdirs();
            for (File f : original.listFiles())
            {
                copyFile(f, finalDestination);
            }
        }
        else
        {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try
            {
                fis = new FileInputStream(original);
                fos = new FileOutputStream(finalDestination);
                byte[] buffer = new byte[4096];
                int read = fis.read(buffer);
                while (read != -1)
                {
                    fos.write(buffer, 0, read);
                    read = fis.read(buffer);
                }
            }
            finally
            {
                closeStreams(fis, fos);
            }
        }
    }

    private static void closeStreams(FileInputStream fis, FileOutputStream fos) throws IOException
    {
        try
        {
            if (fis != null)
            {
                fis.close();
            }
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
    }

    public static File createTempDirectory(File basePath, String prefix) throws IOException
    {
        if (!basePath.isDirectory())
            throw new IOException("Directory '" + basePath.getAbsolutePath() + "' does not exist or is not a directory.");

        Integer i = 1; // infinite loop protection
        Random r = new Random();
        File destPath;
        do
        {
            destPath = new File(basePath.getAbsolutePath() + File.separatorChar + prefix + "_" + Math.abs(r.nextInt()));
            i++;
        } while ((destPath.isFile() || destPath.isDirectory()) && i < 1024);
        if (i >= 1024)
            throw new IOException("Unable to detect unique path");

        if (!destPath.mkdir())
            throw new IOException("Unable to create temp directory");

        return destPath;
    }

    public static File createTempDirectory(String prefix) throws IOException
    {
        File tmpPath = new File(System.getProperty("java.io.tmpdir"));
        if (!tmpPath.isDirectory())
            throw new IOException("Temporary directory '" + tmpPath.getAbsolutePath() + "' does not exist or is not a directory.");
        return createTempDirectory(tmpPath, prefix);
    }

    public static void deleteDirectory(File path) throws IOException
    {
        if (!path.isDirectory())
            throw new IOException("Directory '" + path + "' does not exist or is not a directory.");

        if (path.isDirectory())
        {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    deleteDirectory(files[i]);
                }
                else
                {
                    if (!files[i].delete())
                        throw new IOException("Error while deleting file " + files[i].getAbsolutePath());
                }
            }
        }

        if (!path.delete())
            throw new IOException("Error while deleting directory " + path.getAbsolutePath());
    }

    /**
     * Returns just filename of a full path, e.g. returns smily.gif for /images/smily.gif
     * 
     * @param path
     *        The full path to the filename
     */
    public static String getBasicFilename(String path)
    {
        // get uploaded filename without path
        Integer bIdx = path.lastIndexOf("/");
        if (bIdx < 0)
            bIdx = path.lastIndexOf("\\");
        if (bIdx >= 0 && bIdx != path.length())
            return path.substring(bIdx + 1);
        return path;
    }

    public static byte[] readFile(File file) throws IOException
    {
        byte[] bytes = null;
        InputStream inStream = null;
        long length = file.length();
        try
        {
            inStream = new FileInputStream(file);
            if (length > Integer.MAX_VALUE)
            {
                throw new IOException("File too long " + file.getName());
            }
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = inStream.read(bytes, offset, bytes.length - offset)) >= 0)
            {
                offset += numRead;
            }
            if (offset < bytes.length)
            {
                throw new IOException("Could not completely read file " + file.getName());
            }
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return bytes;
    }

    /**
     * Identify the mimeType of a file.
     * 
     * @param file
     *        the file to identify
     * @return the mimeType of the file or <code>null</code> if the mimeType could not be identified
     * @throws IOException
     *         for any IOException that may occur while opening, reading or closing the file
     */
    public static String getMimeType(File file) throws IOException
    {
        String mType;
        FileInputStream stream = null;
        MimeTypeIdentifier identifier = getMimeTypeIdentifier();
        try
        {
            stream = new FileInputStream(file);
            BufferedInputStream buffer = new BufferedInputStream(stream);
            byte[] bytes = IOUtil.readBytes(buffer, identifier.getMinArrayLength());
            mType = identifier.identify(bytes, file.getPath(), null);
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
        return mType;
    }

    private static MimeTypeIdentifier getMimeTypeIdentifier()
    {
        if (MIMETYPE_IDENTIFIER_FACTORY == null)
        {
            MIMETYPE_IDENTIFIER_FACTORY = new MagicMimeTypeIdentifierFactory();
        }
        return MIMETYPE_IDENTIFIER_FACTORY.get();
    }

    public static boolean isValidRelativePath(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return false;
        }

        if (path.startsWith("/"))
        {
            return false;
        }

        return !INVALID_PATH_PATTERN.matcher(path).matches();
    }

}
