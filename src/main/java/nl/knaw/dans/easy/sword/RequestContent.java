package nl.knaw.dans.easy.sword;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContent
{
    enum MDFileName
    {
        easyMetadata("deprecated"), DansDatasetMetadata("not yet implemented");

        static boolean accepts(final File file)
        {
            final String baseName = file.getName().replace(".xml", "");
            return file.isFile() && Arrays.toString(values()).contains(baseName);
        }

        final String note;

        MDFileName(final String note)
        {
            this.note = note;
        }

        static String fileNames()
        {
            String result = "";
            for (final MDFileName value : values())
            {
                result += " " + value + ".xml (" + value.note + ")";
            }
            return result;
        }
    };

    private static final String MESSAGE = "Expecting a folder with files and a file with one of the names:" + MDFileName.fileNames();

    private final File tempDir;
    private final File dataFolder;
    private final List<File> files;
    private final EasyMetadata easyMetadata;

    private static final Logger logger = LoggerFactory.getLogger(RequestContent.class);

    public RequestContent(final InputStream inputStream) throws SWORDException, SWORDErrorException
    {
        final File metadataFile;
        tempDir = createTempDir();
        files = unzip(inputStream);
        final File[] rootEntries = zipHasTwoRootEntries();

        if (rootEntries[0].isDirectory() && MDFileName.accepts(rootEntries[1]))
        {
            dataFolder = rootEntries[0];
            metadataFile = rootEntries[1];
        }
        else if (rootEntries[1].isDirectory() && MDFileName.accepts(rootEntries[0]))
        {
            dataFolder = rootEntries[1];
            metadataFile = rootEntries[0];
        }
        else
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, MESSAGE);
        }

        folderHasFiles(metadataFile);
        checkFileConstraints();
        easyMetadata = createEasyMetadata(metadataFile);
    }

    private void checkFileConstraints() throws SWORDErrorException
    {
        final List<String> messages = new ArrayList<String>();
        final int maxLength = 256 + dataFolder.getPath().length() - "original".length();
        for (final File file : files)
        {
            final String path = file.getPath();
            if (path.length() > maxLength)
            {
                // limitation caused by varchar(256) of fileItem and folderItem tables
                messages.add("\npath name exceeds 247 characters:\n" + path);
            }
        }
        if (messages.size() > 0)
            throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, "file name constraints violated: " + Arrays.deepToString(messages.toArray()) + "\n");
    }

    private EasyMetadata createEasyMetadata(final File metadataFile) throws SWORDErrorException, SWORDException
    {
        if (metadataFile.getName().startsWith(MDFileName.easyMetadata.name()))
            return EasyMetadataFacade.validate(readMetadata(metadataFile));

        // TODO apply cross-walker conversion
        throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, ("Format not yet implemented: " + metadataFile.getName()));
    }

    private List<File> unzip(final InputStream inputStream) throws SWORDErrorException
    {
        try
        {
            return UnzipUtil.unzip(new ZipInputStream(new BufferedInputStream(inputStream)), tempDir.getPath());
        }
        catch (final ZipException exception)
        {
            logger.error("unzip problem", exception);
            throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, "Failed to unzip deposited file");
        }
        catch (final IOException exception)
        {
            logger.error("unzip problem", exception);
            throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, "Failed to unzip deposited file");
        }
    }

    private File createTempDir() throws SWORDException
    {
        final File basePath = new File(Context.getUnzip());
        final String prefix = "swunzip";
        if (!basePath.exists())
        {
            if (!basePath.mkdir())
                throw new SWORDException("please create location for temporary unzip directories: " + basePath.getAbsolutePath());
        }
        try
        {
            return FileUtil.createTempDirectory(basePath, prefix);
        }
        catch (final IOException exception)
        {
            throw new SWORDException("Could not create temp dir: " + basePath.getAbsolutePath() + "/" + prefix);
        }
    }

    private File[] zipHasTwoRootEntries() throws SWORDErrorException
    {
        final File[] rootContent = tempDir.listFiles();
        if (files.size() < 2 || rootContent == null || rootContent.length != 2)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, MESSAGE);
        }
        return rootContent;
    }

    private void folderHasFiles(final File metadataFile) throws SWORDErrorException
    {
        for (final File file : files)
        {
            if (file.isFile() && !file.getPath().equals(metadataFile.toString()))
                return;
        }
        throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, "No files in the folder.");
    }

    public List<File> getFiles()
    {
        return files;
    };

    public void clearTemp()
    {
        // delete files before folders
        for (int i = files.size(); --i >= 0;)
            files.get(i).delete();
        tempDir.delete();
    }

    public EasyMetadata getEasyMetadata() throws SWORDException, SWORDErrorException
    {
        return easyMetadata;
    }

    private byte[] readMetadata(final File metadataFile) throws SWORDErrorException, SWORDException
    {
        byte[] readFile;
        try
        {
            readFile = FileUtil.readFile(metadataFile);
        }
        catch (final FileNotFoundException exception)
        {
            // should never happen: prevented by checks in constructor
            throw new SWORDException(("File not found: " + metadataFile));
        }
        catch (final IOException exception)
        {
            throw new SWORDException("Failed to extract the EasyMetadata", exception);
        }
        return readFile;
    }

    public File getDataFolder()
    {
        return dataFolder;
    }
}
