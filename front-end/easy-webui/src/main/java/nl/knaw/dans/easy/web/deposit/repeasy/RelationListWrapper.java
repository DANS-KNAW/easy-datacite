package nl.knaw.dans.easy.web.deposit.repeasy;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationListWrapper extends AbstractListWrapper<RelationListWrapper.RelationModel> {
    private static final Logger logger = LoggerFactory.getLogger(RelationListWrapper.class);

    private static final long serialVersionUID = -7229861811665091371L;

    private Map<String, List<MetadataItem>> listMap = new HashMap<String, List<MetadataItem>>();

    public RelationListWrapper(EmdRelation emdRelation) {

        addRelations(emdRelation.getBasicIdentifierMap());
        addRelations(emdRelation.getRelationMap());
    }

    private <R extends MetadataItem> void addRelations(Map<String, List<R>> dcRels) {
        for (String type : dcRels.keySet()) {
            List<MetadataItem> l = new ArrayList<MetadataItem>();
            l.addAll(dcRels.get(type));
            if (listMap.containsKey(type))
                listMap.get(type).addAll(l);
            else
                listMap.put(type, l);
        }
    }

    public ChoiceRenderer getChoiceRenderer() {
        return new KvpChoiceRenderer();
    }

    public RelationModel getEmptyValue() {
        RelationModel model = new RelationModel();
        return model;
    }

    public List<RelationModel> getInitialItems() {
        List<RelationModel> listItems = new ArrayList<RelationModel>();
        for (String relationType : listMap.keySet()) {
            List<? extends MetadataItem> relations = listMap.get(relationType);
            for (MetadataItem relation : relations) {
                listItems.add(new RelationModel(relation, relationType));
            }
        }
        return listItems;
    }

    @Override
    public int size() {
        return getInitialItems().size();
    }

    public int synchronize(List<RelationModel> listItems) {

        // clear previous entries
        for (String relationType : listMap.keySet()) {
            listMap.get(relationType).clear();
        }

        // add new entries
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++) {
            RelationModel model = listItems.get(i);
            MetadataItem relation = model.getRelation();

            if (relation != null) {
                String relationType = model.relationType == null ? "" : model.relationType;
                listMap.get(relationType).add(relation);

                if (model.hasErrors()) {
                    handleErrors(model.getErrors(), i);
                    errors += model.getErrors().size();
                }
                model.clearErrors();
            }
        }
        return errors;
    }

    public static class RelationModel<R extends MetadataItem> extends AbstractEasyModel {

        private static final long serialVersionUID = 3841830253279006843L;

        private String relationType;
        private boolean emphasis;
        private String subjectTitle;
        private String subjectLink;

        public RelationModel(R relation, String relationType) {
            if (relation == null) {
                throw new IllegalArgumentException("Model for relation cannot be created.");
            }
            if ("".equals(relationType)) {
                relationType = null;
            }
            this.relationType = relationType;
            if (relation instanceof Relation) {
                Relation easyRelation = (Relation) relation;
                emphasis = easyRelation.hasEmphasis();
                subjectTitle = easyRelation.getSubjectTitle().getValue().trim();
                subjectLink = easyRelation.getSubjectLink() == null ? null : easyRelation.getSubjectLink().toString();
            } else if (relation instanceof BasicIdentifier) {
                String value = ((BasicIdentifier) relation).getValue().trim();
                try {
                    new URL(value);
                    subjectTitle = relationType == null ? value : relationType;
                    subjectLink = value;
                }
                catch (MalformedURLException e) {
                    int indexOfUrl = value.toLowerCase().indexOf("url=");
                    int indexOfTitle = value.toLowerCase().indexOf("title=");
                    String title = (indexOfTitle < 0 ? value : indexOfTitle < indexOfUrl ? value.substring(indexOfTitle + 6, indexOfUrl) : value
                            .substring(indexOfTitle + 6)).trim();
                    subjectLink = indexOfUrl < 0 ? null : value.substring(indexOfUrl + 4).split(" ")[0].trim();
                    try {
                        new URL(subjectLink);
                    }
                    catch (MalformedURLException e2) {
                        subjectLink = null;
                    }
                    if (relationType == null)
                        subjectTitle = title;
                    else
                        subjectTitle = relationType + ": " + title;
                }
                emphasis = false;
            }
        }

        protected RelationModel() {}

        public Relation getRelation() {
            Relation relation;
            if (relationType == null && subjectTitle == null && subjectLink == null) {
                relation = null;
            } else {
                relation = new Relation();
                relation.setEmphasis(emphasis);
                relation.setSubjectTitle(subjectTitle);
                if (StringUtils.isNotBlank(subjectLink)) {
                    try {
                        relation.setSubjectLink(new URI(subjectLink));
                    }
                    catch (URISyntaxException e) {
                        final String message = new PropertiesMessage("RelationListWrapper").getString(EasyResources.INVALID_URL).replace("$1", subjectLink);
                        logger.error(message, e);
                        addErrorMessage(message);
                    }
                }

            }
            return relation;
        }

        public boolean isEmphasis() {
            return emphasis;
        }

        public void setEmphasis(boolean emphasis) {
            this.emphasis = emphasis;
        }

        public String getSubjectTitle() {
            return subjectTitle;
        }

        public void setSubjectTitle(String subjectTitle) {
            this.subjectTitle = subjectTitle;
        }

        public String getSubjectLink() {
            return subjectLink;
        }

        public void setSubjectLink(String subjectLink) {
            this.subjectLink = subjectLink;
        }

        public void setRelationType(KeyValuePair relationTypeKVP) {
            relationType = relationTypeKVP == null ? null : relationTypeKVP.getKey();
        }

        public KeyValuePair getRelationType() {
            return new KeyValuePair(relationType, null);
        }
    }
}
