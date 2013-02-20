package nl.knaw.dans.easy.web.view.dataset;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.StateChangeDate;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.DepositPage;
import nl.knaw.dans.easy.web.fileexplorer.FileExplorer;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage.Mode;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusPanel extends AbstractEasyPanel
{

    private static final String DATE_TIME_FORMAT = "DateAndTimeFormat";

    private static final long serialVersionUID = -4453008625659085694L;

    private static final Logger logger = LoggerFactory.getLogger(StatusPanel.class);

    private boolean initiated;

    private final DatasetModel datasetModel;

    private final DatasetViewPage.Mode mode;

    public StatusPanel(String wicketId, DatasetModel datasetModel, Mode mode)
    {
        super(wicketId);
        this.datasetModel = datasetModel;
        this.mode = mode;
    }

    protected Dataset getDataset()
    {
        return datasetModel.getObject();
    }

    public boolean isInitiated()
    {
        return initiated;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        setOutputMarkupId(true);

        /*
         * //Update the status panel when the workflow is changed (we don't want this anymore)
        registerAjaxEventListener(WorkflowStepPanel.AJAX_EVENT_WORKFOWSTEP_COMPLETED_CHANGED, new AjaxEventListener()
        {

            private static final long serialVersionUID = -5155074898187559541L;

            public void handleAjaxEvent(AjaxRequestTarget target)
            {
                target.addComponent(StatusPanel.this);
            }

        }); */

        Component datasetStatus = new Label("datasetStatus", new Model()
        {

            private static final long serialVersionUID = 3612225734563374597L;

            public Serializable getObject()
            {
                return getString("datasetState." + datasetModel.getObject().getAdministrativeState().toString());
            }

        });
        add(datasetStatus);

        Component statusDate = new DateTimeLabel("statusDate", getString(DATE_TIME_FORMAT), new Model()
        {

            private static final long serialVersionUID = 1139426060975374951L;

            @Override
            public Serializable getObject()
            {
                List<StateChangeDate> dates = datasetModel.getObject().getAdministrativeMetadata().getStateChangeDates();
                return dates.size() > 0 ? dates.get(dates.size() - 1).getChangeDate() : datasetModel.getObject().getLastModified();
            }

        });
        add(statusDate);

        Link continueDeposit = new Link("continueDeposit")
        {

            private static final long serialVersionUID = 3509720352618875479L;

            @Override
            public void onClick()
            {
                logger.debug("continueDeposit clicked.");
                setResponsePage(new DepositPage(datasetModel, DepositDiscipline.EMD_DEPOSITFORM_WIZARD));
            }

        };
        add(continueDeposit);

        final ModalWindow popup = new ModalWindow("popup");
        popup.setUseInitialHeight(false);
        popup.setInitialWidth(450);
        popup.add(CSSPackageResource.getHeaderContribution(FileExplorer.class, "style/modal.css"));
        add(popup);

        AjaxLink<Void> deleteDataset = new AjaxLink<Void>("deleteDataset")
        {
            private static final long serialVersionUID = 3429899621436517328L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                target.prependJavascript("Wicket.Window.unloadConfirmation = false;");
                logger.debug("deleteDataset clicked.");
                popup.setTitle("Delete dataset");
                popup.setContent(new DeleteDatasetPanel(popup, datasetModel));
                popup.show(target);
            }
        };
        add(deleteDataset);

        AjaxLink<Void> restoreDeleted = new AjaxLink<Void>("restoreDeleted")
        {

            private static final long serialVersionUID = -2472554212526731592L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                logger.debug("restoreDeleted clicked");
                popup.setTitle("Restore dataset");
                popup.setContent(new RestoreDatasetPanel(popup, datasetModel));
                popup.show(target);
            }

        };
        add(restoreDeleted);

        Link unsubmit = new Link("unsubmit")
        {

            private static final long serialVersionUID = 425342631452858602L;

            @Override
            public void onClick()
            {
                logger.debug("unsubmit clicked.");
                setResponsePage(new DatasetIntermediatePage(datasetModel, IntermediatePage.Mode.UNSUBMIT));
            }

        };
        unsubmit.setOutputMarkupPlaceholderTag(true);
        add(unsubmit);

        Link publish = new Link("publish")
        {

            private static final long serialVersionUID = 3011340626581111232L;

            @Override
            public void onClick()
            {
                logger.debug("publish clicked.");
                setResponsePage(new DatasetIntermediatePage(datasetModel, IntermediatePage.Mode.PUBLISH));
                // logging for statistics
                Dataset dataset = datasetModel.getObject();
                StatisticsLogger.getInstance().logEvent(StatisticsEvent.DATASET_PUBLISHED, new DatasetStatistics(dataset), new DisciplineStatistics(dataset));
            }

        };
        publish.setOutputMarkupPlaceholderTag(true);
        add(publish);

        Link unpublish = new Link("unpublish")
        {
            private static final long serialVersionUID = -2661636063796818136L;

            @Override
            public void onClick()
            {
                logger.debug("unpublish clicked.");
                setResponsePage(new DatasetIntermediatePage(datasetModel, IntermediatePage.Mode.UNPUBLISH));
            }

        };
        unpublish.setOutputMarkupPlaceholderTag(true);
        add(unpublish);

        Link maintain = new Link("maintain")
        {
            private static final long serialVersionUID = -4388333589227028947L;

            @Override
            public void onClick()
            {
                logger.debug("maintain clicked.");
                setResponsePage(new DatasetIntermediatePage(datasetModel, IntermediatePage.Mode.MAINTAIN));
            }

        };
        maintain.setOutputMarkupPlaceholderTag(true);
        add(maintain);

        Link republish = new Link("republish")
        {
            private static final long serialVersionUID = -8485962066677994209L;

            @Override
            public void onClick()
            {
                logger.debug("republish clicked.");
                setResponsePage(new DatasetIntermediatePage(datasetModel, IntermediatePage.Mode.REPUBLISH));
            }

        };
        republish.setOutputMarkupPlaceholderTag(true);
        add(republish);

        Link reuseLink = new Link("reuseLink")
        {

            private static final long serialVersionUID = -7409712694726666776L;

            @Override
            public void onClick()
            {
                logger.debug("Reuse link clicked.");
                handleReuseDataset();
            }

        };
        reuseLink.setVisible(Mode.VIEW.equals(getMode()));
        add(reuseLink);

        PublicationProgressPanel pubProgressPanel = new PublicationProgressPanel("pubProgressPanel", datasetModel);
        add(pubProgressPanel);

    }

    private void handleReuseDataset()
    {
        try
        {
            Dataset newDataset = Services.getDatasetService().cloneDataset(getSessionUser(), getDataset());
            DatasetModel newModel = new DatasetModel(newDataset);
            setResponsePage(new DepositPage(newModel));
        }
        catch (ServiceException e)
        {
            String sid = getDataset().getStoreId();
            errorMessage(EasyResources.ERROR_CLONING_DATASET, sid);
            logger.error("Could not clone dataset with sid " + sid, e);
            setResponsePage(ErrorPage.class);
        }
    }

    private Mode getMode()
    {
        return mode;
    }

    private class DatasetIntermediatePage extends IntermediatePage
    {
        public DatasetIntermediatePage(DatasetModel datasetModel, IntermediatePage.Mode mode)
        {
            super(datasetModel, mode);
        }

        @Override
        Page getReturnToPage()
        {
            return new DatasetViewPage(getDatasetModel(), DatasetViewPage.Mode.VIEW);
        }
    }

}
