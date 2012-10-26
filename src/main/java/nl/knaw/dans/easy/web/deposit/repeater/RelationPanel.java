package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.deposit.repeasy.RelationListWrapper.RelationModel;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.PublicSearchResultPage;
import nl.knaw.dans.easy.web.template.emd.atomic.VerifyUrlPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class RelationPanel extends AbstractChoicePanel<RelationModel>
{

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
    public RelationPanel(final String wicketId, final IModel model, final ChoiceList choiceList)
    {
        super(wicketId, model, choiceList);
    }

    public void setUseRelationType(boolean useRelationType)
    {
        this.useRelationType = useRelationType;
    }

    @Override
    protected Panel getRepeatingComponentPanel(final ListItem item)
    {
        if (isInEditMode())
        {
            return new RepeatingEditModePanel(item);
        }
        else
        {
            return new RepeatingViewModePanel(item);
        }
    }

    class RepeatingEditModePanel extends Panel
    {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingEditModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            final DropDownChoice relationTypeChoice = new DropDownChoice("relationTypeChoice", new PropertyModel(item.getDefaultModelObject(), "relationType"),
                    getChoiceList().getChoices(), getRenderer());
            relationTypeChoice.setNullValid(isNullValid());
            CheckBox emphasizeCheckBox = new CheckBox("emphasize", new PropertyModel(item.getDefaultModelObject(), "emphasis"));
            //for simple deposit, we use emphasize checbox, otherwise dropdownchoice is used.
            relationTypeChoice.setVisible(useRelationType);
            final TextField subjectTitleField = new TextField("subjectTitleField", new PropertyModel(item.getDefaultModelObject(), "subjectTitle"));

            final TextField subjectLinkField = new TextField("subjectLinkField", new PropertyModel(item.getDefaultModelObject(), "subjectLink"));

            String currRelItemPath = item.getPageRelativePath().replace("depositPanel:depositForm:", "");
            add(emphasizeCheckBox);
            add(relationTypeChoice);
            add(subjectTitleField);
            add(subjectLinkField);
            add(new VerifyUrlPanel("verifyPopup", "relation.url.verify.label", "#subjectTitle", currRelItemPath + ":" + this.getId() + ":"
                    + subjectLinkField.getPageRelativePath()));
            PageParameters params = new PageParameters();
            params.add("q", "");// temporary
            emphasizeCheckBox.setVisible(true);
        }

    }

    class RepeatingViewModePanel extends Panel
    {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);

            String title = (String) new PropertyModel(item.getDefaultModel(), "subjectTitle").getObject();
            String href = (String) new PropertyModel(item.getDefaultModel(), "subjectLink").getObject();

            ExternalLink link = new ExternalLink("relation", href, title);
            link.setEnabled(!StringUtils.isBlank(href));
            add(link);

        }

    }

}
