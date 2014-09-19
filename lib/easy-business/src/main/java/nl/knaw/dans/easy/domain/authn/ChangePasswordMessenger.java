package nl.knaw.dans.easy.domain.authn;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.util.Messenger;

public class ChangePasswordMessenger extends Messenger<ChangePasswordMessenger.State> {

    public enum State {
        NotChanged, InsufficientData, NotAuthenticated, SystemError, PasswordChanged
    }

    private static final long serialVersionUID = -4626315934144818803L;

    private final EasyUser user;

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;

    private String token;

    private final boolean mailContext;

    public ChangePasswordMessenger(EasyUser user, boolean mailContext) {
        super(ChangePasswordMessenger.State.class);
        this.user = user;
        this.mailContext = mailContext;
    }

    public EasyUser getUser() {
        return user;
    }

    public String getUserId() {
        return user.getId();
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setOldPassword(final String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isMailContext() {
        return mailContext;
    }

    public void setState(final State state) {
        super.setState(state);
    }

    public void setState(final State state, final Throwable e) {
        super.setState(state, e);
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [mailContext=" + mailContext + " state=" + getState() + " user=" + (user == null ? "null" : user.toString())
                + "] " + getExceptionsAsString();
    }

}
