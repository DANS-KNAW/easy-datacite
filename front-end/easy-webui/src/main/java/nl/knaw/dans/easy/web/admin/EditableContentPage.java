package nl.knaw.dans.easy.web.admin;

import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceImpl;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.MaintenanceNotification;
import nl.knaw.dans.easy.servicelayer.NewDepositorNotification;
import nl.knaw.dans.easy.servicelayer.OldDepositorNotification;
import nl.knaw.dans.easy.servicelayer.PublishNotification;
import nl.knaw.dans.easy.servicelayer.ReplyNotification;
import nl.knaw.dans.easy.servicelayer.RepublishNotification;
import nl.knaw.dans.easy.servicelayer.RequestNotification;
import nl.knaw.dans.easy.servicelayer.SubmitNotification;
import nl.knaw.dans.easy.servicelayer.UnpublishNotification;
import nl.knaw.dans.easy.servicelayer.UnsubmitNotification;
import nl.knaw.dans.easy.servicelayer.UpdatePasswordMessage;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.authn.RegistrationPage;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;
import nl.knaw.dans.easy.web.deposit.DepositPanel;
import nl.knaw.dans.easy.web.deposit.LicensePanel;
import nl.knaw.dans.easy.web.editabletexts.EditableTextPage;
import nl.knaw.dans.easy.web.fileexplorer.ModalDownload;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.permission.PermissionRequestForm;
import nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyRequestsSearchResultPage;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.https.RequireHttps;

