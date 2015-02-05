package nl.knaw.dans.easy.security.authz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.exceptions.AnonymousUserException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.GroupImpl;
import nl.knaw.dans.easy.security.And;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.DatasetStateCheck;
import nl.knaw.dans.easy.security.EmbargoFreeCheck;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.IsDepositorOfDatasetCheck;
import nl.knaw.dans.easy.security.Or;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDatasetAutzStrategy implements AuthzStrategy {
    private static final long serialVersionUID = -2767729708349822038L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDatasetAutzStrategy.class);

    public static final String SM_YES = "dataset.authzstrategy.sm.yes";
    public static final String SM_LOGIN = "dataset.authzstrategy.sm.login";
    public static final String SM_JOIN_GROUP = "dataset.authzstrategy.sm.joinGroup";
    public static final String SM_ASK_PERMISSION = "dataset.authzstrategy.sm.askPermission";
    public static final String SM_INACCESSIBLE = "dataset.authzstrategy.sm.inaccessible";
    public static final String SM_EMBARGO = "dataset.authzstrategy.sm.embargo";
    public static final String SM_UNKNOWN = "dataset.authzstrategy.sm.unknown";

    public static final String MSG_LOGIN = "explorer.message.login";
    public static final String MSG_EMBARGO = "explorer.message.embargo";
    public static final String MSG_PERMISSION = "explorer.message.permission";
    public static final String MSG_PERMISSION_SUBMITTED = "explorer.message.permission.submitted";
    public static final String MSG_PERMISSION_RETURNED = "explorer.message.permission.returned";
    public static final String MSG_PERMISSION_DENIED = "explorer.message.permission.denied";
    public static final String MSG_PERMISSION_GRANTED = "explorer.message.permission.granted";
    public static final String MSG_PERMISSION_BUTTON = "explorer.message.permission.button";
    public static final String MSG_PERMISSION_EMBARGO = "explorer.message.permission.embargo";
    public static final String MSG_PERMISSION_LOGIN = "explorer.message.permission.login";
    public static final String MSG_GROUP = "explorer.message.group";
    public static final String MSG_OTHER = "explorer.message.other";
    public static final String MSG_NO_FILES = "explorer.message.noFiles";
    public static final String MSG_DEPOSITOR = "explorer.message.depositor";
    public static final String MSG_DEPOSITOR_DRAFT = "explorer.message.depositor.draft";

    protected static final int NOT_EVALUATED = -1;
    protected static final int NOT_ALLOWED = 0;
    protected static final int ALLOWED = 1;

    private static SecurityOfficer RULE_FOR_DISCOVERY;
    private static SecurityOfficer RULE_FOR_READING;

    protected static SecurityOfficer getRuleForDiscovery() {
        if (RULE_FOR_DISCOVERY == null) {
            SecurityOfficer depositorCanSee = new And(new IsDepositorOfDatasetCheck(), //
                    new DatasetStateCheck(DatasetState.DRAFT, DatasetState.SUBMITTED, DatasetState.PUBLISHED, DatasetState.MAINTENANCE));

            RULE_FOR_DISCOVERY = new Or(new DatasetStateCheck(DatasetState.PUBLISHED), //
                    depositorCanSee, //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN));
        }
        return RULE_FOR_DISCOVERY;
    }

    protected static SecurityOfficer getRuleForReading() {
        if (RULE_FOR_READING == null) {
            SecurityOfficer depositorCanRead = new And(new IsDepositorOfDatasetCheck(), //
                    new DatasetStateCheck(DatasetState.SUBMITTED, DatasetState.PUBLISHED, DatasetState.MAINTENANCE));

            SecurityOfficer publishedAndNotUnderEmbargo = new And( //
                    new DatasetStateCheck(DatasetState.PUBLISHED), //
                    new EmbargoFreeCheck());

            RULE_FOR_READING = new Or(publishedAndNotUnderEmbargo, //
                    depositorCanRead, //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN));
        }
        return RULE_FOR_READING;
    }

    private EasyUser easyUser;
    private Dataset dataset;

    private ContextParameters ctxParameters;
    private int discoverRuleEvaluation = NOT_EVALUATED;
    private int readRuleEvaluation = NOT_EVALUATED;
    private int userProfile = NOT_EVALUATED;

    private TriState discoverable;
    private TriState readable;

    protected AbstractDatasetAutzStrategy() {

    }

    protected AbstractDatasetAutzStrategy(User user, Object... contextObjects) {
        if (user instanceof EasyUser) {
            easyUser = (EasyUser) user;
        }
        for (Object ctxObject : contextObjects) {
            if (ctxObject instanceof Dataset) {
                dataset = (Dataset) ctxObject;
            }
        }
    }

    protected void checkAttributes() {
        if (easyUser == null)
            throw new IllegalArgumentException("Insufficient parameters: no easyUser");
        if (dataset == null)
            throw new IllegalArgumentException("Insufficient parameters: no dataset");
    }

    public EasyUser getEasyUser() {
        return easyUser;
    }

    public Dataset getDataset() {
        return dataset;
    }

    protected void clone(AbstractDatasetAutzStrategy sameStrategy) {
        sameStrategy.dataset = dataset;
        sameStrategy.easyUser = easyUser;

        sameStrategy.discoverRuleEvaluation = getDiscoverRuleEvaluation();
        sameStrategy.readRuleEvaluation = getReadRuleEvaluation();
        sameStrategy.ctxParameters = getContextParameters();
        sameStrategy.userProfile = getUserProfile();
    }

    protected ContextParameters getContextParameters() {
        if (ctxParameters == null) {
            ctxParameters = new ContextParameters(easyUser, dataset);
        }
        return ctxParameters;
    }

    protected int getDiscoverRuleEvaluation() {
        if (discoverRuleEvaluation == NOT_EVALUATED) {
            discoverRuleEvaluation = getRuleForDiscovery().isEnableAllowed(getContextParameters()) ? ALLOWED : NOT_ALLOWED;
        }
        return discoverRuleEvaluation;
    }

    protected int getReadRuleEvaluation() {
        if (readRuleEvaluation == NOT_EVALUATED) {
            readRuleEvaluation = getRuleForReading().isEnableAllowed(getContextParameters()) ? ALLOWED : NOT_ALLOWED;
        }
        return readRuleEvaluation;
    }

    /**
     * UserProfile addapted on key-abstraction {@link AccessCategory}:
     * <ul>
     * <li>1 ANONYMOUS_ACCESS,</li>
     * <li>2 OPEN_ACCESS,</li>
     * <li>4 GROUP_ACCESS,</li>
     * <li>8 REQUEST_PERMISSION,</li>
     * <li>16 ACCESS_ELSEWHERE,</li>
     * <li>32 NO_ACCESS</li>
     * <li>64 FREELY_AVAILABLE</li>
     * </ul>
     * 
     * @return
     */
    protected int getUserProfile() {
        if (userProfile == NOT_EVALUATED) {
            if (easyUser.isAnonymous()) {
                userProfile = AccessCategory.MASK_ANONYMOUS; // FREELY_AVAILABLE + ANONYMOUS_ACCESS
            } else if (easyUser.hasRole(Role.ARCHIVIST, Role.ADMIN) || dataset.hasDepositor(easyUser)) {
                userProfile = AccessCategory.MASK_ALL; // ALL
            } else
            // known user
            {
                userProfile = AccessCategory.MASK_KNOWN; // FREELY_AVAILABLE + ANONYMOUS_ACCESS +
                                                         // OPEN_ACCESS
                if (dataset.isGroupAccessGrantedTo(easyUser)) {
                    userProfile |= AccessCategory.SINGLE_GROUP_ACCESS; // + GROUP_ACCESS
                }
                if (dataset.isPermissionGrantedTo(easyUser)) {
                    userProfile |= AccessCategory.SINGLE_REQUEST_PERMISSION; // + REQUEST_PERMISSION
                }
            }

            // block not-active users
            if (!easyUser.isActive()) {
                userProfile = 0;
            }
        }
        return userProfile;
    }

    protected abstract int getResourceDiscoveryProfile();

    protected abstract int getResourceReadProfile();

    protected abstract boolean canAllBeRead();

    @Override
    public TriState canChildrenBeDiscovered() {
        if (discoverable == null) {
            discoverable = getDiscoverRuleEvaluation() == NOT_ALLOWED ? TriState.NONE : andProfiles(getUserProfile(), getResourceDiscoveryProfile());
        }
        return discoverable;
    }

    @Override
    public String explainCanChildrenBeDiscovered() {
        StringBuilder explanation = new StringBuilder();
        explanation.append(getRuleForDiscovery().explainEnableAllowed(getContextParameters())) //
                .append("\n") //
                .append("UserProfile=") //
                .append(getUserProfile()) //
                .append(" ResourceProfile=") //
                .append(getResourceDiscoveryProfile()) //
                .append("\nCan be discovered=") //
                .append(canChildrenBeDiscovered().toString());
        return explanation.toString();
    }

    @Override
    public TriState canChildrenBeRead() {
        if (readable == null) {
            readable = getReadRuleEvaluation() == NOT_ALLOWED ? TriState.NONE : andProfiles(getUserProfile(), getResourceReadProfile());
        }
        return readable;
    }

    @Override
    public boolean canBeDiscovered() {
        return getDiscoverRuleEvaluation() == ALLOWED;
    }

    @Override
    public boolean canBeRead() {
        return getReadRuleEvaluation() == ALLOWED;
    }

    @Override
    public List<AuthzMessage> getReadMessages() {
        List<AuthzMessage> messages = new ArrayList<AuthzMessage>();

        if (easyUser.hasRole(Role.ARCHIVIST)) {
            return messages;
        }

        if (dataset.hasDepositor(easyUser)) {
            if (dataset.getAdministrativeState().equals(DatasetState.DRAFT)) {
                // Note: you are able to view all files, because you are the depositor of this dataset.
                // You are however not allowed to access any file, to prevent abuse of this system for
                // file storage.
                messages.add(new AuthzMessage(MSG_DEPOSITOR_DRAFT));
            } else {
                // Note: you are able to view/access all files, because you are the depositor of this
                // dataset.
                messages.add(new AuthzMessage(MSG_DEPOSITOR));
            }
            return messages;
        }

        boolean datasetHasVisibleItems = false;
        boolean datasetHasPermissionRestrictedItems = false;
        boolean datasetHasGroupRestrictedItems = false;
        try {
            FileStoreAccess fileStoreAccess = Data.getFileStoreAccess();
            DmoStoreId datasetStoreId = dataset.getDmoStoreId();
            boolean userHasPermission = dataset.isPermissionGrantedTo(easyUser);
            boolean userHasGroupAccess = dataset.isGroupAccessGrantedTo(easyUser);

            datasetHasVisibleItems = fileStoreAccess.hasVisibleFiles(datasetStoreId, !easyUser.isAnonymous(), userHasGroupAccess, userHasPermission);
            // what is the use of examining the following when the previous is false?
            // the rest of the method used not bother about that, but now it are expensive queries
            datasetHasPermissionRestrictedItems = fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class, AccessibleTo.RESTRICTED_REQUEST);
            datasetHasGroupRestrictedItems = fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class, AccessibleTo.RESTRICTED_GROUP);
        }
        catch (StoreAccessException e1) {
            LOGGER.error("can't establish file permissions", e1);
        }

        if (!datasetHasVisibleItems) {
            // No visible files.
            messages.add(new AuthzMessage(MSG_NO_FILES));
        }

        if (loginNeeded()) {
            // You need to log in to be able to view/access (some of) the files. [Log In]
            messages.add(new AuthzMessage(MSG_LOGIN));
        }

        if (dataset.isUnderEmbargo()) {
            // The files of this dataset will be accessible from DD-MM-YYYY.
            messages.add(new AuthzMessage(MSG_EMBARGO));
        }

        if (datasetHasPermissionRestrictedItems) {
            // You need to have special permission to be able to access (some of) the files.
            messages.add(new AuthzMessage(MSG_PERMISSION));

            try {
                State permissionState = dataset.getPermissionSequenceList().getPermissionReply(easyUser.getId()).getState();
                if (permissionState.equals(State.Submitted)) {
                    // Permission status: submitted <status date> [View request]
                    messages.add(new AuthzMessage(MSG_PERMISSION_SUBMITTED));
                } else if (permissionState.equals(State.Returned)) {
                    // Permission status: returned <status date> [Review request]
                    messages.add(new AuthzMessage(MSG_PERMISSION_RETURNED));
                } else if (permissionState.equals(State.Granted)) {
                    // Permission status: granted <status date> [View request]
                    messages.add(new AuthzMessage(MSG_PERMISSION_GRANTED));
                } else if (permissionState.equals(State.Denied)) {
                    // Permission status: denied <status date> [View request]
                    messages.add(new AuthzMessage(MSG_PERMISSION_DENIED));
                }
            }
            catch (IllegalArgumentException e) {
                if (!dataset.isUnderEmbargo()) {
                    // [Request permission]
                    messages.add(new AuthzMessage(MSG_PERMISSION_BUTTON));
                } else {
                    // Request permission after the date the files become accessible.
                    messages.add(new AuthzMessage(MSG_PERMISSION_EMBARGO));
                }
            }
            catch (AnonymousUserException e) {
                if (dataset.isUnderEmbargo()) {
                    // Request permission after the date the files become accessible.
                    messages.add(new AuthzMessage(MSG_PERMISSION_EMBARGO));
                } else {
                    // You can request permission after logging in.
                    messages.add(new AuthzMessage(MSG_PERMISSION_LOGIN));
                }
            }

        }

        if (datasetHasGroupRestrictedItems && !easyUser.isMemberOf(new GroupImpl(Group.ID_ARCHEOLOGY))) {
            // You need to be a '<group>' group member to be able to access (some of) the files.
            // Please contact <group e-mail address>.
            messages.add(new AuthzMessage(MSG_GROUP));
        }

        if (dataset.getAccessCategory().equals(AccessCategory.NO_ACCESS) || dataset.getAccessCategory().equals(AccessCategory.ACCESS_ELSEWHERE)) {
            // Note: (some of) the files are not available via Easy. They may be accessible elsewhere
            // (please refer to the 'relation' field in the description) or accessible in another
            // way(please contact info@…)
            messages.add(new AuthzMessage(MSG_OTHER));
        }

        return messages;
    }

    @Override
    public AuthzMessage getSingleReadMessage() {
        if (canAllBeRead()) {
            return new AuthzMessage(SM_YES);
        } else if (easyUser.isAnonymous() && hasOpenAccessForRegisterdUsers()) {
            return new AuthzMessage(SM_LOGIN);
        } else if (hasGroupAccess()) {
            return new AuthzMessage(SM_JOIN_GROUP);
        } else if (hasPermissionAccess()) {
            return new AuthzMessage(SM_ASK_PERMISSION);
        } else if (isInAccessible()) {
            return new AuthzMessage(SM_INACCESSIBLE);
        } else if (dataset.isUnderEmbargo()) {
            return new AuthzMessage(SM_EMBARGO, dataset.getDateAvailable());
        }
        return new AuthzMessage(SM_UNKNOWN);
    }

    private boolean loginNeeded() {

        return easyUser.isAnonymous() && (containsNonAnonymousAccessFiles() || containsNonAnonymousVisibilityFiles());
    }
    
    private boolean containsNonAnonymousAccessFiles() {
        
        try {
            Set<AccessibleTo> accessibleToValues = Data.getFileStoreAccess().getItemVoAccessibilities(
                    Data.getFileStoreAccess().getRootFolder(dataset.getDmoStoreId()));
            accessibleToValues.remove(AccessibleTo.ANONYMOUS);
            return !accessibleToValues.isEmpty();
        }
        catch (StoreAccessException e) {
            LOGGER.error("can't establish file access permissions", e);
        }
        
        return false;
    }
    
    private boolean containsNonAnonymousVisibilityFiles() {
        
        try {
            Set<VisibleTo> visibleToValues = Data.getFileStoreAccess().getItemVoVisibilities(
                    Data.getFileStoreAccess().getRootFolder(dataset.getDmoStoreId()));
            visibleToValues.remove(VisibleTo.ANONYMOUS);
            return !visibleToValues.isEmpty();
        }
        catch (StoreAccessException e) {
            LOGGER.error("can't establish file visibility permissions", e);
        }
        
        return false;
    }
    
    private boolean hasOpenAccessForRegisterdUsers() {
        return (AccessCategory.SINGLE_OPEN_ACCESS_FOR_REGISTERED_USERS & getResourceReadProfile()) > 0;
    }

    private boolean hasGroupAccess() {
        return (AccessCategory.SINGLE_GROUP_ACCESS & getResourceReadProfile()) > 0;
    }

    private boolean hasPermissionAccess() {
        return (AccessCategory.SINGLE_REQUEST_PERMISSION & getResourceReadProfile()) > 0;
    }

    private boolean isInAccessible() {
        return (AccessCategory.MASK_INACCESSIBLE & getResourceReadProfile()) > 0;
    }

    private static TriState andProfiles(int profile, int resourceProfile) {
        TriState state = TriState.NONE;
        int andResult = profile & resourceProfile;
        if (andResult > 0) {
            state = andResult < resourceProfile ? TriState.SOME : TriState.ALL;
        }
        return state;
    }

    protected static boolean profileMatches(int profile, int resourceProfile) {
        int andResult = profile & resourceProfile;
        return andResult == resourceProfile;
    }

}
