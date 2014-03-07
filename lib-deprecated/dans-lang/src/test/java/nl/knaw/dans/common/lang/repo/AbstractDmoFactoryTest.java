package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AbstractDmoFactoryTest
{

    private static DmoNamespace foo = new DmoNamespace("foo");

    @Test(expected = IllegalArgumentException.class)
    public void factoryForUnregistered() throws Exception
    {
        AbstractDmoFactory.unRegister(foo);
        assertFalse(AbstractDmoFactory.isRegistered(foo));
        AbstractDmoFactory.factoryFor("foo:bar");
    }

    @Test
    public void factoryFor() throws Exception
    {
        AbstractDmoFactory<?> factory = createFactory();

        AbstractDmoFactory.register(foo, factory);
        assertTrue(AbstractDmoFactory.isRegistered(foo));
        assertEquals(factory, AbstractDmoFactory.factoryFor(foo));
        assertEquals(factory, AbstractDmoFactory.factoryFor("foo"));
        assertEquals(factory, AbstractDmoFactory.factoryFor("foo:bar"));

        assertEquals(factory, AbstractDmoFactory.unRegister(foo));
    }

    private AbstractDmoFactory<?> createFactory()
    {
        AbstractDmoFactory<?> factory = new AbstractDmoFactory<DataModelObject>()
        {
            @Override
            public DataModelObject newDmo()
            {
                return null;
            }

            @Override
            public DmoNamespace getNamespace()
            {
                return null;
            }

            @Override
            public DataModelObject createDmo(String storeId)
            {
                return null;
            }
        };
        return factory;
    }

}
