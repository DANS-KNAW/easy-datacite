package nl.knaw.dans.easy.web;

import org.wicketstuff.progressbar.spring.ITaskService;

/**
 * Service for session scoped beans.
 */
public class SessionScopeService
{

    private static SessionScopeService INSTANCE;

    private ITaskService               taskService;

    private SessionScopeService()
    {
        INSTANCE = this;
    }

    /**
     * Used by spring framework to set a reference to the ITaskService.
     * 
     * @param taskService
     */
    public void setTaskService(ITaskService taskService)
    {
        this.taskService = taskService;
    }

    /**
     * Get a session scoped proxy to the ITaskService.
     * 
     * @return session scoped proxy to the ITaskService
     */
    public static ITaskService getTaskService()
    {
        return INSTANCE.taskService;
    }

}
