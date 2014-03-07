package nl.knaw.dans.easy.data.search;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.bean.AbstractSearchBeanFactory;

public class EasySearchBeanFactory extends AbstractSearchBeanFactory
{
    private final static Class<?>[] searchBeanClasses = new Class[] {EasyDatasetSB.class, DatasetSB.class};

    public Class<?>[] getSearchBeanClasses()
    {
        return searchBeanClasses;
    }
}
