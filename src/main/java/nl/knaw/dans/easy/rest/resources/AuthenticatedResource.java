package nl.knaw.dans.easy.rest.resources;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.servicelayer.services.Services;

import com.sun.jersey.core.util.Base64;

/**
 * This class extends the Resource class and facilitates authentication.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class AuthenticatedResource extends Resource
{
    /**
     * Convenient immutable String for the authentication type.
     */
    protected static final String AUTHENTICATION_TYPE = "Basic ";

    /**
     * Fetches authentication data from the request headers and tries to
     * authenticate. Authentication is done with the HTTP Basic method for now.
     * 
     * @return EasyUser The authenticated user or an anonymous user.
     * @throws ServiceException
     *             Thrown if something goes wrong.
     */
    protected EasyUser authenticate() throws ServiceException
    {
        List<String> authHeaders = getRequestHeaders().getRequestHeader(HttpHeaders.AUTHORIZATION);
        return authHeaders != null && !authHeaders.isEmpty() ? authenticate(authHeaders.get(0)) : new EasyUserAnonymous();
    }

    private EasyUser authenticate(String authHeader) throws ServiceException
    {
        if (authHeader.startsWith(AUTHENTICATION_TYPE))
        {
            String decodedAuthHeader = Base64.base64Decode(authHeader.substring(AUTHENTICATION_TYPE.length()));

            if (decodedAuthHeader.contains(":"))
            {
                String[] auth = decodedAuthHeader.split(":");
                return authenticate(auth[0], auth[1]);
            }
        }
        return new EasyUserAnonymous();
    }

    private EasyUser authenticate(final String username, final String password) throws ServiceException
    {
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication(username, password);
        Services.getUserService().authenticate(auth);
        return auth.getUser() != null ? auth.getUser() : new EasyUserAnonymous();
    }

}
