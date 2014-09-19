package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteDatasetPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DeleteDatasetPanel.class);

    public DeleteDatasetPanel(final ModalWindow window, final DatasetModel datasetModel) {
        super(window.getContentId());

        add(new Label("text", new StringResourceModel("text", this, datasetModel)));

        add(new IndicatingAjaxLink<Void>("yes") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                handleDeleteDataset(datasetModel);

                Page page = EasySession.get().getRedirectPage(getPage().getPageClass());
                if (page != null && page instanceof AbstractEasyPage) {
                    ((AbstractEasyPage) page).refresh();
                }
                if (page != null) {
                    setResponsePage(page);
                }
            }
        });

        add(new IndicatingAjaxLink<Void>("no") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                window.close(target);
            }
        });
    }

    private void handleDeleteDataset(DatasetModel datasetModel) {
        try {
            Services.getDatasetService().deleteDataset(EasySession.getSessionUser(), datasetModel.getObject());
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.ERROR_DELETING_DATASET, datasetModel.getObject().getStoreId());
            logger.error(message, e);
            setResponsePage(ErrorPage.class);
        }
    }

    private String errorMessage(final String messageKey, final String... param) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR, param);
    }
}
