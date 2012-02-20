package nl.knaw.dans.easy.web.authn;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.easy.web.common.ApplicationUser;


public class FederativeUserInfoExtractor
{
    static final String FEDUSER_ATTRIBUTE_NAME_EDUPERSONPN = "Shib-eduPersonPN";
    static final String FEDUSER_ATTRIBUTE_NAME_HOMEORG = "Shib-HomeOrg";
    static final String FEDUSER_ATTRIBUTE_NAME_EMAIL = "Shib-email";
    static final String FEDUSER_ATTRIBUTE_NAME_GIVENNAME = "Shib-givenName";
    static final String FEDUSER_ATTRIBUTE_NAME_SURNAME = "Shib-surName";
     // Notes
     // request.getAttributeNames(); will not return the Shibboleth variables
     // Also we can see the variables on eof12: 
     // https://eof12.dans.knaw.nl/cgi-bin/env
     // But when the vars get into tomcat via AJP the prefix "AJP_" is removed 
     // and underscores '_' are translated to minuses '-'. 
     //
     // From our Java servlet we might want to use: 
     // Shib-HomeOrg ->  organisation
     // Shib-eduPersonPN -> fedUserId
     // Shib-email -> email
     // Shib-givenName -> firstName
     // Shib-surName -> lastName

    public static String extractFederativeUserId(HttpServletRequest request)
    {
        String fedUserId = null;
        
        fedUserId = (String)request.getAttribute(FEDUSER_ATTRIBUTE_NAME_EDUPERSONPN);
        
        return fedUserId;
    }
    
    public static ApplicationUser extractFederativeUser(HttpServletRequest request)
    {
        ApplicationUser appUser = new ApplicationUser();
        
        // Extract information to initialize the User object
        appUser.setUserId(extractFederativeUserId(request)); 
        
        String fedEmail = (String)request.getAttribute(FEDUSER_ATTRIBUTE_NAME_EMAIL);
        if (fedEmail != null && !fedEmail.isEmpty())
        {
            appUser.setEmail(fedEmail); 
        }
        
        String fedFirstName = (String)request.getAttribute(FEDUSER_ATTRIBUTE_NAME_GIVENNAME);
        if (fedFirstName != null && !fedFirstName.isEmpty())
        {
            appUser.setFirstname(fedFirstName); 
        }
        
        String fedSurname = (String)request.getAttribute(FEDUSER_ATTRIBUTE_NAME_SURNAME);
        if (fedSurname != null && !fedSurname.isEmpty())
        {
            appUser.setSurname(fedSurname); 
        }
        
        String fedOrganization = (String)request.getAttribute(FEDUSER_ATTRIBUTE_NAME_HOMEORG);
        if (fedOrganization != null && !fedOrganization.isEmpty())
        {
            appUser.setOrganization(fedOrganization); 
        }
        
        return appUser;
    }
    
    // Utility for printing request header info
    public static void printRequest(HttpServletRequest request)
    {
        System.out.println("headers");
        Enumeration e = request.getHeaderNames();
        String value = null;
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            value = request.getHeader(name);
            System.out.println(name + "=" + value);
        }
        
        System.out.println("attributes");
        e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            value = request.getAttribute(name).toString();
            System.out.println(name + "=" + value);
        }
        
        // NOTE the Shibboleth attributes are not available from the getAttributeNames()
    }
}
