package nl.knaw.dans.easy.rest.resources;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.rest.util.SearchHitConverter;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * This class provides methods to access the simple search resource.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
@Path("search")
public class SearchResource extends AuthenticatedResource {

    /**
     * Executes a simple search on the metadata using the query parameter 'q' as input for the search term.
     * 
     * @param searchTerm
     *        Search term taken from the query parameter 'q'
     * @param offset
     *        Offset index of result list.
     * @param limit
     *        Max number of hits to be returned.
     * @return Response containing the hits to the search query.
     */
    @GET
    public Response search(@DefaultValue("") @QueryParam(value = "q") String searchTerm, @DefaultValue("0") @QueryParam(value = "offset") int offset,
            @DefaultValue("10") @QueryParam(value = "limit") int limit)
    {
        try {
            SimpleSearchRequest request = new SimpleSearchRequest(searchTerm);
            request.setOffset(offset);
            request.setLimit(limit);
            List<?> hits = Services.getSearchService().searchPublished(request, authenticate()).getHits();
            return responseXmlOrJson(SearchHitConverter.convert(hits));
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

}
