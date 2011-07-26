package nl.knaw.dans.easy.domain.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.user.UserImpl;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;
import nl.knaw.dans.easy.util.StringUtil;

/**
 * Representation for the user of Easy.
 */
@LdapObject(objectClasses = {"easyUser", "dansUser", "inetOrgPerson", "organizationalPerson", "person"})
public class EasyUserImpl extends UserImpl implements EasyUser
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2895529338320356222L;
    
    @LdapAttribute(id = "easyLogMyActions")
    private boolean logMyActions = true;

    private Set<Role> roles = new HashSet<Role>();
    
    private Set<String> groupIds = new HashSet<String>();
    
    @LdapAttribute(id = "easyDai")
    private String dai;
    
    @LdapAttribute(id = "easyDiscipline1")
    private String discipline1;
    
    @LdapAttribute(id = "easyDiscipline2")
    private String discipline2;
    
    @LdapAttribute(id = "easyDiscipline3")
    private String discipline3;
    
    @LdapAttribute(id = "easyHasConfirmedGeneralConditions")
    private boolean acceptedGeneralConditions;
    
    private CreatorRole creatorRole;

    /**
     * Default constructor.
     */
    public EasyUserImpl()
    {
        super();
    }

    public EasyUserImpl(String userId)
    {
        super(userId);
    }

    /**
     * Constructor with values.
     *
     * @param userId
     *        UserImpl id.
     * @param email
     *        email address.
     */
    public EasyUserImpl(final String userId, final String email)
    {
        super(userId, email);
    }

    public EasyUserImpl(final Role...roles )
    {
        for (Role role : roles)
        {
            addRole(role);
        }
    }

    @LdapAttribute(id = "easyRoles")
    public Set<Role> getRoles()
    {
        return roles;
    }
    
    /**
     * Used by wicket.
     * @param roles
     */
    public void setRoles(Set<Role> roles)
    {
        this.roles = roles;
    }
    
    @LdapAttribute(id = "easyRoles")
    public void addRole(Role role)
    {
        roles.add(role);
    }

    public boolean removeRole(Role role)
    {
        return roles.remove(role);
    }

    public boolean hasRole(Role... roles)
    {
        boolean hasRole = false;
        if (roles != null)
        {
            for (int i = 0; i < roles.length && !hasRole; i++)
            {
                hasRole = this.roles.contains(roles[i]);
            }
        }
        return hasRole;
    }
    
    public Set<Group> getGroups()
    {
        return new LinkedHashSet<Group>(RepoAccess.getDelegator().getGroups(this));
    }
    
    public void joinGroup(Group group)
    {
        groupIds.add(group.getId());
    }
    
    public boolean leaveGroup(Group group)
    {
        return removeGroupId(group.getId());
    }
    
    public boolean isMemberOf(Group... groups)
    {
        boolean isMemberOf = false;
        if (groups != null)
        {
            for (int i = 0; i < groups.length && !isMemberOf; i++)
            {
                isMemberOf = groupIds.contains(groups[i].getId());
            }
        }
        return isMemberOf;
    }
    
    public boolean isMemberOfGroup(Collection<String> groupIds)
    {
        boolean isMemberOf = false;
        Iterator<String> iter = groupIds.iterator();
        while (iter.hasNext() && !isMemberOf)
        {
            isMemberOf = this.groupIds.contains(iter.next());
        }
        return isMemberOf;
    }
    
    @LdapAttribute(id = "easyGroups")
    public Set<String> getGroupIds()
    {
        return groupIds;
    }
    
    /**
     * Used by wicket.
     * @param groupIds
     */
    public void setGroupIds(Set<String> groupIds)
    {
        this.groupIds = groupIds;
    }
    
    @LdapAttribute(id = "easyGroups")
    public void addGroupId(String groupId)
    {
        groupIds.add(groupId);
    }
    
    public boolean removeGroupId(String groupId)
    {
        return groupIds.remove(groupId);
    }
    
    public CreatorRole getCreatorRole()
    {
        if (creatorRole == null)
        {
            if (hasRole(Role.ARCHIVIST, Role.ADMIN))
            {
                creatorRole = CreatorRole.ARCHIVIST;
            }
            else
            {
                creatorRole = CreatorRole.DEPOSITOR;
            }
        }
        return creatorRole;
    }

    public String getDisplayRoles()
    {
        return StringUtil.commaSeparatedList(roles);
    }
    
    public String getDisplayGroups()
    {
        return StringUtil.commaSeparatedList(groupIds);
    }


    public void synchronizeOn(EasyUser otherUser)
    {
        super.synchronizeOn(otherUser);
        setGroupIds(otherUser.getGroupIds());
        setRoles(otherUser.getRoles());
    }

    /**
     * String representation.
     *
     * @return string representation
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    /**
     * Test if object is equal.
     *
     * @param obj
     *        object to test
     * @return true if object is equal.
     */
    @Override
    public boolean equals(final Object obj)
    {
        return super.equals(obj);
    }

    public boolean rolesAreEqual(EasyUserImpl otherUser)
    {
        boolean equal = roles.size() == otherUser.roles.size();
        if (equal)
        {
            for (Role role : roles)
            {
                equal &= otherUser.hasRole(role);
            }
        }
        return equal;
    }
    
    public boolean groupsAreEqual(EasyUserImpl otherUser)
    {
        boolean equal = groupIds.size() == otherUser.groupIds.size();
        if (equal)
        {
            for (String groupId : groupIds)
            {
                equal &= otherUser.groupIds.contains(groupId);
            }
        }
        return equal;
    }

    /**
     * Return hashCode.
     *
     * @return hashcode
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

	public boolean isAnonymous()
	{
		return false;
	}

    @Override
    public boolean isLogMyActions()
    {
        return logMyActions;
    }

    @Override
    public void setLogMyActions(boolean logMyActions)
    {
        this.logMyActions = logMyActions;
    }


    @Override
    public String getDai()
    {
        return dai;
    }

    @Override
    public void setDai(String dai)
    {
        this.dai = dai;
    }

    @Override
    public void setDiscipline1(String discipline1)
    {
        this.discipline1 = discipline1;
    }

    @Override
    public String getDiscipline1()
    {
        return discipline1;
    }

    @Override
    public void setDiscipline2(String discipline2)
    {
        this.discipline2 = discipline2;
    }

    @Override
    public String getDiscipline2()
    {
        return discipline2;
    }

    @Override
    public void setDiscipline3(String discipline3)
    {
        this.discipline3 = discipline3;
    }

    @Override
    public String getDiscipline3()
    {
        return discipline3;
    }

    @Override
	public boolean hasAcceptedGeneralConditions() {
		return acceptedGeneralConditions;
	}

    @Override
	public void setAcceptedGeneralConditions(boolean acceptedGeneralConditions) {
		this.acceptedGeneralConditions = acceptedGeneralConditions;
	}
    
    
}
