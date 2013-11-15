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

        RepeatingView qualifierView = new RepeatingView("repeatingQualifier");
        for (String key : map.keySet())
        {
            RepeatingView relationsView = new RepeatingView("repeatingRelation");
            for (Relation relation : map.get(key))
            {
                if (relation.hasEmphasis())
                {
                    String relTitle = relation.getSubjectTitle().getValue();
                    String relUrl = relation.getSubjectLink().toString();

                    ExternalLink link = new ExternalLink("relationLink", relUrl);
                    link.setVisible(relUrl != null);
                    link.add(new Label("relationTitle", relTitle).setVisible(relTitle != null));

                    WebMarkupContainer item = new WebMarkupContainer(relationsView.newChildId());
                    relationsView.add(item);
                    item.add(link);
                }
            }
            if (relationsView.size() != 0)
            {
                WebMarkupContainer item = new WebMarkupContainer(qualifierView.newChildId());
                qualifierView.add(item);
                item.add(relationsView);
                item.add(new Label("qualifier", key));
            }
        }
        this.add(qualifierView);
        this.setVisible(qualifierView.size() != 0);
    }
}
