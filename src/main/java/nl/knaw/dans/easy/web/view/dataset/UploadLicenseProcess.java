/**
 * 
 */
package nl.knaw.dans.easy.web.view.dataset;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

public class UploadLicenseProcess extends UploadSingelFilePostProcess implements IUploadPostProcess
{

    void processUploadedFile(final File file, final Dataset dataset) throws UploadPostProcessException
    {
        dataset.setAdditionalLicenseContent(file);
        try
        {
            Services.getDatasetService().saveAdditionalLicense(EasySession.get().getUser(), dataset);
        }
        catch (final ServiceException e)
        {
            throw new UploadPostProcessException(e);
        }
        catch (DataIntegrityException e)
        {
            throw new UploadPostProcessException(e);
        }
    }

    @Override
    public boolean needsProcessing(final List<File> files)
    {
        // TODO to allow other types, make private in AdditionalLicenseUnit:
        // UNIT_LABEL, MIME_TYPE
        //return needsProcessing(files, ".pdf");
        return files != null && files.size() > 0;
    }
}
