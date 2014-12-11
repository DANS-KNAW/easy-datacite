package nl.knaw.dans.common.wicket.components.popup;

import org.apache.wicket.Component;
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

    protected Component content;
    protected String anchorName;
    protected String id;

    public HelpPopup(String id, final String anchorName, final String content) {
        super(id);
        this.content = new Label("popupHTML", String.format("%s", content)).setEscapeModelStrings(false);
        this.anchorName = anchorName;

        main();
    }

    public HelpPopup(String id, final String anchorName, final Panel content) {
        super(id);
        this.content = content;
        this.anchorName = anchorName;

        main();
    }

    private void main() {
        ExternalLink helpTrigger = new ExternalLink("helpPopup", "#");
        helpTrigger.add(new SimpleAttributeModifier("data-toggle", "modal"));
        helpTrigger.add(new SimpleAttributeModifier("data-target", "#" + anchorName + "Modal"));
        add(helpTrigger);

        WebMarkupContainer popupModal = new WebMarkupContainer("popupModal") {
            private static final long serialVersionUID = 1L;

            public boolean isVisible() {
                /**
                 * <pre>
                 * This is necessary because of a bug in Wicket 1.4:
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
                 * </pre>
                 */
                return content.determineVisibility();
            }
        };
        popupModal.add(new SimpleAttributeModifier("id", anchorName + "Modal")).add(new SimpleAttributeModifier("aria-labelledby", anchorName + "ModalLabel"));
        popupModal.add(content);
        add(popupModal);
    }
}
