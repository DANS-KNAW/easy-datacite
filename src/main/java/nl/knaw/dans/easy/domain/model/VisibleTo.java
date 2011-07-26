package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.common.lang.dataset.AccessCategory;

/**
 * Enumeration of allowed visibleTo-relation value.
 * @author akmi
 *
 */
//TODO unify this enum to key-abstraction AccessCategory
public enum VisibleTo {


    ANONYMOUS,

    KNOWN,

    RESTRICTED_REQUEST,

    RESTRICTED_GROUP,

    NONE;
    
    public static VisibleTo translate(AccessCategory accessCategory)
    {
        VisibleTo vt = null;
        if (AccessCategory.ANONYMOUS_ACCESS.equals(accessCategory))
        {
            vt = ANONYMOUS;
        }
        else if (AccessCategory.OPEN_ACCESS.equals(accessCategory))
        {
            vt = KNOWN;
        }
        else if (AccessCategory.GROUP_ACCESS.equals(accessCategory))
        {
            vt = RESTRICTED_GROUP;
        }
        else if (AccessCategory.REQUEST_PERMISSION.equals(accessCategory))
        {
            vt = RESTRICTED_REQUEST;
        }
        else if (AccessCategory.ACCESS_ELSEWHERE.equals(accessCategory)
                || AccessCategory.NO_ACCESS.equals(accessCategory))
        {
            vt = NONE;
        }
        return vt;
    }
    
    public static AccessCategory translate(VisibleTo visibleTo)
    {
        AccessCategory ac = AccessCategory.NO_ACCESS;
        if (ANONYMOUS.equals(visibleTo))
        {
            ac = AccessCategory.ANONYMOUS_ACCESS;
        }
        else if (KNOWN.equals(visibleTo))
        {
            ac = AccessCategory.OPEN_ACCESS;
        }
        else if (RESTRICTED_GROUP.equals(visibleTo))
        {
            ac = AccessCategory.GROUP_ACCESS;
        }
        else if (RESTRICTED_REQUEST.equals(visibleTo))
        {
            ac = AccessCategory.REQUEST_PERMISSION;
        }
        return ac;
    }
}
