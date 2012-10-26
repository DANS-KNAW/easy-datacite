/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * @author akmi
 *
 */
public class VerifyUrlPanel extends Panel
{

    /**
     * serial UID.
     */
    private static final long serialVersionUID = 6909389448240472866L;

    /**
     * Constructor.
     * @param id
     * @param label
     * @param focusTarget
     * @param inputName
     */
    public VerifyUrlPanel(final String id, final String label, final String focusTarget, final String inputName)
    {
        super(id);
        init(label, focusTarget, inputName);
    }

    private void init(final String label, final String focusTarget, final String inputName)
    {
        this.add(new ExternalLink("verifyPopup", new Model(focusTarget), new ResourceModel(label))
        {
            /**
             *
             */
            private static final long serialVersionUID = 6763084868772891198L;

            @Override
            protected void onComponentTag(ComponentTag arg0)
            {
                super.onComponentTag(arg0);
                arg0.put("onclick", "checkUrl('" + inputName + "');");
            }
        });
    }

}
