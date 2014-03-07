package nl.knaw.dans.common.wicket.components.upload.command;

import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * @author lobo
 */
public abstract class EasyUploadCommand extends DynamicWebResource
{
    private static final long serialVersionUID = 164170720396174821L;

    public static final String UPLOADID_ARGUMENT = "uploadId";

    public Integer[] getUploadProcessIds()
    {
        RequestCycle rc = RequestCycle.get();

        // find uploadIds argument
        HttpServletRequest req = ((WebRequest) rc.getRequest()).getHttpServletRequest();
        String[] arg = req.getParameterValues(UPLOADID_ARGUMENT);
        if (arg == null || (arg != null && arg.length == 0))
            throw new MissingResourceException("Missing argument", UPLOADID_ARGUMENT, UPLOADID_ARGUMENT);

        // get argument
        String[] strUploadIds = arg;
        if (strUploadIds.length == 0)
            throw new MissingResourceException("Missing argument value", UPLOADID_ARGUMENT, UPLOADID_ARGUMENT);
        Integer[] uploadIds = new Integer[strUploadIds.length];
        for (int i = 0; i < uploadIds.length; i++)
        {
            uploadIds[i] = Integer.parseInt(strUploadIds[i]);
        }
        return uploadIds;
    }
}
