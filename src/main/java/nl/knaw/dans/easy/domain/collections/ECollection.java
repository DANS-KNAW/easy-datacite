package nl.knaw.dans.easy.domain.collections;

import java.net.URL;
import java.util.Iterator;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;

public enum ECollection
{
    EasyCollection(new DmoNamespace("easy-collection")),
    
    EasyInterestArea(new DmoNamespace("easy-interest-area")),
    
    /* EasyDiscipline(new DmoNamespace("easy-discipline")), */
    
    EasyResearchArea(new DmoNamespace("easy-research-area"));

    private static final String TEMPLATE_LOCATION = "xml-files/";
    private static final String TEMPLATE_EXTENSION = ".xml";
    
    public final DmoNamespace namespace;
    
    ECollection(DmoNamespace namespace)
    {
        this.namespace = namespace;
    }
    
    public URL getTemplateURL()
    {
        return this.getClass().getResource(TEMPLATE_LOCATION + namespace.getValue() + TEMPLATE_EXTENSION);
    }
    
    public static boolean isECollection(DmoNamespace namespace)
    {
        boolean isECollection = false;
        Iterator<ECollection> iter = iterator();
        while (iter.hasNext() && !isECollection)
        {
            isECollection = iter.next().namespace.equals(namespace);
        }
        return isECollection;
    }
    
    public static boolean isECollection(DmoStoreId dmoStoreId)
    {
        return isECollection(dmoStoreId.getNamespace());
    }
    
    public static boolean isECollection(DataModelObject dmo)
    {
        return isECollection(dmo.getDmoNamespace());
    }
    
    public static DmoNamespace[] allNamespaces()
    {
        ECollection[] allColls = values();
        DmoNamespace[] namespaces = new DmoNamespace[allColls.length];
        for (int i = 0; i < allColls.length; i++)
        {
            namespaces[i] = allColls[i].namespace;
        }
        return namespaces;
    }
    
    public static Iterator<ECollection> iterator()
    {
        return new ECollectionIterartor();
    }
    
    
    private static class ECollectionIterartor implements Iterator<ECollection>
    {
        
        private ECollection[] array;
        private int index;
        
        ECollectionIterartor()
        {
            array = ECollection.values();
        }

        @Override
        public boolean hasNext()
        {
            return index < array.length;
        }

        @Override
        public ECollection next()
        {
            return array[index++];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("ECollections cannot be removed.");
        }
        
    }
    

}
