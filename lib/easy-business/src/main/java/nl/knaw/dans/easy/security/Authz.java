package nl.knaw.dans.easy.security;

/**
 * Classes implementing this interface are concerned with authorization.
 * 
 * @author ecco Aug 2, 2009
 */
public interface Authz
{
    /**
     * Get the SecurityOfficer for the given item.
     * 
     * @param item
     *        string-representation of a component or action
     * @return the SecurityOfficer for the given item, or, if a SecurityOfficer for the given item is not
     *         available, a default SecurityOfficer that denies all implications
     */
    SecurityOfficer getSecurityOfficer(String item);

    boolean isProtectedPage(String pageName);

    boolean hasSecurityOfficer(String item);

}
