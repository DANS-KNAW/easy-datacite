package nl.knaw.dans.easy.domain.worker;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public abstract class AbstractWorker
{

    private final UnitOfWork unitOfWork;
    private final List<WorkListener> listeners = new ArrayList<WorkListener>();

    public AbstractWorker(EasyUser sessionUser)
    {
        this.unitOfWork = new EasyUnitOfWork(sessionUser);
    }

    public AbstractWorker(UnitOfWork uow)
    {
        this.unitOfWork = uow;
    }

    public void addWorkListener(WorkListener listener)
    {
        listeners.add(listener);
        getUnitOfWork().addListener(listener);
    }

    public void addWorkListeners(WorkListener... workListeners)
    {
        for (WorkListener listener : workListeners)
        {
            addWorkListener(listener);
        }
    }

    public boolean removeWorkListener(WorkListener listener)
    {
        getUnitOfWork().removeListener(listener);
        return listeners.remove(listener);
    }
    
    public List<WorkListener> getListeners()
    {
        return listeners;
    }
    
    public void informListeners(Throwable t)
    {
        for (WorkListener listener : getListeners())
        {
            listener.onException(t);
        }
    }

    public UnitOfWork getUnitOfWork()
    {
        return unitOfWork;
    }
}
