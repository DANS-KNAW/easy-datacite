package nl.knaw.dans.easy.web.authn.login;

import javax.servlet.http.HttpServletRequest;

public class PartiallyMockedResultPage extends FederativeAuthenticationResultPage
{
    static boolean hasShibolethSession = true;

    public boolean hasShibbolethSession(final HttpServletRequest request)
    {
        // wicket 1.5 has a addRequestHeader(String, String) on the tester class
        // work around for 1.4
        request.setAttribute("shibSessionId", "mockedSessionID");
        request.setAttribute("email", "mockeEmail");
        request.setAttribute("firstName", "mockedFirstName");
        request.setAttribute("surname", "mockedSurname");
        request.setAttribute("remoteUser", "mockedRemoteUser");
        request.setAttribute("organization", "mockedOrganization");
        return hasShibolethSession;
    }

    @Override
    public boolean isBookmarkable()
    {
        // required for: setStatelessHint(true) in init()
        return true;
    }
}
