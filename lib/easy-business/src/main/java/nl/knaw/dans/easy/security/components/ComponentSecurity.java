package nl.knaw.dans.easy.security.components;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.security.SecurityAgent;
import nl.knaw.dans.i.security.annotations.SecuredOperationUtil;

public class ComponentSecurity
{

    public List<SecurityAgent> getDmoCollectionsAgents()
    {
        List<SecurityAgent> agents = new ArrayList<SecurityAgent>();
        SecurityOfficer officer = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);
        for (String securityId : SecuredOperationUtil.getDeclaredSecurityIdsOnInterface(CollectionManager.class))
        {
            SecurityAgent agent = new SecurityOficerAdapter(securityId, officer);
            agents.add(agent);
        }
        return agents;
    }

}
