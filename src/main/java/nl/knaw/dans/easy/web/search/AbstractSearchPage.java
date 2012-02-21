package nl.knaw.dans.easy.web.search;

import java.lang.reflect.Constructor;

import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.search.custom.ArchaeologyCriteriumListener;

import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSearchPage extends AbstractEasyNavPage
{
	private static final Logger	LOGGER 				= LoggerFactory.getLogger(AbstractSearchPage.class);
    
	public AbstractSearchPage()
	{
		super();
	}
	
	public AbstractSearchPage(SearchModel searchModel)
	{
		super(searchModel);
		enhanceModel(searchModel);
	}
	
	public AbstractSearchPage(PageParameters parameters)
	{
		super(parameters);
	}	
		
	public static AbstractSearchPage instantiate(Class<? extends AbstractSearchPage> searchPageClass, SearchModel searchModel)
	{
		try
		{
			Constructor<? extends AbstractSearchPage> constructor =  searchPageClass.getConstructor(SearchModel.class);
			return constructor.newInstance(searchModel);
		}
		catch(Exception e)
		{
			LOGGER.error("The constructor of AbstractSearchPage "+ (searchPageClass == null ? "null" : searchPageClass.toString()) +" disappeared, got hidden or threw an exception.", e);
			throw new InternalWebError(); 
		}		
	}
	
	public SearchModel getSearchModel()
	{
	    SearchModel searchModel = (SearchModel) getDefaultModel();
		return searchModel;
	}
	
	public void setSearchModel(SearchModel searchModel)
	{
	    enhanceModel(searchModel);
		this.setDefaultModel(searchModel);
	}

    protected void enhanceModel(SearchModel searchModel)
    {
        if (searchModel != null)
        {
            searchModel.getRequestBuilder().addCriteriumListener(ArchaeologyCriteriumListener.instance());
        }
    }
	
	public SearchData getSearchData()
	{
		SearchModel searchModel = getSearchModel();
		if (searchModel != null)
			return searchModel.getObject();
		else
			return null;
	}

}
