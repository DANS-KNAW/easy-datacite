package nl.knaw.dans.platform.language.pakbon;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PakbonValidatorCredentials implements Serializable
{
    private String username;
    private String password;

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
