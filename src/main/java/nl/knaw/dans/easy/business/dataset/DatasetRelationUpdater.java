package nl.knaw.dans.easy.business.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.ECollectionEntry;

/**
 * Updates dataset relations that target objects with one of the namespaces of
 * {@link ECollection#values()}.
 * <p/>
 * The working can be illustrated with set mathematics.
 * <pre>
 * Let original    o = {1, 2, 3, 4, 5}<br/>
 * Let update      u = {3, 4, 6, 7}<br/>
 * Then<br/>
 * To be removed   a = o - u = {1, 2, 5} and<br/>
 * to be added     b = u - 0 = {6, 7}.
 * </pre>
 * 
 * @author henkb
 */
public class DatasetRelationUpdater
{

    public void update(Dataset dataset, Map<ECollection, List<ECollectionEntry>> collectionEntryMap)
    {
        List<ECollectionEntry> collectionEntries = new ArrayList<ECollectionEntry>();
        for (Entry<ECollection, List<ECollectionEntry>> mapEntry : collectionEntryMap.entrySet())
        {
            collectionEntries.addAll(mapEntry.getValue());
        }
        update(dataset, collectionEntries);
    }

    public void update(Dataset dataset, List<ECollectionEntry> collectionEntries)
    {
        List<DmoStoreId> colMemberships = new ArrayList<DmoStoreId>();
        List<DmoStoreId> oaiMemberships = new ArrayList<DmoStoreId>();
        for (ECollectionEntry entry : collectionEntries)
        {
            colMemberships.add(entry.getCollectionId());
            if (entry.isPublishedAsOAISet())
            {
                oaiMemberships.add(entry.getCollectionId());
            }
        }
        DmoNamespace[] namespaces = ECollection.allNamespaces();
        Set<DmoStoreId> originalColMemberships = dataset.getRelations().getCollectionMemberships(namespaces);
        Set<DmoStoreId> originalOaiMemberships = dataset.getRelations().getCollectionMemberships(namespaces);
        updateCollectionMemberships(dataset, originalColMemberships, colMemberships);
        updateOAISetMemberships(dataset, originalOaiMemberships, oaiMemberships);
    }

    protected void updateCollectionMemberships(Dataset dataset, Set<DmoStoreId> originalColMemberships, List<DmoStoreId> colMemberships)
    {
        List<DmoStoreId> a = new ArrayList<DmoStoreId>(originalColMemberships);
        List<DmoStoreId> b = new ArrayList<DmoStoreId>(colMemberships);

        a.removeAll(colMemberships);
        b.removeAll(originalColMemberships);

        dataset.getRelations().removeCollectionMembership(a);
        dataset.getRelations().addCollectionMembership(b);
    }

    protected void updateOAISetMemberships(Dataset dataset, Set<DmoStoreId> originalOaiMemberships, List<DmoStoreId> oaiMemberships)
    {
        List<DmoStoreId> a = new ArrayList<DmoStoreId>(originalOaiMemberships);
        List<DmoStoreId> b = new ArrayList<DmoStoreId>(oaiMemberships);

        a.removeAll(oaiMemberships);
        b.removeAll(originalOaiMemberships);

        dataset.getRelations().removeOAISetMembership(a);
        dataset.getRelations().addOAISetMembership(b);
    }

}
