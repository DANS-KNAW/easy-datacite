package nl.knaw.dans.easy.domain.authn;

public class UsernamePasswordAuthentication extends Authentication
{

    private static final long serialVersionUID = -100329252053435195L;

    public UsernamePasswordAuthentication()
    {
        super();
    }

    public UsernamePasswordAuthentication(String userId, String password)
    {
        super(userId, password);
    }

}
