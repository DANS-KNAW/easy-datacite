package nl.knaw.dans.common.wicket.components.search.criteria;

import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;

import org.apache.wicket.model.IModel;

/**
 * @author lobo
 */
public abstract class AbstractSearchCriterium implements SearchCriterium
{
	private static final long	serialVersionUID	= 3926821137796912508L;
	private IModel<String>	labelModel;

	public AbstractSearchCriterium(IModel<String> labelModel)
	{
		this.labelModel = labelModel;
	}
	
	@Override
	public IModel<String> getLabelModel()
	{
		return labelModel;
	}
}
