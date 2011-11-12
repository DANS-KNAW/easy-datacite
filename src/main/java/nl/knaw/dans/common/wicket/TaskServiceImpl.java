package nl.knaw.dans.common.wicket;

import java.util.concurrent.Executor;

import org.wicketstuff.progressbar.spring.TaskService;

/**
 * Should be registered as SESSION scoped bean to prevent memory leaks with
 * unfinished tasks.
 *
 */
public class TaskServiceImpl extends TaskService
{
    /**
     * ** NO PUBLIC CONSTRUCTOR ** 
     * 
     * Spring proxy instantiation needs a no-argument constructor.
     */
    public TaskServiceImpl()
    {
        super(null);
    }

    /**
     * ** NO PUBLIC CONSTRUCTOR ** 
     * 
     * Used by Spring framework.
     * 
     * @param executor
     *        the executor
     */
    public TaskServiceImpl(Executor executor)
    {
        super(executor);
    }

}
