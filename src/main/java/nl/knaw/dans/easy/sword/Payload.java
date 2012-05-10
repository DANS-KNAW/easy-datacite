package nl.knaw.dans.easy.sword;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class Payload
{
    enum MDFileName
    {
        easyMetadata, DansDatasetMetadata;

        static boolean accepts(final File file)
        {
            final String baseName = file.getName().replace(".xml", "");
            return file.isFile() && Arrays.toString(values()).contains(baseName);
        }

        static String fileNames()
        {
            String result = "";
            for (MDFileName value : values())
            {
                result += " " + value + ".xml";
            }
            return result;
        }
    };

    private static final String MESSAGE = "Expecting a file with one of the names " + MDFileName.fileNames() + "and a folder with files.";

    private static Logger       log     = LoggerFactory.getLogger(EasyBusinessFacade.class);

    private final File          tempDir;
    private final File          dataFolder;
    private final List<File>    files;
    private final EasyMetadata  easyMetadata;

    public Payload(final InputStream inputStream) throws SWORDException, SWORDErrorException
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
            throw newSwordInputException(MESSAGE, null);
        }

        folderHasFiles(metadataFile);
        easyMetadata = createEasyMetadata(metadataFile);
    }

    private EasyMetadata createEasyMetadata(final File metadataFile) throws SWORDErrorException, SWORDException
    {
        if (metadataFile.getName().startsWith(MDFileName.easyMetadata.name()))
            return EasyMetadataFacade.validate(readMetadata(metadataFile));
        
        // TODO apply cross-walker conversion
        return EasyMetadataFacade.validate(readMetadata(metadataFile));
    }

    private List<File> unzip(final InputStream inputStream) throws SWORDErrorException
    {
        try
        {
            return UnzipUtil.unzip(new ZipInputStream(new BufferedInputStream(inputStream)), tempDir.getPath());
        }
        catch (final ZipException exception)
        {
            throw newSwordInputException("Failed to unzip deposited file", exception);
        }
        catch (final IOException exception)
        {
            throw newSwordInputException("Failed to unzip deposited file", exception);
        }
    }

    private File createTempDir() throws SWORDException
    {
        try
        {
            return FileUtil.createTempDirectory(new File(Context.getUnzip()), "swunzip");
        }
        catch (final IOException exception)
        {
            throw newSWORDException("Could not create temp dir for unzip", exception);
        }
    }

    private File[] zipHasTwoRootEntries() throws SWORDErrorException
    {
        final File[] rootContent = tempDir.listFiles();
        if (files.size() < 2 || rootContent == null || rootContent.length != 2)
        {
            throw newSwordInputException(MESSAGE, null);
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
        throw newSwordInputException("No files in the folder.", null);
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
            throw newSwordInputException("File not found: " + metadataFile, exception);
        }
        catch (final IOException exception)
        {
            throw newSWORDException("Failed to extract the EasyMetadata", exception);
        }
        return readFile;
    }

    private static SWORDException newSWORDException(final String message, final Exception exception)
    {
        log.error(message, exception);
        return new SWORDException(message);
    }

    private static SWORDErrorException newSwordInputException(final String message, final Exception exception)
    {
        log.error(message, exception);
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, message);
    }

    public File getDataFolder()
    {
        return dataFolder;
    }
}
