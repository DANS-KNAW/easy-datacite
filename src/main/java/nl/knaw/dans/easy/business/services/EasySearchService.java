package nl.knaw.dans.easy.business.services;

import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchQuery;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.domain.model.PermissionRequestSearchInfo;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.search.RecursiveListCache;
import nl.knaw.dans.easy.servicelayer.services.SearchService;

public class EasySearchService extends AbstractEasyService implements SearchService 
{
	public SearchResult<? extends DatasetSB> searchAllWork(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		addFilterByStates(request, 
				new DatasetState[] { DatasetState.SUBMITTED, DatasetState.MAINTENANCE }
		);

		return doSearch(request);
	}

	public SearchResult<? extends DatasetSB> searchMyDataset(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		addFilterByDepositorId(request, user.getId());
		addFilterByStates(request, 
				new DatasetState[] { 
					DatasetState.DRAFT, 
					DatasetState.SUBMITTED,
					DatasetState.PUBLISHED,
					DatasetState.MAINTENANCE
				}
		);

		return doSearch(request);
	}
	
    public SearchResult<? extends DatasetSB> searchMyRequests(SearchRequest request, EasyUser requester) throws ServiceException
    {
        final String requesterId = requester.getId();
        addFilterByRequesterId(request, requesterId);
        addFilterByStates(request, new DatasetState[] {DatasetState.PUBLISHED, DatasetState.MAINTENANCE});

        final SearchResult<? extends DatasetSB> searchResult = doSearch(request);
        try
        {
            // dirty solution for ticket 544: user "abc" sees also requests of user "abc123"
            final List<?> hits = searchResult.getHits();
            for (final Object hit : hits)
            {
                boolean found = false;
                final EasyDatasetSB dataset = ((SimpleSearchHit<EasyDatasetSB>) hit).getData();
                for (final PermissionRequestSearchInfo permissionRequest : dataset.getPermissionStatusList())
                    found = false || requesterId.equals(permissionRequest.getRequesterId());
                if (!found)
                    hits.remove(hit);
            }
        }
        catch (final ClassCastException e)
        {
        }
        return searchResult;
    }

    public SearchResult<? extends DatasetSB> searchMyWork(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		addFilterByStates(request, 
				new DatasetState[] { 
					DatasetState.SUBMITTED, 
					DatasetState.MAINTENANCE 
				}
		);
		addFilterByAssigneeId(request, user.getId());

		return doSearch(request);
	}

	public SearchResult<? extends DatasetSB> searchOurWork(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		addFilterByStates(request, 
				new DatasetState[] { 
					DatasetState.SUBMITTED, 
					DatasetState.MAINTENANCE 
				}
		);
		addFilterByAssigneeId(request, WorkflowData.NOT_ASSIGNED);

		return doSearch(request);
	}

	public SearchResult<? extends DatasetSB> searchPublished(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		addFilterByStates(request, 
				new DatasetState[] { 
					DatasetState.PUBLISHED,
					DatasetState.MAINTENANCE
				}
		);

		return doSearch(request);
	}

	public SearchResult<? extends DatasetSB> searchTrashcan(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		addFilterByState(request, DatasetState.DELETED);

		return doSearch(request);
	}
	
	public SearchResult<? extends DatasetSB> searchAll(
			SearchRequest request, EasyUser user) throws ServiceException
	{
		return doSearch(request); 
	}
	
    public int getNumberOfRequests(EasyUser user) throws ServiceException
    {
        if (user.isAnonymous()) return 0;
        SearchRequest   request = new SimpleSearchRequest();
        // we only need the total hits, no data!
        request.setOffset(0);
        request.setLimit(0);
        
        SearchResult<? extends DatasetSB> searchResult = searchMyRequests(request, user);
        return searchResult.getTotalHits();
    }

    public int getNumberOfDatasets(EasyUser user) throws ServiceException
    {
        if (user.isAnonymous()) return 0;
        SearchRequest   request = new SimpleSearchRequest();
        // we only need the total hits, no data!
        request.setOffset(0);
        request.setLimit(0);
        
        SearchResult<? extends DatasetSB> searchResult = searchMyDataset(request, user);
        return searchResult.getTotalHits();
    }

