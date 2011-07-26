package nl.knaw.dans.easy.business.bean;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.springframework.beans.FatalBeanException;

public class MigrationArchivistCreator
{
    
    protected static void createMigrationArchivist()
    {
        try
        {
            if (!Data.getUserRepo().exists("migration"))
            {
                EasyUser migration = new EasyUserImpl("migration");
                migration.addRole(Role.USER);
                migration.addRole(Role.ARCHIVIST);
                migration.addRole(Role.ADMIN);
                migration.setAcceptConditionsOfUse(true);
                migration.setDepartment("SDG");
                migration.setEmail("henk.van.den.berg@dans.knaw.nl");
                migration.setFirstname("Easy");
                migration.setInitials("II");
                migration.setOrganization("DANS");
                migration.setPassword("migration");
                migration.setState(State.ACTIVE);
                migration.setSurname("Migration");
                Data.getUserRepo().add(migration);
            }
        }
        catch (RepositoryException e)
        {
            throw new FatalBeanException("Could not create migration archivist: ", e);
        }
    }

}
