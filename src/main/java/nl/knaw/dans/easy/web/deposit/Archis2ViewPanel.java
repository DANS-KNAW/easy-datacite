package nl.knaw.dans.easy.web.deposit;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class Archis2ViewPanel extends AbstractCustomPanel
{

    private static final long serialVersionUID = -9039579510082841556L;
    private final EasyMetadata easyMetadata;

    public Archis2ViewPanel(final String id, final EasyMetadata easyMetadata)
    {
        this(id, new Model<EasyMetadata>(easyMetadata));
    }

    public Archis2ViewPanel(final String id, final IModel<EasyMetadata> model)
    {
        super(id, model);
        easyMetadata = (EasyMetadata) model.getObject();
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        if (isInEditMode())
        {
            throw new UnsupportedOperationException("EditMode not supported.");
        }
        else
        {
//          return new SeparatedListPanel(CUSTOM_PANEL_ID,createArchisLinks()); // TODO fix: Component can't be added to itself
            return new ViewPanel();
        }
    }

    class ViewPanel extends Panel
    {

        private static final long serialVersionUID = -3441453142983333780L;

        /* the first time we don't want to show the separator as it is actually a prefix */
        private boolean showSeparator = false;

        public ViewPanel()
        {
            super(CUSTOM_PANEL_ID);
            final List<Component> links = createArchisLinks();
            final ListView<Component> listView = new ListView<Component>("list", links)
            {

                private static final long serialVersionUID = 3720302690110935794L;

                @Override
                protected void populateItem(final ListItem<Component> item)
                {
                    item.add(item.getModelObject());
                    item.add(new WebMarkupContainer("separator").setVisible(showSeparator));
                    showSeparator = true;
                }
            };
            add(listView);
            setVisible(!links.isEmpty());
        }
    }
    
    private List<Component> createArchisLinks()
    {
        final List<Component> links = new ArrayList<Component>();
        for (final BasicIdentifier basicId : easyMetadata.getEmdIdentifier().getAllIdentfiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR))
        {
            final String digits = ArchisCollector.getDigits(basicId.getValue());
            links.add(new ArchisLink("link", "label",digits));
        }
        return links;
    }
}
