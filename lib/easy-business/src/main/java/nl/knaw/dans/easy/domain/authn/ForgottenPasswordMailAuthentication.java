package nl.knaw.dans.easy.domain.authn;

public class ForgottenPasswordMailAuthentication extends MailAuthentication {

    private static final long serialVersionUID = 5005585519293255084L;

    public ForgottenPasswordMailAuthentication(String userId, String returnedTime, String returnedToken) {
        super(userId, returnedTime, returnedToken);
    }

}
