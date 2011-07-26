package nl.knaw.dans.common.wicket.components.jumpoff;

import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.MarkupUnit;
import nl.knaw.dans.common.wicket.components.UnescapedLabel;
import nl.knaw.dans.common.wicket.model.DMOModel;

import org.apache.wicket.markup.html.panel.Panel;

public class JumpoffViewPanel extends Panel
{
	private static final long serialVersionUID = 6905841265646369464L;

	public JumpoffViewPanel(String id, DMOModel<JumpoffDmo> model)
	{
		super(id, model);
		JumpoffDmo jumpoffDmo = (JumpoffDmo)getDefaultModelObject();
		MarkupUnit markupUnit = jumpoffDmo.getMarkupUnit();
		
		add(new UnescapedLabel("jumpoffMarkup", markupUnit.getHtml()));
		add(new JumpoffMetadataPanel("jumpoffMetadataPanel", model));
		
	}

}
