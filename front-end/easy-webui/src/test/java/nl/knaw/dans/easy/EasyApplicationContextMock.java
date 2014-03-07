package nl.knaw.dans.easy;

import static org.easymock.EasyMock.eq;

import java.io.File;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Mock of the file applicationContext.xml<br>
 * Convenience methods and constructors provide frequently required mocked or default beans.
 */
public class EasyApplicationContextMock extends ApplicationContextMock
{
    private static final long serialVersionUID = 1L;
    private UsernamePasswordAuthentication authentication;

    /** Creates an instance without any beans. */
    public EasyApplicationContextMock()
    {
        super();
    }

    /**
     * Convenience constructor that provides default beans for authz, security and editableContentHome
     * and a mocked bean for systemReadOnlyStatus. Whenever a test changes files in the
     * {@link FileSystemHomeDirectory}, use {@link #EasyApplicationContextMock(boolean, String)}.
     * 
     * @param ReadOnly
     *        true means the system is preparing for a controlled shutdown, repository updates are not
     *        allowed in this state.
     */
    public EasyApplicationContextMock(final boolean ReadOnly)
    {
        super();
        init(new File("src/main/assembly/dist/res/example/editable/"));
    }

    /**
     * Convenience constructor that provides default beans for authz, security and editableContentHome
     * and a mocked bean for systemReadOnlyStatus.
     * 
     * @param ReadOnly
     *        true means the system is preparing for a controlled shutdown, repository updates are not
     *        allowed in this state.
     * @param editableFiles
     *        typically a temporary partial copy of src/main/assembly/dist/res/example/editable/
     */
    public EasyApplicationContextMock(final boolean ReadOnly, final File editableFiles)
    {
        super();
        init(editableFiles);
    }

    private void init(final File editableFiles)
    {
        setCodedAuthz(new CodedAuthz());
        setSecurity(new Security(getCodedAuthz()));
        setEditableContentHome(new FileSystemHomeDirectory(editableFiles));
        setSystemReadOnlyStatus(PowerMock.createMock(SystemReadOnlyStatus.class));
        setStaticContentBaseUrl("http://example/base/url");
        EasyMock.expect(getSystemReadOnlyStatus().getReadOnly()).andStubReturn(false);
        getCodedAuthz().setSystemReadOnlyStatus(getSystemReadOnlyStatus());
    }

    /**
     * Mocks a sessionUser that did not deposit any datasets, and hence also has no permission requests.
     * If no searchService bean is set, a mocked one is created with PowerMock. Otherwise eventual
     * previous expectations remain effective.
     * 
     * @param user
     * @throws ServiceException
     *         declaration required to mock searchService.getNumberOfXXX
     * @throws IllegalStateException
     *         if a real {@link EasySearchService} instance was assigned as bean
     */
    public void expectNoDepositsBy(final EasyUser user) throws ServiceException
    {
        setMockedSearchService();
        EasyMock.expect(getSearchService().getNumberOfDatasets(eq(user))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfRequests(eq(user))).andStubReturn(0);
    }

    /**
     * Mocks no datasaets in the tool bar for an archivist. If no {@link SearchService} bean is set, a
     * mocked one is created with PowerMock. Otherwise eventual previous expectations remain effective.
     * 
     * @param user
     * @throws ServiceException
     *         declaration required to mock searchService.getNumberOfXXX
     * @throws IllegalStateException
     *         if a real {@link EasySearchService} instance was assigned as bean
     */
    public void expectNoDepositsFor(final EasyUser user) throws ServiceException
    {
        setMockedSearchService();
        EasyMock.expect(getSearchService().getNumberOfItemsInAllWork(eq(user))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInMyWork(eq(user))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInOurWork(eq(user))).andStubReturn(0);
        EasyMock.expect(getSearchService().getNumberOfItemsInTrashcan(eq(user))).andStubReturn(0);
    }

    /**
     * Creates an authentication passed on by {@link EasyWicketTester} to
     * {@link EasySession#setLoggedIn(nl.knaw.dans.easy.domain.authn.Authentication)}. If no
     * {@link UserService} bean is set, a mocked one is created with PowerMock. Otherwise eventual
     * previous expectations remain effective.
     * 
     * @param sessionUser
     *        an instance used to mock {@link Authentication#getUser()} and
     *        {@link Authentication#getUserId()}
     * @throws ServiceException
     *         declaration required to mock {@link UserService#newUsernamePasswordAuthentication()}
     * @throws IllegalStateException
     *         if a real {@link UserService} instance was assigned as bean
     */
    public void expectAuthenticatedAs(final EasyUser sessionUser) throws ServiceException
    {
        authentication = new UsernamePasswordAuthentication();
        authentication.setUser(sessionUser);
        if (!sessionUser.isAnonymous())
            authentication.setUserId(sessionUser.getId());
        setMockedUserService();
        EasyMock.expect(getUserService().newUsernamePasswordAuthentication()).andStubReturn(getAuthentication());
    }

    private void setMockedUserService()
    {
        try
        {
            isMock(getUserService());
        }
        catch (final NoSuchBeanDefinitionException e)
        {
            setUserService(PowerMock.createMock(UserService.class));
        }
    }

    public UserService getUserService()
    {
        return (UserService) getBean("userService");
    }

    public void setUserService(final UserService userService)
    {
        putBean("userService", userService);
    }

    public FileSystemHomeDirectory getEditableContentHome()
    {
        return (FileSystemHomeDirectory) getBean("editableContentHome");
    }

    public void setEditableContentHome(final FileSystemHomeDirectory fileSystemHomeDirectory)
    {
        putBean("editableContentHome", fileSystemHomeDirectory);
    }

    private void setMockedSearchService()
    {
        try
        {
            isMock(getSearchService());
        }
        catch (final NoSuchBeanDefinitionException e)
        {
            setSearchService(PowerMock.createMock(SearchService.class));
        }
    }

    public SearchService getSearchService()
    {
        return (SearchService) getBean("searchService");
    }

    public void setSearchService(final SearchService searchService)
    {
        putBean("searchService", searchService);
    }

    public CodedAuthz getCodedAuthz()
    {
        return (CodedAuthz) getBean("authz");
    }

    public void setCodedAuthz(final CodedAuthz codedAuthz)
    {
        putBean("authz", codedAuthz);
    }

    public SystemReadOnlyStatus getSystemReadOnlyStatus()
    {
        return (SystemReadOnlyStatus) getBean("systemReadOnlyStatus");
    }

    public void setSystemReadOnlyStatus(final SystemReadOnlyStatus systemReadOnlyStatus)
    {
        putBean("systemReadOnlyStatus", systemReadOnlyStatus);
    }

    public void setStaticContentBaseUrl(String url)
    {
        putBean("staticContentBaseUrl", url);
    }

    public String getStaticContentBaseUrl()
    {
        return (String) getBean("staticContentBaseUrl");
    }

    public Security getSecurity()
    {
        return (Security) getBean("security");
    }

    public void setSecurity(final Security security)
    {
        putBean("security", security);
    }

    public UsernamePasswordAuthentication getAuthentication()
    {
        return authentication;
    }

    private void isMock(Object userService)
    {
        if (!userService.toString().toLowerCase().contains("mock for"))
            throw new IllegalStateException("can't add mocked expectations to a real instance");
    }
}
