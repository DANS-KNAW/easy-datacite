package nl.knaw.dans.easy;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.DisciplineImpl;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormDescriptorLoader;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.JumpoffService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.fileexplorer.Util;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Mock of the file applicationContext.xml, one of the two helper classes that eases unit testing of the easy webui application. A sample calling sequence is
 * shown by {@link EasyWicketTester}. <br>
 * <br>
 * The expectXxx methods add beans that are required for subclasses of {@link AbsractEasyNavPage}, think of the tool bars. The JavaDoc of each expect method
 * tells which beans are set and which beans are mocked ones. You can extend the expectations for the mocked beans and/or provide alternative beans with the
 * get/put-BeanName methods. Vice versa an expectXxx method extends the expectations of existing mocked beans.<br>
 * <br>
 * The provided beans assume the use of annotated SpringBean injection. In the page under test and its components you may have to replace the deprecated use of
 * Services.getXyZ() by:
 * 
 * <pre>
 * &#064;SpringBean(name = &quot;xyZ&quot;)
 * private XyZ xyZ;
 * </pre>
 */
public class EasyApplicationContextMock extends ApplicationContextMock {
    private static final long serialVersionUID = 1L;
    private UsernamePasswordAuthentication authentication;
    private ItemService itemServiceDelegate = new ItemServiceDelegate();

    public EasyApplicationContextMock() {
        super();
        // clean up statics of eventual previous tests
        new Util().setDatasetService(null);
        new DisciplineUtils().setDepositService(null);
    }

    /**
     * Assigns default beans for authz and security, a mocked bean for systemReadOnlyStatus implies the system is not expected to go down any time soon.
     * 
     * @throws IllegalStateException
     *         if a real {@link SystemReadOnlyStatus} instance was assigned as bean
     */
    public void expectStandardSecurity() {
        expectSecurity(false);
    }

    /**
     * Assigns default beans for authz and security, a mocked bean for systemReadOnlyStatus implies readOnly mode in preparation for a shutdown.
     * 
     * @throws IllegalStateException
     *         if a real {@link SystemReadOnlyStatus} instance was assigned as bean
     */
    public void expectReadOnlySecurity() {
        expectSecurity(true);
    }

    private void expectSecurity(boolean value) {
        setAuthz(new CodedAuthz());
        setSecurity(new Security(getAuthz()));
        setMockedsetSystemReadOnlyStatus();
        EasyMock.expect(getSystemReadOnlyStatus().getReadOnly()).andStubReturn(value);
        getAuthz().setSystemReadOnlyStatus(getSystemReadOnlyStatus());
    }

