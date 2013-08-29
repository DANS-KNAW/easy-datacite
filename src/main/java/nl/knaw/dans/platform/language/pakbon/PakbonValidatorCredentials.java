package nl.knaw.dans.platform.language.pakbon;

public class PakbonValidatorCredentials
{
    
    private static PakbonValidatorCredentials INSTANCE;
    
    public static PakbonValidatorCredentials instance()
    {
        if (INSTANCE == null)
        {
            throw new IllegalStateException(PakbonValidatorCredentials.class.getSimpleName() + " not initialized.");
        }
        return INSTANCE;
    }
    
    private final String username;
    private final String password;
    
    public PakbonValidatorCredentials(String username, String password)
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
