package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DmoStoreIdTest
{

    @Test
    public void testEqualsAndHash()
    {
        DmoStoreId dmoStoreId1 = new DmoStoreId(new DmoNamespace("foo"), "1");
        DmoStoreId dmoStoreId2 = new DmoStoreId(new DmoNamespace("foo"), "1");

        assertEquals(dmoStoreId1, dmoStoreId2);
        assertTrue(dmoStoreId1.equals(dmoStoreId2));
        assertTrue(dmoStoreId2.equals(dmoStoreId1));
        assertTrue(dmoStoreId1.equals(dmoStoreId1));
        assertTrue(dmoStoreId2.equals(dmoStoreId2));

        DmoStoreId dmoStoreId3 = new DmoStoreId(new DmoNamespace("foo"), "2");

        assertFalse(dmoStoreId1.equals(dmoStoreId3));
        assertFalse(dmoStoreId2.equals(dmoStoreId3));

        assertTrue(dmoStoreId1.hashCode() == dmoStoreId2.hashCode());
        assertFalse(dmoStoreId1.hashCode() == dmoStoreId3.hashCode());
    }

    @Test
    public void isValidId()
    {
        assertTrue(DmoStoreId.isValidId("a"));
        assertTrue(DmoStoreId.isValidId("42"));
        assertTrue(DmoStoreId.isValidId("a83"));
        assertTrue(DmoStoreId.isValidId("a-5"));
        assertTrue(DmoStoreId.isValidId("6-b"));
        assertTrue(DmoStoreId.isValidId("a-42-a83-a-5-6-b"));
    }

    @Test
    public void notValid()
    {
        assertFalse(DmoStoreId.isValidId(null));
        assertFalse(DmoStoreId.isValidId(""));
        assertFalse(DmoStoreId.isValidId(" "));
        assertFalse(DmoStoreId.isValidId("  "));
        assertFalse(DmoStoreId.isValidId(" abc"));
        assertFalse(DmoStoreId.isValidId("12:er"));
        assertFalse(DmoStoreId.isValidId("\""));
        assertFalse(DmoStoreId.isValidId("{"));
        assertFalse(DmoStoreId.isValidId("+"));
        assertFalse(DmoStoreId.isValidId("12=3"));
    }

    @Test
    public void split()
    {
        String[] split = DmoStoreId.split("easy-dataset:123");
        assertEquals("easy-dataset", split[0]);
        assertEquals("123", split[1]);

        split = DmoStoreId.split("easy-model:oai-set1");
        assertEquals("easy-model", split[0]);
        assertEquals("oai-set1", split[1]);
    }

    @Test
    public void notSplit()
    {
        assertTrue(isValidSplit("easy-model:oai-set1"));

        assertFalse(isValidSplit("easy-model:"));
        assertFalse(isValidSplit(":oai-set1"));
        assertFalse(isValidSplit(null));
        assertFalse(isValidSplit(""));
        assertFalse(isValidSplit(" "));
        assertFalse(isValidSplit(":"));
        assertFalse(isValidSplit("bla:@foo"));
    }

    private boolean isValidSplit(String value)
    {
        try
        {
            DmoStoreId.split(value);
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
        return true;
    }

    @Test
    public void constructorDmoNamespaceString()
    {
        DmoNamespace namespace = new DmoNamespace("easy-foo");

        assertTrue(validConstructorArgs(namespace, "123"));
        assertTrue(validConstructorArgs(namespace, "abc"));
        assertTrue(validConstructorArgs(namespace, "-"));
        assertTrue(validConstructorArgs(namespace, "abc-def-67"));

        assertFalse(validConstructorArgs(namespace, "=-"));
    }

    private boolean validConstructorArgs(DmoNamespace namespace, String id)
    {
        try
        {
            new DmoStoreId(namespace, id);
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
        return true;
    }

    @Test
    public void constructorString()
    {
        assertTrue(validConstructorArgs("easy-dataset:445"));
        assertTrue(validConstructorArgs("easy-dataset:ff-2-h"));

        assertFalse(validConstructorArgs(null));
        assertFalse(validConstructorArgs(""));
        assertFalse(validConstructorArgs(" "));
        assertFalse(validConstructorArgs("a:\\"));
        assertFalse(validConstructorArgs("*:foo"));
    }

    private boolean validConstructorArgs(String storeId)
    {
        try
        {
            new DmoStoreId(storeId);
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
        return true;
    }

}
