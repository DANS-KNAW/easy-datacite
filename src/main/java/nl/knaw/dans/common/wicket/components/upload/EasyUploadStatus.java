package nl.knaw.dans.common.wicket.components.upload;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo This class contains the information concerning the upload process. It is being held by an
 *         UploadProcess class.
 */
@SuppressWarnings("serial")
public class EasyUploadStatus extends UploadStatus
{
    /** Log. */
    private static final Logger LOG = LoggerFactory.getLogger(EasyUploadStatus.class);

    private Integer uploadId;

    public EasyUploadStatus(Integer uploadId, String message)
    {
        super(message);
        this.uploadId = uploadId;
    }

    public JSONObject toJSONObject()
    {
        JSONObject jobj = new JSONObject();
        try
        {
            LOG.debug("response: {}\n{}", getMessage(), Arrays.toString(getMessage().toCharArray()));
            try
            {
                String encoded = URLEncoder.encode(getMessage(), "UTF-8");
                LOG.debug("encoded: {}", encoded);
                LOG.debug("en/de-coded: {}", URLDecoder.decode(encoded, "UTF-8"));
                jobj.put("message", encoded);
            }
            catch (UnsupportedEncodingException e)
            {
                jobj.put("message", getMessage());
            }
            jobj.put("uploadId", uploadId);
            jobj.put("error", isError());
            jobj.put("finished", isFinished());
            jobj.put("percentComplete", getPercentComplete());
        }
        catch (JSONException e)
        {
            // TODO: send exception to general exception handler
            LOG.error("Caught error while serializing UploadStatus object to JSON.", e);
            return jobj;
        }
        return jobj;
    }

    public Integer getUploadId()
    {
        return uploadId;
    }

    @Override
    public String toString()
    {
        return "JSON = " + toJSONObject().toString();
    }
}
