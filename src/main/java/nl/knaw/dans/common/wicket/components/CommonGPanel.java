package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.model.IModel;

public class CommonGPanel<T> extends CommonBasePanel<T>
{
	private static final long	serialVersionUID	= 339202458630052699L;

	public CommonGPanel(String id)
	{
		super(id);
	}
    
    public CommonGPanel(String id, IModel<T> model)
	{
		super(id, model);
	}
}
