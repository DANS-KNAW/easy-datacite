package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.ExcludeMessageFilter;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.behavior.FormModificationDetectorBehavior;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.DatasetNotification;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EditableInfoPage;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.RedirectData;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractRepeaterPanel;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.view.dataset.DatasetUrlComposerImpl;
import nl.knaw.dans.easy.web.wicket.RecursivePanel;
import nl.knaw.dans.easy.web.wicket.WizardNavigationListener;
import nl.knaw.dans.easy.web.wicket.WizardNavigationPanel;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositPanel extends AbstractDatasetModelPanel {
    private static final String DEPOSIT_FORM = "depositForm";
    public static final String INFO_PAGE = "deposit.completed.header";

    private static final long serialVersionUID = 66648246305751046L;

    private static Logger logger = LoggerFactory.getLogger(DepositPanel.class);

    public static final String EDITABLE_DEPOSIT_COMPLETE_TEMPLATE = "/pages/DepositComplete.template";

    private transient EmdPanelFactory panelFactory;

    private final FormDescriptor formDescriptor;
    private final FormDefinition emdFormDefinition;
    private final DatasetSubmission submission;
    private boolean initiated;
    private FormPage currentPage;

    private DepositForm depoForm;

    @SpringBean(name = "datasetService")
    private DatasetService datasetService;

    public DepositPanel(String wicketId, DepositDiscipline discipline, String formDefinitionId, DatasetModel model) {
        super(wicketId, model);
        setOutputMarkupId(true);

        logger.debug("Creating a depositPanel with formDefinition " + formDefinitionId);

        formDescriptor = discipline.getEmdFormDescriptor();
        emdFormDefinition = formDescriptor.getFormDefinition(formDefinitionId);
        EasyUser currentUser = ((EasySession) getSession()).getUser();
        submission = new DatasetSubmissionImpl(emdFormDefinition, getDataset(), currentUser);
        if (emdFormDefinition != null && emdFormDefinition.getFormPages().size() > 0) {
            currentPage = emdFormDefinition.getFormPages().get(0);
        } else {
            fatalMessage(EasyResources.INTERNAL_ERROR, "");
            logger.error("Invalid FormDefinition: " + emdFormDefinition == null ? "null" : "formPages.size() == 0");
            throw new RestartResponseException(new ErrorPage());
        }

    }

    public FormDefinition getEmdFormDefinition() {
        return emdFormDefinition;
    }

    public FormPage getCurrentPage() {
        return currentPage;
    }

    public int getCurrentPageIndex() {
        return getEmdFormDefinition().getFormPages().indexOf(getCurrentPage());
    }

    public int getTotalPages() {
        return getEmdFormDefinition().getFormPages().size();
    }

    public boolean isLastPage() {
        return getCurrentPageIndex() + 1 == getTotalPages();
    }

    public void setCurrentPage(FormPage currentPage) {
        this.currentPage = currentPage;
    }

    public void setCurrentPage(String currentPageId) {
        this.currentPage = getEmdFormDefinition().getFormPage(currentPageId);
    }

    @Override
    protected void onBeforeRender() {
        if (!initiated) {
            init();
            DatasetNotification.setDatasetUrlComposer(DatasetUrlComposerImpl.getInstance(getPage().getPageMap()));
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init() {
        logger.debug("Init DepositPanel: " + this);
        DepositForm depositForm = getDepositForm();
        addOrReplace(depositForm);
        depositForm.add(new FormModificationDetectorBehavior() {

            private static final long serialVersionUID = -6373403759956095397L;

            @Override
            protected String getDisplayMessage() {
                return getString(EasyResources.DEPOSIT_UNSAVED_CHANGES);
            }

        });
    }

    public DepositForm getDepositForm() {
        if (depoForm == null || depoForm.getErrors() == 0) {
            depoForm = new DepositForm(DEPOSIT_FORM);
        }
        return depoForm;
    }

    private EmdPanelFactory getPanelFactory() {
        if (panelFactory == null) {
            panelFactory = new EmdPanelFactory(RecursivePanel.PANEL_WICKET_ID, this, getDatasetModel());
            logger.debug("Created transient panelFactory.");
        }
        return panelFactory;
    }

    // we assume here that saveContents is executed before submitContents.
    private void submitContents() {
        try {
            for (String msgKey : submission.getGlobalErrorMessages()) {
                final String message = errorMessage(msgKey);
                logger.error(message);
            }
            for (String msgKey : submission.getGlobalInfoMessages()) {
                final String message = infoMessage(msgKey);
                logger.info(message);
            }

            datasetService.submitDataset(submission);

            FormPage firstErrorPage = submission.getFirstErrorPage();
            if (firstErrorPage != null) {
                // we go to the first page that has metadata errors.
                logger.debug("Going to init(DepositForm) because of submission errors on " + firstErrorPage.getId());
                setCurrentPage(firstErrorPage);
                depoForm = null;
                init();
                // end of procedure
            } else if (submission.hasGlobalMessages()) {
                // we stay on submission page and display global messages.
                for (String msgKey : submission.getGlobalErrorMessages()) {
                    errorMessage(msgKey);
                }
                for (String msgKey : submission.getGlobalInfoMessages()) {
                    infoMessage(msgKey);
                }
                // end of procedure
            } else {
                // we leave the page and show thank-you-very-much-page.

                final String message = infoMessage(EasyResources.DEPOSIT_COMPLETED, submission.getSessionUser().getEmail());
                logger.info(message);
                setResponsePage(new EditableInfoPage(getString(INFO_PAGE), EDITABLE_DEPOSIT_COMPLETE_TEMPLATE, EasySession.get().getUser()));
                // end of procedure
            }
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.DATASET_SUBMISSION);
            logger.error(message, e);
            throw new RestartResponseException(new ErrorPage());
        }
        catch (DataIntegrityException e) {
            final String message = errorMessage(EasyResources.DATASET_SUBMISSION);
            logger.error(message, e);
            throw new RestartResponseException(new ErrorPage());
        }
        finally {
            // logging for statistics
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.DATASET_DEPOSIT, new DatasetStatistics(submission.getDataset()));
        }
    }

    public boolean saveContents() {
        Dataset dataset = getDataset();
        try {
            datasetService.saveEasyMetadata(submission.getSessionUser(), dataset);
        }
        catch (ServiceException e) {
            if (e.getCause() instanceof ConcurrentUpdateException) {
                final String message = errorMessage(EasyResources.DATASET_METADATA_CHANGED);
                logger.error(message, e);
            } else {
                final String message = errorMessage(EasyResources.DATASET_FAIL_TO_SAVE);
                logger.error(message, e);
            }
            // TODO set a more user friendly message
            setResponsePage(new ErrorPage());
            return false;
        }
        catch (DataIntegrityException e) {
            final String message = errorMessage(EasyResources.DATASET_FAIL_TO_SAVE);
            logger.error(message, e);
            throw new RestartResponseException(new ErrorPage());
        }
        logger.debug("Saved contents. Dataset=" + dataset);
        return true;
    }

    /**
     * @author ecco Apr 14, 2009
     */
    class DepositForm extends Form<Object> implements WizardNavigationListener {

        private static final long serialVersionUID = 478774282035192135L;

        private FormPage requestedPage;

        private Label head;
        private FeedbackPanel formFeedBack;
        private WizardNavigationPanel wizardNavigationPanel;
        private RecursivePanel recursivePanel;
        private ButtonPanel buttonPanel;
        private NextPreviousPanel nextPreviousPanel1;
        private NextPreviousPanel nextPreviousPanel2;

        private int errors;

        public DepositForm(String id) {
            super(id);
            setOutputMarkupId(true);
            requestedPage = currentPage;
        }

        public int getErrors() {
            return errors;
        }

        @Override
        protected void onBeforeRender() {
            initFormComponents();
            super.onBeforeRender();
        }

        private void initFormComponents() {
            logger.debug("initFormComponents");
            addOrReplace(getHead());
            addOrReplace(getFormFeedBack());
            addOrReplace(getWizardNavigationPanel());
            addOrReplace(getRecursivePanel());
            addOrReplace(getButtonPanel());
            addOrReplace(getNextPreviousPanel1());
            addOrReplace(getNextPreviousPanel2());
            if (submission.hasMetadataErrors()) {
                error(getString(EasyResources.DEFAULT_FORM_ERROR));
            }
        }

        public Label getHead() {
            if (head == null) {
                head = new Label("head", new StringResourceModel("page.title", new Model<DepositForm>(this)));
            }
            return head;
        }

        public FeedbackPanel getFormFeedBack() {
            if (formFeedBack == null) {
                formFeedBack = WicketUtil.addCommonFeedbackPanel(this, new ExcludeMessageFilter(this));
            }
            return formFeedBack;
        }

        public WizardNavigationPanel getWizardNavigationPanel() {
            if (wizardNavigationPanel == null) {
                wizardNavigationPanel = new WizardNavigationPanel("navigationPanel", emdFormDefinition, this);
                wizardNavigationPanel.setLabelResourceKey("navigationPanel.label");
                wizardNavigationPanel.setCurrentPage(currentPage);
            }
            return wizardNavigationPanel;
        }

        public ButtonPanel getButtonPanel() {
            if (buttonPanel == null) {
                buttonPanel = new ButtonPanel("buttonPanel", this);
            }
            return buttonPanel;
        }

        public NextPreviousPanel getNextPreviousPanel1() {
            if (nextPreviousPanel1 == null) {
                nextPreviousPanel1 = new NextPreviousPanel("nextPreviousPanel1", this);
            }
            return nextPreviousPanel1;
        }

        public NextPreviousPanel getNextPreviousPanel2() {
            if (nextPreviousPanel2 == null) {
                nextPreviousPanel2 = new NextPreviousPanel("nextPreviousPanel2", this);
            }
            return nextPreviousPanel2;
        }

        private RecursivePanel getRecursivePanel() {
            if (recursivePanel == null) {
                recursivePanel = new RecursivePanel("recursivePanel", getPanelFactory(), currentPage);
                recursivePanel.setHeadVisible(false);
            }
            return recursivePanel;
        }

        /**
         * Get the title of the current page.
         * <p/>
         * see also: <br/>
         * DepositPage.properties, page.title=Deposit dataset - ${currentPageTitle})
         * 
         * @return title of the current page
         */
        public String getCurrentPageTitle() {
            return getString(currentPage.getLabelResourceKey(), null, "");
        }

        protected void onPageClick(int requestedPageIndex) {
            FormPage requestedFormPage = getEmdFormDefinition().getFormPages().get(requestedPageIndex);
            onPageClick(requestedFormPage);
        }

        /**
         * Event can come from {@link WizardNavigationPanel}.
         */
        public void onPageClick(FormPage requestedFormPage) {
            logger.debug("onPageClick. Requested=" + requestedFormPage.getId());
            requestedPage = requestedFormPage;
        }

        @Override
        protected void onSubmit() {
            logger.debug("onSubmit of form " + this.getClass() + " currentPage=" + currentPage.getLabelResourceKey());

            // Important. Find submittingButton before synchronize. The button may become
            // invisible in the hierarchy after synchronization.
            IFormSubmittingComponent submittingButton = findSubmittingButton();

            errors = 0;
            submission.clearAllMessages();
            // synchronize sourceLbusiness objects on listItems from RepeaterPanels.
            super.visitChildren(AbstractRepeaterPanel.class, new IVisitor<Component>() {

                public Object component(Component component) {
                    errors += ((AbstractRepeaterPanel<?>) component).synchronize();
                    return IVisitor.CONTINUE_TRAVERSAL;
                }

            });

            // logger.debug("submittingButton=" + submittingButton);
            if (submittingButton != null && isNavigationOrSubmitButton((Component) submittingButton)) // do
                                                                                                      // not
                                                                                                      // save
                                                                                                      // with
                                                                                                      // clicks
                                                                                                      // on
                                                                                                      // plus-/minus
                                                                                                      // buttons
            {
                logger.debug("Save deposit form");
                if (!saveContents()) {
                    return;
                }

                String buttonId = ((Component) submittingButton).getId();
                if ((ButtonPanel.SUBMIT.equals(buttonId) && errors == 0)) {
                    submitContents();
                    return;
                }

                if (ButtonPanel.SAVE.equals(buttonId) && errors == 0) {
                    // LB: if the setResponsePage is passing a class for some reason
                    // the message will not show up on the other page's feedback
                    // panel unless the reporter is null. I have no idea why this
                    // is so and no (more) patience to find out. It works, so I left
                    // it like this.
                    final String message = WicketUtil.commonMessage(null, EasyResources.DATASET_SAVED, FeedbackMessage.INFO);
                    logger.info(message);

                    RedirectData rData = getEasySession().getRedirectData(getPage().getClass());
                    if (rData != null) {
                        setResponsePage(rData.getPageClass(), rData.getPageParameters());
                    }
                }
            } else {
                if (submittingButton == null) {
                    setCurrentPage(emdFormDefinition.getFormPages().get(0));
                    depoForm = null;
                    init();
                    return;
                }
            }

            if (!currentPage.equals(requestedPage) && errors == 0) {
                if (!(submittingButton instanceof AjaxSubmitLink)) {
                    // only init() when not submitted by plus-/minus buttons:
                    // (very important) plus-/minus buttons on the clients browser page aren't in the
                    // tree with new
                    // panels!
                    logger.debug("Going to init()");
                    setCurrentPage(requestedPage);
                    init();
                }
            }

            if (errors > 0) {
                logger.error(getString(EasyResources.NR_ERRORS_IN_FORM).replace("$1", String.valueOf(errors)));
                if (!(submittingButton instanceof AjaxSubmitLink)) {
                    // the error message seems to interfere with the onSubmit/target.addComponent
                    // procedure of the Ajax
                    // submission.
                    errorMessage(EasyResources.NR_ERRORS_IN_FORM, String.valueOf(errors));
                }
            }
        }

        private boolean isNavigationOrSubmitButton(Component component) {
            return getButtonPanel().contains(component, true) || getWizardNavigationPanel().contains(component, true);
        }
    }

    class NextPreviousPanel extends Panel {
        private static final long serialVersionUID = 1L;
        public static final String PREVIOUS = "previous";
        public static final String NEXT = "next";
        private final DepositForm depoForm;

        public NextPreviousPanel(String id, DepositForm depoForm) {
            super(id);
            this.depoForm = depoForm;

            init();
        }

        private void init() {
            add(createNextLink(NEXT, depoForm));
            add(createPreviousLink(PREVIOUS, depoForm));
        }
    }

    private SubmitLink createPreviousLink(String id, final DepositForm depoForm) {
        final int currentPageIndex = getCurrentPageIndex();

        return new SubmitLink(id) {
            private static final long serialVersionUID = -4982579653885099401L;

            @Override
            public void onSubmit() {
                depoForm.onPageClick(currentPageIndex - 1);
            }

            @Override
            public boolean isVisible() {
                return currentPageIndex > 0;
            }
        };
    }

    private SubmitLink createNextLink(String id, final DepositForm depoForm) {
        final int currentPageIndex = getCurrentPageIndex();

        return new SubmitLink(id) {
            private static final long serialVersionUID = -3105624268606924788L;

            @Override
            public void onSubmit() {
                depoForm.onPageClick(currentPageIndex + 1);
            }

            @Override
            public boolean isVisible() {
                return currentPageIndex < getTotalPages() - 1;
            }
        };
    }

    class ButtonPanel extends Panel {

        public static final String SUBMIT = "submit";
        public static final String SAVE = "save";
        public static final String NEXT = "next";
        private static final long serialVersionUID = 4513083082403949353L;
        private boolean initiated;
        private final DepositForm depoForm;

        protected ModalWindow confirmModal;

        public ButtonPanel(String id, DepositForm depoForm) {
            super(id);
            this.depoForm = depoForm;
        }

        @Override
        protected void onBeforeRender() {
            if (!initiated) {
                initComponents();
                initiated = true;
            }
            super.onBeforeRender();
        }

        private void initComponents() {
            String saveButtonText;

            if (DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST.equals(emdFormDefinition.getId())) {
                saveButtonText = getString("button.save");
            } else {
                saveButtonText = getString("button.saveDraft");
            }

            SubmitLink save = new SubmitLink(SAVE) {
                private static final long serialVersionUID = -2915965926376583607L;

                @Override
                public void onSubmit() {
                    logger.debug("Save button clicked");
                    // handled by depoForm.onSubmit()
                }
            };

            Label saveLabel = new Label("saveLabel", saveButtonText);
            save.add(saveLabel);
            save.setDefaultFormProcessing(true);
            add(save);

            add(createNextLink(NEXT, depoForm));

            final Label submitDatasetInfoMessage = new Label("submitDatasetInfoMessage", "");
            add(submitDatasetInfoMessage);

            SubmitLink submit = new SubmitLink(SUBMIT) {
                private static final long serialVersionUID = 2680341944559569382L;

                @Override
                public void onSubmit() {

                }

                /**
                 * This Link is visible when: 1. This is the last page of the Wizard AND 2. The form isn't an archivist form? AND 3. There are any files
                 * uploaded to this dataset
                 * 
                 * @return
                 */
                @Override
                public boolean isVisible() {
                    if (isLastPage() && !DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST.equals(getEmdFormDefinition().getId())) {

                        submitDatasetInfoMessage.setDefaultModelObject(getString("submit.info.message"));
                        submitDatasetInfoMessage.setVisible(true);
                        return true;
                    }

                    submitDatasetInfoMessage.setVisible(false);
                    return false;
                }
            };
            // Remove the standard <em> tags that Wicket inserts around disabled links/buttons.
            submit.setAfterDisabledLink("");
            submit.setBeforeDisabledLink("");
            add(submit);
        }
    }

    /**
     * This method is needed to run init() method of DepositPanel class. This method is used by ArchisEditPanel.
     * 
     * @param initiated
     */
    public void setInitiated(boolean initiated) {
        this.initiated = initiated;
    }
}
