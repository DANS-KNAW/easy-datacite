package nl.knaw.dans.common.ldap.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapAttributeValueTranslator;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.user.Person;
import nl.knaw.dans.common.lang.user.PersonVO;

import org.junit.Test;

@SuppressWarnings("unused")
public class LdapMapperTest {

    @Test
    public void mapPerson() throws MissingAttributeException, LdapMappingException, NamingException {
        LdapMapper<Person> mapper = new LdapMapper<Person>(PersonVO.class);
        Person sophie = new PersonVO();
        sophie.setAddress("Sophiastraat 21");
        sophie.setCity("Sophia");
        sophie.setCountry("Sophoria");
        sophie.setDepartment("Sophomores");
        sophie.setEmail("sophie@sophomores.com");
        sophie.setFirstname("Sophie");
        sophie.setFunction("Sophocles");
        sophie.setInitials("S.O.S.");
        sophie.setOrganization("Sophonon");
        sophie.setPostalCode("1234 SL");
        sophie.setPrefixes("van de");
        sophie.setSurname("Wever");
        sophie.setTelephone("020 1234567");
        sophie.setTitle("Prof. Dr.");

        Attributes attrs = mapper.marshal(sophie, false);
        // size: 14 + objectclass + cn + displayName
        assertEquals(17, attrs.size());

        // NamingEnumeration<? extends Attribute> nenum = attrs.getAll();
        // while (nenum.hasMoreElements())
        // {
        // Attribute attr = nenum.next();
        // System.err.println(attr.getID() + " " + attr.get());
        // }

        Person sophie2 = mapper.unmarshal(attrs);
        assertEquals("Sophie", sophie2.getFirstname());
    }

    @Test
    public void getAnnotatedFields() throws SecurityException, NoSuchFieldException {
        LdapMapper<Clazz> mapper = new LdapMapper<Clazz>(Clazz.class);
        List<Field> annotatedFields = mapper.getAnnotatedFields();
        assertEquals(12, annotatedFields.size());
        assertTrue(annotatedFields.contains(SuperClazz.class.getDeclaredField("title")));
        assertTrue(annotatedFields.contains(SuperClazz.class.getDeclaredField("object")));
        assertTrue(annotatedFields.contains(SuperClazz.class.getDeclaredField("justAnInt")));
        assertTrue(annotatedFields.contains(Clazz.class.getDeclaredField("integer")));
    }

    @Test
    public void getAnnotatedMethods() {
        LdapMapper<Clazz> mapper = new LdapMapper<Clazz>(Clazz.class);
        List<Method> getMethods = mapper.getAnnotatedGetMetods();
        List<Method> setMethods = mapper.getAnnotatedSetMethods();
        assertEquals(3, getMethods.size());
        assertEquals(2, setMethods.size());
    }

    @Test
    public void unmarshalNoInstance() throws LdapMappingException {
        LdapMapper<Clazz> mapper = new LdapMapper<Clazz>(Clazz.class);
        Attributes attrs = new BasicAttributes();
        Clazz clazz = mapper.unmarshal(attrs);
        assertNotNull(clazz);
        assertTrue(clazz instanceof Clazz);
    }

    @Test
    public void getObjectClasses() {
        LdapMapper<Clazz> mapper = new LdapMapper<Clazz>(Clazz.class);
        LinkedHashSet<String> objectClasses = (LinkedHashSet<String>) mapper.getObjectClasses();
        String[] check = {"inetOrgPerson", "organizationalPerson", "person", "foo", "bar", "top"};
        assertEquals(check.length, objectClasses.size());
        Iterator<String> iter = objectClasses.iterator();
        for (int i = 0; i < check.length; i++) {
            assertEquals(check[i], iter.next());
        }
    }

    @Test(expected = MissingAttributeException.class)
    public void requiredField() throws LdapMappingException {
        LdapMapper<Clazz> mapper = new LdapMapper<Clazz>(Clazz.class);
        Clazz instance = new Clazz();
        instance.setRequiredString("this string cannot be null");
        mapper.marshal(instance, false);
    }

    @Test(expected = MissingAttributeException.class)
    public void requiredMethod() throws LdapMappingException {
        LdapMapper<Clazz> mapper = new LdapMapper<Clazz>(Clazz.class);
        Clazz instance = new Clazz();
        instance.setTitle("must have title");
        mapper.marshal(instance, false);
    }

    /**
     * Tests unmarshalling and marshalling of a field and a method using a translator
     * 
     * @throws LdapMappingException
     * @throws NamingException
     */
    @Test
    public void mapWithTranslation() throws LdapMappingException, NamingException {

        LdapMapper<ObjectWithTranslationMapping> mapper = new LdapMapper<ObjectWithTranslationMapping>(ObjectWithTranslationMapping.class);
        ObjectWithTranslationMapping objectMappedToLdap = new ObjectWithTranslationMapping();
        objectMappedToLdap.stringInField = "mapthisfield";
        objectMappedToLdap.setStringFromMethod("mapthismethod");

        // Marshaling
        Attributes attrs = mapper.marshal(objectMappedToLdap, false);
        // - Field
        Attribute attrField = attrs.get("translateField");
        String mappedFieldValue = (String) attrField.get();

        assertEquals("mapthisfieldT", mappedFieldValue);
        // - Method
        Attribute attrMethod = attrs.get("translateMethod");
        String mappedMethodValue = (String) attrMethod.get();

        assertEquals("mapthismethodT", mappedMethodValue);

        // Unmarshaling
        ObjectWithTranslationMapping objectMappedFromLdap = mapper.unmarshal(attrs);
        // - Field
        mappedFieldValue = objectMappedFromLdap.stringInField;

        assertEquals("mapthisfieldTF", mappedFieldValue);
        // - Method
        mappedMethodValue = objectMappedFromLdap.getStringFromMethod();

        assertEquals("mapthismethodTF", mappedMethodValue);
    }

