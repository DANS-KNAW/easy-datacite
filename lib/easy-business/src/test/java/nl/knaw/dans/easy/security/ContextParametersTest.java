package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.knaw.dans.common.lang.repo.AbstractStorableObject;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.ContextParameters;

import org.junit.Test;

public class ContextParametersTest {

    @Test
    public void testContructor() {
        Object[] args1 = {"bla", new EasyUserImpl("kees"), new DatasetImpl("123"), "encore bla"};

        ContextParameters ctxParameters = new ContextParameters(args1);
        assertEquals("kees", ctxParameters.getSessionUser().getId());
        assertEquals("123", ctxParameters.getDataset().getStoreId());
        assertEquals("bla", ctxParameters.getObject(String.class, 0));
        assertEquals("encore bla", ctxParameters.getObject(String.class, 1));
        assertNull(ctxParameters.getObject(String.class, 2));
        assertNull(ctxParameters.getObject(Object.class, 0));
        assertNull(ctxParameters.getUserUnderEdit());

        Object[] args2 = {new DatasetImpl("1"), "bla", new EasyUserImpl("kees"), new DatasetImpl("123"), "encore bla", new EasyUserImpl("jan")};
        ctxParameters = new ContextParameters(args2);
        assertEquals("kees", ctxParameters.getSessionUser().getId());
        assertEquals("1", ctxParameters.getDataset().getStoreId());
        assertEquals("bla", ctxParameters.getObject(String.class, 0));
        assertEquals("encore bla", ctxParameters.getObject(String.class, 1));
        assertNull(ctxParameters.getObject(String.class, 2));
        assertNull(ctxParameters.getObject(Object.class, 0));

        assertEquals("jan", ctxParameters.getUserUnderEdit().getId());
        assertEquals("123", ((AbstractStorableObject) ctxParameters.getObject(DatasetImpl.class, 0)).getStoreId());

        ctxParameters = new ContextParameters(new DatasetImpl("1"), args1);
        assertEquals("1", ctxParameters.getDataset().getStoreId());
        assertEquals(args1, ctxParameters.getObject(Object[].class, 0));
        // --> Objects must be in the same array.
    }

}
