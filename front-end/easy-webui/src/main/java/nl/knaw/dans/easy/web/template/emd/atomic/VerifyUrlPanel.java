package nl.knaw.dans.easy.web.template.emd.atomic;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class VerifyUrlPanel extends Panel {
    private static final long serialVersionUID = 6909389448240472866L;

    public VerifyUrlPanel(final String id, final String label, final String focusTarget, final String inputName) {
        super(id);
        add(JavascriptPackageResource.getHeaderContribution(VerifyUrlPanel.class, "VerifyUrlPanel.js"));
        this.add(createPopupLink(label, focusTarget, inputName));
    }

    private ExternalLink createPopupLink(final String label, final String focusTarget, final String inputName) {
        return new ExternalLink("verifyPopup", new Model<String>(focusTarget), new ResourceModel(label)) {
            private static final long serialVersionUID = 6763084868772891198L;

            @Override
            protected void onComponentTag(ComponentTag arg0) {
                super.onComponentTag(arg0);
                arg0.put("onclick", "checkUrl('" + inputName + "');");
            }
        };
    }
}
