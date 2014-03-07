package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreDatasetPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(RestoreDatasetPanel.class);

    public RestoreDatasetPanel(final ModalWindow window, final DatasetModel datasetModel)
    {
        super(window.getContentId());

        add(new Label("text", new StringResourceModel("text", this, datasetModel)));

        add(new IndicatingAjaxLink<Void>("yes")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                handleRestoreDataset(datasetModel);
                window.close(target);
            }
        });

        add(new IndicatingAjaxLink<Void>("no")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }
        });
    }

    protected void handleRestoreDataset(DatasetModel datasetModel)
    {
        try
        {
            Services.getDatasetService().restoreDataset(EasySession.getSessionUser(), datasetModel.getObject());
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.ERROR_RESTORING_DATASET, datasetModel.getObject().getStoreId());
            logger.error(message, e);
        }
        catch (DataIntegrityException e)
        {
            final String message = errorMessage(EasyResources.ERROR_RESTORING_DATASET, datasetModel.getObject().getStoreId());
            logger.error(message, e);
        }
    }

    private String errorMessage(final String messageKey, final String... param)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR, param);
    }
}
