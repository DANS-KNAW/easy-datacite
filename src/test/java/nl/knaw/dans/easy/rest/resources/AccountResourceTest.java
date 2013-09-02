package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.simple.EmptySearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.exceptions.AnonymousUserException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

public class AccountResourceTest extends RestTest
{

    private final EasyUser user = Mockito.mock(EasyUser.class);

    private Services services;

    @Before
    public void setUp()
    {
        services = new Services();
    }

    @Test
    public void getAccount() throws ServiceException
    {
        setUpUserService();

        ClientResponse response = resource().path("account").header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=").get(ClientResponse.class);

        assertEquals(200, response.getStatus());
        verify(user, times(1)).getCommonName();
    }

    private void setUpUserService()
    {
        UserService userService = new EasyUserService()
        {
            @Override
            public void authenticate(Authentication authentication) throws ServiceException
            {
                authentication.setUser(user);
            }
        };
        services.setUserService(userService);
    }

    @Test
    public void getAnonymousAccount() throws ServiceException
    {
        setUpUserService();

        ClientResponse response = resource().path("account").get(ClientResponse.class);

        assertEquals(200, response.getStatus());
        verify(user, times(0)).getCommonName();
    }

    @Test
    public void getUserServiceException()
    {
        setUpUserServiceException();

        ClientResponse response = resource().path("account").header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=").get(ClientResponse.class);

        assertEquals(500, response.getStatus());
        verify(user, times(0)).getCommonName();
    }

    private void setUpUserServiceException()
    {
        UserService userService = new EasyUserService()
        {
            @Override
            public void authenticate(Authentication authentication) throws ServiceException
            {
                throw (new ServiceException("WTF"));
            }
        };
        services.setUserService(userService);
    }

    @Test
    public void getDatasets() throws ServiceException
    {
        setUpUserService();
        when(user.getId()).thenReturn("easy-user:1");
        setUpSearchService();

        ClientResponse response = resource().path("account/datasets").header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=").get(ClientResponse.class);

        assertEquals(200, response.getStatus());
        assertEquals("<hits></hits>", response.getEntity(String.class));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setUpSearchService() throws ServiceException
    {
        SearchService searchServiceMock = mock(SearchService.class);
        when(searchServiceMock.searchPublished(isA(SearchRequest.class), isA(EasyUser.class))).thenReturn(new EmptySearchResult());
        services.setSearchService(searchServiceMock);
    }

    @Test
    public void getDatasetsNotAuthorized()
    {
        setUpAnonymousUserException();

        ClientResponse response = resource().path("account/datasets").header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=").get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    private void setUpAnonymousUserException()
    {
        UserService userService = new EasyUserService()
        {
            @Override
            public void authenticate(Authentication authentication) throws ServiceException
            {
                throw (new AnonymousUserException());
            }
        };
        services.setUserService(userService);
    }

    @Test
    public void getDatasetsServiceException()
    {
        setUpUserServiceException();

        ClientResponse response = resource().path("account/datasets").header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=").get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

}
