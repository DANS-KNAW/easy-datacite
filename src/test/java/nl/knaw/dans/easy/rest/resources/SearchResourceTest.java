package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.WebResource;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SearchResourceTest extends RestTest
{
    private SearchService searchServiceMock;
    private SearchResult searchResultMock;

    @Before
    public void setUp()
    {
        Services services = new Services();

        searchServiceMock = Mockito.mock(SearchService.class);
        services.setSearchService(searchServiceMock);

        searchResultMock = Mockito.mock(SearchResult.class);
        when(searchResultMock.getHits()).thenReturn(new ArrayList());
    }

    @Test
    public void search() throws ServiceException
    {
        when(searchServiceMock.searchPublished(isA(SearchRequest.class), isA(EasyUser.class))).thenReturn(searchResultMock);

        WebResource webResource = resource().path("search");

        String responseBody = webResource.get(String.class);

        assertEquals(200, webResource.head().getStatus());
        assertEquals("<hits></hits>", responseBody);
    }

    @Test
    public void searchInternalServerError() throws ServiceException
    {
        when(searchServiceMock.searchPublished(isA(SearchRequest.class), isA(EasyUser.class))).thenThrow(ServiceException.class);

        WebResource webResource = resource().path("search");

        assertEquals(500, webResource.head().getStatus());
    }

}
