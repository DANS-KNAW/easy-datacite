package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.IsoDate;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnzipResult
{
    private static final String              METADATA             = "easyMetadata.xml";
    private static final String              DATA                 = "data";
    private static final String              DESCRIPTION          = "Expecting a file '" + METADATA + "' and a folder '" + DATA + "'.";
    private static final SWORDErrorException WANT_FILE_AND_FOLDER = new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, DESCRIPTION);
    private static Logger                    log                  = LoggerFactory.getLogger(SwordDatasetUtil.class);

    private final List<File>                 files;
    private final File                       folder;
    private final String                     destPath;
    private byte[]                           easyMetadata;
    private int                              noOpSumbitCounter;

    public UnzipResult(final InputStream inputStream) throws SWORDException, SWORDErrorException
    {
        try
        {
            // TODO configure temp directory
            File dir = new File("temp");
            if (!dir.isDirectory())
            {
                // for testing purposes
                dir = new File("target/tmp");
                dir.mkdirs();
            }
            final File tempDir = FileUtil.createTempDirectory(dir, "swunzip");

            final String zipFile = tempDir.getPath() + "/received.zip";
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
                if (file.isFile())
                    return;
            }
            // oops, just folders
            throw WANT_FILE_AND_FOLDER;
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

    public Dataset submit(final EasyUser user, final boolean mock) throws SWORDException
    {
        SwordDatasetUtil.validateEasyMetadata(getEasyMetaData());
        // TODO validate not just syntactical but also permitted/mandatory fields
        final EasyMetadata metadata = SwordDatasetUtil.unmarshallEasyMetaData(getEasyMetaData());

        if (mock)
        {
            return mockSubmittedDataset(metadata, user);
        }
        return SwordDatasetUtil.submitNewDataset(user, metadata, getDataFolder(), getFiles());
    }

    private byte[] getEasyMetaData() throws SWORDException
    {
        if (easyMetadata == null)
        {
            try
            {
                easyMetadata = FileUtil.readFile(getMetadataFile());
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

    private File getDataFolder()
    {
        return new File(folder.getPath() + "/" + DATA);
    }

    private static SWORDException newSWORDException(final String message, final Exception exception)
    {
        log.error(message, exception);
        return new SWORDException(message);
    }

    public String getPath()
    {
        return destPath + "/data/";
    }

    private Dataset mockSubmittedDataset(final EasyMetadata metadata, EasyUser user) throws SWORDException
    {
        ++noOpSumbitCounter;
        final String pid = (noOpSumbitCounter+"xxxxxxxx").replaceAll("(..)(...)(...)", "urn:nbn:nl:ui:$1-$2-$3");
        final String storeID = "mockedStoreID:" + noOpSumbitCounter;
        final Dataset dataset = EasyMock.createMock(Dataset.class);

        // TODO the following lines duplicates logic of DatasetImpl, move to EasyMetadata?
        final List<IsoDate> lda = metadata.getEmdDate().getEasAvailable();
        final List<IsoDate> lds = metadata.getEmdDate().getEasDateSubmitted();
        final DateTime dateAvailable = (lda.size() == 0 ? null : lda.get(0).getValue());
        final IsoDate dateSubmitted = (lds.size() == 0) ? new IsoDate() : lds.get(0);
        final boolean underEmbargo = dateAvailable != null && new DateTime().plusMinutes(1).isBefore(dateAvailable);

        EasyMock.expect(dataset.getEasyMetadata()).andReturn(metadata).anyTimes();
        EasyMock.expect(dataset.getStoreId()).andReturn(storeID).anyTimes();
        EasyMock.expect(dataset.getAccessCategory()).andReturn(metadata.getEmdRights().getAccessCategory()).anyTimes();
        EasyMock.expect(dataset.getDateSubmitted()).andReturn(dateSubmitted).anyTimes();
        EasyMock.expect(dataset.getDateAvailable()).andReturn(dateAvailable).anyTimes();
        EasyMock.expect(dataset.getPreferredTitle()).andReturn(metadata.getPreferredTitle()).anyTimes();
        EasyMock.expect(dataset.getDepositor()).andReturn(user).anyTimes();
        EasyMock.expect(dataset.getPersistentIdentifier()).andReturn(pid).anyTimes();
        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(underEmbargo).anyTimes();
        EasyMock.replay(dataset);
        return dataset;
    }
}
