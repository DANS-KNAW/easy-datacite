package nl.knaw.dans.common.fedora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.repo.exception.RemoteException;
import nl.knaw.dans.common.lang.repo.exception.StorageDeviceException;
import nl.knaw.dans.common.lang.util.Wait;
import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.StreamUtility;

public class Repository
{

    public static final int DEFAULT_RETRY_TIME_OUT_SECONDS = 60;

    public static final int MAX_RETRY_COUNT = 3;

    private static final Logger logger = LoggerFactory.getLogger(Repository.class);

    private static final String FEDORA_UPLOAD = "fedora-upload-";

    private final String baseUrl;
    private final String username;
    private final String userpass;

    private FedoraClient fedoraClient;
    private FedoraAPIA fedoraAPIA;
    private FedoraAPIM fedoraAPIM;

    private int retryTimeOutSeconds;
    private int maxRetryCount;

    public Repository(String baseUrl, String username, String userpass)
    {
        this.baseUrl = baseUrl;
        this.username = username;
        this.userpass = userpass;
    }

    public int getRetryTimeOutSeconds()
    {
        if (retryTimeOutSeconds <= 0)
        {
            retryTimeOutSeconds = DEFAULT_RETRY_TIME_OUT_SECONDS;
        }
        return retryTimeOutSeconds;
    }

    public void setRetryTimeOutSeconds(int retryTimeOut)
    {
        this.retryTimeOutSeconds = retryTimeOut;
    }

    public int getMaxRetryCount()
    {
        if (maxRetryCount <= 0)
        {
            maxRetryCount = MAX_RETRY_COUNT;
        }
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount)
    {
        this.maxRetryCount = maxRetryCount;
    }

    public FedoraClient getFedoraClient() throws RepositoryException
    {
        if (fedoraClient == null)
        {
            try
            {
                fedoraClient = new FedoraClient(baseUrl, username, userpass);
                logger.info("Instantiated a FedoraClient for " + baseUrl);
            }
            catch (final MalformedURLException e)
            {
                final String msg = "Unable to instantiate a FedoraClient: ";
                logger.debug(msg, e);
                throw new RepositoryException(msg, e);
            }
        }
        return fedoraClient;
    }

    public FedoraAPIA getFedoraAPIA() throws RepositoryException
    {
        if (fedoraAPIA == null)
        {
            try
            {
                fedoraAPIA = getFedoraClient().getAPIA();
            }
            catch (final ServiceException e)
            {
                final String msg = "Unable to create SOAP stub for API-A: ";
                logger.debug(msg, e);
                throw new RepositoryException(msg, e);
            }
            catch (final IOException e)
            {
                final String msg = "Unable to create SOAP stub for API-A: ";
                logger.debug(msg, e);
                throw new RepositoryException(msg, e);
            }
        }
        return fedoraAPIA;
    }

    public FedoraAPIM getFedoraAPIM() throws RepositoryException
    {
        if (fedoraAPIM == null)
        {
            try
            {
                fedoraAPIM = getFedoraClient().getAPIM();
            }
            catch (final ServiceException e)
            {
                final String msg = "Unable to create SOAP stub for API-M: ";
                logger.debug(msg, e);
                throw new RepositoryException(msg, e);
            }
            catch (final IOException e)
            {
                final String msg = "Unable to create SOAP stub for API-M: ";
                logger.debug(msg, e);
                throw new RepositoryException(msg, e);
            }
        }
        return fedoraAPIM;
    }

    public DateTime getServerDate() throws RepositoryException
    {
        DateTime serverDate = null;
        try
        {
            serverDate = new DateTime(getFedoraClient().getServerDate());
        }
        catch (final IOException e)
        {
            final String msg = "Unable to get server date: ";
            logger.debug(msg, e);
            throw new RepositoryException(msg, e);
        }
        return serverDate;
    }

    public String getServerVersion() throws RepositoryException
    {
        String serverVersion = null;
        try
        {
            serverVersion = getFedoraClient().getServerVersion();
        }
        catch (final IOException e)
        {
            final String msg = "Unable to get server version: ";
            logger.debug(msg, e);
            throw new RepositoryException(msg, e);
        }
        return serverVersion;
    }

    public List<String> getCompatibleServerVersions()
    {
        return FedoraClient.getCompatibleServerVersions();
    }

    public String getUploadURL() throws RepositoryException
    {
        String uploadURL = null;
        try
        {
            uploadURL = getFedoraClient().getUploadURL();
        }
        catch (final IOException e)
        {
            final String msg = "Unable to get URL for upload: ";
            logger.debug(msg, e);
            throw new RepositoryException(msg, e);
        }
        return uploadURL;
    }

