package nl.knaw.dans.easy.business.authn;

import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;



public class LoginService
{
    
    //private static Logger logger = LoggerFactory.getLogger(LoginService.class);
    
    public LoginService()
    {
        
    }

    public UsernamePasswordAuthentication newAuthentication()
    {
        UsernamePasswordAuthentication upAuthn = new UsernamePasswordAuthentication();

        // store requestTime and requestToken and check when this authentication comes back.
        // ..
        return upAuthn;
    }
    
    public void login(final UsernamePasswordAuthentication authentication)
    {
        if (AuthenticationSpecification.isSatisfiedBy(authentication))
        {
            authentication.setState(Authentication.State.Authenticated);
        }
    }

}
