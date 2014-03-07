package nl.knaw.dans.easy.domain.user;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.easy.domain.model.user.Group;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@LdapObject(objectClasses = {"easyGroup", "organizationalUnit"})
public class GroupImpl implements Group
{

    private static final long serialVersionUID = 1012313686311702776L;

    @LdapAttribute(id = "ou", required = true)
    private String groupId;

    @LdapAttribute(id = "description")
    private String description;

    @LdapAttribute(id = "dansState")
    private State state = State.ACTIVE;

    /**
     * NOT PART OF PUBLIC API - only used for deserialization.
     */
    public GroupImpl()
    {

    }

    public GroupImpl(String groupId)
    {
        this.groupId = groupId;
    }

    public String getId()
    {
        return groupId;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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
        boolean equals = false;
        if (obj != null)
        {
            if (obj == this)
            {
                equals = true;
            }
            else
            {
                if (obj.getClass() == this.getClass())
                {
                    final GroupImpl otherGroup = (GroupImpl) obj;
                    equals = new EqualsBuilder().append(this.groupId, otherGroup.groupId).append(this.state, otherGroup.state)
                            .append(this.description, otherGroup.description).isEquals();
                }
            }
        }

        return equals;
    }

    /**
     * Return hashCode.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(1, 3).append(this.groupId).append(this.state).toHashCode();
    }

    @Override
    public String toString()
    {
        return getDescription();
    }

}
