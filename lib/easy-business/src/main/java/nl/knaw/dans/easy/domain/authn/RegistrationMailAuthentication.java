package nl.knaw.dans.easy.domain.authn;

public class RegistrationMailAuthentication extends MailAuthentication
{

    private static final long serialVersionUID = 746976155854221282L;

    public RegistrationMailAuthentication(String userId, String returnedTime, String returnedToken)
    {
        super(userId, returnedTime, returnedToken);
    }

}
