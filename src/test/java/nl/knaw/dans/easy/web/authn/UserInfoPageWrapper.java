package nl.knaw.dans.easy.web.authn;

/** Replaces ITestPageSource which clashes with setResponsePage */
public class UserInfoPageWrapper extends UserInfoPage
{
    static boolean inEditMode;
    static boolean enableModeSwith;
    static String userId;

    public UserInfoPageWrapper()
    {
        super(userId, inEditMode, enableModeSwith);
    }
}
