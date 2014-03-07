package nl.knaw.dans.easy.business.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollection;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.util.TestHelper;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractDmoFactory.class)
public class EasyDatasetServiceTest extends TestHelper
{

    private static EasyStore easyStore;
    private static DatasetService service;
    private static EasyUserRepo userRepo;
    private static DisciplineCollection disciplineCollection;

    @BeforeClass
    public static void beforeClass()
    {
        new Security(new CodedAuthz());

        easyStore = EasyMock.createMock(EasyStore.class);
        Data data = new Data();
        data.setEasyStore(easyStore);

        userRepo = EasyMock.createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        disciplineCollection = EasyMock.createMock(DisciplineCollection.class);

        service = new EasyDatasetService(disciplineCollection);
    }

    @Test
    public void getDataModelObject() throws ServiceException, ObjectNotInStoreException, RepositoryException
    {
        DmoStoreId storeId = new DmoStoreId("easy-dataset:123");
        EasyUser user = new EasyUserImpl("foo");
        user.addRole(Role.ARCHIVIST);
        user.setState(State.ACTIVE);

        EasyMock.reset(easyStore);
        EasyMock.expect(easyStore.retrieve(storeId)).andReturn(new DatasetImpl("easy-dataset:123"));

        EasyMock.replay(easyStore);
        service.getDataModelObject(user, storeId);
        EasyMock.verify(easyStore);

    }

    @Test
    public void getDataset() throws Exception
    {
        DmoStoreId storeId = new DmoStoreId("easy-dataset:123");
        EasyUser user = new EasyUserImpl("foo");
        user.addRole(Role.ARCHIVIST);
        user.setState(State.ACTIVE);

        EasyMock.reset(easyStore);
        EasyMock.expect(easyStore.retrieve(storeId)).andReturn(new DatasetImpl("easy-dataset:123"));

        EasyMock.replay(easyStore);
        Dataset dataset = service.getDataset(user, storeId);
        EasyMock.verify(easyStore);

        AuthzStrategy strategy = dataset.getAuthzStrategy();
        assertNotNull(strategy);

        // the dataset has no children so we expect they cannot be discovered.
        assertEquals(AuthzStrategy.TriState.NONE, strategy.canChildrenBeDiscovered());
        assertEquals(AuthzStrategy.TriState.NONE, strategy.canChildrenBeRead());

        // we are archivist so we can discover and read the unpublished dataset.
        assertTrue(strategy.canBeDiscovered());
        assertTrue(strategy.canBeRead());
    }

    @Test(expected = DataIntegrityException.class)
    public void saveEasyMetadataNoDepositorOnDataset() throws Exception
    {
        DataIntegrityException t = null;
        TestReporter reporter = new TestReporter();
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        EasyUser user = new EasyUserImpl("foo");
        user.addRole(Role.ARCHIVIST);
        user.setState(State.ACTIVE);

        try
        {
            service.saveEasyMetadata(user, dataset, reporter);
        }
        catch (ServiceException e)
        {
            t = (DataIntegrityException) e.getCause();
        }
        assertEquals(0, reporter.getTotalActionCount());
        assertEquals(1, reporter.reportedExceptions.size());
        assertEquals(t, reporter.reportedExceptions.get(0).getCause());

        throw (t);
    }

    @Test
    public void saveEasyMetadataStateUnsaved() throws Exception
    {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("jan");
        EasyUser sessionUser = new EasyUserImpl("jan");

        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);

        TestReporter reporter = new TestReporter();

