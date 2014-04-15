/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.pf.language.emd.EmdRelation;
import nl.knaw.dans.pf.language.emd.types.EmdScheme;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akmi
 */
public class RelationInfoPanel extends Panel
{
    private static final long serialVersionUID = -5703756567092947861L;
    private static final Logger logger = LoggerFactory.getLogger(RelationInfoPanel.class);
    private ChoiceList qualifierLabels;

    @SpringBean(name = "depositService")
    private DepositService depositService;

    /**
     * @param id
     * @param model
     */
    public RelationInfoPanel(String id, EmdRelation emdRelation)
    {
        super(id);
        Map<String, List<Relation>> relationMap = emdRelation.getRelationMap();
        qualifierLabels = retrieveLabels(EmdScheme.COMMON_DCTERMS_RELATION);

        RepeatingView qualifiedView = new RepeatingView("repeatingQualifier");
        for (String key : relationMap.keySet())
        {
            RepeatingView relationsView = new RepeatingView("repeatingRelation");
            for (Relation relation : relationMap.get(key))
            {
                if (relation.hasEmphasis())
                {
                    Component link = createLink(relation);
                    if (link.isVisible())
                    {
                        WebMarkupContainer item = addNewItemTo(relationsView);
                        item.add(link);
                    }
                }
            }
            if (relationsView.size() != 0)
            {
                WebMarkupContainer item = addNewItemTo(qualifiedView);
                item.add(relationsView);
                item.add(createQualifier(key));
            }
        }
        this.add(qualifiedView);
        this.setVisible(qualifiedView.size() != 0);
    }

    private WebMarkupContainer addNewItemTo(RepeatingView view)
    {
        WebMarkupContainer item = new WebMarkupContainer(view.newChildId());
        view.add(item);
        return item;
    }

    private Component createLink(Relation relation)
    {
        String title = relation.getSubjectTitle() == null ? null : relation.getSubjectTitle().getValue();
        String url = relation.getSubjectLink() == null ? null : relation.getSubjectLink().toString();
        if (title == null || title.trim().length() == 0)
            title = url;

        boolean enabled = url != null && url.trim().length() != 0;
        boolean visible = title != null && title.trim().length() != 0;
        return new ExternalLink("relation", url, title).setEnabled(enabled).setVisible(visible);
    }

    private Label createQualifier(String key)
    {
        String label = qualifierLabels.getValue(key);
        if (label == null)
            label = key; // fall back to less user friendly value
        return new Label("qualifier", label);
    }

    private ChoiceList retrieveLabels(EmdScheme list)
    {
        try
        {
            return depositService.getChoices(list.getId(), getLocale());
        }
        catch (ServiceException e)
        {
            logger.warn("can not get user friendly values for " + list.getId(), e);
        }
        return null;
    }
}
