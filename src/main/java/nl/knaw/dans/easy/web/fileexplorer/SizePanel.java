package nl.knaw.dans.easy.web.fileexplorer;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class SizePanel extends Panel
{
    private static final long serialVersionUID = 1L;

    public SizePanel(String id, String size)
    {
        super(id);
        add(new Label("size", size));
    }

}
