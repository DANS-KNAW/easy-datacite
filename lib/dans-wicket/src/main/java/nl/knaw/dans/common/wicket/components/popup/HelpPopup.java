package nl.knaw.dans.common.wicket.components.popup;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Panel that creates a Help-pop-up. The contents of the pop-up is pre-loaded into the HTML-file.
 */
public class HelpPopup extends Panel {
    private static final long serialVersionUID = -2271696165117255618L;

    public HelpPopup(String id, final String anchorName, final String content) {
        super(id);

        add(new ExternalLink("helpPopup", "#")
            .add(new SimpleAttributeModifier("data-toggle", "modal"))
            .add(new SimpleAttributeModifier("data-target", "#" + anchorName + "Modal"))
        );
        WebMarkupContainer popupModal = new WebMarkupContainer("popupModal");
        popupModal.add(new SimpleAttributeModifier("id", anchorName + "Modal"))
                  .add(new SimpleAttributeModifier("aria-labelledby", anchorName + "ModalLabel"));
        popupModal.add(new Label("popupHTML", String.format("%s", content)).setEscapeModelStrings(false));
        add(popupModal);
    }
}