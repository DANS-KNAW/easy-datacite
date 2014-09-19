package nl.knaw.dans.easy.web.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModalYesNoPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public ModalYesNoPanel(final ModalWindow window) {
        super(window.getContentId());

        add(new IndicatingAjaxLink<Void>("yes") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                handleYesClicked();
                window.close(target);
            }

        });

        add(new IndicatingAjaxLink<Void>("no") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                handleNoClicked();
                window.close(target);
            }
        });
    }

    /**
     * Does nothing by default, subclass may override.
     */
    protected void handleYesClicked() {}

    /**
     * Does nothing by default, subclass may override.
     */
    protected void handleNoClicked() {}
}
