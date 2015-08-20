package nl.knaw.dans.easy.tools.task;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.util.Dialogue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Erase all entries from MigrationRepo (ldap) - I don't think you need this task.
 */
public class CleanMigrationRepoTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(CleanMigrationRepoTask.class);

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        MigrationRepo repo = Data.getMigrationRepo();
        boolean confirmed = Dialogue.confirm("This will erase all entries in the context " + repo.getContext() + "." + "\nDo you want to continue?");

        if (confirmed) {
            try {
                deleteAllEntries(repo);
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
        } else {
            logger.info("User aborted " + getTaskName());
        }
    }

    private void deleteAllEntries(MigrationRepo repo) throws RepositoryException {
        List<String> ids;
        while (!(ids = repo.findAllEntries(100)).isEmpty()) {
            for (String id : ids) {
                repo.delete(id);
            }
        }
    }

}
