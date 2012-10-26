package nl.knaw.dans.common.wicket.components.upload.command;

import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcess;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcesses;

import org.apache.wicket.markup.html.DynamicWebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo
 * Called by browser when an upload process needs to be canceled. Currently this
 * class does not return anything. For the moment it can safely be assumed canceling
 * the process goes well.
 */
public class EasyUploadCancelCommand extends EasyUploadCommand
{
    /** Log. */
    private static final Logger LOG = LoggerFactory.getLogger(EasyUploadCancelCommand.class);

    private static final long serialVersionUID = 1L;

    public static final String RESOURCE_NAME = "uploadCancel";

    protected ResourceState getResourceState()
    {
        this.setCacheable(false);
        return new UploadCancelResourceState();
    }

    private class UploadCancelResourceState extends DynamicWebResource.ResourceState
    {
        public UploadCancelResourceState()
        {
            try
            {
                Integer[] uploadIds = getUploadProcessIds();//NOPMD;

                // log incoming request
                String ids = "";
                for (int i = 0; i < uploadIds.length; i++)
                    ids += uploadIds[i].toString() + ", ";
                LOG.info("Upload CANCEL request for upload process ids: " + ids);

                // create JSON response
                for (int i = 0; i < uploadIds.length; i++)
                {
                    EasyUploadProcess process = EasyUploadProcesses.getInstance().getUploadProcessById(uploadIds[i]);
                    if (process != null)
                    {
                        process.cancel();
                        EasyUploadProcesses.getInstance().unregister(process);
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("Caught exception while canceling upload process", e);
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
            return 0;
        }

        /**
         * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getData()
         */
        public byte[] getData()
        {
            return "".getBytes();
        }
    }

}
