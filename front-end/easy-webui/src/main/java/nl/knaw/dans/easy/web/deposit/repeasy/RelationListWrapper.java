package nl.knaw.dans.easy.web.deposit.repeasy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.PropertiesMessage;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.pf.language.emd.EmdRelation;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationListWrapper extends AbstractListWrapper<RelationListWrapper.RelationModel>
{
    private static final Logger logger = LoggerFactory.getLogger(RelationListWrapper.class);

    private static final long serialVersionUID = -7229861811665091371L;
    // private static Logger logger = LoggerFactory.getLogger(RelationListWrapper.class);

    private Map<String, List<Relation>> listMap = new HashMap<String, List<Relation>>();

    public RelationListWrapper(EmdRelation emdRelation)
    {
        listMap = emdRelation.getRelationMap();
    }

    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    public RelationModel getEmptyValue()
    {
        RelationModel model = new RelationModel();
        return model;
    }

    public List<RelationModel> getInitialItems()
    {
        List<RelationModel> listItems = new ArrayList<RelationModel>();
        for (String relationType : listMap.keySet())
        {
            List<Relation> relations = listMap.get(relationType);
            for (Relation relation : relations)
            {
                listItems.add(new RelationModel(relation, relationType));
            }
        }
        return listItems;
    }

    @Override
    public int size()
    {
        return getInitialItems().size();
    }

    public int synchronize(List<RelationModel> listItems)
    {

        // clear previous entries
        for (String relationType : listMap.keySet())
        {
            listMap.get(relationType).clear();
        }

        // add new entries
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++)
        {
            RelationModel model = listItems.get(i);
            Relation relation = null;
            relation = model.getRelation();

            if (relation != null)
            {
                String relationType = model.relationType == null ? "" : model.relationType;
                listMap.get(relationType).add(relation);

                if (model.hasErrors())
                {
                    handleErrors(model.getErrors(), i);
                    errors += model.getErrors().size();
                }
                model.clearErrors();
            }
        }
        return errors;
    }

    public static class RelationModel extends AbstractEasyModel
    {

        private static final long serialVersionUID = 3841830253279006843L;

        private String relationType;
        private boolean emphasis;
        private String subjectTitle;
        private String subjectLink;

        public RelationModel(Relation relation, String relationType)
        {
            if (relation == null)
            {
                throw new IllegalArgumentException("Model for relation cannot be created.");
            }
            if ("".equals(relationType))
            {
                relationType = null;
            }
            this.relationType = relationType;
            emphasis = relation.hasEmphasis();
            subjectTitle = relation.getSubjectTitle().getValue();
            subjectLink = relation.getSubjectLink() == null ? null : relation.getSubjectLink().toString();
        }

        protected RelationModel()
        {
        }

        public Relation getRelation()
        {
            Relation relation;
            if (relationType == null && subjectTitle == null && subjectLink == null)
            {
                relation = null;
            }
            else
            {
                relation = new Relation();
                relation.setEmphasis(emphasis);
                relation.setSubjectTitle(subjectTitle);
                if (StringUtils.isNotBlank(subjectLink))
                {
                    try
                    {
                        relation.setSubjectLink(new URI(subjectLink));
                    }
                    catch (URISyntaxException e)
                    {
                        final String message = new PropertiesMessage("RelationListWrapper").getString(EasyResources.INVALID_URL).replace("$1", subjectLink);
                        logger.error(message, e);
                        addErrorMessage(message);
                    }
                }

            }
            return relation;
        }

        public boolean isEmphasis()
        {
            return emphasis;
        }

        public void setEmphasis(boolean emphasis)
        {
            this.emphasis = emphasis;
        }

        public String getSubjectTitle()
        {
            return subjectTitle;
        }

        public void setSubjectTitle(String subjectTitle)
        {
            this.subjectTitle = subjectTitle;
        }

        public String getSubjectLink()
        {
            return subjectLink;
        }

        public void setSubjectLink(String subjectLink)
        {
            this.subjectLink = subjectLink;
        }

        public void setRelationType(KeyValuePair relationTypeKVP)
        {
            relationType = relationTypeKVP == null ? null : relationTypeKVP.getKey();
        }

        public KeyValuePair getRelationType()
        {
            return new KeyValuePair(relationType, null);
        }
    }
}
