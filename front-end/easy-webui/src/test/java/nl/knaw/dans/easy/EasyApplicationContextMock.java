package nl.knaw.dans.easy;

import static org.easymock.EasyMock.eq;

import java.io.File;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasySearchService;
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
 * Mock of the file applicationContext.xml, one of the two helper classes that eases unit testing of the
 * easy webui application. A sample calling sequence is shown by {@link EasyWicketTester}. <br>
 * <br>
 * The expectXxx methods add beans that are required for subclasses of {@link AbsractEasyNavPage}, think
 * of the tool bars. The JavaDoc of each expect method tells which beans are set and which beans are
 * mocked ones. You can extend the expectations for the mocked beans and/or provide alternative beans
 * with the get/put-BeanName methods. Vice versa an expectXxx method extends the expectations of existing
 * mocked beans.<br>
 * <br>
 * The provided beans assume the use of annotated SpringBean injection. In the page under test and its
 * components you may have to replace the deprecated use of Services.getXyZ() by:
 * 
 * <pre>
 * &#064;SpringBean(name = &quot;xyZ&quot;)
 * private XyZ xyZ;
 * </pre>
 */
public class EasyApplicationContextMock extends ApplicationContextMock
{
    private static final long serialVersionUID = 1L;
    private UsernamePasswordAuthentication authentication;

    /**
     * Assigns default beans for authz, security and a mocked bean for systemReadOnlyStatus.
     * 
     * @param readOnly
     *        true means the system is preparing for a controlled shutdown, repository updates are not
     *        allowed in this state.
     * @throws IllegalStateException
     *         if a real {@link SystemReadOnlyStatus} instance was assigned as bean
     */
    public void expectStandardSecurity(final boolean readOnly)
    {
        setAuthz(new CodedAuthz());
        setSecurity(new Security(getAuthz()));
        setMockedsetSystemReadOnlyStatus();
        EasyMock.expect(getSystemReadOnlyStatus().getReadOnly()).andStubReturn(readOnly);
        getAuthz().setSystemReadOnlyStatus(getSystemReadOnlyStatus());
    }

    /**
     * Provides default beans for editableContentHome and StaticContentBaseUrl. Whenever a test changes
     * files in the {@link FileSystemHomeDirectory}, use {@link #expectDefaultResources(File)}.
     */
    public void expectDefaultResources()
    {
        setEditableContentHome(new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable/")));
        setStaticContentBaseUrl("http://mocked/base/url");
    }

    /**
     * Provides default beans for editableContentHome and StaticContentBaseUrl.
     * 
     * @param editableFiles
     *        typically a temporary partial copy of src/main/assembly/dist/res/example/editable/
     */
    public void expectDefaultResources(final File editableFiles)
    {
        setEditableContentHome(new FileSystemHomeDirectory(editableFiles));
        setStaticContentBaseUrl("http://mocked/base/url");
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
     *        a real instance used to mock {@link Authentication#getUser()} and
     *        {@link Authentication#getUserId()}
     * @param userId
     *        the id of the sessionUser (having it as a separate argument allows a mocked sessionUser)
     * @throws ServiceException
     *         declaration required to mock {@link UserService#newUsernamePasswordAuthentication()}
     * @throws IllegalStateException
     *         if a real {@link UserService} instance was assigned as bean
     */
    public void expectAuthenticatedAs(final EasyUser sessionUser, final String userId) throws ServiceException
    {
        authentication = new UsernamePasswordAuthentication();
        authentication.setUser(sessionUser);
        if (sessionUser != null)
            authentication.setUserId(userId);
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

    public CodedAuthz getAuthz()
    {
        return (CodedAuthz) getBean("authz");
    }

    public void setAuthz(final CodedAuthz codedAuthz)
    {
        putBean("authz", codedAuthz);
    }

    private void setMockedsetSystemReadOnlyStatus()
    {
        try
        {
            isMock(getSystemReadOnlyStatus());
        }
        catch (final NoSuchBeanDefinitionException e)
        {
            setSystemReadOnlyStatus(PowerMock.createMock(SystemReadOnlyStatus.class));
        }
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