    /**
     * Provides default beans for editableContentHome and StaticContentBaseUrl. Whenever a test changes files in the {@link FileSystemHomeDirectory}, use
     * {@link #expectDefaultResources(File)}.
     */
    public void expectDefaultResources() {
        setEditableContentHome(new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable/")));
        setStaticContentBaseUrl("http://mocked/base/url");
    }

    /**
     * Sets the authentication with a new session user. If no searchService/uesrService bean is set, a mocked one is created with PowerMock. Otherwise eventual
     * previous expectations remain effective. {@Link EasyWicketTester#create(EasyApplicationContextMock)} fetches {@link #getAuthentication()} to create
     * a proper session.
     * 
     * @return an active user with role user and without groups the tool bars will show no datasets
     * @throws ServiceException
     *         required by the syntax
     * @throws IllegalStateException
     *         if a real {@link UserService} or{@link SearchService} instance was assigned as bean
     */
    public EasyUserImpl expectAuthenticatedAsVisitor() throws ServiceException {
        final EasyUserImpl sessionUser = new EasyUserTestImpl("mocked-user:somebody");
        sessionUser.setInitials("S.U.R.");
        sessionUser.setSurname("Name");
        sessionUser.addRole(Role.USER);
        sessionUser.setState(User.State.ACTIVE);

        expectAuthenticatedAs(sessionUser);

        setMockedSearchService();
        EasyMock.expect(getSearchService().getNumberOfDatasets(eq(sessionUser))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfRequests(eq(sessionUser))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInAllWork(eq(sessionUser))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInMyWork(eq(sessionUser))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInOurWork(eq(sessionUser))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInTrashcan(eq(sessionUser))).andStubReturn(0);
        return sessionUser;
    }

    /**
     * Supplies zeros for tool bar elements that show numbers of datasets, regardless of any authenticated user. If no searchService bean is set, a mocked one
     * is created with PowerMock. Otherwise eventual previous expectations remain effective.
     * 
     * @throws ServiceException
     *         required by the syntax
     * @throws IllegalStateException
     *         if a real {@link SearchService} instance was assigned as bean
     */
    public void expectNoDatasets() throws ServiceException {
        setMockedSearchService();
        EasyMock.expect(getSearchService().getNumberOfDatasets(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfRequests(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInAllWork(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInMyWork(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInOurWork(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInTrashcan(isA(EasyUser.class))).andStubReturn(0);
    }

    /**
     * Supplies expectations for datasets without accessible audio/video files. If no itemService bean is set, a mocked one is created with PowerMock. Calls to
     * {@link ItemService#getAccessibleAudioVideoFiles(EasyUser, Dataset)} are mocked and methods calling {@link FileStoreAccess} are delegated. If required,
     * you can mock the delegated methods via {@link InMemoryDatabase}.
     * 
     * @throws ServiceException
     *         required by the syntax
     * @throws IllegalStateException
     *         if a real {@link ItemService} instance was assigned as bean
     */
    public void expectNoAudioVideoFiles() throws ServiceException {
        setMockedItemService();
        expect(getItemService().getAccessibleAudioVideoFiles(isA(EasyUser.class), isA(Dataset.class))).andStubReturn(new ArrayList<FileItemVO>());
    }

    /**
     * Sets the authentication with a new session user. If no uesrService bean is set, a mocked one is created with PowerMock. Otherwise eventual previous
     * expectations remain effective. {@Link EasyWicketTester#create(EasyApplicationContextMock)} fetches {@link #getAuthentication()} to create a proper
     * session.
     * 
     * @return an active user with role user and without groups the tool bars will show no unpublished deposits and an empty trashcan
     * @throws ServiceException
     */
    public void expectAuthenticatedAs(final EasyUser sessionUser) throws ServiceException {
        authentication = new UsernamePasswordAuthentication();
        authentication.setUser(sessionUser);
        if (sessionUser != null)
            authentication.setUserId(sessionUser.getId());

        setMockedUserService();
        EasyMock.expect(getUserService().newUsernamePasswordAuthentication()).andStubReturn(getAuthentication());
    }

    /**
     * Provides default beans for editableContentHome and StaticContentBaseUrl.
     * 
     * @param editableFiles
     *        typically a temporary partial copy of src/main/assembly/dist/res/example/editable/
     */
    public void expectDefaultResources(final File editableFiles) {
        setEditableContentHome(new FileSystemHomeDirectory(editableFiles));
        setStaticContentBaseUrl("http://mocked/base/url");
    }

    /**
     * Mocks a choice list for disciplines. If no {@link DepositService} bean is set, a mocked one is created with PowerMock. Otherwise eventual previous
     * expectations remain effective.
     * 
     * @param user
     * @throws ServiceException
     *         declaration required to mock depositService.getChoices
     * @throws IllegalStateException
     *         if a real {@link DepositService} instance was assigned as bean
     */
    public void expectDisciplineChoices(final KeyValuePair... keyValuePairs) throws ServiceException {
        final List<KeyValuePair> choices = new ArrayList<KeyValuePair>();
        for (final KeyValuePair kvp : keyValuePairs)
            choices.add(kvp);
        final ChoiceList choiceList = new ChoiceList(choices);

        setMockedDepositService();
        expect(getDepositService().getChoices(isA(String.class), isNull(Locale.class))).andReturn(choiceList).anyTimes();
        expect(getDepositService().getChoices(isA(String.class), isA(Locale.class))).andReturn(choiceList).anyTimes();
    }

    /**
     * Loads the deposit disciplines for a mocked service. If no {@link DepositService} bean is set, a mocked one is created with PowerMock. Otherwise eventual
     * previous expectations remain effective.
     * 
     * @throws ServiceException
     *         declaration required to mock depositService.getChoices
     * @throws IllegalStateException
     *         if a real {@link DepositService} instance was assigned as bean
     */
    public void expectDepositDisciplines() throws Exception, ServiceException {
        setMockedDepositService();
        final Map<MetadataFormat, DepositDiscipline> disciplineMap = new HashMap<MetadataFormat, DepositDiscipline>();
        final List<DepositDiscipline> list = new ArrayList<DepositDiscipline>();
        for (final MetadataFormat mdFormat : MetadataFormat.values()) {
            disciplineMap.put(mdFormat, loadDiscipline(mdFormat));
            list.add(disciplineMap.get(mdFormat));
        }
        expect(getDepositService().getDisciplines()).andStubReturn(list);
    }

    private DisciplineImpl loadDiscipline(final MetadataFormat mdFormat) throws Exception {

        final String location = FormDescriptorLoader.FORM_DESCRIPTIONS + mdFormat.name().toLowerCase() + ".xml";
        final FileInputStream stream = new FileInputStream("../../lib/easy-business/src/main/java/nl/knaw/dans/easy/domain/form/" + location);
        final FormDescriptor formDescriptor = (FormDescriptor) JiBXObjectFactory.unmarshal(FormDescriptor.class, stream);
        final DisciplineImpl discipline = new DisciplineImpl(formDescriptor);
        expect(getDepositService().getDiscipline(mdFormat)).andStubReturn(discipline);;
        return discipline;
    }

    /**
     * Mocks a {@Link JumpoffDmo}. If no {@link JumpoffService} bean is set, a mocked one is created with PowerMock. Otherwise eventual previous
     * expectations remain effective.
     * 
     * @param user
     * @throws ServiceException
     *         declaration required to mock JumpoffService.getJumpoffDmoFor
     * @throws IllegalStateException
     *         if a real {@link JumpoffService} instance was assigned as bean
     */
    public void expectNoJumpoff() throws ServiceException {
        setMockedJumpoffService();
        EasyMock.expect(getJumpoffService().getJumpoffDmoFor(EasyMock.isA(EasyUser.class), EasyMock.isA(DmoStoreId.class))).andStubReturn(null);
        EasyMock.expect(getJumpoffService().getJumpoffDmoFor(EasyMock.isA(EasyUserImpl.class), EasyMock.isA(DmoStoreId.class))).andStubReturn(null);
    }

    private void setMockedJumpoffService() {
        try {
            isMock(getJumpoffService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setJumpoffService(PowerMock.createMock(JumpoffService.class));
        }
    }

    public JumpoffService getJumpoffService() {
        return (JumpoffService) getBean("jumpoffService");
    }

    public void setJumpoffService(final JumpoffService jumpoffService) {
        putBean("jumpoffService", jumpoffService);
    }

    public void setMockedDepositService() {
        try {
            isMock(getDepositService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setDepositService(PowerMock.createMock(DepositService.class));
        }
    }

    public DatasetService getDatasetService() {
        return (DatasetService) getBean("datasetService");
    }

    public void setDatasetService(final DatasetService datasetService) {
        putBean("datasetService", datasetService);
    }

    public DepositService getDepositService() {
        return (DepositService) getBean("depositService");
    }

    public void setDepositService(final DepositService depositService) {
        putBean("depositService", depositService);
    }

    private void setMockedUserService() {
        try {
            isMock(getUserService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setUserService(PowerMock.createMock(UserService.class));
        }
    }

    public UserService getUserService() {
        return (UserService) getBean("userService");
    }

    public void setUserService(final UserService userService) {
        putBean("userService", userService);
    }

    public FileSystemHomeDirectory getEditableContentHome() {
        return (FileSystemHomeDirectory) getBean("editableContentHome");
    }

    public void setEditableContentHome(final FileSystemHomeDirectory fileSystemHomeDirectory) {
        putBean("editableContentHome", fileSystemHomeDirectory);
    }

    @SuppressWarnings("unchecked")
    private void setMockedItemService() throws ServiceException {
        try {
            isMock(getItemService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            ItemService mock = PowerMock.createMock(ItemService.class);
            expect(mock.hasChildItems(anyObject(DmoStoreId.class))).andStubDelegateTo(itemServiceDelegate);
            expect(mock.getFilenames(anyObject(DmoStoreId.class), EasyMock.anyBoolean())).andStubDelegateTo(itemServiceDelegate);
            expect(
                    mock.getFileItemsRecursively(anyObject(EasyUser.class), anyObject(Dataset.class), (Collection<FileItemVO>) anyObject(ItemFilters.class),
                            anyObject(ItemFilters.class), anyObject(DmoStoreId.class))).andStubDelegateTo(itemServiceDelegate);
            expect(mock.getFilesAndFolders(anyObject(EasyUser.class), anyObject(Dataset.class), (Collection<DmoStoreId>) anyObject())).andStubDelegateTo(
                    itemServiceDelegate);
            expect(
                    mock.getFilesAndFolders(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class), isA(Integer.class),
                            isA(Integer.class), anyObject(ItemOrder.class), anyObject(ItemFilters.class))).andStubDelegateTo(itemServiceDelegate);
            expect(
                    mock.getFolders(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                            anyObject(ItemOrder.class), anyObject(ItemFilters.class))).andStubDelegateTo(itemServiceDelegate);
            expect(
                    mock.getFiles(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                            anyObject(ItemOrder.class), anyObject(ItemFilters.class))).andStubDelegateTo(itemServiceDelegate);
            setItemService(mock);
        }
    }

    public void setItemService(final ItemService itemService) {
        putBean("itemService", itemService);
    }

    public ItemService getItemService() {
        return (ItemService) getBean("itemService");
    }

    private void setMockedSearchService() {
        try {
            isMock(getSearchService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setSearchService(PowerMock.createMock(SearchService.class));
        }
    }

    public SearchService getSearchService() {
        return (SearchService) getBean("searchService");
    }

    public void setSearchService(final SearchService searchService) {
        putBean("searchService", searchService);
    }

    public CodedAuthz getAuthz() {
        return (CodedAuthz) getBean("authz");
    }

    public void setAuthz(final CodedAuthz codedAuthz) {
        putBean("authz", codedAuthz);
    }

    private void setMockedsetSystemReadOnlyStatus() {
        try {
            isMock(getSystemReadOnlyStatus());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setSystemReadOnlyStatus(PowerMock.createMock(SystemReadOnlyStatus.class));
        }
    }

    public SystemReadOnlyStatus getSystemReadOnlyStatus() {
        return (SystemReadOnlyStatus) getBean("systemReadOnlyStatus");
    }

    public void setSystemReadOnlyStatus(final SystemReadOnlyStatus systemReadOnlyStatus) {
        putBean("systemReadOnlyStatus", systemReadOnlyStatus);
    }

    public void setStaticContentBaseUrl(final String url) {
        putBean("staticContentBaseUrl", url);
    }

    public String getStaticContentBaseUrl() {
        return (String) getBean("staticContentBaseUrl");
    }

    public Security getSecurity() {
        return (Security) getBean("security");
    }

    public void setSecurity(final Security security) {
        putBean("security", security);
    }

    public UsernamePasswordAuthentication getAuthentication() {
        return authentication;
    }

    private void isMock(final Object userService) {
        if (!userService.toString().toLowerCase().contains("mock for"))
            throw new IllegalStateException("can't add mocked expectations to a real instance");
    }
}
