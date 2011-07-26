package nl.knaw.dans.easy.web.permission;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.PossiblyDisabledTextArea;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.fileexplorer2.FileUtil;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.easy.web.view.dataset.UnitMetaDataResource;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class PermissionRequestForm extends PermissionForm
{
    private static final long serialVersionUID = 6204591036947047986L;

    public static final String EDITABLE_PERMISSION_REQUEST_TEMPLATE = "/editable/PermissionRequest.template";
    
    private static final String STATUS_RESOURCE_KEY = "permission.request.status.value.";
//    private static final String TITLE_RESOURCE_KEY = "permission.request.research.title.";
//    private static final String THEME_RESOURCE_KEY = "permission.request.research.theme.";

    private static final String CONDITIONS_WID = "acceptConditions";
    private static final String THEME_WID = "theme";
    private static final String TITLE_WID = "title";
    private static final String SUBMIT_WID = "submit";
    private static final String CANCEL_WID = "cancel";
    private static final String MSG_ACCEPT_ADDITIONAL   = "permission.request.acceptAdditionalConditions.label";
    private static final String ADDITIONAL_CONDITIONS_WID = "acceptAdditional";
    private static final String RETURNED_INTRO = "PermissionRequestReturnedIntro";
    
    private final PermissionRequestModel prmRequest;

    public PermissionRequestForm(final String wicketId, final AbstractEasyPage fromPage,
            final DatasetModel datasetModel)
    {
        super(wicketId, fromPage, datasetModel);
        addCommonFeedbackPanel();

        final EasyUser sessionUser = getSessionUser();
        final boolean initialRequest = !getDataset().getPermissionSequenceList().hasSequenceFor(sessionUser);
        final PermissionSequence userSequence =
        	getDataset().getPermissionSequenceList().getSequenceFor(sessionUser);

        final PermissionSequence.State status = initialRequest ? null : userSequence.getState();
        final String explanation = initialRequest ? null : userSequence.getReplyText();
        final boolean editMode = initialRequest || (State.Returned.equals(status));

        prmRequest = getDataset().getPermissionSequenceList().getPermissionRequest(sessionUser);

        add(new Label("intro", getString(RETURNED_INTRO)).setVisible(State.Returned.equals(status)));
        
        // TODO add help links and required marks (only in editMode)
//        final SimpleLabelPanel titleLabel =
//                new SimpleLabelPanel(TITLE_WID, TITLE_RESOURCE_KEY + "label", TITLE_RESOURCE_KEY
//                        + "anchor", true);
//        final SimpleLabelPanel themaLabel =
//                new SimpleLabelPanel(THEME_WID, THEME_RESOURCE_KEY + "label", THEME_RESOURCE_KEY
//                        + "anchor", true);

        final IModel titleModel = new PropertyModel(prmRequest, PermissionRequestModel.REQUEST_TITLE);
        final IModel themeModel = new PropertyModel(prmRequest, PermissionRequestModel.REQUEST_THEME);
        final IModel conditionsModel =
                new PropertyModel(prmRequest, PermissionRequestModel.ACCEPTING_CONDITIONS_OF_USE);
        final IModel statusModel = new ResourceModel(STATUS_RESOURCE_KEY + status, "" + status);

        addComponent(new Label("status.label"));
        addComponent(new Label("status.value", statusModel)).setVisible(status != null);
        addComponent(new Label("explanation.label"));

        addComponent(new MultiLineLabel("explanation.value", explanation)).setVisible(explanation != null);

        addRequired(new TextField(TITLE_WID, titleModel)).setEnabled(editMode);
        addRequired(new PossiblyDisabledTextArea(THEME_WID, themeModel, editMode));
        addRequired(new CheckBox(CONDITIONS_WID, conditionsModel)).setEnabled(editMode);
        
        // Additional conditions
        final UnitMetaDataResource additionalLicenseResource = FileUtil.getAdditionalLicenseResource(datasetModel);
        final Component additionalLicense = new ResourceLink<UnitMetaDataResource>("additionalLicense", additionalLicenseResource);
        final boolean hasAdditionalLicense = additionalLicenseResource != null;
        additionalLicense.setVisible(hasAdditionalLicense);
        add(additionalLicense);
        // TODO  Change the validation in the business layer;
        // add the additional acceptance and an indicator if its needed to the PermissionRequestModel
        //
        // Note: select if acceptance not needed, because the checkbox form element is required
        // Also select if conditions of use was already accepted (returned request)
        // prmRequest.isAcceptingConditionsOfUse()
        final Model<Boolean> additionalAccepted = new Model<Boolean>(!hasAdditionalLicense || prmRequest.isAcceptingConditionsOfUse());
        if(hasAdditionalLicense)
        {
            addRequired(new CheckBox(ADDITIONAL_CONDITIONS_WID, additionalAccepted)).setEnabled(editMode);
        }
        else
        {
            // not visible and not required
            add(new CheckBox(ADDITIONAL_CONDITIONS_WID, additionalAccepted).setVisible(false));
        }
        final Label acceptAdditionalBoxLabel = new Label("acceptAdditionalLabel",new ResourceModel(MSG_ACCEPT_ADDITIONAL));
        add(acceptAdditionalBoxLabel);

        addComponent(new SubmitLink(SUBMIT_WID)).setVisible(editMode);
        add(new EasyEditablePanel("editablePanel", EDITABLE_PERMISSION_REQUEST_TEMPLATE));
        add(new Link(CANCEL_WID)
        {
            private static final long serialVersionUID = -6091186801938439734L;

            @Override
            public void onClick()
            {
                logAction("permissionrequest cancelled.", prmRequest);
                pageBack();
            }
        });
    }


    @Override
    protected void onSubmit()
    {
        try
        {
            prmRequest.setPermissionsTabLink(DatasetViewPage.urlFor(getDataset(), DatasetPermissionsTab.TAB_INDEX, true, this));
            prmRequest.setRequestLink(PermissionReplyPage.urlFor(getDataset(), getSessionUser(), this));

        	Services.getDatasetService().savePermissionRequest(getSessionUser(), getDataset(), prmRequest);
            logAction("permission request submitted.", prmRequest);
            final String message = infoMessage(EasyResources.PERMISSION_REQUESTED);
            logger.info(message);
        }
        catch (final ServiceException e)
        {
			final String message = errorMessage(EasyResources.PERMISSION_REQUEST_FAIL);
			logger.error(message, e);
            throw new InternalWebError();
        }
        catch (DataIntegrityException e)
        {
            final String message = errorMessage(EasyResources.PERMISSION_REQUEST_FAIL);
            logger.error(message, e);
            throw new InternalWebError();
        }
        pageBack();
    }

    private FormComponent addRequired(final FormComponent field)
    {
        addWithComponentFeedback(field, new Model(field.getId()));
        field.setRequired(true);
        return field;
    }

    protected void logAction(final String action, final PermissionRequestModel permissionRequest)
    {
        logger.debug(String.format("%s title=[%s] theme=[%s] accept conditions=[%s]", action,
                permissionRequest.getRequestTitle(),
                StringUtils.abbreviate(permissionRequest.getRequestTheme(), 25),
                permissionRequest.isAcceptingConditionsOfUse()));
    }

}
