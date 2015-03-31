package nl.knaw.dans.easy;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.DOI_RESOLVER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.transform.TransformerException;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;
import nl.knaw.dans.pf.language.xml.transform.XMLTransformer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataciteResourcesBuilder {

    private static final String RESOURCES_FORMAT = "<resources>\n%s\n</resources>";
    private static final String DOI_DATA_FORMAT = " <DOIdata>\n  <DOI>%s</DOI>\n  <URL>%s</URL>\n  <metadata>\n   %s\n   </metadata>\n </DOIdata>";

    private static final String EXCEPTION_MESSAGE_FORMAT = "Could not create content for DataCite request. DOI = %s, reason : %s";
    private static final String MISSING_STYLESHEET = "Stylesheet not found on classpath: '%s'";

    private static Logger logger = LoggerFactory.getLogger(DataciteResourcesBuilder.class);

    private final URL styleSheetURL;

    /**
     * @param xslEmdToDatacite
     *        location on the class path of the XSL that transforms an EMD to a DataCite resource
     */
    public DataciteResourcesBuilder(String xslEmdToDatacite) {
        styleSheetURL = this.getClass().getClassLoader().getResource(xslEmdToDatacite);
        if (styleSheetURL == null) {
            String message = String.format(MISSING_STYLESHEET, xslEmdToDatacite);
            logger.error(message);
            throw new IllegalStateException(message);
        }
    }

    public String create(EasyMetadata... emds) throws DataciteServiceException {
        validateArguments(emds);
        StringBuffer sb = new StringBuffer();
        for (EasyMetadata emd : emds)
            sb.append(createDoiData(emd));
        return String.format(RESOURCES_FORMAT, sb.toString());
    }

    private void validateArguments(EasyMetadata... emds) {
        if (emds == null || emds.length == 0)
            throw new IllegalArgumentException("expecting at least one EMD");
        for (EasyMetadata emd : emds) {
            if (StringUtils.isBlank(emd.getEmdIdentifier().getDansManagedDoi()))
                throw new IllegalArgumentException("all EMDs should have a DOI");
        }
    }

    private String createDoiData(EasyMetadata emd) throws DataciteServiceException {
        String doi = emd.getEmdIdentifier().getDansManagedDoi();
        try {
            URL doiUrl = new URL(new URL(DOI_RESOLVER), doi);
            String dataciteMetadata = transform(toInputStrem(emd));
            return String.format(DOI_DATA_FORMAT, doi, doiUrl, dataciteMetadata);
        }
        catch (XMLSerializationException e) {
            throw createServiceException(doi, e);
        }
        catch (IOException e) {
            throw createServiceException(doi, e);
        }
        catch (TransformerException e) {
            throw createServiceException(doi, e);
        }
    }

    private ByteArrayInputStream toInputStrem(EasyMetadata emd) throws UnsupportedEncodingException, XMLSerializationException {
        String xmlString = new EmdMarshaller(emd).getXmlString();
        return new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
    }

    private DataciteServiceException createServiceException(String doi, Exception e) {
        String message = String.format(EXCEPTION_MESSAGE_FORMAT, doi, e.getMessage());
        return new DataciteServiceException(message, e);
    }

    private String transform(InputStream inputStream) throws UnsupportedEncodingException, TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new XMLTransformer(styleSheetURL, XMLTransformer.TF_SAXON).transform(inputStream, out);
        return new String(out.toByteArray(), "UTF-8");
    }
}
