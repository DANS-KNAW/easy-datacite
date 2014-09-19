package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.web.deposit.repeasy.RelationListWrapper.RelationModel;
import nl.knaw.dans.easy.web.template.emd.atomic.VerifyUrlPanel;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class RelationPanel extends AbstractChoicePanel<RelationModel> {

    private static final long serialVersionUID = -822413494904086019L;
    // private static Logger logger = LoggerFactory.getLogger(RelationPanel.class);

    private boolean useRelationType;

    /**
     * Constructor that takes a model with a ListWrapper&lt;RelationWrapper> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     * @param choices
     *        a list of choices
     */
    public RelationPanel(final String wicketId, final IModel<Relation> model, final ChoiceList choiceList) {
        super(wicketId, model, choiceList);
    }

    public void setUseRelationType(boolean useRelationType) {
        this.useRelationType = useRelationType;
    }

    @Override
    protected Panel getRepeatingComponentPanel(final ListItem item) {
        if (isInEditMode()) {
            return new RepeatingEditModePanel(item);
        } else {
            return new RepeatingViewModePanel(item);
        }
    }

    class RepeatingEditModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingEditModePanel(final ListItem<RelationModel> item) {
            super(REPEATING_PANEL_ID);

            final PropertyModel<String> typeModel = createStringModel(item, "relationType");
            final PropertyModel<String> titleModel = createStringModel(item, "subjectTitle");
            final PropertyModel<String> linkModel = createStringModel(item, "subjectLink");

            // for simple deposit, we use emphasize checbox, otherwise dropdownchoice is used.
            final Component emphasizeCheckBox = new CheckBox("emphasize", createBooleanModel(item, "emphasis")).setVisible(true);
            @SuppressWarnings({"unchecked", "rawtypes"})
            final Component relationTypeChoice = new DropDownChoice("relationTypeChoice", typeModel, getChoiceList().getChoices(), getRenderer())//
                    .setNullValid(isNullValid()).setVisible(useRelationType);

            final TextField<String> subjectTitleField = new TextField<String>("subjectTitleField", titleModel);
            final TextField<String> subjectLinkField = new TextField<String>("subjectLinkField", linkModel);

            add(emphasizeCheckBox);
            add(relationTypeChoice);
            add(subjectTitleField);
            add(subjectLinkField);

            String currRelItemPath = item.getPageRelativePath().replace("depositPanel:depositForm:", "");
            String inputName = currRelItemPath + ":" + this.getId() + ":" + subjectLinkField.getPageRelativePath();
            add(new VerifyUrlPanel("verifyPopup", "relation.url.verify.label", "#subjectTitle", inputName));
        }
    }

    class RepeatingViewModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem<RelationModel> item) {
            super(REPEATING_PANEL_ID);

            String title = createStringModel(item, "subjectTitle").getObject();
            String href = createStringModel(item, "subjectLink").getObject();

            ExternalLink link = new ExternalLink("relation", href, title);
            link.setEnabled(!StringUtils.isBlank(href));
            add(link);
        }
    }

    private PropertyModel<String> createStringModel(final ListItem<RelationModel> item, String propertyName) {
        return new PropertyModel<String>(item.getDefaultModelObject(), propertyName);
    }

    private PropertyModel<Boolean> createBooleanModel(final ListItem<RelationModel> item, String propertyName) {
        return new PropertyModel<Boolean>(item.getDefaultModelObject(), propertyName);
    }
}
