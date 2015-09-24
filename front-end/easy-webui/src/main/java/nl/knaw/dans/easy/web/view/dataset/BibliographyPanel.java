package nl.knaw.dans.easy.web.view.dataset;

import static org.apache.commons.lang.StringUtils.isBlank;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

public class BibliographyPanel extends AbstractEasyPanel<EasyMetadata> {
    private static final long serialVersionUID = -6945991727691872102L;

    /** Wicket id. */
    public static final String NAME_LABEL = "authorName";
    public static final String DATE_LABEL = "publicationDate";
    public static final String TITLE_LABEL = "title";
    public static final String MANAGING_ORG_LABEL = "managingOrganisation";
    public static final String URL_LABEL = "url";

    public static final String SEPARATOR = "; ";

    public BibliographyPanel(String wicketId, IModel<EasyMetadata> model) {
        super(wicketId, model);
        init();
    }

    private void init() {
        EasyMetadata emd = (EasyMetadata) getDefaultModelObject();

        add(new Label(NAME_LABEL, getAuthors(emd)));
        String dateStr = getDate(emd);
        add(new Label(DATE_LABEL, dateStr).add(new SimpleAttributeModifier("datetime", dateStr)));
        String titleStr = getTitle(emd);
        add(new Label(TITLE_LABEL, titleStr).add(new SimpleAttributeModifier("title", titleStr)));

        String managingOrganisation = "";
        String doi = emd.getEmdIdentifier().getOtherAccessDoi();
        final boolean hasOtherAccessDoi = !isBlank(doi);
        if (!hasOtherAccessDoi) {
            doi = emd.getEmdIdentifier().getDansManagedDoi();
            managingOrganisation = "DANS.";
        }
        String pid = emd.getEmdIdentifier().getPersistentIdentifier();
        if (!isBlank(doi)) {
            add(new Label(URL_LABEL, EmdConstants.DOI_RESOLVER + "/" + doi));
        } else {
            add(new Label(URL_LABEL, EmdConstants.BRI_RESOLVER + "?identifier=" + pid));
        }

        add(new Label(MANAGING_ORG_LABEL, managingOrganisation).setVisible(!hasOtherAccessDoi));
    }

    private String getDate(EasyMetadata emd) {
        // the year of the dataset creation
        DateTime date = emd.getEmdDate().getDateCreated();
        if (date != null)
            return Integer.toString(date.getYear());
        else
            return "";
    }

    private String getAuthors(EasyMetadata emd) {
        // assume creators are Authors
        return emd.toString(SEPARATOR, Term.Name.CREATOR);
    }

    private String getTitle(EasyMetadata emd) {
        return emd.getPreferredTitle();
    }
}
