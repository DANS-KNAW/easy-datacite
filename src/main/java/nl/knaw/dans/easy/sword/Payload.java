package nl.knaw.dans.easy.sword;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private static Logger    log = LoggerFactory.getLogger(EasyBusinessFacade.class);

    private final List<File> files;
    private final File       tempDir;
    private final File       dataFolder;
    private final File       metadataFile;

    public Payload(final InputStream inputStream) throws SWORDException, SWORDErrorException
    {
        try
        {
            tempDir = FileUtil.createTempDirectory(new File(Context.getUnzip()), "swunzip");
            files = UnzipUtil.unzip(new ZipInputStream(new BufferedInputStream(inputStream)), tempDir.getPath());
            final File[] rootContent = tempDir.listFiles();
            if (files.size() < 2 || rootContent == null || rootContent.length != 2)
            {
                // an XML file and a folder in the root of the unzipped directory, no more no less
                throw newSwordInputException("Expecting a file and a folder with files.",null);
            }
            if (rootContent[0].isDirectory() && rootContent[1].isFile())
            {
                dataFolder = rootContent[0];
                metadataFile = rootContent[1];
            }
            else if (rootContent[1].isDirectory() && rootContent[0].isFile())
            {
                dataFolder = rootContent[1];
                metadataFile = rootContent[0];
            }
            else
            {
                throw newSwordInputException("Expecting a file and a folder with files.",null);
            }
            for (final File file : files)
            {
                // yes, we do have some real data
                if (file.isFile() && !file.getPath().equals(metadataFile.toString()))
                    return;
            }
            // oops, just folders
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "No files in the data folder.");
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
        try
        {
            return EasyMetadataFacade.validate(FileUtil.readFile(metadataFile));
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
