package nl.knaw.dans.common.search.bean;

import nl.knaw.dans.common.lang.search.bean.AbstractSearchBeanFactory;

public class DummySearchBeanFactory extends AbstractSearchBeanFactory
{

    private static final DummySearchBeanFactory instance = new DummySearchBeanFactory();

    @Override
    public Class<?>[] getSearchBeanClasses()
    {
        return new Class[] {DummySB.class};
    }

    public static DummySearchBeanFactory getInstance()
    {
        return instance;
    }

}
