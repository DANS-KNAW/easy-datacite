package nl.knaw.dans.easy.servicelayer.services;

import java.util.Locale;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public interface SearchService {
    SearchResult<? extends DatasetSB> searchPublished(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchAll(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchMyDataset(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchMyRequests(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchAllWork(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchOurWork(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchMyWork(SearchRequest request, EasyUser user) throws ServiceException;

    SearchResult<? extends DatasetSB> searchTrashcan(SearchRequest request, EasyUser user) throws ServiceException;

    int getNumberOfDatasets(EasyUser user) throws ServiceException;

    int getNumberOfRequests(EasyUser user) throws ServiceException;

    int getNumberOfItemsInAllWork(EasyUser user) throws ServiceException;

    int getNumberOfItemsInOurWork(EasyUser user) throws ServiceException;

    int getNumberOfItemsInMyWork(EasyUser user) throws ServiceException;

    int getNumberOfItemsInTrashcan(EasyUser user) throws ServiceException;

    RecursiveList getRecursiveList(String listId, Locale locale) throws ServiceException;
}
