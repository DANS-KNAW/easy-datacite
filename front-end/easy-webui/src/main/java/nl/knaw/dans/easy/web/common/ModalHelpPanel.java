package nl.knaw.dans.easy.web.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class ModalHelpPanel extends Panel
{
    private static final long serialVersionUID = -1504434939411160265L;

    public ModalHelpPanel(final ModalWindow window, String helpFileName)
    {
        super(window.getContentId());

        // Add content, copy html into the label
        add(new Label("popupHTML", new HelpFileReader(helpFileName).read()).setEscapeModelStrings(false));

        add(new IndicatingAjaxLink<Void>("close")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }
        });
    }

}
