/**
 *
 */
package nl.knaw.dans.easy.domain.model.user;

import java.util.Collection;
import java.util.Set;

import nl.knaw.dans.common.lang.user.User;

/**
 * Interface for the business user object.
 */
public interface EasyUser extends User {

    public enum Role {
        /**
         * Standard user role.
         */
        USER,
        /**
         * The user has archivist privileges.
         */
        ARCHIVIST,
        /**
         * The user has administrator privileges.
         */
        ADMIN

    }

    Set<Role> getRoles();

    void addRole(Role role);

    boolean removeRole(Role role);

    boolean hasRole(Role... roles);

    CreatorRole getCreatorRole();

    Set<Group> getGroups();

    void joinGroup(Group group);

    boolean leaveGroup(Group group);

    boolean isMemberOf(Group... groups);

    boolean isMemberOfGroup(Collection<String> groupIds);

    Set<String> getGroupIds();

    void addGroupId(String groupId);

    void synchronizeOn(EasyUser otherUser);

    boolean isLogMyActions();

    void setLogMyActions(boolean logMyActions);

    void setDiscipline1(String discipline1);

    String getDiscipline1();

    void setDiscipline2(String discipline2);

    String getDiscipline2();

    void setDiscipline3(String discipline3);

    String getDiscipline3();

    String getDai();

    void setDai(String dai);

    boolean hasAcceptedGeneralConditions();

    void setAcceptedGeneralConditions(boolean accepted);
}
