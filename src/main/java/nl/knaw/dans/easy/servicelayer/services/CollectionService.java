package nl.knaw.dans.easy.servicelayer.services;

import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.ECollectionEntry;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.security.annotations.SecuredOperation;

public interface CollectionService extends EasyService
{

    DmoCollection getRoot(ECollection eColl) throws ServiceException;

    DmoCollection getCollection(DmoStoreId dmoStoreId) throws ServiceException;

    DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws ServiceException;

    void saveCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException;

    void attachCollection(EasyUser sessionUser, DmoCollection parent, DmoCollection child) throws ServiceException;

    void detachCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException;

    void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException;

    void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException;

    List<ECollectionEntry> getCollectionEntries(ECollection eColl) throws ServiceException;

    Map<ECollection, List<ECollectionEntry>> getCollectionEntries() throws ServiceException;

    Map<ECollection, List<ECollectionEntry>> getCollectionEntries(Dataset dataset) throws ServiceException;

    /**
     * Update collection memberships of the given dataset.
     * <p/>
     * {@literal @}SecuredOperation(id =
     * "nl.knaw.dans.easy.servicelayer.services.CollectionService.updateCollectionMemberships")
     * 
     * @param sessionUser
     * @param dataset
     * @param entryMap
     * @throws ServiceException
     */
    @SecuredOperation
    void updateCollectionMemberships(EasyUser sessionUser, Dataset dataset, Map<ECollection, List<ECollectionEntry>> entryMap) throws ServiceException;

    RecursiveList getRecursiveList(ECollection eColl) throws ServiceException;

    RecursiveList getRecursiveList(DmoNamespace namespace) throws ServiceException;

}
