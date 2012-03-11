package nl.knaw.dans.easy.business.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.dataset.DatasetRelationUpdater;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.domain.model.ECollectionEntry;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.CollectionService;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.security.annotations.SecuredOperation;

public class EasyCollectionService extends AbstractEasyService implements CollectionService
{
    
    private DatasetRelationUpdater datasetRelationUpdater;


    public EasyCollectionService()
    {

    }

    @Override
    public DmoCollection getRoot(ECollection eColl) throws ServiceException
    {
        DmoCollection collection;
        try
        {
            collection = Data.getCollectionAccess().getRoot(eColl);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return collection;
    }

    @Override
    public DmoCollection getCollection(DmoStoreId dmoStoreId) throws ServiceException
    {
        DmoCollection collection;
        try
        {
            collection = Data.getCollectionAccess().getCollection(dmoStoreId);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return collection;
    }

    @Override
    public void saveCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getCollectionAccess().saveCollection(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws ServiceException
    {
        DmoCollection collection;
        try
        {
            collection = Data.getCollectionAccess().createCollection(sessionUser, parent, label, shortName);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return collection;
    }

    @Override
    public void attachCollection(EasyUser sessionUser, DmoCollection parent, DmoCollection child) throws ServiceException
    {
        try
        {
            Data.getCollectionAccess().attachCollection(sessionUser, parent, child);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void detachCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getCollectionAccess().detachCollection(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getCollectionAccess().publishAsOAISet(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getCollectionAccess().unpublishAsOAISet(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public Map<ECollection, List<ECollectionEntry>> getCollectionEntries() throws ServiceException
    {
        Map<ECollection, List<ECollectionEntry>> entryMap = new LinkedHashMap<ECollection, List<ECollectionEntry>>();
        for (ECollection eColl : ECollection.values())
        {
            entryMap.put(eColl, getCollectionEntries(eColl));
        }
        return entryMap;
    }

    @Override
    public List<ECollectionEntry> getCollectionEntries(ECollection eColl) throws ServiceException
    {
        List<ECollectionEntry> entries = new ArrayList<ECollectionEntry>();
        DmoCollection root = getRoot(eColl);
        for (DmoCollection kid : root.getChildren())
        {
            collectEntries(entries, kid, 0);
        }
        return entries;
    }

    private void collectEntries(List<ECollectionEntry> entries, DmoCollection collection, int level)
    {
        level++;
        ECollectionEntry entry = new ECollectionEntry(collection, level);
        entries.add(entry);
        for (DmoCollection kid : collection.getChildren())
        {
            collectEntries(entries, kid, level);
        }
    }
    
    @Override
    public Map<ECollection, List<ECollectionEntry>> getCollectionEntries(Dataset dataset) throws ServiceException
    {
        Map<ECollection, List<ECollectionEntry>> entryMap = new LinkedHashMap<ECollection, List<ECollectionEntry>>();
        DatasetRelations relations = dataset.getRelations();
        for (ECollection eColl : ECollection.values())
        {
            Set<DmoStoreId> ids = relations.getCollectionMemberships(eColl.namespace);
            List<ECollectionEntry> entries = new ArrayList<ECollectionEntry>();
            for (DmoStoreId id : ids)
            {
                entries.add(new ECollectionEntry(getCollection(id), 0));
            }
            entryMap.put(eColl, entries);
        }
        return entryMap;
    }
    
    @SecuredOperation(id = "nl.knaw.dans.easy.servicelayer.services.CollectionService.updateCollectionMemberships")
    @Override
    public void updateCollectionMemberships(EasyUser sessionUser, Dataset dataset, Map<ECollection, List<ECollectionEntry>> entryMap) throws ServiceException
    {
        getDatasetRelationUpdater().update(dataset, entryMap);
        try
        {
            String id = sessionUser == null || sessionUser.isAnonymous() ? "anonymous" : sessionUser.getId();
            Data.getEasyStore().update(dataset, "relations updated by " + id);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
    }
    
    private DatasetRelationUpdater getDatasetRelationUpdater()
    {
        if (datasetRelationUpdater == null)
        {
            datasetRelationUpdater = new DatasetRelationUpdater();
        }
        return datasetRelationUpdater;
    }
}
