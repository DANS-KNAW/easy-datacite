package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.web.common.DatasetModel;

import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A resource that provides export for metadata of datasets. See the PARAMs for expected parameters. Usage:
 * http://domain/resources/easy/export?sid=easy-dataset:1&format=(xml|csv) (defaults to csv) added to {@link nl.knaw.dans.easy.web.ResourceBookmark} to be
 * mounted in {@link nl.knaw.dans.easy.web.EasyWicketApplication}
 * 
 * @author lindar
 */
public class MetadataExportResource extends DynamicWebResource {
    // CONSTANTS
    protected static final String DATASET_ID_PARAM = "sid";
    protected static final String EXPORT_FORMAT_PARAM = "format";

    enum ExportFormat {
        XML, CSV
    }

    public static final String RESOURCE_NAME = "export";

    private static final Logger logger = LoggerFactory.getLogger(MetadataExportResource.class);

    // MEMBERS
    private DatasetModel getDatasetModel(String datasetid) {
        DatasetModel dm;
        try {
            dm = new DatasetModel(datasetid);
        }
        catch (ObjectNotAvailableException e) {
            logger.error("Object not found: ", e);
            throw new InternalWebError();
        }
        catch (ServiceException e) {
            logger.error("Unable to load model object: ", e);
            throw new InternalWebError();
        }
        return dm;
    }

    @Override
    protected ResourceState getResourceState() {
        return new MetadataExportResource.ResourceState() {
            @Override
            public byte[] getData() {
                if (!getParameters().containsKey(DATASET_ID_PARAM)) {
                    throw new IllegalArgumentException("dataset id (" + DATASET_ID_PARAM + ") cannot be blank");
                }

                String dataset_id = getParameters().getString(DATASET_ID_PARAM);
                setCacheable(false);

                DatasetModel datasetModel = getDatasetModel(dataset_id);
                if (!getParameters().containsKey(EXPORT_FORMAT_PARAM)) {
                    logger.warn("no export format given. defaulting to csv");
                }
                if (ExportFormat.XML.name().equalsIgnoreCase(getParameters().getString(EXPORT_FORMAT_PARAM))) {
                    CharSequence xml;
                    try {
                        xml = new EmdMarshaller(datasetModel.getObject().getEasyMetadata()).getXmlString();
                        return xml.toString().getBytes();
                    }
                    catch (XMLSerializationException e) {
                        logger.error(e.getMessage());
                    }
                }

                return datasetModel.getObject().getEasyMetadata().toString(";").getBytes();

            }

            @Override
            public String getContentType() {
                if (ExportFormat.XML.name().equalsIgnoreCase(getParameters().getString(EXPORT_FORMAT_PARAM))) {
                    return "text/xml";
                }
                return "text/csv";
            }

        };
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);

        String datasetId = getParameters().getString(DATASET_ID_PARAM);
        String link = "<https://easy.dans.knaw.nl/ui/datasets/id/" + datasetId + "> ; rel=\"describes\"";
        response.setHeader("Link", link);

        String extension = getParameters().containsKey(EXPORT_FORMAT_PARAM) ? getParameters().getString(EXPORT_FORMAT_PARAM) : ExportFormat.CSV.name();
        try {
            String ds = URLEncoder.encode(getParameters().getString(DATASET_ID_PARAM), "UTF-8");
            response.setAttachmentHeader(ds + "." + extension.toLowerCase());
        }
        catch (UnsupportedEncodingException e) {
            response.setAttachmentHeader("export." + extension.toLowerCase());
        }
    }

}
