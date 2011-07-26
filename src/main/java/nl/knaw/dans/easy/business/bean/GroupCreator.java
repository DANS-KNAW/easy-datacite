package nl.knaw.dans.easy.business.bean;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.GroupImpl;

import org.springframework.beans.FatalBeanException;

public class GroupCreator
{
    
    protected static void createGroups()
    {
        Group archeology = new GroupImpl(Group.ID_ARCHEOLOGY);
        Group history = new GroupImpl(Group.ID_HISTORY);
        try
        {
            if (!Data.getGroupRepo().exists(archeology.getId()))
            {
                Data.getGroupRepo().add(archeology);
            }
            
            if (!Data.getGroupRepo().exists(history.getId()))
            {
                Data.getGroupRepo().add(history);
            }
        }
        catch (RepositoryException e)
        {
            throw new FatalBeanException("Could not create groups: ", e);
        }
    }

}
