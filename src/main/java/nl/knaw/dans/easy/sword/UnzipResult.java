package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipException;

import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnzipResult
{
    private static final String              METADATA              = "easyMetadata.xml";
    private static final String              DATA                  = "data";
    private static final String              DESCRIPTION           = "Expecting a file '" + METADATA + "' and a folder '" + DATA + "'.";
    private static final SWORDErrorException WANT_FILE_AND_FOLDER  = new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, DESCRIPTION);

    private static Logger                    log                   = LoggerFactory.getLogger(EasyBusinessFacade.class);

    private final List<File>                 files;
    private final File                       folder;
    private final File                       tempDir;
    private final String                     destPath;
    private final String                     zipFile;
    private byte[]                           easyMetadata;

    public UnzipResult(final InputStream inputStream) throws SWORDException, SWORDErrorException
    {
        try
        {
            tempDir = FileUtil.createTempDirectory(new File(Context.getUnzip()), "swunzip");
            zipFile = tempDir.getPath() + "/received.zip";
            destPath = tempDir.getPath() + "/unzipped";
            if (!new File(destPath).mkdir())
                throw new SWORDException("Failed to unzip");
            saveFile(inputStream, zipFile);
            files = UnzipUtil.unzip(new File(zipFile), destPath);
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
                if (file.isFile() && !file.getPath().equals(destPath+"/"+METADATA))
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
            throw newSWORDException("Failed to unzip deposited file", exception);
        }
    }

    private static void saveFile(final InputStream inputStream, final String file) throws SWORDException
    {
        try
        {
            new File(file).createNewFile();
            final OutputStream outputStream = new FileOutputStream(file);
            try
            {
                final byte buffer[] = new byte[2048];
                int count;
                while ((count = inputStream.read(buffer, 0, buffer.length)) != -1)
                    outputStream.write(buffer, 0, count);
            }
            finally
            {
                outputStream.close();
            }
        }
        catch (final IOException exception)
        {
            throw newSWORDException("Failed to save deposited zip file", exception);
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
        new File(zipFile).delete();
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
        return new File(folder.getPath() + "/" + METADATA);
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
