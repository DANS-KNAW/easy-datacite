package nl.knaw.dans.easy.web.view.dataset;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.business.md.amd.ReplaceAdditionalMetadataStrategy;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

public class UploadFileMetadataProcess extends UploadSingleFilePostProcess
{
    private UploadStatus status = new UploadStatus("Processing file metadata");

    @Override
    public boolean needsProcessing(final List<File> files)
    {
        return needsProcessing(files, ".xml");
    }

    @Override
    void processUploadedFile(final File file, final Dataset dataset) throws UploadPostProcessException
    {
        AdditionalMetadataUpdateStrategy strategy = new ReplaceAdditionalMetadataStrategy();
        EasyUser sessionUser = EasySession.getSessionUser();
        try
        {
            Services.getItemService().updateFileItemMetadata(sessionUser, dataset, file, strategy);
        }
        catch (final ServiceException e)
        {
            throw new UploadPostProcessException(e);
        }
    }

    @Override
    public UploadStatus getStatus()
    {
        return status;
    }
}
