package nl.knaw.dans.easy.domain.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoNamespace;

public abstract class SimpleCollectionCreator
{
    private static final Map<DmoNamespace, SimpleCollectionCreator> registry 
        = Collections.synchronizedMap(new HashMap<DmoNamespace, SimpleCollectionCreator>());

    public static SimpleCollection createRoot(DmoNamespace namespace)
    {
        SimpleCollectionCreator creator;
        synchronized (registry)
        {
            creator = registry.get(namespace);
        }

        if (creator == null)
        {
            throw new IllegalArgumentException("No root-creator for namespace " + namespace);
        }
        return creator.createRoot();
    }

    public static void register(SimpleCollectionCreator creator)
    {
        synchronized (registry)
        {
            registry.put(creator.getNamespace(), creator);
        }
    }

    public static void register(List<SimpleCollectionCreator> creators)
    {
        for (SimpleCollectionCreator creator : creators)
        {
            register(creator);
        }
    }

    public static SimpleCollectionCreator unRegister(DmoNamespace namespace)
    {
        synchronized (registry)
        {
            return registry.remove(namespace);
        }
    }

    public static boolean isRegistered(DmoNamespace namespace)
    {
        synchronized (registry)
        {
            return registry.containsKey(namespace);
        }
    }

    protected abstract DmoNamespace getNamespace();

    protected abstract SimpleCollection createRoot();

    /**
     * Enables Spring injection into the abstract and static {@link SimpleCollectionCreator}.
     * <pre>
     *  <bean class="nl.knaw.dans.easy.domain.collections.SimpleCollectionCreator$Registrator">
     *      <property name="creators">
     *          <list>
     *              <bean class="nl.knaw.dans.easy.domain.collections.EasyCollectionCreator" />
     *              <!-- other creators -->
     *          </list>
     *      </property>
     *  </bean>
     * </pre>
     */
    public static class Registrator
    {

        public void setCreators(List<SimpleCollectionCreator> creators)
        {
            SimpleCollectionCreator.register(creators);
        }

    }

}
