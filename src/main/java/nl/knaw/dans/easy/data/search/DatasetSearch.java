package nl.knaw.dans.easy.data.search;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;

public interface DatasetSearch
{
    public SearchResult<? extends DatasetSB> search(SearchRequest request) throws SearchEngineException, SearchBeanFactoryException;
}
