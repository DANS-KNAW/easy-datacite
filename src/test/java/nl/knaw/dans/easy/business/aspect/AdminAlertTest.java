package nl.knaw.dans.easy.business.aspect;

import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.mail.AdminMailer;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.business.services.EasyDatasetService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class AdminAlertTest
{

    private static EasyStore store;
    private static FooMailer mailer;
    private static EasyUserRepo userRepo;

    @BeforeClass
    public static void beforeClass()
    {
        store = EasyMock.createMock(EasyStore.class);
        Data data = new Data();
        data.setEasyStore(store);

        userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        mailer = new FooMailer();
        ExternalServices extServices = new ExternalServices();
        extServices.setAdminMailer(mailer);

        new Security(new CodedAuthz());
    }

    @Test
    public void serviceExceptionCatcher() throws Exception
    {
        EasyUser sessionUser = new EasyUserImpl("ben");
        sessionUser.setState(State.ACTIVE);

        DatasetService ds = new EasyDatasetService();

        Dataset dataset = new DatasetImpl("foo");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.DRAFT);
        dataset.getAdministrativeMetadata().setDepositorId("ben");

        EasyMock.reset(store, userRepo);
        EasyMock.expect(userRepo.exists("ben")).andReturn(true);
        EasyMock.expect(store.ingest(EasyMock.isA(Dataset.class), EasyMock.isA(String.class))).andThrow(new RepositoryException("foo&bar")).anyTimes();
        EasyMock.replay(store, userRepo);

        try
        {
            ds.saveEasyMetadata(sessionUser, dataset);
        }
        catch (ServiceException e)
        {
            // expected
        }

        EasyMock.verify(store, userRepo);
        checkMail("ServiceException");
    }

    private void checkMail(String check)
    {
        if (Tester.isVerbose())
            System.out.println("\n--------- subject --------\n" + mailer.subject);

        if (Tester.isVerbose())
            System.out.println("\n+++++++++ message ++++++++\n" + mailer.text + "\n");

        assertTrue(mailer.text.contains(check));
    }

    static class FooMailer extends AdminMailer
    {
        String subject;
        String text;
        boolean fail;

        public FooMailer()
        {
            super(null, "Easy");
        }

        @Override
        protected boolean send(String subject, String text)
        {
            this.subject = subject;
            this.text = text;
            return !fail;
        }

        public void setFail(boolean fail)
        {
            this.fail = fail;
        }
    }

}
