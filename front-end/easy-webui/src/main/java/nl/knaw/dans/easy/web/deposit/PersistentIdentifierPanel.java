package nl.knaw.dans.easy.web.deposit;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class PersistentIdentifierPanel extends AbstractCustomPanel {

    private static final long serialVersionUID = -2435808589132843377L;

    public PersistentIdentifierPanel(String id, IModel<EasyMetadata> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel() {
        EmdIdentifier emdID = ((EasyMetadata) getModelObject()).getEmdIdentifier();
        String doi = emdID.getDansManagedDoi();
        String urn = isBlank(doi) ? emdID.getPersistentIdentifier() : null;
        CustomPanel panel = new CustomPanel(CUSTOM_PANEL_ID);
        panel.add(createLink("urn", EmdConstants.BRI_RESOLVER + "?identifier=", urn));
        panel.add(createLink("doi", EmdConstants.DOI_RESOLVER + "/", doi));
        return panel;
    }

    private Component createLink(String wicketID, String resolver, String linkID) {
        if (!isBlank(linkID)) {
            try {
                return (new ExternalLink(wicketID, resolver + URLEncoder.encode(linkID, "UTF-8"), linkID));
            }
            catch (UnsupportedEncodingException e) {
                // happens either never or always
                return (new ExternalLink(wicketID, resolver + linkID, linkID));
            }
        } else {
            return (new ExternalLink(wicketID, "#").setVisible(false));
        }
    }

    private static class CustomPanel extends Panel {
        // required to configure the HTML
        private static final long serialVersionUID = 1312199834066009539L;

        public CustomPanel(String wicketID) {
            super(wicketID);
        }
    }
}
