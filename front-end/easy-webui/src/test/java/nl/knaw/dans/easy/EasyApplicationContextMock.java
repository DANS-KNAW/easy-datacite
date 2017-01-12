package nl.knaw.dans.easy;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.DisciplineImpl;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormDescriptorLoader;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
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
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.env.Environment;

/**
 * Mock of the file applicationContext.xml, one of the two helper classes that eases unit testing of the easy webui application. A sample calling sequence is
 * shown by {@link EasyWicketTester}. <br>
 * <br>
 * The expectXxx methods add beans that are required for subclasses of {@link nl.knaw.dans.easy.web.main.AbstractEasyNavPage}, think of the tool bars. The
 * JavaDoc of each expect method tells which beans are set and which beans are mocked ones. You can extend the expectations for the mocked beans and/or provide
 * alternative beans with the get/put-BeanName methods. Vice versa an expectXxx method extends the expectations of existing mocked beans.<br>
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

    @Override
    public String getApplicationName() {
        throw new NotImplementedException();
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> aClass) {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> aClass) throws BeansException {
        throw new NotImplementedException();
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String s, Class<A> aClass) throws NoSuchBeanDefinitionException {
        throw new NotImplementedException();
    }

    @Override
    public <T> T getBean(Class<T> aClass) throws BeansException {
        throw new NotImplementedException();
    }

    @Override
    public Environment getEnvironment() {
        throw new NotImplementedException();
    }

    private static class DisciplineContainerProxy extends DisciplineContainerImpl {

        private static final long serialVersionUID = 1L;
        private final List<DisciplineContainer> subDisciplines;

        public DisciplineContainerProxy(final String storeId, final List<DisciplineContainer> subDisciplines) throws ObjectNotInStoreException,
                RepositoryException
        {
            super(storeId);
            this.subDisciplines = subDisciplines;
        }

        @Override
        public List<DisciplineContainer> getSubDisciplines() throws DomainException {
            return subDisciplines;
        }
    }

    /**
     * Creates an instance, clears static bean variables and resets all powermocks. Creating the context too late results in more obvious errors that forgetting
     * to clean up. Tests not using this class should still perform cleanup.
     */
    public EasyApplicationContextMock() {
        super();
        expectDefaultFooterLinks();
        expectOtherExternalLinks();
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

    private void expectSecurity(final boolean value) {
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

    public void expectDefaultFooterLinks() {
        setUsingEasyLink("http://mocked/using-easy/url");
        setContactLink("http://mocked/contact/url");
        setDisclaimerLink("http://mocked/disclaimer/url");
        setLegalLink("http://mocked/legal/url");
        setPropertyRightStatementLink("http://mocked/property-right-statement/url");
        setCertificeringLink("http://mocked/certificering/url");
        setReusingDataLink("http://mocked/reusing-data/url");
    }

    public void expectOtherExternalLinks() {
        // Following link only appears on deposit intro page, but affects more than just the Deposit test
        setAboutDepositingDataLink("http://mocked/about-depositing-data-link/url");
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
        expectZeros();
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
    public void expectNoDatasetsInToolBar() throws ServiceException {
        setMockedSearchService();
        expectZeros();
    }

    /**
     * Supplies zeros for tool bar elements that show numbers of datasets, regardless of any authenticated user. If no searchService bean is set, a mocked one
     * is created with PowerMock. Otherwise eventual previous expectations remain effective.
     * 
     * @param published
     *        expected to be returned on any searchPublished call.
     * @throws ServiceException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void expectNoDatasetsInToolBar(final SearchResult<? extends DatasetSB> published) throws ServiceException {
        setMockedSearchService();
        expectZeros();
        expect(getSearchService().searchPublished((SearchRequest) anyObject(), isA(EasyUser.class))).andStubReturn((SearchResult) published);
    }

    private void expectZeros() throws ServiceException {
        expect(getSearchService().getNumberOfDatasets(isA(EasyUser.class))).andStubReturn(0);
        expect(getSearchService().getNumberOfRequests(isA(EasyUser.class))).andStubReturn(0);
        expect(getSearchService().getNumberOfItemsInAllWork(isA(EasyUser.class))).andStubReturn(0);
        expect(getSearchService().getNumberOfItemsInMyWork(isA(EasyUser.class))).andStubReturn(0);
        expect(getSearchService().getNumberOfItemsInOurWork(isA(EasyUser.class))).andStubReturn(0);
        expect(getSearchService().getNumberOfItemsInTrashcan(isA(EasyUser.class))).andStubReturn(0);
    }

    /**
     * Supplies expectations for datasets without accessible audio/video files. If no itemService bean is set, a mocked one is created with PowerMock. Calls to
     * {@link ItemService#getAudioVideoFiles(EasyUser, Dataset)} are mocked and methods calling {@link FileStoreAccess} are delegated. If required, you can mock
     * the delegated methods via {@link FileStoreMocker}.
     * 
     * @throws ServiceException
     *         required by the syntax
     * @throws StoreAccessException
     * @throws IllegalStateException
     *         if a real {@link ItemService} instance was assigned as bean
     */
    @SuppressWarnings("unchecked")
    public void expectNoAudioVideoFiles() throws ServiceException, StoreAccessException {
        setMockedItemService();
        expect(getItemService().getAudioVideoFiles(isA(EasyUser.class), isA(Dataset.class))).andStubReturn(new LinkedList<FileItemVO>());
        expect(getItemService().allAccessibleToUser(isA(EasyUser.class), isA(Collection.class))).andStubReturn(false);
    }

    /**
     * Supplies expectations for datasets without accessible audio/video files. If no itemService bean is set, a mocked one is created with PowerMock. Calls to
     * {@link ItemService#getAudioVideoFiles(EasyUser, Dataset)} are mocked and methods calling {@link FileStoreAccess} are delegated. If required, you can mock
     * the delegated methods via {@link FileStoreMocker}.
     * 
     * @throws ServiceException
     *         required by the syntax
     * @throws StoreAccessException
     * @throws IllegalStateException
     *         if a real {@link ItemService} instance was assigned as bean
     */
    @SuppressWarnings("unchecked")
    public void expectAudioVideoFiles(boolean allAccessibleToUser, LinkedList<FileItemVO> avFiles) throws ServiceException, StoreAccessException {
        setMockedItemService();
        expect(getItemService().getAudioVideoFiles(isA(EasyUser.class), isA(Dataset.class))).andStubReturn(avFiles);
        expect(getItemService().allAccessibleToUser(isA(EasyUser.class), isA(Collection.class))).andStubReturn(allAccessibleToUser);
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
     * @param keyValuePairs
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
        final DisciplineImpl discipline = loadDiscipline(MetadataFormat.ANY_DISCIPLINE);
        disciplineMap.put(MetadataFormat.ANY_DISCIPLINE, discipline);
        list.add(discipline);
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
     * Mocks {@link DisciplineContainerImpl} objects. If no {@link EasyStore} bean is set, a mocked one is created with PowerMock. Otherwise eventual previous
     * expectations remain effective.
     * 
     * @param disciplineStoreId
     *        typically new DmoStoreId(DisciplineContainer.NAMESPACE, "root") when expecting to use a TermPanelDefinition:dcterms.audience
     * @param subDisciplines
     *        typically an ArrayList&lt;DisciplineContainer&gt; when expecting to use a TermPanelDefinition:dcterms.audience
     * @throws ObjectNotInStoreException
     *         declaration required to mock EasyStore.retreive
     * @throws RepositoryException
     *         declaration required to mock EasyStore.retreive
     * @throws ServiceException
     *         declaration required to mock EasyStore.retreive
     * @throws IllegalStateException
     *         if a real {@link DepositService} instance was assigned as bean
     */
    public void expectDisciplineObject(final DmoStoreId disciplineStoreId, final List<DisciplineContainer> subDisciplines) throws ObjectNotInStoreException,
            RepositoryException, ServiceException
    {
        setMockedEasyStore();
        final DisciplineContainerProxy proxy = new DisciplineContainerProxy(disciplineStoreId.getStoreId(), subDisciplines);
        expect(getEasyStore().retrieve(eq(disciplineStoreId))).andStubReturn(proxy);
    }

    /**
     * Expects a dataset to be retrieved for any user. If no {@link DatasetService} bean is set, a mocked one is created with PowerMock. Otherwise eventual
     * previous expectations remain effective.
     * 
     * @param id
     *        storeId of the expected dataset
     * @param dataset
     *        either a mock or an instance
     * @throws ServiceException
     *         declaration required to mock EasyStore.retreive
     * @throws IllegalStateException
     *         if a real {@link DepositService} instance was assigned as bean
     */
    public void expectDataset(DmoStoreId id, Dataset dataset) throws ServiceException {
        setMockedDatasetService();
        expect(getDatasetService().getDataset(isA(EasyUser.class), eq(id))).andStubReturn(dataset);
    }

    /**
     * Mocks a {@Link JumpoffDmo}. If no {@link JumpoffService} bean is set, a mocked one is created with PowerMock. Otherwise eventual previous
     * expectations remain effective.
     * 
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

    private void setMockedDatasetService() {
        try {
            isMock(getDatasetService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setDatasetService(PowerMock.createMock(DatasetService.class));
        }
    }

    public DatasetService getDatasetService() {
        return (DatasetService) getBean("datasetService");
    }

    public void setDatasetService(final DatasetService datasetService) {
        putBean("datasetService", datasetService);
    }

    private void setMockedDepositService() {
        try {
            isMock(getDepositService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            setDepositService(PowerMock.createMock(DepositService.class));
        }
    }

    public DepositService getDepositService() {
        return (DepositService) getBean("depositService");
    }

    /**
     * registers the supplied service as a bean
     * 
     * @param depositService
     *        an instance of the EasyDepositService loads form descriptions for deposit pages and the dataset description tab
     */
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

    private void setMockedItemService() throws ServiceException, StoreAccessException {
        try {
            isMock(getItemService());
        }
        catch (final NoSuchBeanDefinitionException e) {
            final ItemService mock = PowerMock.createMock(ItemService.class);
            ItemServiceDelegate.delegate(mock);
            setItemService(mock);
        }
    }

    public void setItemService(final ItemService itemService) {
        putBean("itemService", itemService);
    }

    public ItemService getItemService() {
        return (ItemService) getBean("itemService");
    }

    private void setMockedEasyStore() throws ServiceException, StoreAccessException {
        try {
            isMock(getEasyStore());
        }
        catch (final NoSuchBeanDefinitionException e) {
            final EasyStore mock = PowerMock.createMock(EasyStore.class);
            setEasyStore(mock);
        }
    }

    public void setEasyStore(final EasyStore easyStore) {
        putBean("easyStore", easyStore);
        new Data().setEasyStore(easyStore);
    }

    public EasyStore getEasyStore() {
        return (EasyStore) getBean("easyStore");
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

    private void setUsingEasyLink(final String url) {
        putBean("usingEasyLink", url);
    }

    private void setContactLink(final String url) {
        putBean("contactLink", url);
    }

    private void setDisclaimerLink(final String url) {
        putBean("disclaimerLink", url);
    }

    private void setLegalLink(final String url) {
        putBean("legalLink", url);
    }

    private void setPropertyRightStatementLink(final String url) {
        putBean("propertyRightStatementLink", url);
    }

    private void setCertificeringLink(final String url) {
        putBean("certificeringLink", url);
    }

    private void setReusingDataLink(final String url) {
        putBean("reusingDataLink", url);
    }

    private void setAboutDepositingDataLink(final String url) {
        putBean("aboutDepositingDataLink", url);
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
