package nl.knaw.dans.easy.web.view.dataset;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummaryPanel extends AbstractEasyPanel<Object> {

    private static final Logger logger = LoggerFactory.getLogger(DescriptionPanel.class);

    /**
     * Wicket id.
     */
    public static final String CREATOR = "creator";

    /**
     * Wicket id.
     */
    public static final String DATE_CREATED = "dateCreated";

    /**
     * Wicket id.
     */
    public static final String PID_LABEL = "pid";

    /**
     * Wicket id.
     */
    private static final String PID_LINK = "pidLink";

    /**
     * Wicket id.
     */
    public static final String DESCRIPTIONS = "descriptions";

    /**
     * Wicket id.
     */
    public static final String DESCRIPTION = "description";

    /**
     * String used to separate creator items.
     */
    public static final String SEPARATOR = "; ";

    /**
     * String used to separate creator items.
     */
    public static final String SEPARATOR_FOR_DATES = ", ";

    private final EasyMetadata emd;

    private static final long serialVersionUID = 5181882887614791831L;

    private static final String BIBLIO = "bibliography";

    public SummaryPanel(String wicketId, Dataset dataset) {
        super(wicketId);
        emd = dataset.getEasyMetadata();
        init();
    }

    private void init() {
        String dateCreated = getDateCreated();
        EmdIdentifier emdIdentifier = emd.getEmdIdentifier();
        String doi = emdIdentifier.getDansManagedDoi();
        String pid = emdIdentifier.getPersistentIdentifier();

        add(new Label(CREATOR, getCreators()));
        add(new Label(DATE_CREATED, dateCreated).setVisible(!StringUtils.isBlank(dateCreated)));
        add(createListView(DESCRIPTIONS, getDescriptions()));
        if (!isBlank(doi)) {
            add(finishLink(PID_LABEL, doi, createDoiLink(PID_LINK, doi)));
        } else {
            add(finishLink(PID_LABEL, pid, createPidLink(PID_LINK, pid)));
        }
        if (emdIdentifier != null && (emdIdentifier.getPersistentIdentifier() != null || emdIdentifier.getDansManagedDoi() != null))
            add(new BibliographyPanel(BIBLIO, new Model<EasyMetadata>(emd)));
        else
            add(new Label(BIBLIO, getString("bibliography.draft")));
    }

    private static Component finishLink(String wicketID, String label, Link<Object> link) {
        return link.add(new Label(wicketID, label)).setVisible(!StringUtils.isBlank(label));
    }

    private ListView<BasicString> createListView(String wicketID, List<BasicString> descriptions) {
        return new ListView<BasicString>(wicketID, descriptions) {

            private static final long serialVersionUID = -6597598635055541684L;

            @Override
            protected void populateItem(ListItem<BasicString> item) {
                final BasicString bString = item.getModelObject();
                item.add(new MultiLineLabel(DESCRIPTION, bString.getValue()));
            }

        };
    }

    private Link<Object> createDoiLink(String wicketID, final String doi) {
        Link<Object> link = new Link<Object>(wicketID) {

            private static final long serialVersionUID = 1L;

            public String getURL() {
                return EmdConstants.DOI_RESOLVER + "/" + doi;
            }

            @Override
            public void onClick() {
                logger.debug("pidLink clicked: " + getURL());
            }

        };
        return link;
    }

    private Link<Object> createPidLink(String wicketID, final String pid) {
        Link<Object> link = new Link<Object>(wicketID) {

            private static final long serialVersionUID = -475314441520496889L;

            public String getURL() {
                try {
                    return EmdConstants.BRI_RESOLVER + "?identifier=" + URLEncoder.encode(pid, "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    // happens either never or always
                    return EmdConstants.BRI_RESOLVER + "?identifier=" + pid;
                }
            }

            @Override
            public void onClick() {
                logger.debug("pidLink clicked: " + getURL());
            }

        };
        return link;
    }

    private String getCreators() {
        return emd.toString(SEPARATOR, Term.Name.CREATOR);
    }

    private String getDateCreated() {
        return emd.toString(SEPARATOR_FOR_DATES, Term.Name.CREATED);
    }

    private List<BasicString> getDescriptions() {
        return emd.getEmdDescription().getDcDescription();
    }

}
