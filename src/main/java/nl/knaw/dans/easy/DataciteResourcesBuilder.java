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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;
import nl.knaw.dans.pf.language.xml.transform.XMLTransformer;

public class DataciteResourcesBuilder {

    class Resources {
        public Resources(String doiResource, String metadataResource) {
            this.doiResource = doiResource;
            this.metadataResource = metadataResource;
        }

        public String doiResource;
        public String metadataResource;
    }

    private static final String DOI_DATA_FORMAT = "doi=%s\nurl=%s";

    private static final String DOI_EXCEPTION_MESSAGE_FORMAT = "DOI to DataCite transformation failed. %s, DOI = %s, reason : %s";
    private static final String METADATA_EXCEPTION_MESSAGE_FORMAT = "EMD to DataCite transformation failed. %s, DOI = %s, reason : %s";
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

    public Resources create(EasyMetadata emd) throws DataciteServiceException {
        validateArguments(emd);
        return new Resources(createDoiData(emd), createMetadata(emd));
    }

    public String getEmd2DataciteXml(EasyMetadata emd) throws DataciteServiceException {
        try {
            return transform(toSource(emd));
        }
        catch (XMLSerializationException e) {
            throw createDoiServiceException(emd.getEmdIdentifier().getDatasetId(), emd.getEmdIdentifier().getDansManagedDoi(), e);
        }
        catch (IOException e) {
            throw createDoiServiceException(emd.getEmdIdentifier().getDatasetId(), emd.getEmdIdentifier().getDansManagedDoi(), e);
        }
        catch (TransformerException e) {
            throw createDoiServiceException(emd.getEmdIdentifier().getDatasetId(), emd.getEmdIdentifier().getDansManagedDoi(), e);
        }
    }

    protected void validateArguments(EasyMetadata emd) {
        if (StringUtils.isBlank(emd.getEmdIdentifier().getDansManagedDoi()))
            throw new IllegalArgumentException("the EMD should have a DOI");
    }

    protected String createDoiData(EasyMetadata emd) {
        String doi = emd.getEmdIdentifier().getDansManagedDoi();
        String uri = datasetResolver + (datasetResolver.getPath().endsWith("/") ? "" : "/") + emd.getEmdIdentifier().getDatasetId();
        return String.format(DOI_DATA_FORMAT, doi, uri);
    }

    protected String createMetadata(EasyMetadata emd) throws DataciteServiceException {
        String doi = emd.getEmdIdentifier().getDansManagedDoi();
        try {
            return transform(toSource(emd));
        }
        catch (UnsupportedEncodingException e) {
            throw createMetadataServiceException(emd.getEmdIdentifier().getDatasetId(), doi, e);
        }
        catch (TransformerException e) {
            throw createMetadataServiceException(emd.getEmdIdentifier().getDatasetId(), doi, e);
        }
        catch (XMLSerializationException e) {
            throw createMetadataServiceException(emd.getEmdIdentifier().getDatasetId(), doi, e);
        }
    }

    private Source toSource(EasyMetadata emd) throws XMLSerializationException {
        Document prunedEmd = new DocumentPruner(new EmdMarshaller(emd).getW3cDomDocument()).prune();
        return new DOMSource(prunedEmd);
    }

    private String transform(Source source) throws UnsupportedEncodingException, TransformerException, DataciteServiceException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Result result = new StreamResult(out);
            new XMLTransformer(styleSheetURL, XMLTransformer.TF_SAXON).transform(source, result);
            return new String(out.toByteArray(), "UTF-8");
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                // should never happen; interface expects exception, but implementation does not throw anything
            }
        }
    }

    private DataciteServiceException createDoiServiceException(String fedoraID, String doi, Exception e) {
        String message = String.format(DOI_EXCEPTION_MESSAGE_FORMAT, fedoraID, doi, e.getMessage());
        return new DataciteServiceException(message, e);
    }

    private DataciteServiceException createMetadataServiceException(String fedoraID, String doi, Exception e) {
        String message = String.format(METADATA_EXCEPTION_MESSAGE_FORMAT, fedoraID, doi, e.getMessage());
        return new DataciteServiceException(message, e);
    }
}