    public int getNumberOfItemsInAllWork(EasyUser user) throws ServiceException
    {
        if (user.isAnonymous()) return 0;
        SearchRequest   request = new SimpleSearchRequest();
        // we only need the total hits, no data!
        request.setOffset(0);
        request.setLimit(0);
        
        SearchResult<? extends DatasetSB> searchResult = searchAllWork(request, user);
        return searchResult.getTotalHits();
    }

    public int getNumberOfItemsInOurWork(EasyUser user) throws ServiceException
    {
        if (user.isAnonymous()) return 0;
        SearchRequest   request = new SimpleSearchRequest();
        // we only need the total hits, no data!
        request.setOffset(0);
        request.setLimit(0);
        
        SearchResult<? extends DatasetSB> searchResult = searchOurWork(request, user);
        return searchResult.getTotalHits();
    }

    public int getNumberOfItemsInMyWork(EasyUser user) throws ServiceException
    {
        if (user.isAnonymous()) return 0;
        SearchRequest   request = new SimpleSearchRequest();
        // we only need the total hits, no data!
        request.setOffset(0);
        request.setLimit(0);
        
        SearchResult<? extends DatasetSB> searchResult = searchMyWork(request, user);
        return searchResult.getTotalHits();
    }

    public int getNumberOfItemsInTrashcan(EasyUser user) throws ServiceException
    {
        if (user.isAnonymous()) return 0;
        SearchRequest   request = new SimpleSearchRequest();
        // we only need the total hits, no data!
        request.setOffset(0);
        request.setLimit(0);
        
        SearchResult<? extends DatasetSB> searchResult = searchTrashcan(request, user);
        return searchResult.getTotalHits();
    }

    private void addFilterByState(SearchRequest request, DatasetState state) throws ServiceException
	{
		addFilterByStates(request, new DatasetState[] { state });
	}

	@SuppressWarnings("unchecked")
	private void addFilterByStates(SearchRequest request, DatasetState... states) throws ServiceException
	{
		// check if an existing filter is not already a subset of the 
		// allowed states. This is a simple solution to the problem
		// that an incoming request might be a subset of the request
		// that might be filtered upon. This subset would be overwritten
		// and a bigger set would be returned if it were not for this
		// small piece of simplistic code.  
		Field<?> existingFilter = request.getFilterQueries().
			getByFieldName(EasyDatasetSB.DS_STATE_FIELD);
		if (existingFilter != null)
		{
			Object filterVal = existingFilter.getValue();
			for (DatasetState state : states)
			{
				if (filterVal instanceof DatasetState)
				{
					if (filterVal.equals(state))
						return;
				}
				else if (filterVal instanceof String)
				{
					if (filterVal.equals(state.toString()))
						return;
				}
			}
		}
		

		String q = SimpleSearchQuery.OrValues((Object[]) states);
		request.addFilterQuery( new SimpleField(EasyDatasetSB.DS_STATE_FIELD, q) );
	}
	
	private void addFilterByDepositorId(SearchRequest request, String depositorId) throws ServiceException
	{
		request.addFilterQuery( 
				new SimpleField<String>(EasyDatasetSB.DEPOSITOR_ID_FIELD, depositorId) 
			);
	}

	private void addFilterByAssigneeId(SearchRequest request, String assigneeId) throws ServiceException
	{
		request.addFilterQuery( 
				new SimpleField<String>(EasyDatasetSB.ASSIGNEE_ID_FIELD, assigneeId) 
			);
	}
	
	private void addFilterByRequesterId(SearchRequest request, String requesterId)
    {
        request.addFilterQuery( 
        		new SimpleField<String>(EasyDatasetSB.PERMISSION_STATUS_FIELD, requesterId) 
        	);
    }

	protected SearchResult<? extends DatasetSB> doSearch(SearchRequest request)
		throws ServiceException
	{ 
		try
		{
			return Data.getDatasetSearch().search(request);
		} catch (SearchEngineException e)
		{
			throw new ServiceException(e);
		} catch (SearchBeanFactoryException e)
		{
			throw new ServiceException(e);
		}
	}
	
    @Override
    public RecursiveList getRecursiveList(String listId, Locale locale) throws ServiceException
    {
        RecursiveList recursiveList;
        try
        {
            recursiveList = RecursiveListCache.getInstance().getList(listId, locale);
        }
        catch (CacheException e)
        {
            throw new ServiceException(e);
        }
        return recursiveList;
    }
	
}
