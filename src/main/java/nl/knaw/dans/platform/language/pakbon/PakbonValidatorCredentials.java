package nl.knaw.dans.platform.language.pakbon;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PakbonValidatorCredentials implements Serializable
{
    private String username;
    private String password;

    /*
     * A default constructor is required by Wicket Spring to be able to create a proxy for this class.
     */
    public PakbonValidatorCredentials()
    {
    }

    public PakbonValidatorCredentials(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

}
