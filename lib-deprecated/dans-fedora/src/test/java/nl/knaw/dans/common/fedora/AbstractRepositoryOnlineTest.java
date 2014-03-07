package nl.knaw.dans.common.fedora;

import nl.knaw.dans.common.lang.test.Tester;

/**
 * Abstract class for testing repository client classes online.
 * 
 * @author dev
 */
public abstract class AbstractRepositoryOnlineTest
{

    private static final String KEY_FEDORA_BASE_URL = "fedora.base.url";
    private static final String KEY_FEDORA_ADMIN_NAME = "fedora.admin.username";
    private static final String KEY_FEDORA_ADMIN_PASS = "fedora.admin.userpass";

    private static Repository REPOSITORY;

    public static String getBaseUrl()
    {
        return Tester.getString(KEY_FEDORA_BASE_URL);
    }

    public static String getAdminName()
    {
        return Tester.getString(KEY_FEDORA_ADMIN_NAME);
    }

    public static String getAdminPass()
    {
        return Tester.getString(KEY_FEDORA_ADMIN_PASS);
    }

    public static Repository getRepository()
    {
        if (REPOSITORY == null)
        {
            REPOSITORY = new Repository(getBaseUrl(), getAdminName(), getAdminPass());
        }
        return REPOSITORY;
    }

}
