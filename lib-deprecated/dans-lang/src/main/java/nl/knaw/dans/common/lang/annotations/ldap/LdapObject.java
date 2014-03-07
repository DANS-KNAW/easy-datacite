package nl.knaw.dans.common.lang.annotations.ldap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation for ldap-mapped classes and their super classes that sums the ldap objectClasses
 * to use when marshaling.
 * 
 * @author ecco Feb 16, 2009
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LdapObject
{
    /**
     * An array of strings, indicating ldap objectClasses in reverse hierarchical order.
     * 
     * @return array of strings, indicating ldap objectClasses
     */
    String[] objectClasses();
}
