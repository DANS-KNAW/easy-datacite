package nl.knaw.dans.platform.language.pakbon;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.common.lang.ResourceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XsltUriResolver implements URIResolver {

    private static final String XSL_PAKBON2EMD_PATH_DIR = "/res/xslt/pakbon";
    private static final Logger logger = LoggerFactory.getLogger(XsltUriResolver.class);

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            return new StreamSource(ResourceLocator.getFile(XSL_PAKBON2EMD_PATH_DIR + "/" + href));
        }
        catch (Exception e) {
            logger.error("Could not resolve: href='" + href + " base=" + base, e);
            return null;
        }
    }

}
