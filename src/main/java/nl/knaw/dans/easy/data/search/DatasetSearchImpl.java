package nl.knaw.dans.easy.data.search;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetsIndex;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;

public class DatasetSearchImpl implements DatasetSearch
{
    private SearchEngine searchEngine;

    public DatasetSearchImpl(SearchEngine searchEngine)
    {
        this.searchEngine = searchEngine;
        this.searchEngine.setSearchBeanFactory(new EasySearchBeanFactory());
    }

    @SuppressWarnings("unchecked")
    public SearchResult<? extends DatasetSB> search(SearchRequest request) throws SearchEngineException, SearchBeanFactoryException
    {
        request.setIndex(new DatasetsIndex());
        request.addFilterBean(DatasetSB.class);
        return (SearchResult<? extends DatasetSB>) searchEngine.searchBeans(request);
    }

}
