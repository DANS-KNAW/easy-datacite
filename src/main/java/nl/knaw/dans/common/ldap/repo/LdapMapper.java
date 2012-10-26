package nl.knaw.dans.common.ldap.repo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapAttributeValueTranslator;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.util.Base64Coder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic mapper for ldap-annotated objects.
 * <p/>
 * Single values can be mapped by annotating the attribute, the getter or setter.
 * <p/>
 * Collections in the ldap-annotated objects should be mapped on the getter-method (for marshaling)
 * and on an add- or set-method for a single item in the (homogeneous) collection.
 * The reason for this is that the ldap attributes will not be a collection,
 * but instead the ldap entity will have multiple instances of the same attribute - with different values.
 * Each time the unmarshalling 'finds' such an attribute in the ldap entity
 * it will use the single-item 'add' method to add the value to the collection.
 *
 * @see LdapObject
 * @see LdapAttribute
 * @author ecco Feb 16, 2009
 * @param <T>
 *        the mapped type
 */
public class LdapMapper<T>
{

    /**
     * The encryption algorithm used for marshaling {@link LdapAttribute}-annotated fields and methods which have
     * {@link LdapAttribute#oneWayEncrypted()} set to <code>true</code>.
     */
    public static final String ENCRYPTION_ALGORITHM = "SHA";

    public static final String USERPASSWORD = "userPassword";

    /**
     * The implementing class of the mapped type T.
     */
    private Class<? extends T> clazz;

    /**
     * List with annotated fields of the implementing class.
     */
    private List<Field> annotatedFields;

    /**
     * List with annotated getter-methods of the implementing class.
     */
    private List<Method> annotatedGetMethods;

    /**
     * List with annotated setter-methods of the implementing class.
     */
    private List<Method> annotatedSetMethods;

    /**
     * A set with the string-notation of ldap object classes.
     */
    private Set<String> objectClasses;

    /**
     * Synchronization object used while populating the lists with annotated getter-methods and annotated
     * setter-methods.
     */
    private Object syncPopulateMethodsLists = new Object();

    private Map<Class, LdapAttributeValueTranslator> valueTranslatorMap = new HashMap<Class, LdapAttributeValueTranslator>();

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(LdapMapper.class);

    /**
     * Construct a new LdapMapper for the type T and mapped implementing class <code>clazz</code>. Fields and/or methods
     * of the implementing class <code>clazz</code> should be annotated with {@link LdapAttribute}-annotations.
     * Optionally the class type declaration can be annotated with an {@link LdapObject}-annotation.
     *
     * @param clazz
     *        the implementing mapped class
     */
    public LdapMapper(Class<? extends T> clazz)
    {
        this.clazz = clazz;
    }

    /**
     * Marshal an object to attributes.
     *
     * @param instance
     *        the object to be marshaled
     * @param forUpdate
     *        is this an add or update operation
     * @return the attributes derived from annotated fields and methods of the object
     * @throws MissingAttributeException
     *         if a required attribute is null or blank
     * @throws LdapMappingException
     *         wrapper for various exceptions
     */
    public Attributes marshal(T instance, boolean forUpdate) throws MissingAttributeException, LdapMappingException
    {
        if (!clazz.equals(instance.getClass()))
        {
            throw new IllegalArgumentException(instance.getClass().getName() + " is not a " + clazz.getName());
        }
        Attributes attrs = new BasicAttributes();
        Attribute oc = new BasicAttribute("objectclass");
        for (String objectClass : getObjectClasses())
        {
            oc.add(objectClass);
        }
        attrs.put(oc);

        loadAttributesFromFields(instance, attrs, forUpdate);
        loadAttributesFromMethods(instance, attrs, forUpdate);

        return attrs;
    }

