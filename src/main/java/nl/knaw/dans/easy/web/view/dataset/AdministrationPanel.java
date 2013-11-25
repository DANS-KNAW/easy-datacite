package nl.knaw.dans.easy.web.view.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.dataset.AdditionalLicenseUnit;
import nl.knaw.dans.easy.domain.dataset.LicenseUnit;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.wicket.WorkflowStepPanel;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdministrationPanel extends AbstractDatasetModelPanel
{
    public static final String DISPLAY_PROPS_NAME = "conf/dataset/workflow/workflowDisplay.properties";

    private static final long serialVersionUID = 7358463847821513320L;

    private static final Logger logger = LoggerFactory.getLogger(AdministrationPanel.class);

    private static Properties DISPLAY_PROPS;
    private static final String DEPOSITOR_LICENSE = "depositorLicense";
    private static final String ADDITIONAL_LICENSE = "additionalLicense";

    private boolean initiated;

    public AdministrationPanel(final String wicketId, final DatasetModel datasetModel)
    {
        super(wicketId, new DatasetModel(datasetModel));
        addLicensesHistoryPanels();
        addLicenseUploadPanel();
        getDisplayProps();

        // Disable dynamic reload. We don't want the dataset reloading automatically
        // just before saving. We want to save it in exactly the way it was presented.
        getDatasetModel().setDynamicReload(false);
    }

    private void addLicenseUploadPanel()
    {
        final EasyUpload upload = new EasyUpload("licenseUploadPanel", configureUpload());
        UploadLicenseProcess ulp = new UploadLicenseProcess(getDatasetModel());
        upload.registerPostProcess(ulp);
        add(upload);
        add(new ComponentFeedbackPanel("licenseUploadPanel-componentFeedback", upload));
    }

    private EasyUploadConfig configureUpload()
    {
        final EasyUploadConfig uploadConfig = new EasyUploadConfig();
        uploadConfig.setAutoRemoveFiles(true);
        return uploadConfig;
    }

    private void addLicensesHistoryPanels()
    {
        add(createUnitMetaDataPanel(DEPOSITOR_LICENSE, getDatasetModel(), LicenseUnit.UNIT_ID, false));
        add(createUnitMetaDataPanel(ADDITIONAL_LICENSE, getDatasetModel(), AdditionalLicenseUnit.UNIT_ID, true));
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        else
        {
            addOrReplace(createUnitMetaDataPanel(ADDITIONAL_LICENSE, getDatasetModel(), AdditionalLicenseUnit.UNIT_ID, true));
        }
        super.onBeforeRender();
    }

    private UnitMetaDataPanel createUnitMetaDataPanel(String licenseType, DatasetModel datasetModel, String licenseUnitId, boolean showDeleteButton)
    {
        final DatasetService datasetService = Services.getDatasetService();
        try
        {
            if (licenseType == ADDITIONAL_LICENSE)
            {
                return new UnitMetaDataPanel(licenseType, datasetService.getAdditionalLicenseVersions(getDataset()), datasetModel, licenseUnitId,
                        showDeleteButton);
            }
            else
            {
                return new UnitMetaDataPanel(licenseType, datasetService.getLicenseVersions(getDataset()), datasetModel, licenseUnitId, showDeleteButton);
            }
        }
        catch (ServiceException serviceException)
        {
            final String message = "Could not get license history: ";
            logger.error(message, serviceException);
            throw new WicketRuntimeException(message, serviceException);
        }
    }

    private void init()
    {
        final Form workflowForm = new Form("workflowForm")
        {
            private static final long serialVersionUID = 6119303211884582181L;

            @Override
            protected void onSubmit()
            {
                super.onSubmit();

                try
                {
                    Services.getDatasetService().saveAdministrativeMetadata(getSessionUser(), getDataset());
                    final String message = infoMessage(EasyResources.SUCCESFUL_UPDATE);
                    logger.info(message);
                }
                catch (final ServiceException e)
                {
                    final String message = errorMessage(EasyResources.SAVE_WORKFLOW);
                    logger.error(message, e);
                    throw new InternalWebError();
                }
                catch (DataIntegrityException e)
                {
                    final String message = errorMessage(EasyResources.SAVE_WORKFLOW);
                    logger.error(message, e);
                    throw new InternalWebError();
                }
            }
        };

        workflowForm.add(new SubmitLink("saveWorkflow"));

        final WorkflowStep workflow = getDataset().getAdministrativeMetadata().getWorkflowData().getWorkflow();

        final WorkflowStepPanel workflowStepPanel = new WorkflowStepPanel("workflowStepPanel", workflow, DISPLAY_PROPS);
        workflowForm.add(workflowStepPanel);

        final Label totalTimeSpent = new Label("totalTimeSpent", new PropertyModel(workflow, "timeSpent"));
        workflowForm.add(totalTimeSpent);

        if (workflow.getRemarks().size() == 0)
        {
            workflow.addRemark(new Remark());
            workflow.setDirty(false);
        }
        final TextArea<String> textArea = new TextArea<String>("textArea", new Model<String>()
        {
            private static final long serialVersionUID = 8412623237717756209L;

            // incongruent with multiple remark fields in workflowstep, where each remark has its own
            // remarkerId and remarkDate. This implementation just overwrites userId's of remarkers
            // and puts everything in the first remark of the root workflow step.
            @Override
            public String getObject()
            {
                return workflow.getRemarks().get(0).getText();
            }

            @Override
            public void setObject(final String object)
            {
                final Remark remark = workflow.getRemarks().get(0);
                remark.setText((String) object);
                remark.setRemarker(getSessionUser());
            }

        });

        workflowForm.add(textArea);

        add(workflowForm);
    }

    private static Properties getDisplayProps()
    {
        if (DISPLAY_PROPS == null)
        {
            DISPLAY_PROPS = new Properties();
            InputStream inStream = null;
            logger.debug("Loading displayProperties from " + DISPLAY_PROPS_NAME);
            try
            {
                inStream = ResourceLocator.getInputStream(DISPLAY_PROPS_NAME);
                DISPLAY_PROPS.load(inStream);
            }
            catch (final IOException e)
            {
                throw new WicketRuntimeException("Unable to load display properties for " + AdministrationPanel.class.getName(), e);
            }
            catch (final ResourceNotFoundException e)
            {
                throw new WicketRuntimeException("Unable to load display properties for " + AdministrationPanel.class.getName(), e);
            }
            finally
            {
                if (inStream != null)
                {
                    try
                    {
                        inStream.close();
                    }
                    catch (final IOException ioException)
                    {
                        logger.error("Could not close inputStream: ", ioException);
                        throw new WicketRuntimeException("Could not close inputStream: ", ioException);
                    }
                }
            }
        }
        return DISPLAY_PROPS;
    }

}
