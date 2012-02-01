package nl.knaw.dans.easy.domain.federation;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.user.RepoEntry;

@LdapObject(objectClasses = {"dansFedIdMap"})
public class FederativeUserIdMap implements RepoEntry
{
    private static final long serialVersionUID = 8717750427759928766L;

    @LdapAttribute(id = "fedUserId")
    private String fedUserId;
    
    @LdapAttribute(id = "dansUserId")
    private String dansUserId;

    public FederativeUserIdMap()
    {
       // needed for unmarshalling
    }

    public FederativeUserIdMap(String fedUserId, String dansUserId)
    {
        this.fedUserId = fedUserId;
        this.dansUserId = dansUserId;
    }

    @Override
    public String getId()
    {
        return fedUserId;
    }

    public String getFedUserId()
    {
        return fedUserId;
    }

    public String getDansUserId()
    {
        return dansUserId;
    }

}
