package nl.knaw.dans.easy.web.view.dataset;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EmdIdentifier;
import nl.knaw.dans.easy.domain.model.emd.Term;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummaryPanel extends AbstractEasyPanel
{
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
    public static final String TITLE = "title";

    /**
     * Wicket id.
     */
    public static final String PID = "pid";

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

    public SummaryPanel(String wicketId, Dataset dataset)
    {
        super(wicketId);
        emd = dataset.getEasyMetadata();
        init();
    }

    @SuppressWarnings("unchecked")
    private void init()
    {
        add(new Label(CREATOR, getCreators()));
        String dateCreated = getDateCreated();
        add(new Label(DATE_CREATED, dateCreated).setVisible(!StringUtils.isBlank(dateCreated)));
        add(new Label(TITLE, getTitles()));

        final String persistentIdentifier = getPersistentIdentifier();
        Link pidLink = new Link("pidLink")
        {

            private static final long serialVersionUID = -475314441520496889L;

            public String getURL()
            {
                try
                {
                    return EmdConstants.BRI_RESOLVER + "?identifier=" + URLEncoder.encode(persistentIdentifier, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    // happens either never or always
                    return EmdConstants.BRI_RESOLVER + "?identifier=" + persistentIdentifier;
                }
            }

            @Override
            public void onClick()
            {
                logger.debug("pidLink clicked: " + getURL());
            }

        };
        add(pidLink.setVisible(!StringUtils.isBlank(persistentIdentifier)));
        pidLink.add(new Label("pid", persistentIdentifier));

        add(new ListView(DESCRIPTIONS, getDescriptions())
        {

            private static final long serialVersionUID = -6597598635055541684L;

            @Override
            protected void populateItem(ListItem item)
            {
                final BasicString bString = (BasicString) item.getDefaultModelObject();
                item.add(new MultiLineLabel(DESCRIPTION, bString.getValue()));
            }

        });
    }

    private String getCreators()
    {
        return emd.toString(SEPARATOR, Term.Name.CREATOR);
    }

    private String getDateCreated()
    {
        return emd.toString(SEPARATOR_FOR_DATES, Term.Name.CREATED);
    }

    private String getTitles()
    {
        return emd.toString(SEPARATOR, Term.Name.TITLE);
    }

    private String getPersistentIdentifier()
    {
        EmdIdentifier emdIdentifier = emd.getEmdIdentifier();
        if (emdIdentifier == null)
            return null;
        BasicIdentifier identifier = emdIdentifier.getIdentifier(EmdConstants.SCHEME_PID);
        if (identifier == null)
            return null;
        return identifier.getValue();
    }

    private List<BasicString> getDescriptions()
    {
        return emd.getEmdDescription().getDcDescription();
    }

}
