package nl.knaw.dans.easy.business.authn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;

public class LoginService
{
    private static Logger log = LoggerFactory.getLogger(LoginService.class);

    private AuthenticationSpecification authenticationSpecification;

    public UsernamePasswordAuthentication newAuthentication()
    {
        UsernamePasswordAuthentication upAuthn = new UsernamePasswordAuthentication();

        // store requestTime and requestToken and check when this authentication comes back.
        // ..
        return upAuthn;
    }

    public void login(final UsernamePasswordAuthentication authentication)
    {
        if (authenticationSpecification.isSatisfiedBy(authentication))
        {
            authentication.setState(Authentication.State.Authenticated);
        }
    }

    public void setAuthenticationSpecification(AuthenticationSpecification authenticationSpecification)
    {
        this.authenticationSpecification = authenticationSpecification;
    }
}
