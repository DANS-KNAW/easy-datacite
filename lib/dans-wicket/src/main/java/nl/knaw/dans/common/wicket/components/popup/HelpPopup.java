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

        ExternalLink helpTrigger = new ExternalLink("helpPopup", "#");
        helpTrigger.add(new SimpleAttributeModifier("data-toggle", "modal"));
        helpTrigger.add(new SimpleAttributeModifier("data-target", "#" + anchorName + "Modal"));
        add(helpTrigger);

        WebMarkupContainer popupModal = new WebMarkupContainer("popupModal"){
            public boolean isVisible(){
                /* This is necessary because of a bug in Wicket 1.4:
                 * https://issues.apache.org/jira/browse/WICKET-2636
                 *
                 * Essentially, if you would like to put an enclosure on a
                 * level 2 child wicket:id it complains it can't find that
                 * wicket:id in the code. The following example won't work, but
                 * will in Wicket 1.5:
                 *
                 * <wicket:enclosure child="child">
                 *    <div wicket:id="parent">
                 *        <div wicket:id="child"></div>
                 *    </div>
                 * </wicket:enclosure>
                 */
                return !content.isEmpty();
            }
        };
        popupModal.add(new SimpleAttributeModifier("id", anchorName + "Modal")).add(new SimpleAttributeModifier("aria-labelledby", anchorName + "ModalLabel"));
        popupModal.add(new Label("popupHTML", String.format("%s", content)).setEscapeModelStrings(false));
        add(popupModal);
    }
}
