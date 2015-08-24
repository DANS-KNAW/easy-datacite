package nl.knaw.dans.easy.web.view.dataset;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

public class ExternalDOILinkPanel extends AbstractEasyPanel<EasyMetadata> {
    private static final long serialVersionUID = 3331793826624904999L;

    public ExternalDOILinkPanel(String id, IModel<EasyMetadata> model) {
        super(id, model);
        init();
    }

    private void init() {
        EmdIdentifier emdID = ((EasyMetadata) getModelObject()).getEmdIdentifier();
        String doi = emdID.getOtherAccessDoi();
        add(createLink("ext-doi-link", EmdConstants.DOI_RESOLVER + "/", doi));
    }

    private Component createLink(String wicketID, String resolver, String linkID) {
        if (!isBlank(linkID)) {
            try {
                return (new ExternalLink(wicketID, resolver + URLEncoder.encode(linkID, "UTF-8"), resolver + linkID));
            }
            catch (UnsupportedEncodingException e) {
                // happens either never or always
                return (new ExternalLink(wicketID, resolver + linkID, resolver + linkID));
            }
        } else {
            return (new ExternalLink(wicketID, "#").setVisible(false));
        }
    }
}
