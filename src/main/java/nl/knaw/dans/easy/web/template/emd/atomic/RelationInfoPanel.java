/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;

/**
 * @author akmi
 */
public class RelationInfoPanel extends Panel
{
    private static final long serialVersionUID = -5703756567092947861L;

    private final Dataset dataset;

    /**
     * @param id
     * @param model
     */
    public RelationInfoPanel(String id, Dataset dataset)
    {
        super(id);
        this.dataset = dataset;
        init();
    }

    private void init()
    {

        Map<String, List<Relation>> map = dataset.getEasyMetadata().getEmdRelation().getRelationMap();
        RepeatingView view = new RepeatingView("repeatingRelation");

        for (String key : map.keySet())
        {
            for (Relation basicIdentifier : map.get(key))
            {
                String relTitle = basicIdentifier.getSubjectTitle().getValue();
                String relUrl = basicIdentifier.getSubjectLink().toString();
                boolean emphasis = new PropertyModel<Boolean>(basicIdentifier, "emphasis").getObject();
                if (emphasis)
                {
                    WebMarkupContainer item = new WebMarkupContainer(view.newChildId());
                    view.add(item);
                    ExternalLink link = new ExternalLink("relationLink", relUrl);
                    link.setVisible(relUrl != null);
                    link.add(new Label("relationTitle", relTitle).setVisible(relTitle != null));
                    item.add(link);
                }
            }
        }
        view.setVisible(view.size() != 0);
        this.add(view);
        this.setVisible(view.size() != 0);
    }
}
