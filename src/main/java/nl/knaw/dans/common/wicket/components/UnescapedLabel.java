package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A label that does not escape HTML 
 * 
 * @author lobo
 */
public class UnescapedLabel extends Label 
{
	private static final long serialVersionUID = 4653914433944688751L;

	public UnescapedLabel(final String id, String label)
	{
		this(id, new Model(label));
	}
	
	public UnescapedLabel(final String id, IModel model)
	{
		super(id, model);

		setEscapeModelStrings(false);
	}
}
