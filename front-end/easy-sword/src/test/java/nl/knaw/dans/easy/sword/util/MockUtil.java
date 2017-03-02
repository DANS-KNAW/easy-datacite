package nl.knaw.dans.easy.sword.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.business.item.ItemIngesterDelegator;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.authn.Authentication.State;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.sword.Context;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public class MockUtil {
    public static final String PASSWORD = "secret";

    public static final String INVALID_USER_ID = "nobody";
    public static final String VALID_USER_ID = "somebody";
    public static final String ARCHIV_USER_ID = "archivist";
    public static final String UNAUTHORIZED_USER_ID = "somebodyunauthorized";
    public static final String NO_OP_STORE_ID_DOMAIN = "mockedStoreID:";
    public static final EasyUserImpl USER = createSomeBody();
    public static final EasyUserImpl ARCHIVIST = createArchivist();
    public static final EasyUserImpl UNAUTHORIZED_USER = createSomeBodyUnAuthorized();

    private static final int MAX_NR_OF_VERBOSE_NO_OP_TESTS = 1;
    private static int countDatasets = 0;

    private static Services services;

    public static void mockAll() throws Exception {
        mockNow();
        mockItemService();
        mockDatasetService();
        mockUser();
        mockDisciplineService();
        mockFileStoreAccess();
        mockContext();
    }

    public static void mockContext() throws Exception {
        final Context context = new Context();
        context.setCollectionPolicy("No guarantee of service, or that deposits will be retained for any length of time.");
        context.setCollectionTreatment("This is a test server");
        context.setCollectionAbstract("Electronic Archive System, accepts deposits by users registered on {0}");
        context.setdepositTreatment("Details are sent to ~EasyUser.getEmail~");
        context.setCollectionTitle("EASY");
        context.setWorkspaceTitle("DANS sword interface");
        context.setDatasetPath("/ui/datasets/id/");
        context.setProviderURL("http://mockedhost:8080/");
        context.setServletName("servlet/request");
        context.setEasyHome("http://mockedhost:8080/ui/");
        context.setAcceptPackaging("https://easy.dans.knaw.nl/schemas/docs/sword-v1-packaging.html");

        SystemReadOnlyStatus systemReadOnlyStatus = new SystemReadOnlyStatus(new File("target/SystemStatus.properties"));
        context.setSystemReadOnlyStatus(systemReadOnlyStatus);
    }

    private static void mockNow() {
        DateTimeUtils.setCurrentMillisFixed(new DateTime("2011-08-29T14:42:08").getMillis());
    }

    public static void mockFileStoreAccess() throws Exception {
        final FileStoreAccess fileStoreAccess = EasyMock.createMock(FileStoreAccess.class);
        new Data().setFileStoreAccess(fileStoreAccess);

        for (int i = 1; i <= MAX_NR_OF_VERBOSE_NO_OP_TESTS; i++)
            EasyMock.expect(fileStoreAccess.getFilenames(//
                    new DmoStoreId(NO_OP_STORE_ID_DOMAIN + i)) //
            ).andReturn(Arrays.asList(new String[] {"just-a-file-name"})).anyTimes();
        EasyMock.replay(fileStoreAccess);
    }

    @SuppressWarnings("unchecked")
    public static void mockItemService() throws Exception {
        final ItemService itemService = EasyMock.createMock(ItemService.class);
        getServices().setItemService(itemService);

        itemService.addDirectoryContents(//
                EasyMock.isA(EasyUserImpl.class), //
                EasyMock.isA(DatasetImpl.class), //
                EasyMock.isA(DmoStoreId.class), //
                EasyMock.isA(File.class), //
                EasyMock.isA(ItemIngesterDelegator.class), //
                EasyMock.isA(WorkReporter.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(itemService.getFilesAndFolders(//
                EasyMock.isA(EasyUserImpl.class), //
                EasyMock.isA(DatasetImpl.class), //
                EasyMock.isA(DmoStoreId.class))//
        ).andReturn(new ArrayList<ItemVO>()).anyTimes();

        EasyMock.expect(itemService.getFilesAndFolders(//
                EasyMock.isA(EasyUserImpl.class), //
                EasyMock.isA(DatasetImpl.class), //
                EasyMock.isA(DmoStoreId.class)) // parent sid
        ).andReturn(new ArrayList<ItemVO>()).anyTimes();

        EasyMock.expect(itemService.getFiles(//
                EasyMock.isA(EasyUserImpl.class), //
                EasyMock.isA(DatasetImpl.class), //
                EasyMock.isA(DmoStoreId.class)) //
        ).andReturn(new ArrayList<FileItemVO>()).anyTimes();

        EasyMock.replay(itemService);
    }

    private static Services getServices() {
        if (services == null)
            services = new Services();
        return services;
    }

    public static void mockDatasetService() throws Exception {
        // no increment of countDatasets as it makes the test results
        // unpredictable
        final Dataset dataset = new DatasetImpl("mock:" + (countDatasets), MetadataFormat.SOCIOLOGY);
        final DatasetService datasetService = EasyMock.createMock(DatasetService.class);
        getServices().setDatasetService(datasetService);

        EasyMock.expect(datasetService.newDataset(EasyMock.isA(MetadataFormat.class))).andReturn(dataset).anyTimes();
        datasetService.submitDataset(EasyMock.isA(DatasetSubmissionImpl.class), EasyMock.isA(WorkListener.class));
        EasyMock.expectLastCall().anyTimes();

        datasetService.submitDataset(EasyMock.isA(DatasetSubmissionImpl.class));
        EasyMock.expectLastCall().anyTimes();

        datasetService.publishDataset(//
                EasyMock.isA(EasyUser.class),//
                EasyMock.isA(DatasetImpl.class),//
                EasyMock.eq(false),//
                EasyMock.eq(true));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(datasetService);
    }

    private static Data data = new Data();

    public static void mockUser() throws Exception {
        final EasyUserRepo userRepo = EasyMock.createMock(EasyUserRepo.class);
        final UserService userService = EasyMock.createMock(UserService.class);

        data.setUserRepo(userRepo);
        getServices().setUserService(userService);

        EasyMock.expect(userRepo.authenticate(INVALID_USER_ID, PASSWORD)).andReturn(false).anyTimes();
        EasyMock.expect(userRepo.authenticate(null, null)).andReturn(false).anyTimes();
        EasyMock.expect(userRepo.authenticate("", "")).andReturn(false).anyTimes();

        EasyMock.expect(userRepo.findById(ARCHIV_USER_ID)).andReturn(ARCHIVIST).anyTimes();
        EasyMock.expect(userRepo.findById(INVALID_USER_ID)).andReturn(null).anyTimes();

        EasyMock.expect(userRepo.findById(UNAUTHORIZED_USER_ID)).andReturn(UNAUTHORIZED_USER).anyTimes();
        EasyMock.expect(userRepo.exists(UNAUTHORIZED_USER_ID)).andReturn(true).anyTimes();
        EasyMock.expect(userRepo.authenticate(UNAUTHORIZED_USER_ID, PASSWORD)).andReturn(true).anyTimes();

        EasyMock.expect(userRepo.findById(VALID_USER_ID)).andReturn(USER).anyTimes();
        EasyMock.expect(userRepo.exists(VALID_USER_ID)).andReturn(true).anyTimes();
        EasyMock.expect(userRepo.authenticate(VALID_USER_ID, PASSWORD)).andReturn(true).anyTimes();

        final UsernamePasswordAuthentication value = new UsernamePasswordAuthentication(PASSWORD, VALID_USER_ID);
        value.setState(State.Authenticated);
        value.setUser(new EasyUserImpl(VALID_USER_ID));
        userService.authenticate(EasyMock.isA(UsernamePasswordAuthentication.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(userRepo, userService);
    }

    public static void mockDisciplineService() throws Exception {

        final DmoStoreId disciplineId = new DmoStoreId("easy-discipline:2");
        final DisciplineContainerImpl discipline = new DisciplineContainerImpl(disciplineId.getId());
        final DisciplineCollectionService disciplineService = EasyMock.createMock(DisciplineCollectionService.class);;
        getServices().setDisciplineService(disciplineService);
        discipline.setName("Humanities");

        EasyMock.expect(disciplineService.getDisciplineById(EasyMock.isA(DmoStoreId.class))).andReturn(discipline).anyTimes();
        EasyMock.replay(disciplineService);
    }

    private static EasyUserImpl createSomeBody() {
        final EasyUserImpl user = new EasyUserImpl();
        user.setId(VALID_USER_ID);
        user.setPassword(PASSWORD);
        user.setInitials("S.");
        user.setFirstname("Some");
        user.setSurname("Body");
        user.setEmail("some@body.com");
        user.setState(EasyUser.State.ACTIVE);
        user.setSwordDepositAllowed(true);
        return user;
    }

    private static EasyUserImpl createArchivist() {
        final Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ARCHIVIST);
        final EasyUserImpl archivist = new EasyUserImpl();
        archivist.setId(ARCHIV_USER_ID);
        archivist.setPassword(PASSWORD);
        archivist.setInitials("A.I.");
        archivist.setFirstname("Arch");
        archivist.setSurname("VIST");
        archivist.setEmail("arch.i.@vist.com");
        archivist.setState(EasyUser.State.ACTIVE);
        archivist.setRoles(roles);
        return archivist;
    }

    private static EasyUserImpl createSomeBodyUnAuthorized() {
        final EasyUserImpl user = new EasyUserImpl();
        user.setId(UNAUTHORIZED_USER_ID);
        user.setPassword(PASSWORD);
        user.setInitials("S.");
        user.setFirstname("Some");
        user.setSurname("Body");
        user.setEmail("some@body.com");
        user.setState(EasyUser.State.ACTIVE);
        user.setSwordDepositAllowed(false);
        return user;
    }
}
