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
import nl.knaw.dans.easy.domain.model.emd.EmdIdentifier;
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
import nl.knaw.dans.easy.web.fileexplorer2.ModalDownload;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.permission.PermissionRequestForm;
import nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyRequestsSearchResultPage;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.https.RequireHttps;

@RequireHttps
public class EditableContentPage extends AbstractEasyNavPage
{
    public EditableContentPage()
    {
        super();

        // dummy objects
        EasyUser user = EasySession.get().getUser();
        Dataset dataset = new DatasetImpl("easy-dataset:1");
        DatasetSubmissionImpl datasetSubmission = new DatasetSubmissionImpl(new FormDefinition(""), dataset, user);
        PermissionReplyModel prm = new PermissionReplyModel("test");
        prm.setState(State.Granted);

        // LINKS TO EDITABLE PAGES
        addLink("homepage", HomePage.EDITABLE_HOMEPAGE_TEMPLATE);
        addLink("registration", RegistrationPage.EDITABLE_REGISTRATION_TEMPLATE);
        addLink("depositIntro", DepositIntroPage.EDITABLE_DEPOSIT_INTRO_TEMPLATE);
        addLink("depositComplete", DepositPanel.EDITABLE_DEPOSIT_COMPLETE_TEMPLATE, user);
        addLink("licensePanel", LicensePanel.EDITABLE_LICENSE_PANEL_TEMPLATE, new EmdIdentifier());
        addLink("licensePanelOtherAccess", LicensePanel.EDITABLE_LICENSE_PANEL_OTHER_ACCESS_TEMPLATE, new EmdIdentifier());
        addLink("downloadDialog", ModalDownload.EDITABLE_DOWNLOAD_DIALOG_TEMPLATE);
        addLink("permissionRequest", PermissionRequestForm.EDITABLE_PERMISSION_REQUEST_TEMPLATE);
        addLink("myDatasetsSearchResult", MyDatasetsSearchResultPage.MY_DATASETS_SEARCH_RESULTS);
        addLink("myRequestsSearchResult", MyRequestsSearchResultPage.MY_REQUESTS_SEARCH_RESULTS);

        // LINKS TO EDITABLE E-MAILS
        addLink("registrationMailHTML", "/mail/templates/authn/RegistrationMail.html", user, new Registration(user));
        addLink("registrationMailTXT", "/mail/templates/authn/RegistrationMail.txt", user, new Registration(user));

        addLink("updatePasswordMailHTML", "/mail/templates/authn/UpdatePasswordMail.html", user, new UpdatePasswordMessage(user,
                new ForgottenPasswordMessenger(null, "jan@jansen.com")));
        addLink("updatePasswordMailTXT", "/mail/templates/authn/UpdatePasswordMail.txt", user, new UpdatePasswordMessage(user, new ForgottenPasswordMessenger(
                null, "jan@jansen.com")));

        addLink("maintenanceNotificationMailHTML", "/mail/templates/maintenance/maintenaceNotification.html", user, dataset, new MaintenanceNotification(
                dataset));
        addLink("maintenanceNotificationMailTXT", "/mail/templates/maintenance/maintenaceNotification.txt", user, dataset, new MaintenanceNotification(dataset));

        addLink("depositConfirmationHTML", "/mail/templates/deposit/depositConfirmation.html", user, dataset, datasetSubmission, new SubmitNotification(
                datasetSubmission));
        addLink("depositConfirmationTXT", "/mail/templates/deposit/depositConfirmation.txt", user, dataset, datasetSubmission, new SubmitNotification(
                datasetSubmission));

        addLink("depositConfirmationOtherAccessHTML", "/mail/templates/deposit/depositConfirmationOtherAccess.html", user, dataset, datasetSubmission,
                new SubmitNotification(datasetSubmission));
        addLink("depositConfirmationOtherAccessTXT", "/mail/templates/deposit/depositConfirmationOtherAccess.txt", user, dataset, datasetSubmission,
                new SubmitNotification(datasetSubmission));

        addLink("newDepositorNotificationHTML", "/mail/templates/deposit/newDepositorNotification.html", user, dataset, new NewDepositorNotification(dataset,
                user, user));
        addLink("newDepositorNotificationTXT", "/mail/templates/deposit/newDepositorNotification.txt", user, dataset, new NewDepositorNotification(dataset,
                user, user));

        addLink("oldDepositorNotificationHTML", "/mail/templates/deposit/oldDepositorNotification.html", user, dataset, new OldDepositorNotification(dataset,
                user, user));
        addLink("oldDepositorNotificationTXT", "/mail/templates/deposit/oldDepositorNotification.txt", user, dataset, new OldDepositorNotification(dataset,
                user, user));

        addLink("unsubmitNotificationHTML", "/mail/templates/deposit/unsubmitNotification.html", user, dataset, new UnsubmitNotification(dataset));
        addLink("unsubmitNotificationTXT", "/mail/templates/deposit/unsubmitNotification.txt", user, dataset, new UnsubmitNotification(dataset));

        addLink("publishNotificationHTML", "/mail/templates/publish/publishNotification.html", user, dataset, new PublishNotification(dataset));
        addLink("publishNotificationTXT", "/mail/templates/publish/publishNotification.txt", user, dataset, new PublishNotification(dataset));

        addLink("republishNotificationHTML", "/mail/templates/publish/republishNotification.html", user, dataset, new RepublishNotification(dataset));
        addLink("republishNotificationTXT", "/mail/templates/publish/republishNotification.txt", user, dataset, new RepublishNotification(dataset));

        addLink("unpublishNotificationHTML", "/mail/templates/publish/unpublishNotification.html", user, dataset, new UnpublishNotification(dataset));
        addLink("unpublishNotificationTXT", "/mail/templates/publish/unpublishNotification.txt", user, dataset, new UnpublishNotification(dataset));

        addLink("deniedReplyNotificationHTML", "/mail/templates/permission/DeniedReplyNotification.html", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));
        addLink("deniedReplyNotificationTXT", "/mail/templates/permission/DeniedReplyNotification.txt", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));

        addLink("grantedReplyNotificationHTML", "/mail/templates/permission/GrantedReplyNotification.html", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));
        addLink("grantedReplyNotificationTXT", "/mail/templates/permission/GrantedReplyNotification.txt", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));

        addLink("requestNotificationHTML", "/mail/templates/permission/requestNotification.html", user, dataset, new RequestNotification(dataset, user,
                new PermissionRequestModel()));
        addLink("requestNotificationTXT", "/mail/templates/permission/requestNotification.txt", user, dataset, new RequestNotification(dataset, user,
                new PermissionRequestModel()));

        addLink("returnedReplyNotificationHTML", "/mail/templates/permission/ReturnedReplyNotification.html", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));
        addLink("returnedReplyNotificationTXT", "/mail/templates/permission/ReturnedReplyNotification.txt", user, dataset, new ReplyNotification(dataset,
                new PermissionSequenceImpl(user), prm));

        addLink("headerHTML", "/mail/templates/default/header.html", user, dataset);
        addLink("headerTXT", "/mail/templates/default/header.txt", user, dataset);

        addLink("footerHTML", "/mail/templates/default/footer.html", user, dataset);
        addLink("footerTXT", "/mail/templates/default/footer.txt", user, dataset);

        // LINKS TO SEARCH HELP TEXTS
        addLink("searchHelp", "/editable/help/Search.template");
        addLink("refineHelp", "/editable/help/Refine.template");

        // LINKS TO DEPOSIT HELP TEXTS
        addLink("CreatorDC", "/editable/help/Creator.template");
        addLink("CreatorEAS", "/editable/help/EasCreator.template");
        addLink("Title", "/editable/help/Title.template");
        addLink("Description", "/editable/help/Description.template");
        addLink("DateCreated", "/editable/help/DateCreated.template");
        addLink("DateCreatedFreeForm", "/editable/help/DateCreatedFreeForm.template");
        addLink("AccessRights", "/editable/help/AccessRights.template");
        addLink("Upload", "/editable/help/Upload.template");
        addLink("Audience", "/editable/help/Audience.template");

        addLink("ContributorDC", "/editable/help/Contributor.template");
        addLink("ContributorEAS", "/editable/help/EasContributor.template");
        addLink("Subject", "/editable/help/Subject.template");
        addLink("Spatial", "/editable/help/Spatial.template");
        addLink("Temporal", "/editable/help/Temporal.template");
        addLink("Source", "/editable/help/Source.template");
        addLink("Identifier", "/editable/help/Identifier.template");

        addLink("Format", "/editable/help/Format.template");
        addLink("Relation", "/editable/help/Relation.template");
        addLink("LanguageIso639", "/editable/help/LanguageIso639.template");
        addLink("Language", "/editable/help/Language.template");
        addLink("Remarks", "/editable/help/Remarks.template");

        addLink("Alternative", "/editable/help/Alternative.template");
        addLink("Archis", "/editable/help/Archis.template");
        addLink("Date", "/editable/help/Date.template");
        addLink("DateIso8601", "/editable/help/DateIso8601.template");
        addLink("DateAvailable", "/editable/help/DateAvailable.template");
        addLink("FormatIMT", "/editable/help/FormatIMT.template");
        addLink("Publisher", "/editable/help/Publisher.template");
        addLink("RightsHolder", "/editable/help/RightsHolder.template");
        addLink("SpatialBox", "/editable/help/SpatialBox.template");
        addLink("SpatialPoint", "/editable/help/SpatialPoint.template");
        addLink("SubjectAbr", "/editable/help/SubjectAbr.template");
        addLink("TemporalAbr", "/editable/help/TemporalAbr.template");
        addLink("Type", "/editable/help/Type.template");
        addLink("TypeDCMI", "/editable/help/TypeDCMI.template");
    }

    private void addLink(final String id, final String path, final Object... placeholders)
    {
        add(new Link(id)
        {
            public void onClick()
            {
                setResponsePage(new EditableTextPage(path, placeholders));
            }
        });
    }

    @Override
    /**
     * Disable Caching on this page. Reload the page when the user clicks on the browser-back button.
     */
    protected void configureResponse()
    {
        super.configureResponse();
        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
    }
}
