package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.worker.WorkListener;

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDException;

public class UnzipResult
{
    private final List<File> files;
    private final File       folder;

    public UnzipResult(final InputStream inputStream) throws SWORDException
    {
        try
        {
            // TODO configure temp directory
            final File tempDir = FileUtil.createTempDirectory(new File("temp"), "swunzip");
            final String zipFile = tempDir.getPath() + "/received.zip";
            final String destPath = tempDir.getPath() + "/unzipped";
            if (!new File(destPath).mkdir())
                throw new SWORDException("Failed to unzip");
            saveFile(inputStream, zipFile);
            files = UnzipUtil.unzip(new File(zipFile), destPath);
            folder = new File(destPath);
            if (!getDataFolder().isDirectory())
                throw new SWORDException("no data folder found");
            if (!getMetadataFile().isFile())
                throw new SWORDException("no metadata file found");
        }
        catch (final IOException exception)
        {
            throw new SWORDException("Failed to open deposited zip file", exception);
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
        catch (final IOException e)
        {
            throw new SWORDException("Failed to save deposited zip file", null, ErrorCodes.ERROR_CONTENT);
        }
    }

    public List<File> getFiles()
    {
        return files;
    };

    public void submit(final String userName) throws SWORDException
    {
        SwordDatasetUtil.submitNewDataset(userName, getEasyMetaData(), getDataFolder(), getFiles(), new WorkListener[]{});
    }

    private byte[] getEasyMetaData() throws SWORDException
    {
        final byte[] easyMetadata;
        try
        {
            easyMetadata = FileUtil.readFile(getMetadataFile());
        }
        catch (final IOException e)
        {
            throw new SWORDException("Failed to extract the EasyMetadata");
        }
        return easyMetadata;
    }

    private File getMetadataFile()
    {
        return new File(folder.getPath() + "/easyMetadata.xml");
    }

    private File getDataFolder()
    {
        return new File(folder.getPath() + "/data");
    }
}
