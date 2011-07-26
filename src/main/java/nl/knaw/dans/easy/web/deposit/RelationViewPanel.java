package nl.knaw.dans.easy.web.deposit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EmdRelation;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.Relation;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationViewPanel extends AbstractCustomPanel
{

    public static final String CHOICE_LIST_ID = "common.dcterms.relation";
    
    private static final long serialVersionUID = 4767608404968347974L;
    private static final Logger logger = LoggerFactory.getLogger(RelationViewPanel.class);
    
    private final EmdRelation emdRelations;

    public RelationViewPanel(String id, IModel<EasyMetadata> model)
    {
        super(id, model);
        setOutputMarkupId(true);
        EasyMetadata easyMetadata = (EasyMetadata) model.getObject();
        emdRelations = easyMetadata.getEmdRelation();
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        return new CustomPanel();
    }
    
    class CustomPanel extends Panel
    {

        private static final long serialVersionUID = 3743372292968164208L;

        public CustomPanel()
        {
            super(CUSTOM_PANEL_ID);
            try
            {
                final ChoiceList choiceList = Services.getDepositService().getChoices(CHOICE_LIST_ID, getLocale());
                final Map<String, List<BasicIdentifier>> plainRelations = emdRelations.getBasicIdentifierMap();
                final Map<String, List<Relation>> linkedRelations = emdRelations.getRelationMap();
                final List<String> keyList = EmdRelation.getQualifierList();
                ListView<String> listView = new ListView<String>("relationList", keyList)
                {

                    private static final long serialVersionUID = 5776812971859283845L;

                    @Override
                    protected void populateItem(ListItem<String> item)
                    {                        
                        String key = item.getDefaultModelObjectAsString();
                        
                        Label qualifierLabel = new Label("qualifier", choiceList.getValue(key));
                        item.add(qualifierLabel);
                        
                        List<Serializable> relations = new ArrayList<Serializable>();
                        relations.addAll(plainRelations.get(key));
                        relations.addAll(linkedRelations.get(key));
                        InnerListView innerListView = new InnerListView(relations);
                        item.add(innerListView);
                        qualifierLabel.setVisible(relations.size() > 0);
                    }
                    
                };
                add(listView);
                
            }
            catch (ServiceException e)
            {
                logger.error("Unable to render Relations.", e);
            }
        }
        
        @Override
        public boolean isVisible()
        {
            return !emdRelations.isEmpty();
        }
        
    }
    
    class InnerListView extends ListView<Serializable>
    {

        private static final long serialVersionUID = 8265036526196753372L;
        

        public InnerListView(List<Serializable> relations)
        {
            super("qualifiedList", relations);
        }

        @Override
        protected void populateItem(ListItem<Serializable> item)
        {
            Serializable mobj = (Serializable) item.getDefaultModelObject();
            String title;
            String href;
            if (mobj instanceof BasicIdentifier)
            {
                href = "";
                title = ((BasicIdentifier)mobj).getValue();
            }
            else if (mobj instanceof Relation)
            {
                Relation relation = (Relation) mobj;
                href = relation.getSubjectLink() == null ? "" : relation.getSubjectLink().toString();
                title = relation.getSubjectTitle().getValue();
            }
            else
            {
                logger.error("Unknown type in Relations innerList");
                throw new WicketRuntimeException("Unknown type in Relations innerList");
            }
            ExternalLink link = new ExternalLink("relation", href, title);
            link.setEnabled(!StringUtils.isBlank(href));
            item.add(link);
        }
        
    }

}
