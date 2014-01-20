package nl.knaw.dans.easy.web.authn.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.Services;

public class FederationUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String givenName;
    private String surName;
    private String homeOrg;

    private static FederativeUserService federatveUserService;

    public static FederationUser fromHttpRequest(HttpServletRequest request)
    {
        FederationUser appUser = new FederationUser();
        String userId = (String) request.getAttribute(getFederativeUserService().getPropertyNameRemoteUser());
        if (!isSet(userId))
        {
            throw new IllegalArgumentException(String.format("Attribute %s must be present", getFederativeUserService().getPropertyNameRemoteUser()));
        }
        appUser.setUserId(userId);
        String mail = (String) request.getAttribute(getFederativeUserService().getPropertyNameEmail());
        if (isSet(mail))
        {
            appUser.setEmail(mail);
        }
        String givenName = (String) request.getAttribute(getFederativeUserService().getPropertyNameFirstName());
        if (isSet(givenName))
        {
            appUser.setGivenName(givenName);
        }
        String surName = (String) request.getAttribute(getFederativeUserService().getPropertyNameSurname());
        if (isSet(surName))
        {
            appUser.setSurName(surName);
        }
        String homeOrg = (String) request.getAttribute(getFederativeUserService().getPopertyNameOrganization());
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

    public void setUserId(String userId)
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

    public static FederationUser fromFile(File debugFile)
    {
        FederationUser u = new FederationUser();
        BeanWrapper w = new BeanWrapperImpl(u);
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(debugFile));
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Could not find the FederationUser debug file", e);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not read the FederationUser debug file", e);
        }
        w.setPropertyValues(p);
        return u;
    }

    public static FederativeUserService getFederativeUserService()
    {
        if (federatveUserService==null)
            federatveUserService = Services.getFederativeUserService();
        return federatveUserService;
    }
}
