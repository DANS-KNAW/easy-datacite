package nl.knaw.dans.common.wicket.components.explorer.style;

import org.apache.wicket.markup.html.resources.CompressedResourceReference;


public class ExplorerIcon extends CompressedResourceReference
{
	private static final long serialVersionUID = 1L;

	public ExplorerIcon(String image)
	{
		super(ExplorerIcon.class, "windows/icons/"+image);
	}
}
