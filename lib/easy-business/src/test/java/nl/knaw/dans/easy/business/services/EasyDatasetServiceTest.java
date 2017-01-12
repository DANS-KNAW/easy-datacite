package nl.knaw.dans.easy.business.services;

import static nl.knaw.dans.easy.data.store.EasyUnitOfWork.createIngestMessage;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.DataciteServiceConfiguration;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class EasyDatasetServiceTest extends TestHelper {

    private static EasyStore easyStore;
    private static FileStoreAccess fileStoreAccess;
    private static DatasetService service;
    private static EasyUserRepo userRepo;
    private static DisciplineCollection disciplineCollection;

    @Before
    public void before() throws Exception {
        new Security(new CodedAuthz());

        fileStoreAccess = createMock(FileStoreAccess.class);
        new Data().setFileStoreAccess(fileStoreAccess);

        easyStore = createMock(EasyStore.class);
        Data data = new Data();
        data.setEasyStore(easyStore);

        userRepo = createMock(EasyUserRepo.class);
        data.setUserRepo(userRepo);

        disciplineCollection = createMock(DisciplineCollection.class);

        DataciteServiceConfiguration dataciteServiceConfiguration = new DataciteServiceConfiguration();
        dataciteServiceConfiguration.setDatasetResolver(new URL("http://some.domain/and/path"));
        service = new EasyDatasetService(dataciteServiceConfiguration);
        Whitebox.setInternalState(service, "disciplineCollection", disciplineCollection);
    }

    @After
    public void after() {
        resetAll();
    }

    @AfterClass
    public static void afterClass() {
        // the next test class should not inherit from this one
        Data data = new Data();
        data.setEasyStore(null);
        data.setUserRepo(null);
        data.setFileStoreAccess(null);
    }

    @Test
    public void getDataModelObject() throws ServiceException, ObjectNotInStoreException, RepositoryException {
        DmoStoreId storeId = new DmoStoreId("easy-dataset:123");
        EasyUser user = new EasyUserImpl("foo");
        user.addRole(Role.ARCHIVIST);
        user.setState(State.ACTIVE);

        expect(easyStore.retrieve(storeId)).andReturn(new DatasetImpl("easy-dataset:123"));

        replayAll();
        service.getDataModelObject(user, storeId);
        verifyAll();

    }

    @Test
    @Ignore
    public void getDataset() throws Exception {
        DmoStoreId storeId = new DmoStoreId("easy-dataset:123");
        EasyUser user = new EasyUserImpl("foo");
        user.addRole(Role.ARCHIVIST);
        user.setState(State.ACTIVE);

        EasyMock.reset(easyStore);
        EasyMock.expect(easyStore.retrieve(storeId)).andReturn(new DatasetImpl("easy-dataset:123"));

        EasyMock.expect(fileStoreAccess.getFolderItemVO(isA(DmoStoreId.class))).andStubReturn(null);
        // TODO fix next, still reported as not expected method call
        EasyMock.expect(fileStoreAccess.getItemVoVisibilities(null)).andStubReturn(new HashSet<VisibleTo>());

        EasyMock.replay(easyStore, fileStoreAccess);
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
    public void saveEasyMetadataNoDepositorOnDataset() throws Exception {
        DataIntegrityException t = null;
        TestReporter reporter = new TestReporter();
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        EasyUser user = new EasyUserImpl("foo");
        user.addRole(Role.ARCHIVIST);
        user.setState(State.ACTIVE);

        try {
            service.saveEasyMetadata(user, dataset, reporter);
        }
        catch (ServiceException e) {
            t = (DataIntegrityException) e.getCause();
        }
        assertEquals(0, reporter.getTotalActionCount());
        assertEquals(1, reporter.reportedExceptions.size());
        assertEquals(t, reporter.reportedExceptions.get(0).getCause());

        throw (t);
    }

    @Test
    public void saveEasyMetadataStateUnsaved() throws Exception {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("jan");
        EasyUser sessionUser = new EasyUserImpl("jan");

        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);

        TestReporter reporter = new TestReporter();

        expect(easyStore.ingest(dataset, createIngestMessage("dummy-dataset:1", sessionUser))).andReturn("dummy-dataset:1").times(1);
        expect(userRepo.exists("jan")).andReturn(true);
        replayAll();
        {
            service.saveEasyMetadata(sessionUser, dataset, reporter);
            // DRAFT flag set by DatasetStore.ingest
            // assertEquals(AdministrativeState.DRAFT, dataset.getAdministrativeState());
        }
        verifyAll();
        assertEquals(1, reporter.getTotalActionCount());
        assertEquals(1, reporter.getIngestedDatasetCount());
    }

    @Test
    public void saveAdministrativeMetadataStateUnsaved() throws Exception {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("piet");

        EasyUser sessionUser = new EasyUserImpl("jan");
        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);
        sessionUser.addRole(Role.ARCHIVIST);

        TestReporter reporter = new TestReporter();

        expect(easyStore.ingest(dataset, createIngestMessage("dummy-dataset:1", sessionUser))).andReturn("dummy-dataset:1").times(1);
        expect(userRepo.exists("piet")).andReturn(true);
        replayAll();
        {
            service.saveAdministrativeMetadata(sessionUser, dataset, reporter);
        }
        verifyAll();
        assertEquals(1, reporter.getTotalActionCount());
        assertEquals(1, reporter.getIngestedDatasetCount());
    }

    @Test(expected = ServiceException.class)
    public void saveEasyMetadataStateUnsavedAndException() throws Exception {
        ServiceException se = null;
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositorId("jan");
        EasyUser sessionUser = new EasyUserImpl("jan");
        sessionUser.setFirstname("Jan");
        sessionUser.setSurname("Janssen");
        sessionUser.setState(State.ACTIVE);

        TestReporter reporter = new TestReporter();

        expect(easyStore.ingest(dataset, createIngestMessage("dummy-dataset:1", sessionUser))).andThrow(new RepositoryException("Store closed for holidays."));
        expect(userRepo.exists("jan")).andReturn(true);

        expect(fileStoreAccess.hasMember(isA(DmoStoreId.class), eq(FileItemVO.class))).andStubReturn(true);

        replayAll();

        try {
            service.saveEasyMetadata(sessionUser, dataset, reporter);
        }
        catch (ServiceException e) {
            se = e;
        }

        assertEquals(0, reporter.getTotalActionCount());
        assertEquals(1, reporter.reportedExceptions.size());
        assertEquals(se, reporter.reportedExceptions.get(0));
        verifyAll();

        throw (se);
    }

    @Ignore("EasyFedoraStore takes care of shifting dirty units.")
    @Test
    public void saveEasyMetadataStateSavedAndNotDirty() throws Exception {
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

        expect(userRepo.exists("jan")).andStubReturn(true);
        expect(easyStore.isUpdateable(dataset, "jan"));
        // TODO fix next, still reported as not expected method call
        expect(fileStoreAccess.hasMember(isA(DmoStoreId.class), eq(FileItemVO.class))).andStubReturn(false);
        replayAll();

        // For updating MetadataUnits an arbitrary StoreImpl is chosen by EasyUnitOfWork.
        // It happens to be FileItemStore.

        // Since nothing is dirty, we do not expect updates. (if dirty checking in emd is done with
        // string comparison)
        {
            service.saveEasyMetadata(sessionUser, dataset, reporter);
        }
        verifyAll();

        assertEquals(0, reporter.getTotalActionCount());
        assertEquals(0, reporter.getUpdatedMetadataUnitCount());
        assertEquals(0, reporter.reportedExceptions.size());

    }

    @Test(expected = CommonSecurityException.class)
    public void submitDatasetNoDepositor() throws Exception {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        EasyUser sessionUser = new EasyUserImpl("abc");
        sessionUser.setState(State.ACTIVE);
        dataset.getAdministrativeMetadata().setDepositorId("xyz");
        DatasetSubmissionImpl submission = new DatasetSubmissionImpl(null, dataset, sessionUser);
        expect(fileStoreAccess.hasMember(isA(DmoStoreId.class), eq(FileItemVO.class))).andStubReturn(true);
        replayAll();
        service.submitDataset(submission);
    }

    private static int newDmoCount;

    @Test
    public void createDatasetTest() throws RepositoryException, ServiceException, ObjectNotFoundException, DomainException {
        final Dataset input = new DatasetImpl("dummy-dataset:1");

        newDmoCount = 0;
        AbstractDmoFactory.register(Dataset.NAMESPACE, new AbstractDmoFactory<Dataset>() {
            @Override
            public Dataset newDmo() {
                newDmoCount++;
                return input;
            }

            @Override
            public DmoNamespace getNamespace() {
                throw new IllegalStateException("call not expected");
            }

            @Override
            public Dataset createDmo(String storeId) {
                throw new IllegalStateException("call not expected storeId=" + storeId);
            }
        });

        Dataset output = service.newDataset(MetadataFormat.DEFAULT);
        assertEquals(0, output.getEasyMetadata().getEmdAudience().getTermsAudience().size());
        assertEquals(input, output);
        assertEquals(newDmoCount, 1);
    }

    private static class TestReporter extends WorkReporter {

        List<Throwable> reportedExceptions = new ArrayList<Throwable>();

        @Override
        public void onException(Throwable t) {
            super.onException(t);
            reportedExceptions.add(t);
        }
    }

}
