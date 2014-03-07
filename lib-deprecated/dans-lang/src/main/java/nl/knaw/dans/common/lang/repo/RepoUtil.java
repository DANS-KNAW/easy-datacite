package nl.knaw.dans.common.lang.repo;

import nl.knaw.dans.common.lang.repo.exception.InvalidSidException;

/**
 * Repository util class.
 *
 * @author lobo
 */
public class RepoUtil
{
    public static String getNamespaceFromSid(String sid) throws InvalidSidException
    {
        int ns = sid.indexOf(':');
        if (ns <= 0)
            throw new InvalidSidException("Store ID " + sid + " is not a valid store ID");
        return sid.substring(0, ns);
    }
}
