package nl.knaw.dans.easy.web.permission;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.EnumChoiceRenderer;
import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.common.wicket.components.PossiblyDisabledTextArea;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.view.dataset.DataFilesPanel;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.joda.time.DateTime;

class PermissionReplyForm extends PermissionForm
{
    private static final long serialVersionUID = 6204591036947047986L;

    private static final String STATUS_RESOURCE_KEY = "permission.reply.status";
    private static final String EXPLANATION_RESOURCE_KEY = "permission.reply.explanation";

    private static final String DATE_TIME_FORMAT = "DateAndTimeFormat";

    private static final String SUBMIT_WID = "submit";
    private static final String CANCEL_WID = "cancel";
    private static final String EXPLANATION_WID = "explanation";
    private static final String STATUS_WID = "status";

    private static final String GRANTED = PermissionSequence.State.Granted.toString();

    public static class ChoiceItem implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private PermissionSequence.State key;
        private String value;

        ChoiceItem(final PermissionSequence.State state)
        {
            this.setKey(state);
            this.setValue(state.name());
        }

        public State getKey()
        {
            return key;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(final String value)
        {
            this.value = value;
        }

        public void setKey(final PermissionSequence.State key)
        {
            this.key = key;
        }
    }

    // private final static List<ChoiceItem> WRAPPED_CHOICES =
    // Arrays.asList(new ChoiceItem(PermissionSequence.State.Returned), new ChoiceItem(
    // PermissionSequence.State.Denied), new ChoiceItem(
    // PermissionSequence.State.Granted));
    private final static List<State> STATE_CHOICES = Arrays.asList(PermissionSequence.State.Returned, PermissionSequence.State.Denied,
            PermissionSequence.State.Granted);

    private final PermissionReplyModel prmReply;

    public PermissionReplyForm(final String wicketId, final AbstractEasyPage fromPage, final DatasetModel datasetModel, final PermissionSequence sequence)
    {
        super(wicketId, fromPage, datasetModel);
        addCommonFeedbackPanel();

        final boolean editMode = sequence.getState().equals(PermissionSequence.State.Submitted);
        prmReply = getDataset().getPermissionSequenceList().getPermissionReply(sequence.getRequesterId());

        addFields(editMode);
        addButtons(editMode);
        addMotivation(sequence);
        addPersonalInfo(sequence.getRequester());
    }

    // TODO put the personal info on a separate Panel
    private void addPersonalInfo(final EasyUser requester)
    {

        add(new Label("userId", requester.getId()));
        add(new Label("email", requester.getEmail()));

        add(new Label("name", requester.getDisplayName()));
        add(new Label("function", requester.getFunction()));
        add(new Label("telephone", requester.getTelephone()));
        add(new Label("discipline1", requester.getDiscipline1()));
        add(new Label("discipline2", requester.getDiscipline2()));
        add(new Label("discipline3", requester.getDiscipline3()));
        add(new Label("dai", requester.getDai()));

        add(new Label("organization", requester.getOrganization()));
        add(new Label("department", requester.getDepartment()));
        add(new Label("address", requester.getAddress()));
        add(new Label("postalCode", requester.getPostalCode()));
        add(new Label("city", requester.getCity()));
        add(new Label("country", requester.getCountry()));
    }

    private void addMotivation(final PermissionSequence sequence)
    {
        final DateTime requestDate = sequence.getLastRequestDate();
        add(new Label("title", sequence.getRequestTitle()));
        add(new MultiLineLabel("theme", sequence.getRequestTheme()));
        add(new DateTimeLabel("date", getString(DATE_TIME_FORMAT), new Model(requestDate)));
    }

    private void addFields(final boolean editMode)
    {
        // TODO add help links and required marks (only for status submitted)
        // final SimpleLabelPanel explanationLabel =
        // new SimpleLabelPanel(EXPLANATION_WID, EXPLANATION_RESOURCE_KEY + ".label",
        // EXPLANATION_RESOURCE_KEY + ".anchor", true);

        final IModel statusLabelModel = new ResourceModel(STATUS_RESOURCE_KEY + ".label");
        final PropertyModel statusModel = new PropertyModel(prmReply, "state");

        final IModel explanationLabelModel = new ResourceModel(EXPLANATION_RESOURCE_KEY + ".label");
        final IModel explanationModel = new PropertyModel(prmReply, "explanation");
        if (editMode)
        {
            prmReply.setExplanation("");
            prmReply.setState(null);
        }

        final String prefix = STATUS_RESOURCE_KEY + (editMode ? ".choice" : ".value");
        final IChoiceRenderer renderer = new EnumChoiceRenderer(this, prefix);

        final FormComponent choice = new DropDownChoice(STATUS_WID, statusModel, STATE_CHOICES, renderer).setRequired(true);
        final FormComponent explanation = new PossiblyDisabledTextArea(EXPLANATION_WID, explanationModel, editMode);

        addFormComponent(choice, statusLabelModel).setEnabled(editMode);
        addFormComponent(explanation, explanationLabelModel);

        add(new Validator(choice, explanation));
    }

    private class Validator extends AbstractFormValidator
    {
        private static final long serialVersionUID = 2001927122670938692L;
        final FormComponent choice;
        final FormComponent explanation;

        public Validator(final FormComponent choice, final FormComponent explanation)
        {
            this.choice = choice;
            this.explanation = explanation;
        }

        @Override
        public void validate(Form<?> form)
        {
            if (explanation.getValue().trim().length() == 0)
            {
                error(explanation);
                /* TODO maybe not always required, but needs to be in sync with the PermissionWorker.validateReply
                if (! GRANTED.equals(choice.getValue())) 
                {
                    error(explanation);
                }
                 */
            }
        }

        @Override
        public FormComponent<?>[] getDependentFormComponents()
        {
            final FormComponent<?>[] components = {choice, explanation};
            return components;
        }
    }

    private void addButtons(final boolean editMode)
    {
        addComponent(new SubmitLink(SUBMIT_WID)).setVisible(editMode);
        add(new Link(CANCEL_WID)
        {
            private static final long serialVersionUID = -6091186801938439734L;

            @Override
            public void onClick()
            {
                logAction("permission reply cancelled.", prmReply);
                pageBack();
            }
        });
    }

    @Override
    protected void onSubmit()
    {
        try
        {
            prmReply.setRequestLink(PermissionRequestPage.urlFor(getDataset().getStoreId(), prmReply.getRequesterId(), this));
            prmReply.setDatasetLink(DatasetViewPage.urlFor(getDataset(), DataFilesPanel.TAB_INDEX, true, this));
            Services.getDatasetService().savePermissionReply(getSessionUser(), getDataset(), prmReply);
            logAction("permission reply submitted.", prmReply);
            final String message = infoMessage(EasyResources.PERMISSION_SUBMITTED);
            logger.info(message);
        }
        catch (final ServiceException e)
        {
            final String message = errorMessage(EasyResources.PERMISSION_SUBMIT_FAIL);
            logger.error(message, e);
            throw new InternalWebError();
        }
        catch (DataIntegrityException e)
        {
            final String message = errorMessage(EasyResources.PERMISSION_SUBMIT_FAIL);
            logger.error(message, e);
            throw new InternalWebError();
        }
        pageBack();
    }

    protected void logAction(final String action, final PermissionReplyModel permissionReply)
    {
        logger.debug(String.format("%s requesterId=[%s] state=[%s] explanation=[%s]", action, permissionReply.getRequesterId(), permissionReply.getState(),
                StringUtils.abbreviate(permissionReply.getExplanation(), 25)));
    }

}
