package nl.knaw.dans.easy.web.template.emd.atomic;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.common.StyledModalWindow;
import nl.knaw.dans.easy.web.deposit.TransformPakbonPostProcess;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DepositUploadPakbonPanel extends AbstractDatasetModelPanel {
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
    public DepositUploadPakbonPanel(final String id, final DatasetModel model) {
        super(id, model);
        model.setDynamicReload(true);
        EasyUploadConfig uploadConfig = new EasyUploadConfig();
        uploadConfig.setAutoRemoveFiles(true);
        EasyUpload easyUpload = new EasyUpload("uploadPanel", uploadConfig);
        easyUpload.registerPostProcess(new TransformPakbonPostProcess(model));
        add(easyUpload);
        uploadPanelHolder = new WebMarkupContainer("depositUploadPanelbuttonsPanel");

        int width = 600;
        try {
            width = Integer.parseInt(new StringResourceModel("popup.width", this, null).getString());
        }
        catch (NumberFormatException nfe) {}

        final ModalWindow popup = new StyledModalWindow("popup", "Files", width);
        uploadPanelHolder.add(popup);
        this.add(uploadPanelHolder);
        if (hasDirectoriesOrFiles())
            addUploadPanel("display: block");
        else
            addUploadPanel("display: none");
    }

    private void addUploadPanel(String value) {
        uploadPanelHolder.add(new SimpleAttributeModifier("style", value));
    }

    private boolean hasDirectoriesOrFiles() {
        DmoStoreId datasetId = getDataset().getDmoStoreId();
        try {
            return Services.getItemService().hasChildItems(datasetId);
        }
        catch (ServiceException e) {
            logger.error("Error while trying to determine if dataset " + datasetId + " has child items.", e);
            throw new InternalWebError();
        }
    }
}
