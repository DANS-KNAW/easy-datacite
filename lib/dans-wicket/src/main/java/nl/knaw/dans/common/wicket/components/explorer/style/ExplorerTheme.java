package nl.knaw.dans.common.wicket.components.explorer.style;

import org.apache.wicket.markup.html.resources.CompressedResourceReference;

public class ExplorerTheme extends CompressedResourceReference {
    private static final long serialVersionUID = 1L;

    public ExplorerTheme() {
        super(ExplorerTheme.class, "style.css");
    }
}
