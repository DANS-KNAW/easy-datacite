package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.id.DAI;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.pf.language.emd.types.Author;

import org.apache.commons.lang.StringUtils;

public class AuthorListWrapper extends AbstractDefaultListWrapper<AuthorListWrapper.AuthorModel, Author> {

    private static final long serialVersionUID = 1733893895631274476L;

    public AuthorListWrapper(List<Author> wrappedList) {
        super(wrappedList);
    }

    public AuthorListWrapper(List<Author> wrappedList, String schemeName, String schemeId) {
        super(wrappedList, schemeName, schemeId);
    }

    @Override
    public List<AuthorModel> getInitialItems() {
        List<AuthorModel> listItems = new ArrayList<AuthorListWrapper.AuthorModel>();
        for (Author author : getWrappedList()) {
            listItems.add(new AuthorModel(author));
        }
        return listItems;
    }

    @Override
    public int synchronize(List<AuthorModel> listItems) {
        getWrappedList().clear();
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++) {
            AuthorModel authorModel = listItems.get(i);
            Author author = authorModel.getAuthor();
            if (author != null) {
                getWrappedList().add(author);
            }
            if (authorModel.hasErrors()) {
                handleErrors(authorModel.getErrors(), i);
                errors += authorModel.getErrors().size();
            }
            authorModel.clearErrors();
        }
        return errors;
    }

    @Override
    public AuthorModel getEmptyValue() {
        return new AuthorModel();
    }

    public static class AuthorModel extends AbstractEasyModel {

        private static final long serialVersionUID = -6272851082229997716L;

        private String entityId;
        private String initials;
        private String prefix;
        private String surname;
        private String title;
        private String organization;

        private Author object;

        protected AuthorModel() {

        }

        public AuthorModel(Author author) {
            this.object = author;
            this.entityId = author.getEntityId();
            this.initials = author.getInitials();
            this.prefix = author.getPrefix();
            this.surname = author.getSurname();
            this.title = author.getTitle();
            this.organization = author.getOrganization();
        }

        public Author getObject() {
            return object;
        }

        protected Author getAuthor() {
            if (isBlank()) {
                return null;
            }
            Author author;
            author = new Author(title, initials, prefix, surname);
            author.setOrganization(organization);
            author.setEntityId(entityId);

            if (StringUtils.isNotBlank(entityId) && !DAI.isValid(entityId)) {
                addErrorMessage("The Digital Author Id '" + entityId + "' is not valid. (" + DAI.explain(entityId) + ")");
            }
            if (hasPersonalEntries() && (StringUtils.isBlank(initials) || StringUtils.isBlank(surname))) {
                addErrorMessage("If personal data is provided, the fields 'Surname' and 'Initials' are required.");
            }
            return author;
        }

        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        private boolean isBlank() {
            return StringUtils.isBlank(entityId) && StringUtils.isBlank(initials) && StringUtils.isBlank(prefix) && StringUtils.isBlank(surname)
                    && StringUtils.isBlank(title) && StringUtils.isBlank(organization);
        }

        public boolean hasPersonalEntries() {
            return StringUtils.isNotBlank(entityId) || StringUtils.isNotBlank(initials) || StringUtils.isNotBlank(prefix) || StringUtils.isNotBlank(surname)
                    || StringUtils.isNotBlank(title);
        }

    }

}
