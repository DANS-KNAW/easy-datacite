/**
 * 
 */
package nl.knaw.dans.easy.web.view.dataset;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;

@SuppressWarnings("serial")
public class UploadLicenseProcess extends UploadSingleFilePostProcess {
    public UploadLicenseProcess(DatasetModel datasetModel) {
        super(datasetModel);
    }

    protected void processUploadedFile(final File file) throws UploadPostProcessException {
        getDataset().setAdditionalLicenseContent(file);
        try {
            Services.getDatasetService().saveAdditionalLicense(EasySession.get().getUser(), getDataset());
        }
        catch (final ServiceException e) {
            throw new UploadPostProcessException(e);
        }
        catch (DataIntegrityException e) {
            throw new UploadPostProcessException(e);
        }
    }

    @Override
    public boolean needsProcessing(final List<File> files) {
        // TODO to allow other types, make private in AdditionalLicenseUnit:
        // UNIT_LABEL, MIME_TYPE
        // return needsProcessing(files, ".pdf");
        return files != null && files.size() > 0;
    }
}
