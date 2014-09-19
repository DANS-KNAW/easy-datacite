package nl.knaw.dans.common.lang.repo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;

import org.dom4j.Element;

/**
 * An abstract factory for creating DataModelObjects. Concrete factories can be registered at this class. Which concrete factory is assigned to create the
 * specific DataModelObject depends on the namespace of the DataModelObject. Since the namespace is part of the storeId of a DataModelObject, the
 * AbstractDmoFactory can create DataModelObjects of the correct type, if it is given either the namespace or the storeId of the concrete DataModelObject to
 * instantiate.
 * 
 * @param <T>
 *        The type of DataModelObject
 */
public abstract class AbstractDmoFactory<T extends DataModelObject> implements DmoFactory<T> {

    private static final String SID_SPLIT = ":";

    private static final Map<DmoNamespace, DmoFactory<? extends DataModelObject>> registry = Collections
            .synchronizedMap(new HashMap<DmoNamespace, DmoFactory<? extends DataModelObject>>());

    public static SidDispenser sidDispenser;

    public static void register(DmoFactory<?> factory) {
        register(factory.getNamespace(), factory);
    }

    public static void register(DmoNamespace namespace, DmoFactory<?> factory) {
        synchronized (registry) {
            registry.put(namespace, factory);
        }
    }

    public static DmoFactory<?> unRegister(DmoNamespace namespace) {
        synchronized (registry) {
            return registry.remove(namespace);
        }
    }

    public static boolean isRegistered(DmoNamespace namespace) {
        synchronized (registry) {
            return registry.containsKey(namespace);
        }
    }

    public static DmoFactory<?> factoryFor(String storeId) {
        DmoNamespace namespace = computeNamespace(storeId);
        return factoryFor(namespace);
    }

    public static DmoFactory<?> factoryFor(DmoNamespace namespace) {
        DmoFactory<?> factory;
        synchronized (registry) {
            factory = registry.get(namespace);
        }
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for namespace " + namespace);
        }
        return factory;
    }

    private static DmoNamespace computeNamespace(String storeId) {
        if (storeId == null) {
            return null;
        }

        String[] plit = storeId.split(SID_SPLIT);
        return new DmoNamespace(plit[0]);
    }

    protected static String nextSid(DmoNamespace namespace) throws RepositoryException {
        if (sidDispenser != null) {
            return sidDispenser.nextSid(namespace);
        } else {
            return null;
        }
    }

    protected static void setSidDispenser(SidDispenser sidDispenser) {
        AbstractDmoFactory.sidDispenser = sidDispenser;
    }

    /**
     * Create an new instance of the class associated with storeIdOrNamespace, with a newly obtained storeId. The storeId of the returned object may be
     * <code>null</code> if no SidDispenser is associated with the AbstractDmoFactory.
     * <p/>
     * <b>Use this method for dmo's not yet ingested.</b>
     * 
     * @param namespace
     *        namespace
     * @return new instance of class associated with storeIdOrNamespace
     * @throws RepositoryException
     *         for exceptions while obtaining a new storeId
     */
    public static DataModelObject newDmo(DmoNamespace namespace) throws RepositoryException {
        return factoryFor(namespace).newDmo();
    }

    /**
     * Create a new instance of the class associated with the given storeId.
     * <p/>
     * <b>Use this method for retrieved dmo's.</b>
     * 
     * @param storeId
     *        storeId for returned instance
     * @return new instance of the class associated with the given storeId
     */
    public static DataModelObject dmoInstance(String storeId) {
        return factoryFor(storeId).createDmo(storeId);
    }

    protected String nextSid() throws RepositoryException {
        return nextSid(getNamespace());
    }

    /**
     * Does nothing. Subclasses may override.
     * 
     * @throws ObjectDeserializationException
     */
    @Override
    public void setMetadataUnit(DataModelObject dmo, String unitId, Element element) throws ObjectDeserializationException {

    }

    /**
     * Enables Spring injection into the abstract and static {@link AbstractDmoFactory}
     * 
     * <pre>
     *  <bean class="nl.knaw.dans.common.lang.repo.AbstractDmoFactory$Registrator">
     *      <property name="factories">
     *          <list>
     *              <bean class="nl.knaw.dans.snad.MyDmoFactory" />
     *              <!-- other factories -->
     *          </list>
     *      </property>
     *  </bean>
     * </pre>
     */
    public static class Registrator {

        public void setFactories(List<DmoFactory<?>> listOfFactories) {
            for (DmoFactory<?> factory : listOfFactories) {
                AbstractDmoFactory.register(factory);
            }
        }
    }

}
