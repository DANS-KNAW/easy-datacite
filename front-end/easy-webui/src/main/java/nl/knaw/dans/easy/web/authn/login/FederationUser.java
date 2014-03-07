package nl.knaw.dans.easy.web.authn.login;

import java.io.Serializable;

public class FederationUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String givenName;
    private String surName;
    private String homeOrg;

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
}
