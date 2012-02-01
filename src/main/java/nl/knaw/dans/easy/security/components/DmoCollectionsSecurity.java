package nl.knaw.dans.easy.security.components;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.security.SecurityAgent;
import nl.knaw.dans.i.security.annotations.SecuredOperationUtil;

public class DmoCollectionsSecurity
{
    
    public List<SecurityAgent> getSecurityAgents()
    {
        List<SecurityAgent> agents = new ArrayList<SecurityAgent>();
        
        for (String securityId : SecuredOperationUtil.getDeclaredSecurityIds(CollectionManager.class))
        {
            
        }
        return agents;
    }

}
