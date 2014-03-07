package nl.knaw.dans.common.lang.annotations.ldap;

/**
 * Translate Attribute Values from and to LDAP Use this when your methods values don't correspond
 * one-to-one with the attribute values in LDAP. Add your implementation of the translator to the
 * annotation and then the LdapMapper can use it to do the translation while mapping. annotation
 * parameter: valueTranslator
 * 
 * @author paulboon
 */
public interface LdapAttributeValueTranslator<T>
{

    T fromLdap(Object value);

    Object toLdap(T value);
}
