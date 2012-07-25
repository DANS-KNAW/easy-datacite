package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

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
import com.sun.jersey.core.util.MultivaluedMapImpl;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AdvancedSearchResourceTest extends RestTest {
	
	private SearchService searchServiceMock;
	private SearchResult searchResultMock;
	
	@Before
	public void setUp() {
		Services services = new Services();
		
		searchServiceMock = Mockito.mock(SearchService.class);
		services.setSearchService(searchServiceMock);
		
		searchResultMock = Mockito.mock(SearchResult.class);
		when(searchResultMock.getHits()).thenReturn(new ArrayList());
	}
	
	@Test
	public void searchSingleParams() throws ServiceException {
		when(searchServiceMock.searchPublished(isA(SearchRequest.class), isA(EasyUser.class))).thenReturn(searchResultMock);
		
		WebResource webResource = resource().path("advsearch").queryParam("title", "title");
		
		String responseBody = webResource.get(String.class);
		
		assertEquals(200, webResource.head().getStatus());
		assertEquals("<hits></hits>", responseBody);
	}
	
	@Test
	public void searchMultipleParams() throws ServiceException {
		when(searchServiceMock.searchPublished(isA(SearchRequest.class), isA(EasyUser.class))).thenReturn(searchResultMock);
		
		MultivaluedMap params = new MultivaluedMapImpl();
		params.add("title", "title");
		params.add("title", "title2");
		params.add("creator", "creator");
		params.add("description", "description");
		params.add("subject", "subject");
		params.add("coverage", "coverage");
		params.add("identifier", "identifier");
		params.add("date", "date");
		params.add("language", "language");
		params.add("format", "format");
		params.add("access", "access");
		params.add("foo", "bar");
		
		WebResource webResource = resource().path("advsearch").queryParams(params);
		
		String responseBody = webResource.get(String.class);
		
		assertEquals(200, webResource.head().getStatus());
		assertEquals("<hits></hits>", responseBody);
	}
	
	@Test
	public void searchInternalServerError() throws ServiceException {
		when(searchServiceMock.searchPublished(isA(SearchRequest.class), isA(EasyUser.class))).thenThrow(ServiceException.class);
		
		WebResource webResource = resource().path("advsearch");
		
		assertEquals(500, webResource.head().getStatus());
	}
	
}
