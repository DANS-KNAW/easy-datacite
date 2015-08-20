package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class SaveDatasetTask extends AbstractTask {
    private int saveCounter;
    private EasyUser user;

    private boolean enabled = true;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        if (!joint.isCycleSubjectDirty())
            return;
        if (!joint.isFitForSave())
            return;
        if (!isEnabled()) {
            RL.warn(new Event(getTaskName(), "Dry run, changes are not saved"));
            return;
        }
        saveCounter++;
        saveDataset(joint);
    }

    private void saveDataset(JointMap joint) throws FatalTaskException {
        Dataset dataset = joint.getDataset();
        EasyUnitOfWork uow = new EasyUnitOfWork(getUser());
        try {
            uow.attach(dataset);
            uow.commit();
            RL.info(new Event(getTaskName(), "Saved " + saveCounter));
        }
        catch (RepositoryException e) {
            RL.error(new Event(getTaskName(), e, "Could not save"));
            throw new FatalTaskException(dataset.getStoreId(), e, this);
        }
        catch (UnitOfWorkInterruptException e) {
            RL.error(new Event(getTaskName(), e, "Could not save"));
            throw new FatalTaskException(dataset.getStoreId(), e, this);
        }

    }

    private EasyUser getUser() {
        if (user == null) {
            user = new EasyUserImpl() {

                private static final long serialVersionUID = -66924895541046244L;

                @Override
                public String getId() {
                    return "tools-admin";
                }

                @Override
                public String getDisplayName() {
                    return "Admin of Tools";
                }

            };
        }
        return user;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
