package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;

public class PermissionRequestModel implements Serializable
{
    public static final String ACCEPTING_CONDITIONS_OF_USE = "acceptingConditionsOfUse";
    public static final String REQUEST_TITLE = "requestTitle";
    public static final String REQUEST_THEME = "requestTheme";

    private static final long serialVersionUID = 4194195525786100905L;

    private String requestTitle;
    private String requestTheme;
    
    private String requestLink;
    private String permissionsTabLink;

    private boolean acceptingConditionsOfUse;

    public PermissionRequestModel()
    {

    }

    public String getRequestTitle()
    {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle)
    {
        this.requestTitle = requestTitle;
    }

    public String getRequestTheme()
    {
        return requestTheme;
    }

    public void setRequestTheme(String requestTheme)
    {
        this.requestTheme = requestTheme;
    }

    public boolean isAcceptingConditionsOfUse()
    {
        return acceptingConditionsOfUse;
    }

    public void setAcceptingConditionsOfUse(boolean acceptingConditionsOfUse)
    {
        this.acceptingConditionsOfUse = acceptingConditionsOfUse;
    }

    public String getRequestLink()
    {
        return requestLink;
    }

    public void setRequestLink(String requestLink)
    {
        this.requestLink = requestLink;
    }

    public String getPermissionsTabLink()
    {
        return permissionsTabLink;
    }

    public void setPermissionsTabLink(String permissionsTabLink)
    {
        this.permissionsTabLink = permissionsTabLink;
    }

}
