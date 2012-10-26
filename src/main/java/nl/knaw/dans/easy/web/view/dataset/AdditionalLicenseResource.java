package nl.knaw.dans.easy.web.view.dataset;

import java.net.URL;

import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;

public class AdditionalLicenseResource extends UnitMetaDataResource
{
    private static final long serialVersionUID = 1L;

    public AdditionalLicenseResource(DatasetModel datasetModel, UnitMetadata unitMetaData)
    {
        super(datasetModel, unitMetaData);
    }

    @Override
    protected URL getURL() throws ServiceException, CommonSecurityException
    {
        URL url = Services.getDatasetService().getAdditionalLicenseURL(this.getDatasetModel().getObject());
        return url;
    }

}
