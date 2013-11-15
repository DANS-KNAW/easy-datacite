/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

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

        RepeatingView typesView = new RepeatingView("repeatingRelationType");
        for (String key : map.keySet())
        {
            RepeatingView instancesView = new RepeatingView("repeatingRelation");
            for (Relation relation : map.get(key))
            {
                if (relation.hasEmphasis())
                {
                    String relTitle = relation.getSubjectTitle().getValue();
                    String relUrl = relation.getSubjectLink().toString();

                    ExternalLink link = new ExternalLink("relationLink", relUrl);
                    link.setVisible(relUrl != null);
                    link.add(new Label("relationTitle", relTitle).setVisible(relTitle != null));

                    WebMarkupContainer item = new WebMarkupContainer(instancesView.newChildId());
                    instancesView.add(item);
                    item.add(link);
                }
            }
            if (instancesView.size() != 0)
            {
                WebMarkupContainer item = new WebMarkupContainer(typesView.newChildId());
                typesView.add(item);
                item.add(instancesView);
                item.add(new Label("relationType", key));
            }
        }
        this.add(typesView);
        this.setVisible(typesView.size() != 0);
    }
}
