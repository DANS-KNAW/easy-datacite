package nl.knaw.dans.easy.web.template.emd.atomic;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.unzip.UnzipPostProcess;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.DepositPage;
import nl.knaw.dans.easy.web.deposit.TransformPakbonPostProcess;
import nl.knaw.dans.easy.web.fileexplorer.FileExplorer;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.upload.postprocess.ingest.IngestPostProcess;
import nl.knaw.dans.easy.web.view.dataset.UploadFileMetadataProcess;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositUploadPakbonPanel extends AbstractDatasetModelPanel
{
    private static final long serialVersionUID = -1386346983739904268L;

    private static final Logger logger = LoggerFactory.getLogger(DepositUploadPakbonPanel.class);

    private MarkupContainer uploadPanelHolder;

    /**
     * Constructor.
     * 
     * @param id
     *        Wicket id
     * @param dataset
     *        the dataset that is to be the container of uploaded files and folders
     */
    public DepositUploadPakbonPanel(final String id, final DatasetModel model)
    {
        super(id, model);

        model.setDynamicReload(true);

        EasyUploadConfig uploadConfig = new EasyUploadConfig();
        EasyUpload easyUpload = new EasyUpload("uploadPanel", uploadConfig)
        {
            private static final long serialVersionUID = 0L;

            @Override
            public IUploadPostProcess createPostProcess(Class<? extends IUploadPostProcess> pclass)
            {

                IUploadPostProcess rtn = super.createPostProcess(pclass);

                if (rtn instanceof TransformPakbonPostProcess)
                {
                    ((TransformPakbonPostProcess) rtn).setModel(model);
                }

                return rtn;
            }
        };

        // register the post processes (order is important, because it is kept!)
        easyUpload.registerPostProcess(TransformPakbonPostProcess.class);

        add(easyUpload);

        uploadPanelHolder = new WebMarkupContainer("depositUploadPanelbuttonsPanel");

        int width = 600;
        try
        {
            width = Integer.parseInt(new StringResourceModel("popup.width", this, null).getString());
        }
        catch (NumberFormatException nfe)
        {
        }

        final ModalWindow popup = new ModalWindow("popup");
        popup.setUseInitialHeight(false);
        popup.setInitialWidth(width);
        popup.setTitle("Files");
        popup.add(CSSPackageResource.getHeaderContribution(FileExplorer.class, "style/modal.css"));
        uploadPanelHolder.add(popup);
        this.add(uploadPanelHolder);
   }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();

       if (hasDirectoriesOrFiles())
        {
            uploadPanelHolder.add(new SimpleAttributeModifier("style", "display: block"));
        }
        else
        {
            uploadPanelHolder.add(new SimpleAttributeModifier("style", "display: none"));
        }
    }

    private boolean hasDirectoriesOrFiles()
    {
        DmoStoreId datasetId = getDataset().getDmoStoreId();
        try
        {
            return Services.getItemService().hasChildItems(datasetId);
        }
        catch (ServiceException e)
        {
            logger.error("Error while trying to determine if dataset " + datasetId + " has child items.", e);
            throw new InternalWebError();
        }
    }

}
