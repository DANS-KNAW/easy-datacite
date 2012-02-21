package nl.knaw.dans.easy.servicelayer;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.net.URL;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.AbstractNotification;
import nl.knaw.dans.easy.servicelayer.DatasetNotification;
import nl.knaw.dans.easy.servicelayer.DatasetUrlComposer;
import nl.knaw.dans.easy.servicelayer.MaintenanceNotification;
import nl.knaw.dans.easy.servicelayer.NewDepositorNotification;
import nl.knaw.dans.easy.servicelayer.OldDepositorNotification;
import nl.knaw.dans.easy.servicelayer.PublishNotification;
import nl.knaw.dans.easy.servicelayer.RegistrationConfirmation;
import nl.knaw.dans.easy.servicelayer.ReplyNotification;
import nl.knaw.dans.easy.servicelayer.RepublishNotification;
import nl.knaw.dans.easy.servicelayer.RequestNotification;
import nl.knaw.dans.easy.servicelayer.SubmitNotification;
import nl.knaw.dans.easy.servicelayer.UnpublishNotification;
import nl.knaw.dans.easy.servicelayer.UnsubmitNotification;
import nl.knaw.dans.easy.servicelayer.UpdatePasswordMessage;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NotificationTest extends AbstractMailFixture
{

    private static final String       PID             = "urn:nbn:nl:ui:13-xyz-123";
    private static final String       STORE_ID        = "easyId-1";
    private static final String       REQUEST_LINK    = "http://localhost:8080/view/request";

    private static Dataset            dataset;
    private static EasyUser           depositor;
    private static EasyUser           otherUser;
    private static PermissionSequence permissionSequence;

    @BeforeClass
    public static void setMailer() throws Exception
    {
        if (!online())
            return;
        new ExternalServices().setMailOffice(getMailer());
        new Data().setEasyStore(new DummyEasyStore()
        {
            @Override
            public URL getFileURL(final DmoStoreId storeId, final DsUnitId unitId)
            {
                return getClass().getResource(storeId + "-" + unitId.getUnitId() + ".pdf");
            }
        });
    }

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        AbstractMailFixture.beforeClass();
        System.setProperty("easy.home", "../easy-home");
        
        permissionSequence = EasyMock.createMock(PermissionSequence.class);
        dataset = EasyMock.createMock(Dataset.class);
        depositor = EasyMock.createMock(EasyUser.class);
        otherUser = EasyMock.createMock(EasyUser.class);

        final DatasetUrlComposer urlComposer = new DatasetUrlComposer()
        {
            public String getUrl(final String storeId)
            {
                return "http://localhost:8081/view/datasetId/" + storeId;
            }

            public String getPermissionUrl(final String storeId)
            {
                return "http://localhost:8081/view/tab/3/datasetId/" + storeId;
            }

            public String getFileExplorerUrl(String storeId)
            {
                return "http://localhost:8081/view/tab/2/datasetId/" + storeId;
            }

            @Override
            public String getMyDatasetsUrl(String storeId)
            {
                return "http://localhost:8081/mydatasets";
            }
        };
        DatasetNotification.setDatasetUrlComposer(urlComposer);

    }

    @Before
    public void prepare()
    {
        EasyMock.reset(dataset, depositor, otherUser, permissionSequence);

        EasyMock.expect(otherUser.getCommonName()).andReturn("Frits de Kat").anyTimes();
        EasyMock.expect(otherUser.getDisplayName()).andReturn("Frits").anyTimes();
        EasyMock.expect(otherUser.getId()).andReturn("FritsId").anyTimes();
        EasyMock.expect(otherUser.getEmail()).andReturn(getGuineaPig()).anyTimes();
        EasyMock.expect(otherUser.getPassword()).andReturn("geheim").anyTimes();

        EasyMock.expect(depositor.getCommonName()).andReturn("Merel de Bok").anyTimes();
        EasyMock.expect(depositor.getDisplayName()).andReturn("Merel").anyTimes();
        EasyMock.expect(depositor.getId()).andReturn("MerelId").anyTimes();
        EasyMock.expect(depositor.getEmail()).andReturn(getGuineaPig()).anyTimes();

        EasyMock.expect(dataset.getPreferredTitle()).andReturn("On the purchase of hats").anyTimes();
        EasyMock.expect(dataset.getPersistentIdentifier()).andReturn(PID).anyTimes();
        EasyMock.expect(dataset.getDepositor()).andReturn(depositor).anyTimes();
        EasyMock.expect(dataset.getStoreId()).andReturn(STORE_ID).anyTimes();
        EasyMock.expect(dataset.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS).anyTimes();

        EasyMock.expect(permissionSequence.getRequester()).andReturn(otherUser).anyTimes();

        otherUser.addRole(Role.USER);
        EasyMock.expectLastCall();

        EasyMock.replay(dataset, depositor, otherUser, permissionSequence);
    }

    @Test
    public void newDepositor() throws Exception
    {
        execute(new NewDepositorNotification(dataset, depositor, otherUser));
    }

    @Test
    public void oldDepositor() throws Exception
    {
        execute(new OldDepositorNotification(dataset, depositor, otherUser));
    }

    @Test
    public void deniedRequest() throws Exception
    {
        executePermissionReply(State.Denied, "helaas");
    }

    @Test
    public void grantedRequest() throws Exception
    {
        executePermissionReply(State.Granted, "waarom?");
    }

    @Test
    public void returnedRequest() throws Exception
    {
        executePermissionReply(State.Returned, "ok");
    }

    private void executePermissionReply(final State state, final String explanation) throws Exception
    {
        final String requesterId = null;
        final PermissionReplyModel permissionReply = new PermissionReplyModel(requesterId);
        // TODO code smell: names inconsistent
        permissionReply.setRequestLink(REQUEST_LINK);
        permissionReply.setExplanation(explanation);
        permissionReply.setState(state);

        final ReplyNotification sender = new ReplyNotification(dataset, permissionSequence, permissionReply);
        permissionReply.setDatasetLink(sender.getPermissionUrl());
        execute(sender);
    }

    @Test
    public void permissionRequest() throws Exception
    {
        final PermissionRequestModel request = new PermissionRequestModel();
        request.setRequestLink(REQUEST_LINK);

        final RequestNotification sender = new RequestNotification(dataset, otherUser, request);
        request.setPermissionsTabLink(sender.getPermissionUrl());
        execute(sender);
    }

    @Test
    public void publish() throws Exception
    {
        execute(new PublishNotification(dataset));
    }

    @Test
    public void maintenance() throws Exception
    {
        execute(new MaintenanceNotification(dataset));
    }

    @Test
    public void republish() throws Exception
    {
        execute(new RepublishNotification(dataset));
    }

    @Test
    public void submission() throws Exception
    {
        final DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, depositor);
        executeWithLicense(new SubmitNotification(submission));
    }

    @Test
    public void unpublish() throws Exception
    {
        execute(new UnpublishNotification(dataset));
    }

    @Test
    public void unsubmit() throws Exception
    {
        execute(new UnsubmitNotification(dataset));
    }

    @Test
    public void updatePassword() throws Exception
    {
        final ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public String getUpdateURL()
            {
                return "http://localhost:8081/mocked/updatePassword";
            }

            @Override
            public String getUserIdParamKey()
            {
                return "id";
            }
        };
        execute(new UpdatePasswordMessage(otherUser, messenger));
    }

    @Test
    public void register() throws Exception
    {
        final Registration registration = new Registration(otherUser);
        registration.setToken("2068295076");
        final String validationUrl = "http://localhost:8081/validate/token/%s/dateTime/1276870780707/userId/%s";
        registration.setValidationUrl(String.format(validationUrl, "2068295076", otherUser.getId()));
        execute(new RegistrationConfirmation(registration));
    }

    private void execute(final AbstractNotification sender) throws Exception
    {
        if (online)
        {
            assertTrue(sender.sendMail());
        }
        executeComposer(sender);
    }

    private void executeWithLicense(final DatasetNotification sender) throws Exception
    {
        if (online)
        {
            assertTrue(sender.sendMail(true));
        }
        executeComposer(sender);
    }

    private void executeComposer(final AbstractNotification sender) throws Exception
    {
        showPlaceHolders(sender);
        final String subject = sender.getSubject();
        final String text = sender.getText();
        final String html = sender.getHtml();
        if (verbose)
        {
            print(subject);
            print(text);
            print(html);
        }
    }

    private void showPlaceHolders(final AbstractNotification sender)
    {
        if (verbose)
        {
            String s = "==============================\ntemplate: " + sender.getTemplateLocation() + "\npossible placeholders:";
            for (final Method m : sender.getClass().getMethods())
            {
                if (m.getReturnType().equals(String.class))
                {
                    s += "\n\t" + sender.getClass().getSimpleName() + "." + m.getName();
                }
            }
            for (Object o : sender.placeholderSuppliers)
                s += showMorePlaceHolders(getClass(o));
            print(s);
        }
    }

    private Class<? extends Object> getClass(Object o)
    {
        if (o instanceof EasyUser)
            return EasyUser.class;
        else if (o instanceof Dataset)
            return Dataset.class;
        else if (o instanceof PermissionSequence)
            return PermissionSequence.class;
        return o.getClass();
    }

    private String showMorePlaceHolders(final Class<?> placeHolderSupplierClass)
    {
        String s = "";
        for (final Method m : placeHolderSupplierClass.getMethods())
        {
            if (m.getReturnType().equals(String.class))
            {
                s += "\n\t" + placeHolderSupplierClass.getSimpleName() + "." + m.getName();
            }
        }
        return s;
    }
}
