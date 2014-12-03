package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.web.deposit.repeasy.AuthorListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.AuthorListWrapper.AuthorModel;
import nl.knaw.dans.pf.language.emd.types.Author;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class AuthorPanel extends AbstractRepeaterPanel<AuthorModel> {

    private static final long serialVersionUID = 7729872166922893440L;

    public AuthorPanel(String wicketId, ListWrapper<AuthorModel> listWrapper) {
        super(wicketId, listWrapper);
    }

    public AuthorPanel(String wicketId, IModel<Author> model) {
        super(wicketId, model);
    }

    @Override
    protected Panel getRepeatingComponentPanel(ListItem<AuthorModel> item) {
        if (isInEditMode()) {
            return new EditPanel(item);
        } else {
            return new ViewPanel(item);
        }
    }

    class EditPanel extends Panel {

        private static final long serialVersionUID = -416742232389721048L;

        private final int index;

        @SuppressWarnings({"unchecked", "rawtypes"})
        EditPanel(ListItem<AuthorModel> item) {
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
            final TextField idField = new TextField("idField", new PropertyModel(item.getDefaultModelObject(), "entityId")) {

                private static final long serialVersionUID = 1L;

                @Override
                protected boolean shouldTrimInput() {
                    return true;
                }

            };
            add(idField);
            final TextField organizationField = new TextField("organizationField", new PropertyModel(item.getDefaultModelObject(), "organization"));
            add(organizationField);
        }

    }

    class ViewPanel extends Panel {

        private static final long serialVersionUID = -8963343622727828504L;

        private final int index;

        ViewPanel(ListItem<AuthorModel> item) {
            super(REPEATING_PANEL_ID);
            index = item.getIndex();
            AuthorListWrapper.AuthorModel authorModel = item.getModelObject();
            Author author = authorModel.getObject();
            add(new Label("authorLabel", author.toString()));
            String infoDai = author.hasDigitalAuthorId() ? author.getDigitalAuthorId().getURI().toString() : "";
            Label daiLabel = new Label("daiLabel", infoDai);
            add(daiLabel);
            daiLabel.setVisible(author.hasDigitalAuthorId());
        }

    }

}
