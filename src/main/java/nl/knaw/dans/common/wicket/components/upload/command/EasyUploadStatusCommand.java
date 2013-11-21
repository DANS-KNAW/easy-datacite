package nl.knaw.dans.common.wicket.components.upload.command;

import java.util.Arrays;

import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcess;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcesses;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadStatus;

import org.apache.wicket.markup.html.DynamicWebResource;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo This dynamic web resource is being called by the Ajax polling mechanism of the browser
 *         that request an update on the status of one or more uploads. The return value is serialized in
 *         JSON.
 */
@SuppressWarnings("serial")
public class EasyUploadStatusCommand extends EasyUploadCommand
{
    public static final String RESOURCE_NAME = "uploadStatus";
    private static final Logger log = LoggerFactory.getLogger(EasyUploadStatusCommand.class);

    protected ResourceState getResourceState()
    {
        this.setCacheable(false);

        return new UploadStatusResourceState();
    }

    private class UploadStatusResourceState extends DynamicWebResource.ResourceState
    {
        private JSONArray responseWriter = new JSONArray();

        public UploadStatusResourceState()
        {
            try
            {
                Integer[] uploadIds = getUploadProcessIds();
                log.debug("Creating new upload status object for the following upload ID(s): '{}'", Arrays.toString(uploadIds));
                for (int i = 0; i < uploadIds.length; i++)
                {
                    EasyUploadProcess process = EasyUploadProcesses.getInstance().getUploadProcessById(uploadIds[i]);
                    if (process != null)
                    {
                        EasyUploadStatus status = process.getStatus();
                        log.debug("Status of upload process '{}' is: '{}'", uploadIds[i], status);
                        responseWriter.put(status.toJSONObject());
                        if (status.isFinished())
                        {
                            log.debug("Unregister upload process: '{}'", uploadIds[i]);
                            EasyUploadProcesses.getInstance().unregister(process);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                responseWriter = new JSONArray();
                EasyUploadStatus us = new EasyUploadStatus(-1, e.getMessage());
                us.setError(true);
                responseWriter.put(us.toJSONObject());
            }
        }

        /**
         * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getContentType()
         */
        public String getContentType()
        {
            return "text/json";
        }

        /**
         * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getLength()
         */
        public int getLength()
        {
            return responseWriter.toString().length();
        }

        /**
         * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getData()
         */
        public byte[] getData()
        {
            return responseWriter.toString().getBytes();
        }
    }

}
