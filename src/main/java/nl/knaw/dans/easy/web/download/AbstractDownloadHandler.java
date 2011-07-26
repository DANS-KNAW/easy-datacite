/**
 *
 */
package nl.knaw.dans.easy.web.download;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;

import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDownloadHandler implements IResourceStream
{

    private static final long serialVersionUID = -2712566390339071203L;
    private static final Logger logger = LoggerFactory.getLogger(AbstractDownloadHandler.class);

    public Locale getLocale()
    {
        logger.warn("Unexpected call to getLocale()");
        return null;
    }


    public void setLocale(Locale locale)
    {
        logger.warn("Unexpected call to setLocale()");
    }
    
    public abstract void setHeaders(WebResponse response);
    
    protected File getMockFile()
    {
        File mockFile = null;
        try
        {
            mockFile = ResourceLocator.getFile("misc/insufficientRights.html");
        }
        catch (ResourceNotFoundException e)
        {
            throw new ApplicationException(e);
        }
        return mockFile;
    }
    
    protected URL getMockURL()
    {
        URL url = ResourceLocator.getURL("misc/insufficientRights.html");
        return url;
    }

}
