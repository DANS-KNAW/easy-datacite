package nl.knaw.dans.easy.web.common;

import nl.knaw.dans.easy.web.template.Style;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

public class StyledModalWindow extends ModalWindow {
    private static final long serialVersionUID = 1L;

    public StyledModalWindow(String id) {
        super(id);
    }

    public StyledModalWindow(String id, String title) {
        super(id);
        setTitle(title);
    }

    public StyledModalWindow(String id, int initialWidth) {
        super(id);
        setUseInitialHeight(false);
        setInitialWidth(initialWidth);
    }

    public StyledModalWindow(String id, String title, int initialWidth) {
        super(id);
        setTitle(title);
        setUseInitialHeight(false);
        setInitialWidth(initialWidth);
    }

    @Override
    protected ResourceReference newCssResource() {
        // This causes the default Wicket CSS not to be loaded without having to
        // use our own specific CSS file.
        // We've included the new styles in the overall project CSS file.
        return null;
    }
}
