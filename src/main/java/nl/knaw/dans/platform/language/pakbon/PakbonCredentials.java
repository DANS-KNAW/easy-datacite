package nl.knaw.dans.platform.language.pakbon;

public class PakbonCredentials
{
    
    private static PakbonCredentials INSTANCE;
    
    public static PakbonCredentials instance()
    {
        if (INSTANCE == null)
        {
            throw new IllegalStateException(PakbonCredentials.class.getSimpleName() + " not initialized.");
        }
        return INSTANCE;
    }
    
    private final String username;
    private final String password;
    
    public PakbonCredentials(String username, String password)
    {
        this.username = username;
        this.password = password;
        INSTANCE = this;
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
