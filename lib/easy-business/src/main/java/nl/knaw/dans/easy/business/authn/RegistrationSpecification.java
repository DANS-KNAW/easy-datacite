package nl.knaw.dans.easy.business.authn;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.authn.Registration.State;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegistrationSpecification {

    private static Logger logger = LoggerFactory.getLogger(RegistrationSpecification.class);

    protected RegistrationSpecification() {}

    public static boolean isSatisfiedBy(Registration registration) {
        boolean satisfied = hasSufficientData(registration) && hasUniqueID(registration) && hasAllowedUserID(registration);
        return satisfied;
    }

    protected static boolean hasSufficientData(Registration registration) {
        boolean sufficientData = true;
        EasyUser user = registration.getUser();
        if (StringUtils.isBlank(user.getId())) {
            sufficientData = false;
            registration.setState(State.UserIdCannotBeBlank);
        }
        if (StringUtils.isBlank(user.getInitials())) {
            sufficientData = false;
            registration.setState(State.InitialsCannotBeBlank);
        }
        if (StringUtils.isBlank(user.getSurname())) {
            sufficientData = false;
            registration.setState(State.SurnameCannotBeBlank);
        }
        if (StringUtils.isBlank(user.getPassword())) {
            sufficientData = false;
            registration.setState(State.PasswordCannotBeBlank);
        }
        if (StringUtils.isBlank(user.getEmail())) {
            sufficientData = false;
            registration.setState(State.EmailCannotBeBlank);
        }

        return sufficientData;
    }

    protected static boolean hasUniqueID(Registration registration) {
        boolean hasUniqueId = false;
        String userId = registration.getUser().getId();
        try {
            if (!Data.getUserRepo().exists(userId)) {
                hasUniqueId = true;
            } else {
                registration.setState(State.UserIdNotUnique);
            }
        }
        catch (RepositoryException e) {
            logger.error("Could not verify if userId is unique: ", e);
            registration.setState(State.SystemError, e);
        }
        return hasUniqueId;
    }

    protected static boolean hasAllowedUserID(Registration registration) {
        // users cannot have a permissionsequence state as a user id. A user with that
        // id would be able to see all permission requests of that state through the way
        // the searchengine is used. A user with that id would also disrupt the system
        // if he or she would request permission for a dataset.
        // see EasyDatasetSB.permissionStatusList
        for (PermissionSequence.State state : PermissionSequence.State.values()) {
            if (registration.getUserId().equalsIgnoreCase(state.toString()))
                return false;
        }
        return true;
    }
}
