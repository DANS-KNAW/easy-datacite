package nl.knaw.dans.easy.domain.authn;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.util.Messenger;

public class ForgottenPasswordMessenger extends Messenger<ForgottenPasswordMessenger.State> {

    public enum State {
        NothingSend, InsufficientData, UserNotFound, NoQualifiedUsers, MailError, SystemError,
        /**
         * End state if a new password was send successfully by mail.
         */
        NewPasswordSend,
        /**
         * End state if a link was successfully send by mail.
         */
        UpdateURLSend
    }

    private static final long serialVersionUID = 7949580373425981106L;

    private String userId;

    private final String mailToken;

    private String email;

    private String updateURL;

    private String userIdParamKey;

    private List<EasyUser> users = new ArrayList<EasyUser>();

    public ForgottenPasswordMessenger() {
        super(ForgottenPasswordMessenger.State.class);
        mailToken = super.createMailToken(userId);
    }

    public ForgottenPasswordMessenger(String userId, String email) {
        super(ForgottenPasswordMessenger.State.class);
        this.userId = userId;
        this.email = email;
        mailToken = super.createMailToken(userId);
    }

    @Override
    public boolean isCompleted() {
        return State.NewPasswordSend.equals(getState()) || State.UpdateURLSend.equals(getState());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMailToken() {
        return mailToken;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public void setUserIdParamKey(String userIdParamKey) {
        this.userIdParamKey = userIdParamKey;
    }

    public String getUserIdParamKey() {
        return userIdParamKey;
    }

    public List<EasyUser> getUsers() {
        return users;
    }

    public void addUser(EasyUser user) {
        users.add(user);
    }

    public void setState(State state) {
        super.setState(state);
    }

    public void setState(State state, Throwable e) {
        super.setState(state, e);
    }

}
