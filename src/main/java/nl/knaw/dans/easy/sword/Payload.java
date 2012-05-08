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

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Payload
{
    private static final String              EMD              = "easyMetadata.xml";
    private static final String              DATA                  = "data";
    private static final String              DESCRIPTION           = "Expecting a file '" + EMD + "' and files in folder '" + DATA + "'.";
    private static final SWORDErrorException WANT_FILE_AND_FOLDER  = new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, DESCRIPTION);

    private static Logger                    log                   = LoggerFactory.getLogger(EasyBusinessFacade.class);

    private final List<File>                 files;
    private final File                       folder;
    private final File                       tempDir;
    private final String                     destPath;
    private byte[]                           easyMetadata;

    public Payload(final InputStream inputStream) throws SWORDException, SWORDErrorException
    {
        try
        {
            tempDir = FileUtil.createTempDirectory(new File(Context.getUnzip()), "swunzip");
            destPath = tempDir.getPath() + "/unzipped";
            if (!new File(destPath).mkdir())
                throw new SWORDException("Failed to unzip");
            files = UnzipUtil.unzip(new ZipInputStream(new BufferedInputStream(inputStream)), destPath);
            folder = new File(destPath);
            if (files.size() < 2)
            {
                // at least an XML file and a data file
                throw WANT_FILE_AND_FOLDER;
            }
            if (new File(destPath).listFiles().length != 2)
            {
                // an XML file and a folder in the root of the unzipped directory, no more no less
                throw WANT_FILE_AND_FOLDER;
            }
            if (!getDataFolder().isDirectory())
            {
                // not the expected folder in the root
                throw WANT_FILE_AND_FOLDER;
            }
            if (!getMetadataFile().isFile())
            {
                // not the expected file in the root
                throw WANT_FILE_AND_FOLDER;
            }
            for (final File file : files)
            {
                // yes, we do have some real data
                if (file.isFile() && !file.getPath().equals(destPath+"/"+EMD))
                    return;
            }
            // oops, just folders
            throw WANT_FILE_AND_FOLDER;
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
        for (int i=files.size() ; --i>=0 ;)
                files.get(i).delete();
        new File(destPath).delete();
        tempDir.delete();
    }

    public byte[] getEasyMetaData() throws SWORDException, SWORDErrorException
    {
        if (easyMetadata == null)
        {
            try
            {
                easyMetadata = FileUtil.readFile(getMetadataFile());
            }
            catch (final FileNotFoundException exception){
                // should never happen: prevented by checks in constructor
                throw newSwordInputException("File not found: "+getMetadataFile(), exception);
            }
            catch (final IOException exception)
            {
                throw newSWORDException("Failed to extract the EasyMetadata", exception);
            }
        }
        return easyMetadata;
    }

    private File getMetadataFile()
    {
        return new File(folder.getPath() + "/" + EMD);
    }

    public File getDataFolder()
    {
        return new File(folder.getPath() + "/" + DATA);
    }

    private static SWORDException newSWORDException(final String message, final Exception exception)
    {
        log.error(message, exception);
        return new SWORDException(message);
    }

    private static SWORDErrorException newSwordInputException(final String message, final Exception exception)
    {
        log.error(message, exception);
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST,message);
    }
}
