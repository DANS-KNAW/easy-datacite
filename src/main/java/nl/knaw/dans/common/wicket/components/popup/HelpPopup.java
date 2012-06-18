package nl.knaw.dans.common.wicket.components.popup;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Panel that creates a Help-pop-up. The contents of the pop-up is pre-loaded into the HTML-file.
 */
public class HelpPopup extends Panel
{
    private static final long serialVersionUID = -2271696165117255618L;

    @SuppressWarnings("serial")
    public HelpPopup(String id, final String anchorName, final String content)
    {
        super(id);
        add(JavascriptPackageResource.getHeaderContribution(HelpPopup.class, "HelpPopup.js"));
        /*
         * Warning: hackerisch code ahead!  The label is filled with actual HTML-code!
         */
        add(new Label("popupHTML", createHtmlForPopup(anchorName, content)).setEscapeModelStrings(false));

        add(new AbstractBehavior()
        {
            public void renderHead(IHeaderResponse response)
            {
                super.renderHead(response);
                response.renderOnLoadJavascript("DANS.createHelpPopup('" + anchorName + "');");
            }
        });
    }

    private static String createHtmlForPopup(String anchorName, String content)
    {
        return String.format( //
                // Button/link to trigger pop-up
                "<a href=\"#\" class='popupHelp' " //
                        + "id='popupButton%s'>" //
                        + "<img src='/images/button_help.gif' alt='help' /></a>\n" //

                        // Initially hidden div-element that implements the dialog.
                        + "<div id='popupDialog%s' class='yui-pe-content popupDialog' style='display:none'>\n" //
                        + "<div class='hd'>Help</div>\n" //
                        + "<div class='bd'>\n%s</div>\n</div>", anchorName, anchorName, content);
    }
}
