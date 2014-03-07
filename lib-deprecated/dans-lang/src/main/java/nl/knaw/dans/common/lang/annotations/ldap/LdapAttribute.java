package nl.knaw.dans.common.lang.annotations.ldap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mappes a field or method to an ldap attribute.
 * 
 * @author ecco Feb 14, 2009
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface LdapAttribute
{
    /**
     * The attrID of the mapped attribute.
     * 
     * @return the attrID of the mapped attribute
     */
    String id();

    /**
     * Sets whether the attribute is required (must) by the ldap objectClass or not (may). The default is
     * <code>false</code>.
     * 
     * @return <code>true</code> for required fields (must), <code>false</code> otherwise
     */
    boolean required() default false;

    boolean readOnly() default false;

    /**
     * Sets whether the field or method value should be one-way-encrypted to the attribute value. The
     * default is <code>false</code>.
     * 
     * @return <code>true</code> if the value should be one-way-encrypted, <code>false</code> otherwise
     */
    boolean oneWayEncrypted() default false;

    /**
     * Sets whether the field or method value is encrypted to the attribute value. The default is
     * <code>""</code>.
     * 
     * @return the encryption method
     */
    String encrypted() default "";

    /**
     * Specify the implementation class of the valueTranslator to use when mapping from/to Ldap
     * 
     * @return The implementation class of the valueTranslator
     */
    Class<? extends LdapAttributeValueTranslator<? extends Object>> valueTranslator() default PassthroughLdapAttributeValueTranslator.class;
}