@RequireHttps
public class EditableContentPage extends AbstractEasyNavPage {
    public EditableContentPage() {
        super();

        // dummy objects
        EasyUser user = EasySession.get().getUser();
        Dataset dataset = new DatasetImpl("easy-dataset:1");
        DatasetSubmissionImpl datasetSubmission = new DatasetSubmissionImpl(new FormDefinition(""), dataset, user);
        PermissionReplyModel prm = new PermissionReplyModel("test");
        prm.setState(State.Granted);

        // LINKS TO EDITABLE PAGES
        addLink("adminBannerEditLink", AbstractEasyNavPage.EDITABLE_ADMIN_BANNER_TEMPLATE);
        addLink("homepageEditLink", HomePage.EDITABLE_HOMEPAGE_TEMPLATE);
        addLink("registrationEditLink", RegistrationPage.EDITABLE_REGISTRATION_TEMPLATE);
        addLink("depositIntroEditLink", DepositIntroPage.EDITABLE_DEPOSIT_INTRO_TEMPLATE);
        addLink("depositCompleteEditLink", DepositPanel.EDITABLE_DEPOSIT_COMPLETE_TEMPLATE, user);
        addLink("licensePanelEditLink", LicensePanel.EDITABLE_LICENSE_PANEL_TEMPLATE, new EmdIdentifier());
        addLink("licensePanelOtherAccessEditLink", LicensePanel.EDITABLE_LICENSE_PANEL_OTHER_ACCESS_TEMPLATE, new EmdIdentifier());
        addLink("downloadDialogEditLink", ModalDownload.EDITABLE_DOWNLOAD_DIALOG_TEMPLATE);
        addLink("permissionRequestEditLink", PermissionRequestForm.EDITABLE_PERMISSION_REQUEST_TEMPLATE);
        addLink("myDatasetsSearchResultEditLink", MyDatasetsSearchResultPage.MY_DATASETS_SEARCH_RESULTS);
        addLink("myRequestsSearchResultEditLink", MyRequestsSearchResultPage.MY_REQUESTS_SEARCH_RESULTS);

        // LINKS TO EDITABLE E-MAILS
        addLink("registrationMailHTMLEditLink", "/mail/templates/authn/RegistrationMail.html", user, new Registration(user));
        addLink("registrationMailTXTEditLink", "/mail/templates/authn/RegistrationMail.txt", user, new Registration(user));

        addLink("updatePasswordMailHTMLEditLink", "/mail/templates/authn/UpdatePasswordMail.html", user, new UpdatePasswordMessage(user,
                new ForgottenPasswordMessenger(null, "jan@jansen.com")));
        addLink("updatePasswordMailTXTEditLink", "/mail/templates/authn/UpdatePasswordMail.txt", user, new UpdatePasswordMessage(user,
                new ForgottenPasswordMessenger(null, "jan@jansen.com")));

        addLink("maintenanceNotificationMailHTMLEditLink", "/mail/templates/maintenance/maintenaceNotification.html", user, dataset,
                new MaintenanceNotification(dataset));
        addLink("maintenanceNotificationMailTXTEditLink", "/mail/templates/maintenance/maintenaceNotification.txt", user, dataset, new MaintenanceNotification(
                dataset));

        addLink("depositConfirmationHTMLEditLink", "/mail/templates/deposit/depositConfirmation.html", user, dataset, datasetSubmission,
                new SubmitNotification(datasetSubmission));
        addLink("depositConfirmationTXTEditLink", "/mail/templates/deposit/depositConfirmation.txt", user, dataset, datasetSubmission, new SubmitNotification(
                datasetSubmission));

        addLink("depositConfirmationOtherAccessHTMLEditLink", "/mail/templates/deposit/depositConfirmationOtherAccess.html", user, dataset, datasetSubmission,
                new SubmitNotification(datasetSubmission));
        addLink("depositConfirmationOtherAccessTXTEditLink", "/mail/templates/deposit/depositConfirmationOtherAccess.txt", user, dataset, datasetSubmission,
                new SubmitNotification(datasetSubmission));

        addLink("newDepositorNotificationHTMLEditLink", "/mail/templates/deposit/newDepositorNotification.html", user, dataset, new NewDepositorNotification(
                dataset, user, user));
        addLink("newDepositorNotificationTXTEditLink", "/mail/templates/deposit/newDepositorNotification.txt", user, dataset, new NewDepositorNotification(
                dataset, user, user));

        addLink("oldDepositorNotificationHTMLEditLink", "/mail/templates/deposit/oldDepositorNotification.html", user, dataset, new OldDepositorNotification(
                dataset, user, user));
        addLink("oldDepositorNotificationTXTEditLink", "/mail/templates/deposit/oldDepositorNotification.txt", user, dataset, new OldDepositorNotification(
                dataset, user, user));

        addLink("unsubmitNotificationHTMLEditLink", "/mail/templates/deposit/unsubmitNotification.html", user, dataset, new UnsubmitNotification(dataset));
        addLink("unsubmitNotificationTXTEditLink", "/mail/templates/deposit/unsubmitNotification.txt", user, dataset, new UnsubmitNotification(dataset));

        addLink("publishNotificationHTMLEditLink", "/mail/templates/publish/publishNotification.html", user, dataset, new PublishNotification(dataset));
        addLink("publishNotificationTXTEditLink", "/mail/templates/publish/publishNotification.txt", user, dataset, new PublishNotification(dataset));

        addLink("republishNotificationHTMLEditLink", "/mail/templates/publish/republishNotification.html", user, dataset, new RepublishNotification(dataset));
        addLink("republishNotificationTXTEditLink", "/mail/templates/publish/republishNotification.txt", user, dataset, new RepublishNotification(dataset));

        addLink("unpublishNotificationHTMLEditLink", "/mail/templates/publish/unpublishNotification.html", user, dataset, new UnpublishNotification(dataset));
        addLink("unpublishNotificationTXTEditLink", "/mail/templates/publish/unpublishNotification.txt", user, dataset, new UnpublishNotification(dataset));

        addLink("deniedReplyNotificationHTMLEditLink", "/mail/templates/permission/DeniedReplyNotification.html", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));
        addLink("deniedReplyNotificationTXTEditLink", "/mail/templates/permission/DeniedReplyNotification.txt", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));

        addLink("grantedReplyNotificationHTMLEditLink", "/mail/templates/permission/GrantedReplyNotification.html", user, dataset, new ReplyNotification(
                dataset, new PermissionSequenceImpl(user), prm));
        addLink("grantedReplyNotificationTXTEditLink", "/mail/templates/permission/GrantedReplyNotification.txt", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));

        addLink("requestNotificationHTMLEditLink", "/mail/templates/permission/requestNotification.html", user, dataset, new RequestNotification(dataset, user,
                new PermissionRequestModel()));
        addLink("requestNotificationTXTEditLink", "/mail/templates/permission/requestNotification.txt", user, dataset, new RequestNotification(dataset, user,
                new PermissionRequestModel()));

        addLink("returnedReplyNotificationHTMLEditLink", "/mail/templates/permission/ReturnedReplyNotification.html", user, dataset, new ReplyNotification(
                dataset, new PermissionSequenceImpl(user), prm));
        addLink("returnedReplyNotificationTXTEditLink", "/mail/templates/permission/ReturnedReplyNotification.txt", user, dataset, new ReplyNotification(
                dataset, new PermissionSequenceImpl(user), prm));

        addLink("headerHTMLEditLink", "/mail/templates/default/header.html", user, dataset);
        addLink("headerTXTEditLink", "/mail/templates/default/header.txt", user, dataset);

        addLink("footerHTMLEditLink", "/mail/templates/default/footer.html", user, dataset);
        addLink("footerTXTEditLink", "/mail/templates/default/footer.txt", user, dataset);

        // LINKS TO SEARCH HELP TEXTS
        addLink("searchHelpEditLink", "/help/Search.template");
        addLink("refineHelpEditLink", "/help/Refine.template");

        // LINKS TO DEPOSIT HELP TEXTS
        addLink("CreatorDCEditLink", "/help/Creator.template");
        addLink("CreatorEASEditLink", "/help/EasCreator.template");
        addLink("TitleEditLink", "/help/Title.template");
        addLink("DescriptionEditLink", "/help/Description.template");
        addLink("DateCreatedEditLink", "/help/DateCreated.template");
        addLink("DateCreatedFreeFormEditLink", "/help/DateCreatedFreeForm.template");
        addLink("AccessRightsEditLink", "/help/AccessRights.template");
        addLink("UploadEditLink", "/help/Upload.template");
        addLink("AudienceEditLink", "/help/Audience.template");
        addLink("CmdiChoiceEditLink", "/help/CmdiChoice.template");

        addLink("ContributorDCEditLink", "/help/Contributor.template");
        addLink("ContributorEASEditLink", "/help/EasContributor.template");
        addLink("SubjectEditLink", "/help/Subject.template");
        addLink("SpatialEditLink", "/help/Spatial.template");
        addLink("TemporalEditLink", "/help/Temporal.template");
        addLink("SourceEditLink", "/help/Source.template");
        addLink("IdentifierEditLink", "/help/Identifier.template");

        addLink("FormatEditLink", "/help/Format.template");
        addLink("RelationEditLink", "/help/Relation.template");
        addLink("LanguageIso639EditLink", "/help/LanguageIso639.template");
        addLink("LanguageEditLink", "/help/Language.template");
        addLink("RemarksEditLink", "/help/Remarks.template");

        addLink("AlternativeEditLink", "/help/Alternative.template");
        addLink("PakbonEditLink", "/help/Pakbon.template");
        addLink("ArchisEditLink", "/help/Archis.template");
        addLink("DateEditLink", "/help/Date.template");
        addLink("DateIso8601EditLink", "/help/DateIso8601.template");
        addLink("DateAvailableEditLink", "/help/DateAvailable.template");
        addLink("FormatIMTEditLink", "/help/FormatIMT.template");
        addLink("PublisherEditLink", "/help/Publisher.template");
        addLink("RightsHolderEditLink", "/help/RightsHolder.template");
        addLink("SpatialBoxEditLink", "/help/SpatialBox.template");
        addLink("SpatialPointEditLink", "/help/SpatialPoint.template");
        addLink("SubjectAbrEditLink", "/help/SubjectAbr.template");
        addLink("TemporalAbrEditLink", "/help/TemporalAbr.template");
        addLink("TypeEditLink", "/help/Type.template");
        addLink("TypeDCMIEditLink", "/help/TypeDCMI.template");
    }

    private void addLink(final String id, final String path, final Object... placeholders) {
        add(new Link(id) {
            public void onClick() {
                setResponsePage(new EditableTextPage(path, placeholders));
            }
        });
    }

    @Override
    /**
     * Disable Caching on this page. Reload the page when the user clicks on the browser-back button.
     */
    protected void configureResponse() {
        super.configureResponse();
        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
    }
}
