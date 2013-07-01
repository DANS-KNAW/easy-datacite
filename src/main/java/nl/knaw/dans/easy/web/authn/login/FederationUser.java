package nl.knaw.dans.easy.web.authn.login;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.easy.servicelayer.services.Services;

public class FederationUser implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String FEDUSER_ATTRIBUTE_NAME_REMOTE_USER = Services.getFederativeUserService().getPropertyNameRemoteUser();
    private static final String FEDUSER_ATTRIBUTE_NAME_GIVENNAME = Services.getFederativeUserService().getPropertyNameFirstName();
    private static final String FEDUSER_ATTRIBUTE_NAME_SURNAME = Services.getFederativeUserService().getPropertyNameSurname();
    private static final String FEDUSER_ATTRIBUTE_NAME_HOMEORG = Services.getFederativeUserService().getPopertyNameOrganization();
    private static final String FEDUSER_ATTRIBUTE_NAME_EMAIL = Services.getFederativeUserService().getPropertyNameEmail();

    private String userId;
    private String email;
    private String givenName;
    private String surName;
    private String homeOrg;

    public static FederationUser fromHttpRequest(HttpServletRequest request)
    {
        FederationUser appUser = new FederationUser();
        String userId = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_REMOTE_USER);
        if (!isSet(userId))
        {
            throw new IllegalArgumentException(String.format("Attribute %s must be present", FEDUSER_ATTRIBUTE_NAME_REMOTE_USER));
        }
        appUser.setUserId(userId);
        String mail = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_EMAIL);
        if (isSet(mail))
        {
            appUser.setEmail(mail);
        }
        String givenName = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_GIVENNAME);
        if (isSet(givenName))
        {
            appUser.setGivenName(givenName);
        }
        String surName = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_SURNAME);
        if (isSet(surName))
        {
            appUser.setSurName(surName);
        }
        String homeOrg = (String) request.getAttribute(FEDUSER_ATTRIBUTE_NAME_HOMEORG);
        if (isSet(homeOrg))
        {
            appUser.setHomeOrg(homeOrg);
        }

        return appUser;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getSurName()
    {
        return surName;
    }

    public void setSurName(String surName)
    {
        this.surName = surName;
    }

    public String getHomeOrg()
    {
        return homeOrg;
    }

    public void setHomeOrg(String homeOrg)
    {
        this.homeOrg = homeOrg;
    }

    public String getUserId()
    {
        return userId;
    }

    private void setUserId(String userId)
    {
        this.userId = userId;
    }

    private static boolean isSet(String prop)
    {
        return !"".equals(prop) && prop != null;
    }

    public String getUserDescription()
    {
        String n = "";
        if (isSet(surName))
        {
            n += surName;
        }
        if (isSet(givenName))
        {
            n += ", " + givenName;
        }
        if (isSet(email))
        {
            if (n.length() > 0)
            {
                n += " (" + email + ")";
            }
            else
            {
                n += email;
            }
        }
        return n;
    }
}
