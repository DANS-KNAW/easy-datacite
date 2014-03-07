package nl.knaw.dans.common.lang.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;

/**
 * @author lobo Unzipper class. Providing an IUnzipListener will enable one to get some updates on the
 *         progress.
 */
public class UnzipUtil
{
    public static final String[] SKIPPED_FILENAMES = {"Thumbs.db", "__MACOSX", ".DS_Store"};

    public static final List<String> SKIPPED_FILENAMES_LIST = Arrays.asList(SKIPPED_FILENAMES);

    /**
     * I've tried different buffer sizes and their performance impact on a macbookpro 2.2Ghz 3Gm and
     * found 8k to be the optimal buffer size. This however depends of course on the balance between the
     * time it takes the CPU to decompress the zip file and the time it takes the hard disk and memory to
     * read in the bytes. If too many bytes are read CPU time is lost if too little I/O time is lost.
     * 1k=2.17ms;4k=2.8ms;8k=1.878ms;16k=3.750ms;64k=3.775ms;128=3.891ms;1M = 6.501ms
     */
    private static final int BUFFER_SIZE = 8 * 1024;

    private final File zipFile;

    private final String destPath;

    private final UnzipListener unzipListener;

    public static List<File> unzip(final File zipFile, final String destPath) throws IOException
    {
        return extract(zipFile, destPath, null);
    }

    public static List<File> unzip(final ZipInputStream zipInputStream, final String destPath) throws IOException
    {
        return extract(zipInputStream, destPath, 100, null, 0);
    }

    public UnzipUtil(final File zipFile, final String destPath, final UnzipListener unzipListener)
    {
        this.zipFile = zipFile;
        this.destPath = destPath;
        this.unzipListener = unzipListener;
    }

    public List<File> run() throws Exception
    {
        return unzip(zipFile, destPath, unzipListener);
    }

    public File getZipFile()
    {
        return zipFile;
    }

    public String getDestPath()
    {
        return destPath;
    }

    private static boolean createPath(final String basePathStr, final String path, final List<File> files, final FilenameFilter filter) throws IOException
    {
        final File basePath = new File(basePathStr);
        if (!basePath.exists())
            throw new FileNotFoundException(basePathStr);

        final String[] extPaths = path.split(File.separator);
        String pathStr = basePath.getPath();
        if (!pathStr.endsWith(File.separator))
            pathStr += File.separator;

        for (final String pathPiece : extPaths)
        {
            if (!filter.accept(basePath, pathPiece))
                return false;
        }

        for (int i = 0; i < extPaths.length; i++)
        {
            pathStr += extPaths[i] + File.separator;
            final File npath = new File(pathStr);
            if (!npath.isDirectory())
            {
                if (!npath.mkdir())
                    throw new IOException("Error while creating directory " + npath.getAbsolutePath());
                files.add(npath);
            }
        }

        return true;
    }

    /**
     * Main unzipping routine
     * 
     * @param zipFile
     *        The zip file
     * @param destPath
     *        The destination path to which the zip file should be unzipped
     * @param unzipListener
     *        A class may or may not be supplied (then null) to receive updates
     */
    public static List<File> unzip(final File zipFile, final String destPath, final UnzipListener unzipListener) throws IOException
    {
        return extract(zipFile, destPath, unzipListener);
    }

    private static List<File> extract(final File zipFile, final String destPath, final UnzipListener unzipListener) throws FileNotFoundException, ZipException,
            IOException
    {
        final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));

        // get total uncompressed size of zip file
        final ZipFile zf = new ZipFile(zipFile);
        final Enumeration<? extends ZipEntry> e = zf.entries();
        long totSize = 0;
        int totFiles = 0;
        while (e.hasMoreElements())
        {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            totSize += ze.getSize();
            totFiles++;
        }
        zf.close();

        return extract(zis, destPath, totFiles, unzipListener, totSize);
    }

    private static List<File> extract(final ZipInputStream zipInputStream, String destPath, final int initialCapacityForFiles,
            final UnzipListener unzipListener, final long totSize) throws FileNotFoundException, IOException
    {
        if (unzipListener != null)
            unzipListener.onUnzipStarted(totSize);

        final File destPathFile = new File(destPath);
        if (!destPathFile.exists())
            throw new FileNotFoundException(destPath);
        if (!destPathFile.isDirectory())
            throw new IOException("Expected directory, got file.");
        if (!destPath.endsWith(File.separator))
            destPath += File.separator;

        // now unzip
        BufferedOutputStream out = null;
        ZipEntry entry;
        int count;
        final byte data[] = new byte[BUFFER_SIZE];
        final ArrayList<File> files = new ArrayList<File>(initialCapacityForFiles);
        String entryname;
        String filename;
        String path;
        boolean cancel = false;
        final DefaultUnzipFilenameFilter filter = new DefaultUnzipFilenameFilter();

        try
        {
            long bytesWritten = 0;
            while (((entry = zipInputStream.getNextEntry()) != null) && !cancel)
            {
                entryname = entry.getName();
                final int fpos = entryname.lastIndexOf(File.separator);
                if (fpos >= 0)
                {
                    path = entryname.substring(0, fpos);
                    filename = entryname.substring(fpos + 1);
                }
                else
                {
                    path = "";
                    filename = new String(entryname);
                }

                if (!filter.accept(destPathFile, filename))
                {
                    // file filtered out
                    continue;
                }

                if (entry.isDirectory())
                {
                    if (!createPath(destPath, entryname, files, filter))
                        // directory filtered out
                        continue;
                }
                else
                {
                    if (!StringUtils.isBlank(path))
                    {
                        if (!createPath(destPath, path, files, filter))
                            // path filtered out
                            continue;
                    }

                    final String absFilename = destPath + entryname;
                    final FileOutputStream fos = new FileOutputStream(absFilename);
                    out = new BufferedOutputStream(fos, BUFFER_SIZE);
                    try
                    {
                        // inner loop
                        while ((count = zipInputStream.read(data, 0, BUFFER_SIZE)) != -1)
                        {
                            out.write(data, 0, count);
                            bytesWritten += count;

                            if (unzipListener != null)
                                cancel = !unzipListener.onUnzipUpdate(bytesWritten, totSize);
                            if (cancel)
                                break;
                        }
                        out.flush();
                    }
                    finally
                    {
                        out.close();
                        files.add(new File(absFilename));
                    }
                }
            }
        }
        finally
        {
            zipInputStream.close();

            // rollback?
            if (cancel)
            {
                // first remove files
                for (final File file : files)
                {
                    if (!file.isDirectory())
                        file.delete();
                }
                // then folders
                for (final File file : files)
                {
                    if (file.isDirectory())
                        file.delete();
                }
                files.clear();
            }
        }

        if (unzipListener != null)
            unzipListener.onUnzipComplete(files, cancel);

        return files;
    }

    public static class DefaultUnzipFilenameFilter implements FilenameFilter
    {

        public boolean accept(final File dir, final String name)
        {
            return !SKIPPED_FILENAMES_LIST.contains(name);
        }
    }
}
