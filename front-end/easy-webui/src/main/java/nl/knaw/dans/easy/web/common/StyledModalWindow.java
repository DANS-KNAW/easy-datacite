package nl.knaw.dans.easy.web.common;

import nl.knaw.dans.easy.web.template.Style;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

public class StyledModalWindow extends ModalWindow {
    private static final long serialVersionUID = 1L;

    public StyledModalWindow(String id) {
        super(id);
        add(Style.MODAL_HEADER_CONTRIBUTION);
    }

    public StyledModalWindow(String id, String title) {
        super(id);
        add(Style.MODAL_HEADER_CONTRIBUTION);
        setTitle(title);
    }

    public StyledModalWindow(String id, int initialWidth) {
        super(id);
        add(Style.MODAL_HEADER_CONTRIBUTION);
        setUseInitialHeight(false);
        setInitialWidth(initialWidth);
    }

    public StyledModalWindow(String id, String title, int initialWidth) {
        super(id);
        add(Style.MODAL_HEADER_CONTRIBUTION);
        setTitle(title);
        setUseInitialHeight(false);
        setInitialWidth(initialWidth);
    }
}
