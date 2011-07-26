package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * A panel that works with a generic model. The normal panel of Wicket does not
 * have a generic model object, but a default model object that needs casting.
 * Apparently the Wicket community voted against making Panel generic.
 * 
 * @author lobo
 */
public abstract class GPanel<T> extends Panel
{
	private static final long	serialVersionUID	= 1522105023988180614L;

	public GPanel(final String id)
	{
		super(id);
	}

	public GPanel(final String id, final IModel<T> model)
	{
		super(id, model);
	}

	@SuppressWarnings("unchecked")
	public final T getModelObject()
	{
		return (T) getDefaultModelObject();
	}

	public final void setModelObject(final T modelObject)
	{
		setDefaultModelObject(modelObject);
	}

	@SuppressWarnings("unchecked")
	public final IModel<T> getModel()
	{
		return (IModel<T>) getDefaultModel();
	}

	public final void setModel(final IModel<T> model)
	{
		setDefaultModel(model);
	}
}