    // from the annotated object fields to LDAP attributes
    private void loadAttributesFromFields(T instance, Attributes attrs, boolean forUpdate) throws MissingAttributeException, LdapMappingException
    {
        for (Field field : getAnnotatedFields())
        {
            if (!field.getAnnotation(LdapAttribute.class).readOnly())
            {
                String attrID = field.getAnnotation(LdapAttribute.class).id();
                boolean required = field.getAnnotation(LdapAttribute.class).required();
                boolean oneWayEncrypted = field.getAnnotation(LdapAttribute.class).oneWayEncrypted();
                String encrypted = field.getAnnotation(LdapAttribute.class).encrypted();

                Class valueTranslatorClass = field.getAnnotation(LdapAttribute.class).valueTranslator();

                try
                {
                    field.setAccessible(true);
                    Object value = field.get(instance);

                    LdapAttributeValueTranslator valueTranslator = getValueTranslator(valueTranslatorClass);

                    if (required)
                    {
                        checkRequired(value, attrID, instance.getClass() + "." + field.getName());
                    }
                    loadAttribute(attrs, attrID, value, oneWayEncrypted, forUpdate, encrypted, valueTranslator);
                }
                catch (IllegalArgumentException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (InstantiationException e)
                {
                    throw new LdapMappingException(e);
                }
            }
        }

    }

    private synchronized LdapAttributeValueTranslator getValueTranslator(Class valueTranslatorClass) throws InstantiationException, IllegalAccessException
    {
        LdapAttributeValueTranslator valueTranslator = valueTranslatorMap.get(valueTranslatorClass);
        if (valueTranslator == null)
        {
            valueTranslator = (LdapAttributeValueTranslator) valueTranslatorClass.newInstance();
            valueTranslatorMap.put(valueTranslatorClass, valueTranslator);
        }
        return valueTranslator;
    }

    // from the annotated object methods to LDAP attributes
    private void loadAttributesFromMethods(T instance, Attributes attrs, boolean forUpdate) throws MissingAttributeException, LdapMappingException
    {
        for (Method method : getAnnotatedGetMetods())
        {
            if (!method.getAnnotation(LdapAttribute.class).readOnly())
            {
                String attrID = method.getAnnotation(LdapAttribute.class).id();
                boolean required = method.getAnnotation(LdapAttribute.class).required();
                boolean oneWayEncrypted = method.getAnnotation(LdapAttribute.class).oneWayEncrypted();
                String encrypted = method.getAnnotation(LdapAttribute.class).encrypted();

                Class valueTranslatorClass = method.getAnnotation(LdapAttribute.class).valueTranslator();

                try
                {
                    method.setAccessible(true);
                    Object value = method.invoke(instance);
                    LdapAttributeValueTranslator valueTranslator = getValueTranslator(valueTranslatorClass);

                    if (required)
                    {
                        checkRequired(value, attrID, instance.getClass() + "." + method.getName());
                    }
                    loadAttribute(attrs, attrID, value, oneWayEncrypted, forUpdate, encrypted, valueTranslator);
                }
                catch (IllegalArgumentException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (InvocationTargetException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (InstantiationException e)
                {
                    throw new LdapMappingException(e);
                }
            }
        }

    }

    private void checkRequired(Object value, String attrID, String origin) throws MissingAttributeException
    {
        String msg = null;
        if (value == null)
        {
            msg = "The attribute with id '" + attrID + "' is required, but was null: " + origin;

        }
        if (value instanceof String)
        {
            if (StringUtils.isBlank((String) value))
            {
                msg = "The attribute with id '" + attrID + "' is required, but was blank: " + origin;
            }
        }
        if (msg != null)
        {
            logger.debug(msg);
            throw new MissingAttributeException(msg);
        }
    }

    private void loadAttribute(Attributes attrs, String attrID, Object value, boolean oneWayEncrypted, boolean forUpdate, String encrypted,
            LdapAttributeValueTranslator translator) throws LdapMappingException
    {
        if (value instanceof Collection)
        {
            Collection<?> collection = (Collection<?>) value;
            for (Object colValue : collection)
            {
                loadSingleAttribute(attrs, attrID, colValue, oneWayEncrypted, forUpdate, encrypted, translator);
            }
            if (forUpdate && collection.isEmpty())
            {
                attrs.put(new BasicAttribute(attrID));
            }
        }
        else
        {
            loadSingleAttribute(attrs, attrID, value, oneWayEncrypted, forUpdate, encrypted, translator);
        }
    }

    private void loadSingleAttribute(Attributes attrs, String attrID, Object value, boolean oneWayEncrypted, boolean forUpdate, String encrypted,
            LdapAttributeValueTranslator translator) throws LdapMappingException
    {
        if (value != null)
        {
            value = translator.toLdap(value);

            if (oneWayEncrypted)
            {
                value = encrypt(value);
            }
            else if (ENCRYPTION_ALGORITHM.equals(encrypted))
            {
                value = preparePassword(value);
            }
            else if (value.getClass().isEnum())
            {
                value = value.toString();
            }
            else if (Boolean.class.equals(value.getClass()))
            {
                value = ((Boolean) value) ? "TRUE" : "FALSE";
            }
            else if (value instanceof Number)
            {
                value = value.toString();
            }

            Attribute attr = attrs.get(attrID);
            if (attr == null)
            {
                attrs.put(attrID, value);
            }
            else
            {
                attr.add(value);
            }

        }
        else if (!USERPASSWORD.equals(attrID) && forUpdate)
        {
            attrs.put(new BasicAttribute(attrID));
        }
    }

    /**
     * Unmarshal an object from the given attributes. The object to be unmarshaled should have a public no-argument
     * constructor.
     *
     * @param attrs
     *        the attributes to unmarshal from
     * @return the unmarshaled object
     * @throws LdapMappingException
     *         wrapper for various exceptions
     * @see #unmarshal(Object, Attributes)
     */
    public T unmarshal(Attributes attrs) throws LdapMappingException
    {
        T instance = null;
        try
        {
            instance = clazz.getConstructor().newInstance();
        }
        catch (IllegalArgumentException e)
        {
            final String msg = "Class " + clazz.getName() + " should have a public no-argument constructor.";
            logger.error(msg);
            throw new LdapMappingException(msg, e);
        }
        catch (InstantiationException e)
        {
            final String msg = "Class " + clazz.getName() + " should not be abstract.";
            logger.error(msg);
            throw new LdapMappingException(msg, e);
        }
        catch (IllegalAccessException e)
        {
            final String msg = "Class " + clazz.getName() + " should have a public no-argument constructor.";
            logger.error(msg);
            throw new LdapMappingException(msg, e);
        }
        catch (InvocationTargetException e)
        {
            final String msg = "Constructor of class " + clazz.getName() + " throws an exception: ";
            logger.error(msg, e);
            throw new LdapMappingException(msg, e);
        }
        catch (NoSuchMethodException e)
        {
            final String msg = "Class " + clazz.getName() + " should have a public no-argument constructor.";
            logger.error(msg);
            throw new LdapMappingException(msg, e);
        }

        return unmarshal(instance, attrs);
    }

    /**
     * Unmarshal the object from the given attributes.
     *
     * @param instance
     *        object to handle while unmarshaling
     * @param attrs
     *        the attributes to unmarshal from
     * @return the instance with fields set in correspondence with the given attributes
     * @throws LdapMappingException
     *         wrapper for various exceptions
     */
    public T unmarshal(T instance, Attributes attrs) throws LdapMappingException
    {
        if (!clazz.equals(instance.getClass()))
        {
            throw new IllegalArgumentException(instance.getClass().getName() + " is not a " + clazz.getName());
        }
        setFields(instance, attrs);
        setMethods(instance, attrs);
        return instance;
    }

    // from LDAP attributes to the annotated object methods
    private void setMethods(T instance, Attributes attrs) throws LdapMappingException
    {
        for (Method method : getAnnotatedSetMethods())
        {
            String attrID = method.getAnnotation(LdapAttribute.class).id();
            if (!method.getAnnotation(LdapAttribute.class).oneWayEncrypted()
                    || !ENCRYPTION_ALGORITHM.equals(method.getAnnotation(LdapAttribute.class).encrypted()))
            {
                Attribute attr = attrs.get(attrID);
                Class<?> type = method.getParameterTypes()[0];
                Object value = null;

                Class valueTranslatorClass = method.getAnnotation(LdapAttribute.class).valueTranslator();

                try
                {
                    if (attr != null)
                    {
                        method.setAccessible(true);
                        for (int i = 0; i < attr.size(); i++)
                        {
                            Object o = attr.get(i); // are not all attribute values Strings?
                            value = getSingleValue(type, o);
                            if (value != null)
                            {
                                LdapAttributeValueTranslator valueTranslator = getValueTranslator(valueTranslatorClass);
                                value = valueTranslator.fromLdap(value);

                                method.invoke(instance, value);
                            }
                        }
                    }
                }
                catch (IllegalArgumentException e)
                {
                    final String msg = "Expected " + type + " but was " + value;
                    logger.error(msg);
                    throw new LdapMappingException(msg, e);
                }
                catch (NamingException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (InvocationTargetException e)
                {
                    final String msg = "Method threw exception: ";
                    logger.error(msg, e);
                    throw new LdapMappingException(msg, e);
                }
                catch (IndexOutOfBoundsException e)
                {
                    final String msg = "Setter method has no argument: ";
                    logger.error(msg, e);
                    throw new LdapMappingException(msg, e);
                }
                catch (InstantiationException e)
                {
                    final String msg = "Could not instantiate attribute value translator: ";
                    logger.error(msg, e);
                    throw new LdapMappingException(msg, e);
                }
            }
        }

    }

    // from LDAP attributes to the annotated object fields
    private void setFields(T instance, Attributes attrs) throws LdapMappingException
    {
        for (Field field : getAnnotatedFields())
        {

            String attrID = field.getAnnotation(LdapAttribute.class).id();
            if (!field.getAnnotation(LdapAttribute.class).oneWayEncrypted()
                    && !ENCRYPTION_ALGORITHM.equals(field.getAnnotation(LdapAttribute.class).encrypted()))
            {

                Attribute attr = attrs.get(attrID);
                Class<?> type = field.getType();
                Object value = null;

                Class valueTranslatorClass = field.getAnnotation(LdapAttribute.class).valueTranslator();

                try
                {
                    if (attr != null)
                    {
                        value = getSingleValue(type, attr.get());
                        if (value != null)
                        {
                            LdapAttributeValueTranslator valueTranslator = getValueTranslator(valueTranslatorClass);
                            value = valueTranslator.fromLdap(value);

                            field.setAccessible(true);
                            field.set(instance, value);
                        }
                    }
                }
                catch (IllegalArgumentException e)
                {
                    final String msg = "Expected " + type + " but was " + value;
                    logger.error(msg);
                    throw new LdapMappingException(msg, e);
                }
                catch (NamingException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new LdapMappingException(e);
                }
                catch (ClassCastException e)
                {
                    final String msg = "Expected " + type + " but was " + value;
                    logger.error(msg);
                    throw new LdapMappingException(msg, e);
                }
                catch (InstantiationException e)
                {
                    final String msg = "Could not instantiate attribute value translator: ";
                    logger.error(msg, e);
                    throw new LdapMappingException(msg, e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object getSingleValue(Class<?> type, Object o) throws NamingException
    {
        Object value = null;
        if (o != null)
        {
            if (type.isPrimitive())
            {
                value = getPrimitive(type, (String) o);
            }
            else if (type.isEnum())
            {
                value = Enum.valueOf(type.asSubclass(Enum.class), (String) o);
            }
            else
            {
                value = o;
            }
        }
        return value;
    }

    private Object getPrimitive(Class<?> type, String s) throws NamingException
    {
        Object value = s;
        if (Integer.TYPE.equals(type))
        {
            value = Integer.parseInt(s);
        }
        else if (Boolean.TYPE.equals(type))
        {
            value = new Boolean("TRUE".equals(s));
        }
        else if (Long.TYPE.equals(type))
        {
            value = Long.parseLong(s);
        }
        else if (Float.TYPE.equals(type))
        {
            value = Float.parseFloat(s);
        }
        else if (Double.TYPE.equals(type))
        {
            value = Double.parseDouble(s);
        }
        else if (Byte.TYPE.equals(type))
        {
            value = Byte.parseByte(s);
        }
        else if (Short.TYPE.equals(type))
        {
            value = Short.parseShort(s);
        }
        return value;
    }

    /**
     * Get a list of fields annotated with {@link LdapAttribute} of the implementing mapped class and it's super
     * classes.
     *
     * @return list of annotated fields
     */
    protected synchronized List<Field> getAnnotatedFields()
    {
        if (annotatedFields == null)
        {
            annotatedFields = Collections.synchronizedList(new ArrayList<Field>());
            Class<?> superC = clazz;
            while (superC != null)
            {
                Field[] fields = superC.getDeclaredFields();
                for (Field field : fields)
                {
                    if (field.isAnnotationPresent(LdapAttribute.class))
                    {
                        annotatedFields.add(field);
                    }
                }
                superC = superC.getSuperclass();
            }
        }
        return annotatedFields;
    }

    /**
     * Get a list of getter-methods annotated with {@link LdapAttribute} of the implementing mapped class and it's super
     * classes.
     *
     * @return list of annotated getter-methods
     */
    protected List<Method> getAnnotatedGetMetods()
    {
        synchronized (syncPopulateMethodsLists)
        {
            if (annotatedGetMethods == null)
            {
                populateMethodLists();
            }
            return annotatedGetMethods;
        }
    }

    /**
     * Get a list of setter-methods annotated with {@link LdapAttribute} of the implementing mapped class and it's super
     * classes.
     *
     * @return list of annotated setter-methods
     */
    protected List<Method> getAnnotatedSetMethods()
    {
        synchronized (syncPopulateMethodsLists)
        {
            if (annotatedSetMethods == null)
            {
                populateMethodLists();
            }
            return annotatedSetMethods;
        }
    }

    private void populateMethodLists()
    {
        annotatedGetMethods = Collections.synchronizedList(new ArrayList<Method>());
        annotatedSetMethods = Collections.synchronizedList(new ArrayList<Method>());
        Class<?> superC = clazz;
        while (superC != null)
        {
            Method[] methods = superC.getDeclaredMethods();
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(LdapAttribute.class))
                {
                    if (method.getReturnType().equals(void.class))
                    {
                        // this is a setter method
                        annotatedSetMethods.add(method);
                    }
                    else
                    {
                        // its a getter method
                        annotatedGetMethods.add(method);
                    }
                }
            }
            superC = superC.getSuperclass();
        }
    }

    /**
     * Get the set of ldap objectClasses as annotated with {@link LdapObject} on the implementing mapped class and it's
     * super classes.
     *
     * @return the set of ldap objectClasses
     */
    protected Set<String> getObjectClasses()
    {
        if (objectClasses == null)
        {
            objectClasses = new LinkedHashSet<String>();
            Class<?> superC = clazz;
            while (superC != null)
            {
                if (superC.isAnnotationPresent(LdapObject.class))
                {
                    String[] oc = superC.getAnnotation(LdapObject.class).objectClasses();
                    objectClasses.addAll(Arrays.asList(oc));
                }
                superC = superC.getSuperclass();
            }
            if (!objectClasses.contains("top"))
            {
                objectClasses.add("top");
            }
        }
        return objectClasses;
    }

    private String encrypt(Object value) throws LdapMappingException
    {
        String encrypted = null;
        try
        {
            encrypted = hashPassword(value.toString(), ENCRYPTION_ALGORITHM);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new LdapMappingException(e);
        }
        return encrypted;
    }

    private static String hashPassword(final String password, String algorithm) throws NoSuchAlgorithmException
    {
        // Calculate hash value
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(password.getBytes());
        byte[] bytes = md.digest();

        String hash = new String(Base64Coder.encode(bytes));
        return "{" + algorithm + "}" + hash;
    }

    private static String preparePassword(Object password)
    {
        // put the encryption algorithm in front of the password so Ldap recognize the encryption used
        return "{" + ENCRYPTION_ALGORITHM + "}" + password.toString();
    }

}
