package nl.knaw.dans.platform.language.pakbon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.TransformerException;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLDeserializationException;
import nl.knaw.dans.pf.language.xml.transform.XMLTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pakbon2EmdTransformer
{

    private static final Logger logger = LoggerFactory.getLogger(Pakbon2EmdTransformer.class);

    private static final String XSL_PAKBON2EMD = "xslt-files/pakbon2emd.xslt";

    public byte[] transform(String pakbonXml) throws TransformerException
    {
        XMLTransformer transformer = getTransformer();
        ByteArrayInputStream in = new ByteArrayInputStream(pakbonXml.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transform(in, out);
        return out.toByteArray();
    }

    public byte[] transform(InputStream pakbonByteStream) throws TransformerException
    {
        XMLTransformer transformer = getTransformer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transform(pakbonByteStream, out);
        return out.toByteArray();
    }

    public String transformToString(String pakbonXml) throws TransformerException
    {
        return new String(transform(pakbonXml));
    }

    public EasyMetadata transformToEmd(String pakbonXml) throws XMLDeserializationException, TransformerException
    {
        EmdUnmarshaller<EasyMetadata> um = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class);
        EasyMetadata emd = um.unmarshal(transform(pakbonXml));
        return emd;
    }

    private XMLTransformer getTransformer() throws TransformerException
    {
        // Xalan: "Branch target offset too large for short". Milco's style of writing stylesheets too
        // much for Xalan...
        XMLTransformer transformer = new XMLTransformer(getStylesheet(), XMLTransformer.TF_SAXON);
        transformer.getTransformerFactory().setURIResolver(new XsltUriResolver());
        logger.info("UriResolver added for resources at class path " + XsltUriResolver.CLASS_PATH_DIR);
        return transformer;
    }

    private URL getStylesheet()
    {
        URL url = this.getClass().getClassLoader().getResource(XSL_PAKBON2EMD);
        if (url == null)
        {
            String msg = "Missing stylesheet on classpath '" + XSL_PAKBON2EMD + "'";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        return url;
    }

}
