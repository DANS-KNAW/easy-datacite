package nl.knaw.dans.easy.web.common;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

/**
 * Useful properties of the class {@link EasyUser}.
 * 
 * @author ecco Mar 11, 2009
 */
public interface UserProperties {

    public static final String TELEPHONE = "telephone";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String POSTALCODE = "postalCode";
    public static final String ADDRESS = "address";
    public static final String FUNCTION = "function";
    public static final String DISCIPLINE1 = "discipline1";
    public static final String DISCIPLINE2 = "discipline2";
    public static final String DISCIPLINE3 = "discipline3";
    public static final String DEPARTMENT = "department";
    public static final String ORGANIZATION = "organization";
    public static final String EMAIL = "email";
    public static final String DISPLAYNAME = "displayName";
    public static final String SURNAME = "surname";
    public static final String PREFIXES = "prefixes";
    public static final String INITIALS = "initials";
    public static final String TITLE = "title";
    public static final String USER_ID = "userId";
    public static final String STATE = "state";
    public static final String ROLES = "roles";
    public static final String GROUP_IDS = "groupIds";
    public static final String DISPLAYROLES = "displayRoles";
    public static final String DISPLAYGROUPS = "displayGroups";
    public static final String OPTS_FOR_NEWSLETTER = "optsForNewsletter";
    public static final String LOG_MY_ACTIONS = "logMyActions";
    public static final String DAI = "dai";

    public static final String[] ALL_PROPERTIES = {USER_ID, DISPLAYNAME, TITLE, INITIALS, PREFIXES, SURNAME, ORGANIZATION, DEPARTMENT, FUNCTION, ADDRESS,
            POSTALCODE, CITY, COUNTRY, EMAIL, TELEPHONE, STATE, DISPLAYROLES, DISPLAYGROUPS, OPTS_FOR_NEWSLETTER, LOG_MY_ACTIONS, DAI};

    /**
     * Minimum length of a user id.
     */
    public static final int MINIMUM_USER_ID_LENGTH = 5;

}
