package nl.knaw.dans.easy.web.template.emd.atomic;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.unzip.UnzipPostProcess;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.common.StyledModalWindow;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.upload.postprocess.ingest.IngestPostProcess;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositUploadPanel extends AbstractDatasetModelPanel {
    private static final long serialVersionUID = -1386346983739904268L;

    private static final Logger logger = LoggerFactory.getLogger(DepositUploadPanel.class);

    private MarkupContainer uploadPanelHolder;

    @SpringBean(name = "itemService")
    private ItemService itemService;

    /**
     * Constructor.
     * 
     * @param id
     *        Wicket id
     * @param dataset
     *        the dataset that is to be the container of uploaded files and folders
     */
    public DepositUploadPanel(final String id, final DatasetModel model) {
        super(id, model);

        model.setDynamicReload(true);

        EasyUploadConfig uploadConfig = new EasyUploadConfig();
        EasyUpload easyUpload = new EasyUpload("uploadPanel", uploadConfig);
        easyUpload.registerPostProcess(new UnzipPostProcess());
        IngestPostProcess ipp = new IngestPostProcess();
        ipp.setModel(model);
        easyUpload.registerPostProcess(ipp);
        add(easyUpload);
        uploadPanelHolder = new WebMarkupContainer("depositUploadPanelbuttonsPanel");

        int width = 600;
        try {
            width = Integer.parseInt(new StringResourceModel("popup.width", this, null).getString());
        }
        catch (NumberFormatException nfe) {}

        final ModalWindow popup = new StyledModalWindow("popup", "Files", width);
        uploadPanelHolder.add(popup);

        AjaxLink<Void> showFilesLink = new AjaxLink<Void>("popupLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                popup.setContent(new ShowFilesPanel(popup, model));
                popup.show(target);
            }

        };
        uploadPanelHolder.add(showFilesLink);

        AjaxLink<Void> deleteFilesLink = new AjaxLink<Void>("deleteFilesLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                popup.setContent(new DeleteFilesPanel(popup, model));
                popup.show(target);
            }

        };
        uploadPanelHolder.add(deleteFilesLink);

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
            return itemService.hasChildItems(datasetId);
        }
        catch (ServiceException e) {
            logger.error("Error while trying to determine if dataset " + datasetId + " has child items.", e);
            throw new InternalWebError();
        }
    }

}
