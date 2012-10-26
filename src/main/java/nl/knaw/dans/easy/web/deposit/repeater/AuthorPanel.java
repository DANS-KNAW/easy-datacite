package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.model.emd.types.Author;
import nl.knaw.dans.easy.web.deposit.repeasy.AuthorListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.AuthorListWrapper.AuthorModel;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class AuthorPanel extends AbstractRepeaterPanel<AuthorModel>
{

    private static final long serialVersionUID = 7729872166922893440L;

    public AuthorPanel(String wicketId, ListWrapper<AuthorModel> listWrapper)
    {
        super(wicketId, listWrapper);
    }

    public AuthorPanel(String wicketId, IModel<Author> model)
    {
        super(wicketId, model);
    }

    @Override
    protected Panel getRepeatingComponentPanel(ListItem<AuthorModel> item)
    {
        if (isInEditMode())
        {
            return new EditPanel(item);
        }
        else
        {
            return new ViewPanel(item);
        }
    }

    class EditPanel extends Panel
    {

        private static final long serialVersionUID = -416742232389721048L;

        private final int index;

        @SuppressWarnings( {"unchecked", "rawtypes"})
        EditPanel(ListItem<AuthorModel> item)
        {
            super(REPEATING_PANEL_ID);
            index = item.getIndex();
            final TextField titleField = new TextField("titleField", new PropertyModel(item.getDefaultModelObject(), "title"));
            add(titleField);
            final TextField initialsField = new TextField("initialsField", new PropertyModel(item.getDefaultModelObject(), "initials"));
            add(initialsField);
            final TextField insertionsField = new TextField("insertionsField", new PropertyModel(item.getDefaultModelObject(), "prefix"));
            add(insertionsField);
            final TextField surnameField = new TextField("surnameField", new PropertyModel(item.getDefaultModelObject(), "surname"));
            add(surnameField);
            final TextField idField = new TextField("idField", new PropertyModel(item.getDefaultModelObject(), "entityId"))
            {

                private static final long serialVersionUID = 1L;

                @Override
                protected boolean shouldTrimInput()
                {
                    return true;
                }

            };
            add(idField);
            final TextField organizationField = new TextField("organizationField", new PropertyModel(item.getDefaultModelObject(), "organization"));
            add(organizationField);

            add(new WebMarkupContainer("item-demarcation")
            {

                private static final long serialVersionUID = 1L;

                // puts class="repating0" (first item) or class="repeating1" (next items)
                @Override
                protected void onComponentTag(ComponentTag tag)
                {
                    super.onComponentTag(tag);
                    int repeating = index == 0 ? 0 : 1;
                    String classAttr = (String) tag.getAttributes().get("class");
                    String pre = classAttr == null ? "" : classAttr + " ";
                    classAttr = pre + "repeating" + repeating;
                    tag.getAttributes().put("class", classAttr);
                }

            });
        }

    }

    class ViewPanel extends Panel
    {

        private static final long serialVersionUID = -8963343622727828504L;

        private final int index;

        ViewPanel(ListItem<AuthorModel> item)
        {
            super(REPEATING_PANEL_ID);
            index = item.getIndex();
            AuthorListWrapper.AuthorModel authorModel = item.getModelObject();
            Author author = authorModel.getObject();
            add(new Label("authorLabel", author.toString()));
            String infoDai = author.hasDigitalAuthorId() ? author.getDigitalAuthorId().getURI().toString() : "";
            Label daiLabel = new Label("daiLabel", infoDai);
            add(daiLabel);
            daiLabel.setVisible(author.hasDigitalAuthorId());

            // Tabular representation (can be removed after release oct. 2012)
            //            final Label titleLabel = new Label("titleLabel", new PropertyModel(authorModel, "title"));
            //            final Label initialsLabel = new Label("initialsLabel", new PropertyModel(authorModel, "initials"));
            //            final Label insertionsLabel = new Label("insertionsLabel", new PropertyModel(authorModel, "prefix"));
            //            final Label surnameLabel = new Label("surnameLabel", new PropertyModel(authorModel, "surname"));
            //            final Label idLabel = new Label("idLabel", new PropertyModel(authorModel, "entityId"));
            //            final Label organizationLabel = new Label("organizationLabel", new PropertyModel(authorModel, "organization"));
            //            titleLabel.setVisible(StringUtils.isNotBlank(authorModel.getTitle()));
            //            initialsLabel.setVisible(StringUtils.isNotBlank(authorModel.getInitials()));
            //            insertionsLabel.setVisible(StringUtils.isNotBlank(authorModel.getPrefix()));
            //            surnameLabel.setVisible(StringUtils.isNotBlank(authorModel.getSurname()));
            //            idLabel.setVisible(StringUtils.isNotBlank(authorModel.getEntityId()));
            //            organizationLabel.setVisible(StringUtils.isNotBlank(authorModel.getOrganization()));
            //            add(titleLabel);
            //            add(initialsLabel);
            //            add(insertionsLabel);
            //            add(surnameLabel);
            //            add(idLabel);
            //            add(organizationLabel);

            add(new WebMarkupContainer("item-demarcation")
            {

                private static final long serialVersionUID = 1L;

                // puts class="repating0" (first item) or class="repeating1" (next items)
                @Override
                protected void onComponentTag(ComponentTag tag)
                {
                    super.onComponentTag(tag);
                    int repeating = index == 0 ? 0 : 1;
                    String classAttr = (String) tag.getAttributes().get("class");
                    String pre = classAttr == null ? "" : classAttr + " ";
                    classAttr = pre + "repeating" + repeating;
                    tag.getAttributes().put("class", classAttr);
                }

            });
        }

    }

}
