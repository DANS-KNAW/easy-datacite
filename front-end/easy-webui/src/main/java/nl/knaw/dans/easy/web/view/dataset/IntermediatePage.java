package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.DatasetNotification;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.wicket.SelectUserPanel;
import nl.knaw.dans.easy.web.wicket.SubmitLinkListener;

import org.apache.wicket.IClusterable;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IntermediatePage extends AbstractEasyNavPage {
    private static final Logger logger = LoggerFactory.getLogger(IntermediatePage.class);

    private static final String ID_TITLE = "title";
    private static final String ID_INTERMEDIATE_PANEL = "intermediatePanel";
    private static final String ID_INTERMEDIATE_FORM = "intermediateForm";
    private static final String ID_DATASET_TITLE = "datasetTitle";
    private static final String ID_DEPOSITOR_NAME = "depositorName";
    private static final String ID_INCLUDE_LICENSE = "includeLicense";
    private static final String ID_NOTIFY_DEPOSITOR = "notifyDepositor";
    private static final String ID_SUBMIT = "submit";
    private static final String ID_CANCEL = "cancel";

    private final DatasetModel datasetModel;

    @SpringBean(name = "fileStoreAccess")
    FileStoreAccess fileStoreAccess;

    public enum Mode {
        UNSUBMIT("Unsubmit dataset"), PUBLISH("Publish dataset"), UNPUBLISH("Unpublish dataset"), MAINTAIN("Maintain dataset"), REPUBLISH("Republish dataset"), CHANGE_DEPOSITOR(
                "Change depositor");
        private String title;

        Mode(String title) {
            this.title = title;
        }
    }

    public IntermediatePage(DatasetModel datasetModel, Mode mode) {
        super();
        this.datasetModel = datasetModel;

        addCommonFeedbackPanel();
        add(new Label(ID_TITLE, mode.title));

        if (Mode.UNSUBMIT.equals(mode)) {
            add(new UnsubmitPanel());
        } else if (Mode.PUBLISH.equals(mode)) {
            add(new PublishPanel());
        } else if (Mode.UNPUBLISH.equals(mode)) {
            add(new UnpublishPanel());
        } else if (Mode.MAINTAIN.equals(mode)) {
            add(new MaintainPanel());
        } else if (Mode.REPUBLISH.equals(mode)) {
            add(new RepublishPanel());
        } else if (Mode.CHANGE_DEPOSITOR.equals(mode)) {
            add(new ChangeDepositorPanel());
        }
    }

    abstract Page getReturnToPage();

    public DatasetModel getDatasetModel() {
        return (DatasetModel) datasetModel;
    }

    protected Dataset getDataset() {
        return datasetModel.getObject();
    }

    protected String getDepositorName() {
        return getDataset().getAdministrativeMetadata().getDepositor().getDisplayName();
    }

    private void generatePrePublishWarnings() {
        try {
            if (!hasVisibleToAnonymousOrKnown()) {
                final String message = warningMessage(EasyResources.NO_PUBLISHED_DATAFILES);
                logger.warn(message);
            }
        }
        catch (StoreAccessException e) {
            final String message = warningMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
        }
        if (!getDataset().getAdministrativeMetadata().getWorkflowData().getWorkflow().areRequiredStepsCompleted()) {
            final String message = warningMessage(EasyResources.WORKFLOW_NOT_COMPLETED);
            logger.warn(message);
        }
    }

    private boolean hasVisibleToAnonymousOrKnown() throws StoreAccessException {
        // TODO implement multiple VisibleTo arguments? What about group or permission restricted items?
        DmoStoreId datasetStoreId = getDataset().getDmoStoreId();
        return fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class, VisibleTo.ANONYMOUS)
                || fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class, VisibleTo.KNOWN);
    }

    protected class UnsubmitPanel extends AbstractEasyPanel<Object> implements SubmitLinkListener {

        private static final long serialVersionUID = 3939391030695512753L;

        private final IntermediateForm form;

        public UnsubmitPanel() {
            super(ID_INTERMEDIATE_PANEL);
            form = new IntermediateForm(this);
            form.setShowNotifyDepositor(true, true);
            add(form);
        }

        public void onSubmit(SubmitLink submitLink) {
            try {
                Services.getDatasetService().unsubmitDataset(getSessionUser(), getDataset(), form.getMustNotifyDepositor());
                stateChangeSucceeded(DatasetState.DRAFT);
            }
            catch (Exception e) {
                changeFailed(EasyResources.UNSUBMIT_DATASET, e);
            }
        }

    }

    protected class PublishPanel extends AbstractEasyPanel<Object> implements SubmitLinkListener {
        private static final long serialVersionUID = -78333439372508804L;

        private final IntermediateForm form;

        public PublishPanel() {
            super(ID_INTERMEDIATE_PANEL);
            form = new IntermediateForm(this);
            form.setShowNotifyDepositor(true, true);
            form.setShowIncludeLicense(true, false);
            generatePrePublishWarnings();
            add(form);
        }

        public void onSubmit(SubmitLink submitLink) {
            try {
                Services.getDatasetService().publishDataset(getSessionUser(), getDataset(), form.getMustNotifyDepositor(), form.getMustIncludeLicense());
                stateChangeSucceeded(DatasetState.PUBLISHED);
            }
            catch (Exception e) {
                changeFailed(EasyResources.UNABLE_TO_PUBLISH_DATASET, e);
            }
        }

    }

    protected class UnpublishPanel extends AbstractEasyPanel<Object> implements SubmitLinkListener {

        private static final long serialVersionUID = -6362817621826949894L;

        private final IntermediateForm form;

        public UnpublishPanel() {
            super(ID_INTERMEDIATE_PANEL);
            form = new IntermediateForm(this);
            form.setShowNotifyDepositor(true, true);
            add(form);
        }

        public void onSubmit(SubmitLink submitLink) {
            try {
                Services.getDatasetService().unpublishDataset(getSessionUser(), getDataset(), form.getMustNotifyDepositor());
                stateChangeSucceeded(DatasetState.SUBMITTED);
            }
            catch (Exception e) {
                changeFailed(EasyResources.UNABLE_TO_UNPUBLISH_DATSET, e);
            }
        }
    }

    protected class MaintainPanel extends AbstractEasyPanel<Object> implements SubmitLinkListener {
        private static final long serialVersionUID = -1580189067742854756L;

        private final IntermediateForm form;

        public MaintainPanel() {
            super(ID_INTERMEDIATE_PANEL);
            form = new IntermediateForm(this);
            form.setShowNotifyDepositor(true, false);
            add(form);
        }

        public void onSubmit(SubmitLink submitLink) {
            try {
                Services.getDatasetService().maintainDataset(getSessionUser(), getDataset(), form.getMustNotifyDepositor());
                stateChangeSucceeded(DatasetState.MAINTENANCE);
            }
            catch (Exception e) {
                changeFailed(EasyResources.UNABLE_TO_MAINTAIN_DATASET, e);
            }
        }
    }

    protected class RepublishPanel extends AbstractEasyPanel<Object> implements SubmitLinkListener {
        private static final long serialVersionUID = -8958880253397322216L;

        private final IntermediateForm form;

        public RepublishPanel() {
            super(ID_INTERMEDIATE_PANEL);
            form = new IntermediateForm(this);
            form.setShowNotifyDepositor(true, false);
            form.setShowIncludeLicense(true, false);
            generatePrePublishWarnings();
            add(form);
        }

        public void onSubmit(SubmitLink submitLink) {
            try {
                Services.getDatasetService().republishDataset(getSessionUser(), getDataset(), form.getMustNotifyDepositor(), form.getMustIncludeLicense());
                stateChangeSucceeded(DatasetState.PUBLISHED);
            }
            catch (Exception e) {
                changeFailed(EasyResources.UNABLE_TO_REPUBLISH_DATASET, e);
            }
        }
    }

    protected class ChangeDepositorPanel extends AbstractEasyPanel<Object> implements SubmitLinkListener {

        private static final long serialVersionUID = 5992545250405060874L;

        private final IntermediateForm form;
        private final SelectUserPanel selectUserPanel;
        private final Input notifyNewDepositor = new Input();

        public ChangeDepositorPanel() {
            super(ID_INTERMEDIATE_PANEL);
            form = new IntermediateForm(this);
            form.setShowNotifyDepositor(true, false);

            selectUserPanel = new SelectUserPanel("selectUserPanel");
            selectUserPanel.setLabelModel(new ResourceModel("label.select.new.depositor"));
            form.add(selectUserPanel);

            CheckBox notifyNewDepositorCheckBox = new CheckBox("notifyNewDepositor", new PropertyModel<Boolean>(notifyNewDepositor, "checked"));
            form.add(notifyNewDepositorCheckBox);

            add(form);
        }

        public void onSubmit(SubmitLink submitLink) {
            EasyUser newDepositor = selectUserPanel.getSelectedUser();
            if (newDepositor != null) {
                try {
                    Services.getDatasetService().changeDepositor(getSessionUser(), getDataset(), newDepositor, form.getMustNotifyDepositor(),
                            notifyNewDepositor.slavink);
                    changeSucceeded();
                }
                catch (Exception e) {
                    changeFailed(EasyResources.UNABLE_TO_CHANGE_DEPOSITOR, e);
                }
            }
        }
    }

    private void changeSucceeded() {
        final String message = infoMessage(EasyResources.SUCCESFUL_UPDATE);
        logger.info(message);
        setResponsePage(getReturnToPage());
    }

    private void stateChangeSucceeded(DatasetState datasetState) {
        final String message = infoMessage(EasyResources.DATASET_STATUS_CHANGED, datasetState.toString().toLowerCase());
        logger.info(message);
        setResponsePage(getReturnToPage());
    }

    private void changeFailed(String messageKey, Throwable cause) {
        String message = errorMessage(messageKey, getDataset().getStoreId(), cause.getMessage());
        logger.error(message, cause);
    }

    protected class IntermediateForm extends Form<Object> {

        private static final long serialVersionUID = 8451362357786696012L;

        private final SubmitLinkListener listener;
        private final Input notifyDepositor = new Input();
        private final Input includeLicense = new Input();

        private boolean showNotifyDepositor;
        private boolean showIncludeLicense;

        private boolean initiated;

        public IntermediateForm(SubmitLinkListener listener) {
            super(ID_INTERMEDIATE_FORM);
            this.listener = listener;
        }

        public boolean getMustNotifyDepositor() {
            return notifyDepositor.slavink;
        }

        public boolean getMustIncludeLicense() {
            return includeLicense.slavink;
        }

        public void setShowNotifyDepositor(boolean showNotifyDepositor, boolean initialState) {
            this.showNotifyDepositor = showNotifyDepositor;
            notifyDepositor.slavink = initialState;
        }

        public void setShowIncludeLicense(boolean showIncludeLicense, boolean initialState) {
            this.showIncludeLicense = showIncludeLicense;
            includeLicense.slavink = initialState;
        }

        @Override
        protected void onBeforeRender() {
            if (!initiated) {
                init();
                initiated = true;
                DatasetNotification.setDatasetUrlComposer(DatasetUrlComposerImpl.getInstance(getPageMap()));
            }
            super.onBeforeRender();
        }

        private void init() {
            add(new Label(ID_DATASET_TITLE, getDataset().getPreferredTitle()));

            final CheckBox includeLicenseCheckBox = new CheckBox(ID_INCLUDE_LICENSE, new PropertyModel<Boolean>(includeLicense, "checked"));
            includeLicenseCheckBox.setVisible(showIncludeLicense);
            add(includeLicenseCheckBox);

            final CheckBox notifyDepositorCheckBox = createDepositorCheckBox(includeLicenseCheckBox);
            notifyDepositorCheckBox.setVisible(showNotifyDepositor);
            add(notifyDepositorCheckBox);
            add(new Label(ID_DEPOSITOR_NAME, getDepositorName()));

            SubmitLink submit = createSubmitLink();
            add(submit);

            SubmitLink cancel = cerateCancelLink();
            cancel.setDefaultFormProcessing(true);
            add(cancel);

        }

        private SubmitLink createSubmitLink() {
            return new SubmitLink(ID_SUBMIT) {

                private static final long serialVersionUID = 3347417451829990314L;

                @Override
                public void onSubmit() {
                    listener.onSubmit(this);
                }

            };
        }

        private SubmitLink cerateCancelLink() {
            return new SubmitLink(ID_CANCEL) {

                private static final long serialVersionUID = -145305839630590645L;

                @Override
                public void onSubmit() {
                    setResponsePage(getReturnToPage());
                }
            };
        }

        private CheckBox createDepositorCheckBox(final CheckBox includeLicenseCheckBox) {
            return new CheckBox(ID_NOTIFY_DEPOSITOR, new PropertyModel<Boolean>(notifyDepositor, "checked")) {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean wantOnSelectionChangedNotifications() {
                    return true;
                }

                @Override
                protected void onSelectionChanged(Object newSelection) {
                    boolean checked = new Boolean(true).equals(newSelection);
                    includeLicenseCheckBox.setEnabled(checked);
                    if (!checked) {
                        includeLicenseCheckBox.setModelValue(new String[] {""});
                    }
                }
            };
        }
    }

    protected class Input implements IClusterable {
        private static final long serialVersionUID = -667029584818294155L;

        protected boolean slavink;

        public Boolean getChecked() {
            return new Boolean(slavink);
        }

        public void setChecked(Boolean checked) {
            this.slavink = checked.booleanValue();
        }

    }
}
