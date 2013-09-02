package nl.knaw.dans.common.wicket.components.upload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.file.UnzipUtil;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.upload.FileItem;
import org.apache.wicket.util.upload.FileUploadBase.SizeLimitExceededException;
import org.apache.wicket.util.upload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This WebRequest object is used to intercept multipart request that are meant for the UploadPanel It
 * only invokes itself (class: UploadRequest) when an uploadId was send via the URL (GET method) if not
 * then it uses the original web request class that could be useful for traditional Wicket file upload.
 * <p>
 * This request object is necessary for the UploadPanel to function properly. It is installed like so:
 * 
 * <pre>
 * class MyApplication extends WebApplication {
 * ...
 *     @Override
 *     protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
 *         return new UploadWebRequest(servletRequest);
 *     }
 * ...
 * }
 * </pre>
 * 
 * @author lobo
 */
public class EasyUploadWebRequest extends ServletWebRequest
{
    private final HttpServletRequest request;

    private static final Logger LOG = LoggerFactory.getLogger(EasyUploadWebRequest.class);

    private static String baseUrl;

    private Integer uploadId = -1;

    private String filename = "";

    private EasyUploadProcess uploadProcess = null;

    private EasyUploadIFrame.UploadForm uploadForm;

    public EasyUploadWebRequest(final HttpServletRequest request)
    {
        super(request);
        this.request = request;
        if (baseUrl == null || baseUrl.equals(""))
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private WebRequest getDefaultMultipartWebRequest(Bytes maxsize) throws FileUploadException
    {
        return new MultipartServletWebRequest(request, maxsize);
    }

    /**
     * This method starts the upload process. To handle the tracking of the upload prorgess it starts a
     * new EasyUploadProcess. This process can be polled using its unique uploadId.
     * 
     * @see org.apache.wicket.protocol.http.WebRequest#newMultipartWebRequest(org.apache.wicket.util.lang.Bytes)
     */
    public WebRequest newMultipartWebRequest(Bytes maxsize)
    {
        try
        {
            uploadForm = getUploadForm();
            if (uploadForm == null)
                return getDefaultMultipartWebRequest(maxsize);

            // It is SAD, but necessary that the filename has to be send
            // as a GET style parameter. This has to do with the fact that
            // the EasyUploadForm is still not updated at the time that the
            // upload is still in progress. Only AFTER the upload has finished
            // are the form model properties updated. This means there is no
            // use in trying to send the filename as a POST property.
            filename = this.getParameter("filename");
            try
            {
                uploadId = Integer.parseInt(this.getParameter("uploadId"));
            }
            catch (NumberFormatException e)
            {
                uploadId = -1;
            }

            if (uploadId < 0 || filename.equals(""))
                return getDefaultMultipartWebRequest(maxsize);

            // get the clientParams
            Map<String, String[]> params = this.getParameterMap();
            HashMap<String, String> clientParams = new HashMap<String, String>();
            for (String key : params.keySet())
            {
                if (key.equals("filename") || key.equals("uploadId"))
                    continue;
                String[] val = params.get(key);
                if (val.length > 0)
                    clientParams.put(key, val[0]);
            }

            LOG.info("Received upload for file " + filename + ", now starting new EasyUploadProcess for uploadId " + uploadId.toString());

            uploadProcess = new EasyUploadProcess(uploadId, filename, clientParams, uploadForm.getEasyUpload());
            EasyUploadProcesses.getInstance().register(uploadProcess);

            return new EasyUploadRequest(request, maxsize);
        }
        catch (FileUploadException e)
        {
            if (uploadProcess != null)
            {
                String message = "Upload failed: " + e.getLocalizedMessage();
                if (e instanceof SizeLimitExceededException)
                {
                    String filename = EasyUploadRequest.getFilename();
                    final String defaultValue = "Upload of '" + filename + "'failed: it is larger than " + maxsize + ".";
                    message = defaultValue;
                    LOG.info(message);
                }
                else
                    LOG.error(message, e);
                uploadProcess.getStatus().setError(message);
            }

            throw new WicketRuntimeException(e);
        }
    }

    /**
     * @return the baseUrl
     */
    public static String getBaseUrl()
    {
        return baseUrl;
    }

    public EasyUploadIFrame.UploadForm getUploadForm()
    {
        Component comp = this.getPage().get(EasyUploadIFrame.UPLOAD_FORM_ID);
        if (comp instanceof EasyUploadIFrame.UploadForm)
            return ((EasyUploadIFrame.UploadForm) comp);
        else
            return null;
    }

    public Integer getUploadId()
    {
        return uploadId;
    }

    public String getFilename()
    {
        return filename;
    }

}

class EasyUploadRequest extends MultipartServletWebRequest
{
    /** Log. */
    private static final Logger LOG = LoggerFactory.getLogger(UnzipUtil.class);

    public EasyUploadRequest(HttpServletRequest request, Bytes maxSize) throws FileUploadException
    {
        super(request, maxSize);
        onUploadCompleted2();
    }

    /* the following three methods are static for a good reason */

    protected static EasyUploadWebRequest getEasyUploadWebRequest()
    {
        WebRequest request = ((WebRequest) RequestCycle.get().getRequest());
        if (request instanceof EasyUploadWebRequest)
            return ((EasyUploadWebRequest) request);
        else
        {
            LOG.error("Error! This should never happen. The EasyUploadRequest is not part of a EasyUploadWebRequest?!");
            return null;
        }
    }

    public static Integer getUploadId()
    {
        EasyUploadWebRequest request = getEasyUploadWebRequest();
        if (request != null)
            return request.getUploadId();
        else
            return -1;
    }

    public static String getFilename()
    {
        EasyUploadWebRequest request = getEasyUploadWebRequest();
        if (request != null)
            return request.getFilename();
        else
            return "";
    }

    public static EasyUploadProcess getUploadProcess()
    {
        return EasyUploadProcesses.getInstance().getUploadProcessById(getUploadId());
    }

    /**
     * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#wantUploadProgressUpdates()
     */
    protected boolean wantUploadProgressUpdates()
    {
        return true;
    }

    /**
     * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#onUploadStarted(int)
     */
    protected void onUploadStarted(int totalBytes)
    {
        EasyUploadProcess uploadProcess = getUploadProcess();
        if (uploadProcess != null)
            uploadProcess.onUploadStarted(totalBytes);
    }

    /**
     * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#onUploadUpdate(int, int)
     */
    protected void onUploadUpdate(int bytesUploaded, int total)
    {
        EasyUploadProcess uploadProcess = getUploadProcess();
        if (uploadProcess != null)
            uploadProcess.onUploadUpdate(bytesUploaded, total);
    }

    /**
     * This function is not used, since at this point one cannot retrieve the list of files yet. Instead
     * onUploadCompleted2 does the job.
     * 
     * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#onUploadCompleted()
     */
    protected void onUploadCompleted()
    {
    }

    protected void onUploadCompleted2()
    {
        EasyUploadProcess uploadProcess = getUploadProcess();

        if (uploadProcess == null)
            return;

        Iterator it = this.getFiles().values().iterator();
        if (!it.hasNext())
        {
            LOG.error("After uploading no files seem to have been received.");
            uploadProcess.getStatus().setError("No files received");
        }

        FileItem file = (FileItem) it.next();
        if (file == null)
        {
            LOG.error("Got null on received file");
            uploadProcess.getStatus().setError("Unable to access file on server.");
        }

        uploadProcess.onUploadCompleted(file);
    }
}
