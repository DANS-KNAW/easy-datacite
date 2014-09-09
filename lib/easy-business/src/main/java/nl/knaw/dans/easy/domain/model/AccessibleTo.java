package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.dataset.AccessCategory;

/**
 * Enumeration of allowed accessibleTo-relation value.
 * 
 * @Eko Indarto
 */
// TODO unify this enum to key abstraction AccessCategory
public enum AccessibleTo
{

    ANONYMOUS,

    KNOWN,

    RESTRICTED_REQUEST,

    RESTRICTED_GROUP,

    NONE;

    public static AccessibleTo translate(AccessCategory accessCategory)
    {
        AccessibleTo at = null;
        if (AccessCategory.ANONYMOUS_ACCESS.equals(accessCategory))
        {
            at = ANONYMOUS;
        }
        else if (AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS.equals(accessCategory))
        {
            at = KNOWN;
        }
        else if (AccessCategory.GROUP_ACCESS.equals(accessCategory))
        {
            at = RESTRICTED_GROUP;
        }
        else if (AccessCategory.REQUEST_PERMISSION.equals(accessCategory))
        {
            at = RESTRICTED_REQUEST;
        }
        else if (AccessCategory.NO_ACCESS.equals(accessCategory))
        {
            at = NONE;
        }
        return at;
    }

    public static AccessCategory translate(AccessibleTo accessibleTo)
    {
        AccessCategory ac = AccessCategory.NO_ACCESS;
        if (ANONYMOUS.equals(accessibleTo))
        {
            ac = AccessCategory.ANONYMOUS_ACCESS;
        }
        else if (KNOWN.equals(accessibleTo))
        {
            ac = AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS;
        }
        else if (RESTRICTED_GROUP.equals(accessibleTo))
        {
            ac = AccessCategory.GROUP_ACCESS;
        }
        else if (RESTRICTED_REQUEST.equals(accessibleTo))
        {
            ac = AccessCategory.REQUEST_PERMISSION;
        }
        return ac;
    }

}
