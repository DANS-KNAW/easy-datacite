package nl.knaw.dans.easy.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.AnonymousUserException;

/**
 * A simple 'hello, world' (R.I.P. Dennis Ritchie) resource to test the service.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
@Path("/hello")
public class TestResource extends AuthenticatedResource {

    /**
     * The 'hello, world' method.
     * 
     * @return A simple response containing 'hello, world' in the body.
     */
    @GET
    @Path("/world")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {
        return simpleResponse("hello, world");
    }

    /**
     * The 'hello, <user>' method.
     * 
     * @return A simple response containing 'hello, <user>' in the body.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response helloUser() {
        try {
            return simpleResponse("hello, " + authenticate().getFirstname());
        }
        catch (AnonymousUserException e) {
            return simpleResponse("hello, anonymous");
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

}
