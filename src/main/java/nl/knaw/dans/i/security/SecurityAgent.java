package nl.knaw.dans.i.security;

public interface SecurityAgent
{
    
    String getSecurityId();
    
    boolean isAllowed(String ownerId, Object...args);

}
