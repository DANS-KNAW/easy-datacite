package nl.knaw.dans.easy.business.aspect;

import nl.knaw.dans.easy.domain.worker.AbstractWorker;
import nl.knaw.dans.easy.domain.worker.WorkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect WorkerEvents
{

    private static final Logger logger = LoggerFactory.getLogger(WorkerEvents.class);

    pointcut work(AbstractWorker worker) : 
        execution(* work*(..)) && target(worker);

    /**
     * Gives advice around methods listed in pointcut {@link #work(AbstractWorker)}.
     * <p/>
     * Before the method runs, this advice calls {@link WorkListener#onWorkStart()} on all of the listeners registered
     * at the worker. If one of the listeners on the worker replies <code>true</code> on this event, the method will not
     * run and <code>null</code> will be returned.
     * <p/>
     * After the method has successfully run, this advice calls {@link WorkListener#onWorkEnd()} on all of the listeners
     * registered at the worker.
     * 
     * @param worker
     *        the worker to advice
     * @return the result of the method or <code>null</code> if the method was canceled by one of the worker's listeners
     */
    Object around(AbstractWorker worker) : work(worker)
    {
        Object result = null;
        boolean canceled = informOnWorkStart(worker);
        if (canceled)
        {
            logger.debug("Worker " + worker.getClass().getName() + " was canceled before work started " + "\n\tat ("
                    + thisJoinPointStaticPart.getSourceLocation().getFileName() + ":"
                    + thisJoinPointStaticPart.getSourceLocation().getLine() + ")");
        }
        else
        {
            result = proceed(worker);
            informOnWorkEnd(worker);
        }
        return result;
    }

    /**
     * Gives advice after methods listed in pointcut {@link #work(AbstractWorker)} throw an exception.
     * <p/>
     * If the method throws an exception, this advice calls {@link WorkListener#onException(Throwable)} on all of the
     * listeners registered at the worker.
     * 
     * @param worker the worker to advice
     * @param se the thrown exception
     */
    after(AbstractWorker worker) throwing(Throwable se): work(worker)
    {
        for (WorkListener listener : worker.getListeners())
        {
            listener.onException(se);
        }
    }

    private static boolean informOnWorkStart(AbstractWorker worker)
    {
        boolean canceled = false;
        for (WorkListener listener : worker.getListeners())
        {
            canceled |= listener.onWorkStart();
        }
        return canceled;
    }

    private static void informOnWorkEnd(AbstractWorker worker)
    {
        for (WorkListener listener : worker.getListeners())
        {
            listener.onWorkEnd();
        }
    }

}