        EasyMock.reset(easyStore, userRepo);
        EasyMock.expect(easyStore.ingest(dataset, EasyUnitOfWork.createIngestMessage("dummy-dataset:1", sessionUser))).andReturn("dummy-dataset:1").times(1);
        EasyMock.expect(userRepo.exists("jan")).andReturn(true);
        EasyMock.replay(easyStore, userRepo);
        {
            service.saveEasyMetadata(sessionUser, dataset, reporter);
            // DRAFT flag set by DatasetStore.ingest
            // assertEquals(AdministrativeState.DRAFT, dataset.getAdministrativeState());
        }
        EasyMock.verify(easyStore, userRepo);
        assertEquals(1, reporter.getTotalActionCount());
        assertEquals(1, reporter.getIngestedDatasetCount());
    }

    @Test
    public void saveAdministrativeMetadataStateUnsaved() throws Exception
    {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("piet");

        EasyUser sessionUser = new EasyUserImpl("jan");
        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);
        sessionUser.addRole(Role.ARCHIVIST);

        TestReporter reporter = new TestReporter();

        EasyMock.reset(easyStore, userRepo);
        EasyMock.expect(easyStore.ingest(dataset, EasyUnitOfWork.createIngestMessage("dummy-dataset:1", sessionUser))).andReturn("dummy-dataset:1").times(1);
        EasyMock.expect(userRepo.exists("piet")).andReturn(true);
        EasyMock.replay(easyStore, userRepo);
        {
            service.saveAdministrativeMetadata(sessionUser, dataset, reporter);
        }
        EasyMock.verify(easyStore, userRepo);
        assertEquals(1, reporter.getTotalActionCount());
        assertEquals(1, reporter.getIngestedDatasetCount());
    }

    @Test(expected = ServiceException.class)
    public void saveEasyMetadataStateUnsavedAndException() throws Exception
    {
        ServiceException se = null;
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("jan");
        EasyUser sessionUser = new EasyUserImpl("jan");
        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);

        TestReporter reporter = new TestReporter();

        EasyMock.reset(easyStore, userRepo);
        EasyMock.expect(easyStore.ingest(dataset, EasyUnitOfWork.createIngestMessage("dummy-dataset:1", sessionUser))).andThrow(
                new RepositoryException("Store closed for holidays."));
        EasyMock.expect(userRepo.exists("jan")).andReturn(true);
        EasyMock.replay(easyStore, userRepo);

        try
        {
            service.saveEasyMetadata(sessionUser, dataset, reporter);
        }
        catch (ServiceException e)
        {
            se = e;
        }

        EasyMock.verify(easyStore, userRepo);
        assertEquals(0, reporter.getTotalActionCount());
        assertEquals(1, reporter.reportedExceptions.size());
        assertEquals(se, reporter.reportedExceptions.get(0));

        throw (se);
    }

    @Ignore("EasyFedoraStore takes care of shifting dirty units.")
    @Test
    public void saveEasyMetadataStateSavedAndNotDirty() throws Exception
    {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("jan");
        dataset.setStoreId("easy-dataset:333");
        dataset.setLoaded(true);
        ((AbstractDataModelObject) dataset).setDirty(false);

        dataset.getAdministrativeMetadata().setDirty(false);

        EasyUser sessionUser = new EasyUserImpl("jan");
        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);

        TestReporter reporter = new TestReporter();

        // For updating MetadataUnits an arbitrary StoreImpl is chosen by EasyUnitOfWork.
        // It happens to be FileItemStore.
        EasyMock.reset(easyStore);

        // Since nothing is dirty, we do not expect updates. (if dirty checking in emd is done with
        // string comparison)
        EasyMock.replay(easyStore);
        {
            service.saveEasyMetadata(sessionUser, dataset, reporter);
        }
        EasyMock.verify(easyStore);

        assertEquals(0, reporter.getTotalActionCount());
        assertEquals(0, reporter.getUpdatedMetadataUnitCount());
        assertEquals(0, reporter.reportedExceptions.size());

    }

    @Test(expected = CommonSecurityException.class)
    public void submitDatasetNoDepositor() throws Exception
    {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        EasyUser sessionUser = new EasyUserImpl("abc");
        sessionUser.setState(State.ACTIVE);
        dataset.getAdministrativeMetadata().setDepositorId("xyz");
        DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, sessionUser);
        service.submitDataset(submission);
    }

    @Test
    public void createDatasetTest() throws RepositoryException, ServiceException, ObjectNotFoundException, DomainException
    {
        Dataset input = new DatasetImpl("dummy-dataset:1");

        // test SOCIOLOGY, UNSPECIFIED AND HISTORY
        PowerMock.mockStatic(AbstractDmoFactory.class);
        EasyMock.reset(easyStore);
        EasyMock.expect(AbstractDmoFactory.newDmo(Dataset.NAMESPACE)).andReturn(input).times(3);
        PowerMock.replay(AbstractDmoFactory.class);
        {
            Dataset output = service.newDataset(MetadataFormat.SOCIOLOGY);
            assertEquals(0, output.getEasyMetadata().getEmdAudience().getTermsAudience().size());
            assertEquals(input, output);

            output = service.newDataset(MetadataFormat.UNSPECIFIED);
            assertEquals(0, output.getEasyMetadata().getEmdAudience().getTermsAudience().size());
            assertEquals(input, output);

            output = service.newDataset(MetadataFormat.HISTORY);
            assertEquals(0, output.getEasyMetadata().getEmdAudience().getTermsAudience().size());
            assertEquals(input, output);
        }
        PowerMock.verify(AbstractDmoFactory.class);

        // test ARCHAEOLOGY
        DisciplineContainer discInput = new DisciplineContainerImpl("dummy-discipline:1");

        PowerMock.reset(AbstractDmoFactory.class);
        EasyMock.expect(AbstractDmoFactory.newDmo(Dataset.NAMESPACE)).andReturn(input).times(1);
        EasyMock.reset(disciplineCollection);
        EasyMock.expect(disciplineCollection.getDisciplineByName(MetadataFormat.ARCHAEOLOGY.name())).andReturn(discInput).times(1);
        EasyMock.replay(easyStore, disciplineCollection);
        PowerMock.replay(AbstractDmoFactory.class);
        {
            Dataset output = service.newDataset(MetadataFormat.ARCHAEOLOGY);

            assertEquals(1, output.getEasyMetadata().getEmdAudience().getTermsAudience().size());
            BasicString audience = output.getEasyMetadata().getEmdAudience().getTermsAudience().get(0);
            assertEquals("dummy-discipline:1", audience.getValue());
            assertEquals(ChoiceListGetter.CHOICELIST_CUSTOM_PREFIX + ChoiceListGetter.CHOICELIST_DISCIPLINES_POSTFIX, audience.getSchemeId());
        }
        EasyMock.verify(disciplineCollection);
        PowerMock.verify(AbstractDmoFactory.class);
    }

    private static class TestReporter extends WorkReporter
    {

        List<Throwable> reportedExceptions = new ArrayList<Throwable>();

        @Override
        public void onException(Throwable t)
        {
            super.onException(t);
            reportedExceptions.add(t);
        }
    }

}
