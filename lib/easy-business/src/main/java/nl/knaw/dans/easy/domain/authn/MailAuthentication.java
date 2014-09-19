package nl.knaw.dans.easy.domain.authn;

public class MailAuthentication extends Authentication {
    private static final long serialVersionUID = -6084491600104733541L;

    private final String returnedTime;
    private final String returnedToken;

    protected MailAuthentication(String userId, String returnedTime, String returnedToken) {
        super(userId, returnedTime + returnedToken);
        this.returnedTime = returnedTime;
        this.returnedToken = returnedToken;
    }

    public String getReturnedTime() {
        return returnedTime;
    }

    public String getReturnedToken() {
        return returnedToken;
    }

}
