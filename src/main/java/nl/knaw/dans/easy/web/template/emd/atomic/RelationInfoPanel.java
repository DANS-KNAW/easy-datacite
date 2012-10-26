/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.types.Relation;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;

/**
 * @author akmi
 *
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

        List<Relation> list = dataset.getEasyMetadata().getEmdRelation().getEasRelation();
        RepeatingView view = new RepeatingView("repeatingRelation");

        if (list != null && !list.isEmpty())
        {
            for (Iterator<Relation> i = list.iterator(); i.hasNext();)
            {
                Object obj = i.next();
                String relTitle = (String) new PropertyModel(obj, "subjectTitle.value").getObject();
                String relUrl = (String) new PropertyModel(obj, "subjectLink.string").getObject();
                boolean emphasis = (Boolean) new PropertyModel(obj, "emphasis").getObject();
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