    public String upload(File file) throws RepositoryException
    {
        String tempURL = null;
        try
        {
            tempURL = uploadFileWithRetry(file);
            if (logger.isDebugEnabled())
            {
                logger.debug("Uploaded '" + file.getName() + "' as " + tempURL);
            }
        }
        catch (final IOException e)
        {
            final String msg = "Unable to upload a file: ";
            logger.debug(msg, e);
            throw new RepositoryException(msg, e);
        }
        return tempURL;
    }

    public String upload(InputStream inStream) throws RepositoryException, IOException
    {
        String tempURLString = null;
        File tempFile = null;
        FileOutputStream outStream = null;
        try
        {
            tempFile = File.createTempFile(FEDORA_UPLOAD, null);
            outStream = new FileOutputStream(tempFile);
            StreamUtility.pipeStream(inStream, outStream, 8192);
            tempURLString = upload(tempFile);
        }
        catch (IOException e)
        {
            throw new RepositoryException(e);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
            if (outStream != null)
            {
                outStream.close();
            }
            if (tempFile != null && !tempFile.delete())
            {
                logger.warn("WARNING: Could not remove temporary file: " + tempFile.getName());
                tempFile.deleteOnExit();
            }
        }
        return tempURLString;
    }

    public String upload(XMLBean xmlBean) throws RepositoryException
    {
        String tempURLString = null;
        File tempFile = null;
        try
        {
            tempFile = File.createTempFile(FEDORA_UPLOAD, null);
            xmlBean.serializeTo(tempFile);
            tempURLString = upload(tempFile);
        }
        catch (IOException e)
        {
            throw new RepositoryException(e);
        }
        catch (XMLSerializationException e)
        {
            throw new ObjectSerializationException(e);
        }
        finally
        {
            if (!tempFile.delete())
            {
                logger.warn("WARNING: Could not remove temporary file: " + tempFile.getName());
                tempFile.deleteOnExit();
            }
        }
        return tempURLString;
    }

    public DateTime getLastModified(String sid) throws RepositoryException
    {
        return getLastModified(sid, null);
    }

    public DateTime getLastModified(String sid, String streamId) throws RepositoryException
    {
        return getLastModifiedDateTime(FedoraClient.FEDORA_URI_PREFIX + sid + (streamId == null ? "" : "/" + streamId));
    }

    public DateTime getLastModifiedDateTime(String locator) throws RepositoryException
    {
        DateTime lastModifiedDate = null;
        try
        {
            Date date = getFedoraClient().getLastModifiedDate(locator);
            logger.debug("Last modification time for " + locator + " is " + date);
            if (date != null)
            {
                lastModifiedDate = new DateTime(date.getTime());
            }
        }
        catch (IOException e)
        {
            // Fedora gets it from the db but Fedora is slow
            final String msg = "Unable to get date of last modification for locator " + locator;
            logger.warn(msg, e);
        }
        return lastModifiedDate;
    }

    private String uploadFileWithRetry(File file) throws RepositoryException, IOException
    {
        String tempURL = null;
        boolean uploaded = false;
        int tryCount = 0;
        while (!uploaded)
        {
            tryCount++;
            try
            {
                if (!file.exists())
                {
                    throw new FileNotFoundException("File does not exist: " + file.getPath());
                }
                tempURL = getFedoraClient().uploadFile(file);
                uploaded = true;
            }
            catch (IOException e)
            {
                logger.warn("Caught IOException while uploading a file. name=" + file.getName() + " tryCount=" + tryCount + " message=" + e.getMessage());
                if (tryCount >= getMaxRetryCount())
                {
                    throw (e);
                }
                Wait.seconds(getRetryTimeOutSeconds());
            }
        }
        return tempURL;
    }

    protected static void mapRemoteException(final String msg, final java.rmi.RemoteException re) throws RemoteException
    {
        final String message = re.getMessage();
        if (message.startsWith("fedora.server.errors.ObjectNotInLowlevelStorageException"))
        {
            throw new ObjectNotInStoreException(msg, re);
        }
        else if (message.startsWith("fedora.server.errors.ObjectExistsException"))
        {
            throw new ObjectExistsException(msg, re);
        }
        else if (message.startsWith("fedora.server.errors.StorageDeviceException"))
        {
            throw new StorageDeviceException(msg, re);
        }
        // fedora 3.5
        else if (message.startsWith("org.fcrepo.server.errors.ObjectNotInLowlevelStorageException"))
        {
            throw new ObjectNotInStoreException(msg, re);
        }
        else if (message.startsWith("org.fcrepo.server.errors.ObjectExistsException"))
        {
            throw new ObjectExistsException(msg, re);
        }
        else if (message.startsWith("org.fcrepo.server.errors.StorageDeviceException"))
        {
            throw new StorageDeviceException(msg, re);
        }

        else
        {
            throw new RemoteException(msg, re);
        }
    }

}
