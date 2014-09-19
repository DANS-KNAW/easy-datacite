package nl.knaw.dans.easy.web.authn;

import java.text.MessageFormat;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.common.StyledModalWindow;
import nl.knaw.dans.easy.web.common.UserProperties;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;
import nl.knaw.dans.easy.web.wicket.SwitchPanel;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoDisplayPanel extends AbstractEasyStatelessPanel implements EasyResources {
    private static final String WR_CHANGE_PASSWORD_LINK = "changePasswordLink";

    private static final long serialVersionUID = 2646103426056079L;

    private static Logger logger = LoggerFactory.getLogger(UserInfoDisplayPanel.class);

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;
    private boolean hasPassword = false;

    @SpringBean(name = "userService")
    private UserService userService;

    @SpringBean(name = "federativeUserRepo")
    private FederativeUserRepo federativeUserRepo;

    public UserInfoDisplayPanel(final SwitchPanel parent, final String userId, final boolean enableModeSwitch) {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        EasyUser user = fetchUser(userId);
        checkHasPassword(user);
        constructPanel(user);
    }

    private EasyUser fetchUser(final String userId) {
        EasyUser user = null;
        try {
            user = userService.getUserById(getSessionUser(), userId);
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.USER_NOT_FOUND, userId);
            logger.error(message);
            throw new RestartResponseException(new ErrorPage());
        }

        if (user == null) {
            throw new RestartResponseException(new ErrorPage());
        }
        return user;
    }

    private void checkHasPassword(final EasyUser user) {
        // federation users might not have it.
        try {
            hasPassword = userService.isUserWithStoredPassword(user);
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error(message, e);
            throw new InternalWebError();
        }
    }

    private void constructPanel(EasyUser user) {
        super.setDefaultModel(new CompoundPropertyModel<UserProperties>(user));

        addCommonFeedbackPanel();

        add(new Label(UserProperties.USER_ID).setVisible(hasPassword));
        add(new Label(UserProperties.DISPLAYNAME));
        add(new Label(UserProperties.TITLE));
        add(new Label(UserProperties.INITIALS));
        add(new Label(UserProperties.PREFIXES));
        add(new Label(UserProperties.SURNAME));
        add(new Label(UserProperties.ORGANIZATION));
        add(new Label(UserProperties.DEPARTMENT));
        add(new Label(UserProperties.FUNCTION));
        add(new Label(UserProperties.DISCIPLINE1, fetchDisciplineString(user.getDiscipline1())));
        add(new Label(UserProperties.DISCIPLINE2, fetchDisciplineString(user.getDiscipline2())));
        add(new Label(UserProperties.DISCIPLINE3, fetchDisciplineString(user.getDiscipline3())));
        add(new Label(UserProperties.ADDRESS));
        add(new Label(UserProperties.POSTALCODE));
        add(new Label(UserProperties.CITY));
        add(new Label(UserProperties.COUNTRY));
        add(new Label(UserProperties.EMAIL));
        add(new Label(UserProperties.TELEPHONE));
        add(new Label(UserProperties.DAI));

        final ModalWindow popup = new StyledModalWindow("popup", 450);
        List<FederativeUserIdMap> linkedAccountsList = getLinkedAccounts(user);
        add(createLinkedAccountsLabel(linkedAccountsList.size()));
        add(createUnlinkInstitutionAccountsButton(linkedAccountsList, popup));
        add(popup);

        // Have different message depending on boolean; yes or no!
        add(createRadio(user, UserProperties.OPTS_FOR_NEWSLETTER, "userinfo.optsForNewsletter.${optsForNewsletter}"));
        add(createRadio(user, UserProperties.LOG_MY_ACTIONS, "userinfo.logMyActions.${logMyActions}"));

        add(createEditLink().setVisible(enableModeSwitch));
        add(createPasswordLink().setVisible(hasPassword));
    }

    private Component createLinkedAccountsLabel(int count) {
        String format = getString("user.institution.accounts.format");
        String createInstituetionAccountsMessage = MessageFormat.format(format, count);
        return new Label("institutionAccounts", createInstituetionAccountsMessage).setVisible(count > 0);
    }

    private Component createUnlinkInstitutionAccountsButton(final List<FederativeUserIdMap> linkedAccountsList, final ModalWindow popup) {
        return new AjaxLink<Void>("unlinkInstitutionAccountsLink") {
            private static final long serialVersionUID = 3429899621436517328L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.prependJavascript("Wicket.Window.unloadConfirmation = false;");
                logger.debug("Unlink institution account clicked.");
                popup.setTitle("Unlink institution accounts");
                popup.setContent(new UnlinkAccountsPanel(popup, linkedAccountsList, this));
                popup.show(target);
                logger.debug("Unlink institution account Popup shown");
            }
        }.setVisible(linkedAccountsList.size() > 0);
    }

    private Label createRadio(EasyUser user, String wicketId, String resourceKey) {
        return new Label(wicketId, new StringResourceModel(resourceKey, this, new Model<EasyUser>(user)));
    }

    private Component createPasswordLink() {
        return new Link<String>(WR_CHANGE_PASSWORD_LINK) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ChangePasswordPage.class);
            }
        };
    }

    private Component createEditLink() {
        return new Link<String>(EDIT_LINK) {

            private static final long serialVersionUID = -804946462543838511L;

            @Override
            public void onClick() {
                parent.switchMode();
            }

        };
    }

    private List<FederativeUserIdMap> getLinkedAccounts(EasyUser user) {
        try {
            return federativeUserRepo.findByDansUserId(user.getId().toString());
        }
        catch (RepositoryException e) {
            logger.error(errorMessage(EasyResources.INTERNAL_ERROR), e);
            throw new InternalWebError();
        }
    }

    private String fetchDisciplineString(String id) {
        KeyValuePair result = DisciplineUtils.getDisciplineItemById(id);

        return result == null ? "" : result.getValue();
    }

}
