package nl.knaw.dans.common.wicket.components.upload.command;

import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcess;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcesses;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadStatus;

import org.apache.wicket.markup.html.DynamicWebResource;
import org.json.JSONArray;

/**
 * @author lobo This dynamic web resource is being called by the Ajax polling mechanism of the browser
 *         that request an update on the status of one or more uploads. The return value is serialized in
 *         JSON.
 */
public class EasyUploadStatusCommand extends EasyUploadCommand
{
    private static final long serialVersionUID = 1L;

    public static final String RESOURCE_NAME = "uploadStatus";

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
                Integer[] uploadIds = getUploadProcessIds();// NOPMD;

                // log incoming request
                String ids = "";
                for (int i = 0; i < uploadIds.length; i++)
                    ids += uploadIds[i].toString() + ", ";
                // LOG.debug("Upload STATUS request for upload process ids: "+ ids);

                // create JSON response
                for (int i = 0; i < uploadIds.length; i++)
                {
                    EasyUploadProcess process = EasyUploadProcesses.getInstance().getUploadProcessById(uploadIds[i]);
                    if (process != null)
                    {
                        EasyUploadStatus status = process.getStatus();
                        responseWriter.put(status.toJSONObject());
                        if (status.isFinished())
                        {
                            // unregister the upload process after the finished result has been send back
                            // to the client.
                            EasyUploadProcesses.getInstance().unregister(process);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                // write error status to new responseWriter
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
            // LOG.debug("Send upload status update = "+ responseWriter.toString());
            return responseWriter.toString().getBytes();
        }
    }

}
