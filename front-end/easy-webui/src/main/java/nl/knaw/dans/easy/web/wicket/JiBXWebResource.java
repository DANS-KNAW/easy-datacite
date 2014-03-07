package nl.knaw.dans.easy.web.wicket;

import nl.knaw.dans.common.lang.xml.MinimalXMLBean;
import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A WebResource for streaming {@link MinimalXMLBean}s.
 * 
 * @author ecco May 5, 2009
 */
public class JiBXWebResource extends WebResource
{

    public static final int INDENT = 4;
    public static final String CONTENT_TYPE = "text/xml";
    public static final String EXTENSION = ".xml";

    private static final long serialVersionUID = 1649064461407113842L;
    private static final Logger logger = LoggerFactory.getLogger(JiBXWebResource.class);

    private final XMLBean xmlBean;
    private final String title;

    /**
     * Constructor.
     * 
     * @param xmlBean
     *        the JiBXObject to stream
     * @param title
     *        title for the resource, without extension
     */
    public JiBXWebResource(XMLBean xmlBean, String title)
    {
        this.xmlBean = xmlBean;
        this.title = title;
        setCacheable(false);
    }

    @Override
    public IResourceStream getResourceStream()
    {
        StringResourceStream resourceStream = null;
        try
        {
            String xml = xmlBean.asXMLString(INDENT);
            resourceStream = new StringResourceStream(xml, CONTENT_TYPE);
        }
        catch (XMLSerializationException e)
        {
            logger.error("Could not stream " + xmlBean.getClass() + ": ", e);
            String errorMessage = "<error>" + e.toString() + "</error>";
            resourceStream = new StringResourceStream(errorMessage, CONTENT_TYPE);
        }

        return resourceStream;
    }

    @Override
    protected void setHeaders(WebResponse response)
    {
        super.setHeaders(response);
        response.setAttachmentHeader(title + EXTENSION);
    }

}
