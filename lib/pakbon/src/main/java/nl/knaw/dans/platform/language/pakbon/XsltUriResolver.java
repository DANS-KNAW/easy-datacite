package nl.knaw.dans.platform.language.pakbon;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XsltUriResolver implements URIResolver {

    public static final String CLASS_PATH_DIR = "xslt-files/";
    private static final Logger logger = LoggerFactory.getLogger(XsltUriResolver.class);

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(CLASS_PATH_DIR + href);
            return new StreamSource(inputStream);
        }
        catch (Exception e) {
            logger.error("Could not resolve: href='" + href + " base=" + base, e);
            return null;
        }
    }

}
