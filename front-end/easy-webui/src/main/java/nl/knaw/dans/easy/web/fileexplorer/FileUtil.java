package nl.knaw.dans.easy.web.fileexplorer;

import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.view.dataset.AdditionalLicenseResource;
import nl.knaw.dans.easy.web.view.dataset.UnitMetaDataResource;

import org.apache.wicket.WicketRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    // gets additional license from DatasetService
    public static UnitMetaDataResource getAdditionalLicenseResource(DatasetModel datasetModel) {
        UnitMetadata additionalLicense = null;
        try {
            additionalLicense = Services.getDatasetService().getAdditionalLicense(datasetModel.getObject());
            if (additionalLicense == null)
                return null;
            return new AdditionalLicenseResource(datasetModel, additionalLicense);
        }
        catch (final ServiceException e) {
            final String message = "problem with additional license for download notice";
            logger.error(message, e);
            throw new WicketRuntimeException(message, e);
        }
    }
}
