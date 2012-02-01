package nl.knaw.dans.common.ldap.ds;

public class Constants
{

    public static final String CONTEXT_FACTORY              = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String SIMPLE_AUTHENTICATION        = "simple";
    
    // apacheds
    public static final String APACHEDS_DEFAULT_PROVIDERURL          = "ldap://localhost:10389";
    public static final String APACHEDS_DEFAULT_SECURITY_PRINCIPAL   = "uid=admin,ou=system";
    
    // openldap
    public static final String OPENLDAP_DEFAULT_PROVIDERURL          = "ldap://localhost:389";
    public static final String OPENLDAP_DEFAULT_SECURITY_PRINCIPAL   = "cn=Manager,dc=dans,dc=knaw,dc=nl";
    
    public static final String DEFAULT_SECURITY_CREDENTIALS = "secret";
    
    public static final String dcDANS                       = "dans";
    public static final String dcKNAW                       = "knaw";
    public static final String dcNL                         = "nl";
    public static final String DANS_CONTEXT                 = "dc=" + dcDANS + ",dc=" + dcKNAW + ",dc=" + dcNL;
    
    public static final String ouTEST                       = "test";
    public static final String TEST_CONTEXT                 = "ou=" + ouTEST + "," + DANS_CONTEXT;
    
    public static final String ouUSERS                      = "users";
    public static final String TEST_USERS_CONTEXT           = "ou=" + ouUSERS + "," + TEST_CONTEXT;
    
    public static final String ouMIGRATION                  = "migration";
    public static final String TEST_MIGRATION_CONTEXT       = "ou=" + ouMIGRATION + "," + TEST_CONTEXT;
    
    public static final String ouFEDERATION                  = "federation";
    public static final String TEST_FEDERATION_CONTEXT       = "ou=" + ouFEDERATION + "," + TEST_CONTEXT;

}
