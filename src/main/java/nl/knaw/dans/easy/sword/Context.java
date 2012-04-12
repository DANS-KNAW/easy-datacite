package nl.knaw.dans.easy.sword;

public class Context
{
    /** Directory were deposits are unzipped. Default value required for unit tests. */
    private static String unzip = "target/tmp";
    private static String treatment = "easy.sword.server.treatment not configured";
    private static String policy = "easy.sword.server.policy not configured";
    
    public void setUnzip(String unzip)
    {
        Context.unzip = unzip;
    }
    public static String getUnzip()
    {
        return unzip;
    }
    
    public void setTreatment(String treatment)
    {
        Context.treatment = treatment;
    }
    public static String getTreatment()
    {
        return treatment;
    }
    
    public void setPolicy(String policy)
    {
        Context.policy = policy;
    }
    public static String getPolicy()
    {
        return policy;
    }
}