    @LdapObject(objectClasses = {"foo", "bar"})
    private static class SuperClazz {
        @LdapAttribute(id = "title", required = true)
        private String title;
        private String notAnnotated;
        @LdapAttribute(id = "object")
        private Object object;
        private int calculated;
        @LdapAttribute(id = "justInt")
        private int justAnInt;
        @LdapAttribute(id = "justBoolean")
        private boolean justABoolean;
        @LdapAttribute(id = "justByte")
        private byte justAByte;
        @LdapAttribute(id = "justChar")
        private char justAChar;
        @LdapAttribute(id = "justShort")
        private short justAShort;
        @LdapAttribute(id = "justLong")
        private long justALong;
        @LdapAttribute(id = "justFloat")
        private float justAFloat;
        @LdapAttribute(id = "justDouble")
        private double justADouble;
        @LdapAttribute(id = "userpass", oneWayEncrypted = true)
        private String password;
        private String requiredString;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(String notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        @LdapAttribute(id = "calculatedInt")
        public int getCalculated() {
            return calculated * 2;
        }

        @LdapAttribute(id = "calculatedInt")
        public void setCalculated(int calculated) {
            this.calculated = calculated + 3;
        }

        public int getJustAnInt() {
            return justAnInt;
        }

        public void setJustAnInt(int justAnInt) {
            this.justAnInt = justAnInt;
        }

        public boolean isJustABoolean() {
            return justABoolean;
        }

        public void setJustABoolean(boolean justABoolean) {
            this.justABoolean = justABoolean;
        }

        public byte getJustAByte() {
            return justAByte;
        }

        public void setJustAByte(byte justAByte) {
            this.justAByte = justAByte;
        }

        public char getJustAChar() {
            return justAChar;
        }

        public void setJustAChar(char justAChar) {
            this.justAChar = justAChar;
        }

        public short getJustAShort() {
            return justAShort;
        }

        public void setJustAShort(short justAShort) {
            this.justAShort = justAShort;
        }

        public long getJustALong() {
            return justALong;
        }

        public void setJustALong(long justALong) {
            this.justALong = justALong;
        }

        public float getJustAFloat() {
            return justAFloat;
        }

        public void setJustAFloat(float justAFloat) {
            this.justAFloat = justAFloat;
        }

        public double getJustADouble() {
            return justADouble;
        }

        public void setJustADouble(double justADouble) {
            this.justADouble = justADouble;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @LdapAttribute(id = "requiredString", required = true)
        public String getRequiredString() {
            return requiredString;
        }

        public void setRequiredString(String requiredString) {
            this.requiredString = requiredString;
        }

    }

    @LdapObject(objectClasses = {"inetOrgPerson", "organizationalPerson", "person"})
    private static class Clazz extends SuperClazz {
        @LdapAttribute(id = "integer")
        private Integer integer;
        private String notMapped;
        private boolean reversedBoolean;

        public Clazz() {

        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        public String getNotMapped() {
            return notMapped;
        }

        public void setNotMapped(String notMapped) {
            this.notMapped = notMapped;
        }

        @LdapAttribute(id = "reversed")
        public boolean isReversedBoolean() {
            return !reversedBoolean;
        }

        @LdapAttribute(id = "reversed")
        public void setReversedBoolean(boolean reversedBoolean) {
            this.reversedBoolean = reversedBoolean;
        }

    }

    // Implementation for testing mapping with translation
    @LdapObject(objectClasses = {"foo", "bar"})
    private static class ObjectWithTranslationMapping {
        @LdapAttribute(id = "translateField", valueTranslator = AppendXTranslator.class)
        public String stringInField;

        private String stringFromMethod;

        @LdapAttribute(id = "translateMethod", valueTranslator = AppendXTranslator.class)
        public String getStringFromMethod() {
            return stringFromMethod;
        }

        @LdapAttribute(id = "translateMethod", valueTranslator = AppendXTranslator.class)
        public void setStringFromMethod(String stringFromMethod) {
            this.stringFromMethod = stringFromMethod;
        }

        // the mapper needs this constructor
        public ObjectWithTranslationMapping() {
            super();
        }
    }

    // Appends an 'F' on fromLdap and 'T' on toLdap,
    // for checking that both mappings actually take place
    public static class AppendXTranslator implements LdapAttributeValueTranslator<Object> {
        public Object fromLdap(Object value) {
            if (value instanceof String) {
                return (String) value + "F";
            } else {
                return value;
            }
        }

        public Object toLdap(Object value) {
            if (value instanceof String) {
                return (String) value + "T";
            } else {
                return value;
            }
        }
    }

}
