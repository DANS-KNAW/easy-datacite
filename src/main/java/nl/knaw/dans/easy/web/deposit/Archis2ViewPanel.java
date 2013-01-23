package nl.knaw.dans.easy.web.deposit;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.wicket.components.SeparatedListView;
import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.wicket.Component;
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
            return new ViewPanel();
        }
    }

    class ViewPanel extends Panel
    {
        private static final long serialVersionUID = -3441453142983333780L;

        public ViewPanel()
        {
            super(CUSTOM_PANEL_ID);
            final List<BasicIdentifier> identfiers = easyMetadata.getEmdIdentifier().getAllIdentfiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
            add(new SeparatedListView("list", "separator", createArchisLinks(identfiers)));
            setVisible(!identfiers.isEmpty());
        }

        private List<Component> createArchisLinks(List<BasicIdentifier> identfiers)
        {
            final List<Component> links = new ArrayList<Component>();
            for (final BasicIdentifier basicId : identfiers)
            {
                final String digits = ArchisCollector.getDigits(basicId.getValue());
                links.add(new ArchisLink("link", "label", digits));
            }
            return links;
        }
    }
}
