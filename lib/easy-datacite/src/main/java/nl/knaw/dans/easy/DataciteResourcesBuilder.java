package nl.knaw.dans.easy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;
import nl.knaw.dans.pf.language.xml.transform.XMLTransformer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class DataciteResourcesBuilder {

    private static final String RESOURCES_FORMAT = "<resources>\n%s\n</resources>";
    private static final String DOI_DATA_FORMAT = " <DOIdata>\n  <DOI>%s</DOI>\n  <URL>%s</URL>\n  <metadata>\n   %s\n   </metadata>\n </DOIdata>";

    private static final String EXCEPTION_MESSAGE_FORMAT = "EMD to DataCite transformation failed. %s, DOI = %s, reason : %s";
    private static final String MISSING_STYLESHEET = "Stylesheet not found on classpath: '%s'";

    private static Logger logger = LoggerFactory.getLogger(DataciteResourcesBuilder.class);

    private final URL styleSheetURL;
    private final URL datasetResolver;

    /**
     * @param xslEmdToDatacite
     *        location on the class path of the XSL that transforms an EMD to a DataCite resource
     */
    public DataciteResourcesBuilder(String xslEmdToDatacite, URL datasetResolver) {
        this.datasetResolver = datasetResolver;
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
        for (EasyMetadata emd : emds) {
            sb.append(createDoiData(emd));
        }
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
            String url = datasetResolver + (datasetResolver.getPath().endsWith("/") ? "" : "/") + emd.getEmdIdentifier().getDatasetId();
            String dataciteMetadata = transform(toSource(emd));
            return String.format(DOI_DATA_FORMAT, doi, url, dataciteMetadata);
        }
        catch (XMLSerializationException e) {
            throw createServiceException(emd.getEmdIdentifier().getDatasetId(), doi, e);
        }
        catch (IOException e) {
            throw createServiceException(emd.getEmdIdentifier().getDatasetId(), doi, e);
        }
        catch (TransformerException e) {
            throw createServiceException(emd.getEmdIdentifier().getDatasetId(), doi, e);
        }
    }

    private Source toSource(EasyMetadata emd) throws UnsupportedEncodingException, XMLSerializationException {
        Document prunedEmd = new DocumentPruner(new EmdMarshaller(emd).getW3cDomDocument()).prune();
        return new DOMSource(prunedEmd);
    }

    private DataciteServiceException createServiceException(String fedoraID, String doi, Exception e) {
        String message = String.format(EXCEPTION_MESSAGE_FORMAT, fedoraID, doi, e.getMessage());
        return new DataciteServiceException(message, e);
    }

    private String transform(Source source) throws UnsupportedEncodingException, TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        new XMLTransformer(styleSheetURL, XMLTransformer.TF_SAXON).transform(source, result);
        return new String(out.toByteArray(), "UTF-8");
    }
}
