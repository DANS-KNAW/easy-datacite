package nl.knaw.dans.easy.data.collections;

import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

public interface DmoCollectionsAccess
{

    DmoCollection createRoot(EasyUser sessionUser, ECollection eColl) throws CollectionsException;

    DmoCollection getRoot(ECollection eColl) throws CollectionsException;

    DmoCollection getCollection(DmoStoreId dmoStoreId) throws CollectionsException;

    void saveCollection(EasyUser sessionUser, DmoCollection collection) throws CollectionsException;

    DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws CollectionsException;

    void attachCollection(EasyUser sessionUser, DmoCollection parent, DmoCollection child) throws CollectionsException;

    void detachCollection(EasyUser sessionUser, DmoCollection collection) throws CollectionsException;

    void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws CollectionsException;

    void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws CollectionsException;

    Set<DmoStoreId> filterOAIEndNodes(Set<DmoStoreId> memberIds) throws CollectionsException;

    RecursiveList getRecursiveList(ECollection eColl) throws CollectionsException;

    RecursiveList getRecursiveList(DmoNamespace namespace) throws CollectionsException;

}
