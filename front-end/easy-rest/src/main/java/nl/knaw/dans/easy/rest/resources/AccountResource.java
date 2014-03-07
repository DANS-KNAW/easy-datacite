package nl.knaw.dans.easy.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleFieldSet;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.AnonymousUserException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.rest.util.SearchHitConverter;
import nl.knaw.dans.easy.rest.util.UserConverter;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * A resource for users to address their account information.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
@Path("account")
public class AccountResource extends AuthenticatedResource
{

    /**
     * Returns a response that contains the account information of the authenticated user.
     * 
     * @return A response containing account information.
     */
    @GET
    public Response getAccount()
    {
        try
        {
            return responseXmlOrJson(UserConverter.convert(authenticate()));
        }
        catch (AnonymousUserException e)
        {
            return simpleResponse("Authorization failed.");
        }
        catch (ServiceException e)
        {
            return internalServerError(e);
        }
    }

    /**
     * Returns all (published) datasets that are deposited by the user.
     * 
     * @return A list of search hits containing all published datasets that are deposited by the user.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @GET
    @Path("/datasets")
    public Response getDatasets()
    {
        try
        {
            EasyUser user = authenticate();
            SearchRequest request = new SimpleSearchRequest();
            FieldSet fs = new SimpleFieldSet();
            fs.add(new SimpleField("amd_depositor_id", user.getId()));
            request.setFilterQueries(fs);
            SearchResult<? extends DatasetSB> result = Services.getSearchService().searchPublished(request, user);
            return responseXmlOrJson(SearchHitConverter.convert(result.getHits()));
        }
        catch (AnonymousUserException e)
        {
            return notAuthorized();
        }
        catch (ServiceException e)
        {
            return internalServerError(e);
        }
    }

}
