package nl.knaw.dans.common.lang.annotations.ldap;

/**
 * This implementation does not translate at all, but passes the given Object's as-is Can be used as
 * default translator
 * 
 * @author paulboon
 */
public class PassthroughLdapAttributeValueTranslator implements LdapAttributeValueTranslator<Object>
{

    public Object fromLdap(Object value)
    {
        return value;
    }

    public Object toLdap(Object value)
    {
        return value;
    }

}
