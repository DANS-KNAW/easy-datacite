package nl.knaw.dans.easy.web.authn.login;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.ApplicationUser;

class FederativeUserInfoExtractor
{
    static final String FEDUSER_ATTRIBUTE_NAME_UID = Services.getFederativeUserService().getPropertyNameUserId();
    static final String FEDUSER_ATTRIBUTE_NAME_HOMEORG = Services.getFederativeUserService().getPopertyNameOrganization();
    static final String FEDUSER_ATTRIBUTE_NAME_EMAIL = Services.getFederativeUserService().getPropertyNameEmail();
    static final String FEDUSER_ATTRIBUTE_NAME_GIVENNAME = Services.getFederativeUserService().getPropertyNameFirstName();
    static final String FEDUSER_ATTRIBUTE_NAME_SURNAME = Services.getFederativeUserService().getPropertyNameSurname();

    // Notes
    // request.getAttributeNames(); will not return the Shibboleth variables
    // Also we can see the variables on eof12:
    // https://eof12.dans.knaw.nl/cgi-bin/env
    // But when the vars get into tomcat via AJP the prefix "AJP_" is removed
    // and underscores '_' are translated to minuses '-'.
    //
    // From our Java servlet we might want to use:
    // Shib-HomeOrg -> organisation
    // Shib-eduPersonPN -> fedUserId
    // Shib-email -> email
    // Shib-givenName -> firstName
    // Shib-surName -> lastName

    public static String extractFederativeUserId(HttpServletRequest request)
    {
        String fedUserId = null;
        fedUserId = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_UID);
        return fedUserId;
    }

    public static ApplicationUser extractFederativeUser(HttpServletRequest request)
    {
        ApplicationUser appUser = new ApplicationUser();

        // Extract information to initialize the User object
        appUser.setUserId(extractFederativeUserId(request));

        String fedEmail = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_EMAIL);
        if (fedEmail != null && !fedEmail.isEmpty())
        {
            appUser.setEmail(fedEmail);
        }

        String fedFirstName = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_GIVENNAME);
        if (fedFirstName != null && !fedFirstName.isEmpty())
        {
            appUser.setFirstname(fedFirstName);
        }

        String fedSurname = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_SURNAME);
        if (fedSurname != null && !fedSurname.isEmpty())
        {
            appUser.setSurname(fedSurname);
        }

        String fedOrganization = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_HOMEORG);
        if (fedOrganization != null && !fedOrganization.isEmpty())
        {
            appUser.setOrganization(fedOrganization);
        }

        return appUser;
    }
}
