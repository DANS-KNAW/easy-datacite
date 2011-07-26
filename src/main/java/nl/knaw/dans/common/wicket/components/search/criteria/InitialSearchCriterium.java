package nl.knaw.dans.common.wicket.components.search.criteria;

import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;

import org.apache.wicket.model.IModel;

/**
 * An empty search criterium which may be used simply for the label returned
 * by getLabelModel().
 * 
 * @see CriteriumLabel
 * 
 * @author lobo
 */
public class InitialSearchCriterium extends AbstractSearchCriterium
{
	private static final long	serialVersionUID	= 8071037422279464457L;

	public InitialSearchCriterium(IModel<String> labelModel)
	{
		super(labelModel);
	}

	@Override
	public void apply(SimpleSearchRequest sr)
	{
		// do nothing
	}

}
