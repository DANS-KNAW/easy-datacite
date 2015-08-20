package nl.knaw.dans.easy.tools;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

/**
 * Executes a task in the configuration of a repository-system.
 * <p/>
 * In order to start up a new repository-system, a lot of configuration needs to be done. A lot of it can be automated. Each Task carries out a small step in
 * the configuration of the repository-system.
 * <p/>
 * A Task should be repeatable. Running the same Task over and over again
 * <ul>
 * <li>should leave the repository in a consistent state as far as this Task's responsibility stretches;</li>
 * <li>should not lead to doubling of resources;</li>
 * <li>if settings change between two consecutive runs, should undo previous changes and configure the repository according to the new settings.</li>
 * </ul>
 * 
 * @author ecco
 */
public interface Task {

    /**
     * @param joint
     *        JointMap for objects and states
     * @throws TaskException
     *         task ran into an exception; cycle of tasks can continue
     * @throws TaskCycleException
     *         task ran into an exception; cycle has to stop, other cycles can continue
     * @throws FatalTaskException
     *         task ran into an exception; process should be halted
     */
    void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException;

    void close() throws TaskException, TaskCycleException, FatalTaskException;

    /**
     * Called when one of the Tasks of a {@link TaskRunner} dispatches an Event (at least if the application is configured to use the TaskRunnerReporter as the
     * Reporter for nl.knaw.dans.common.lang.log.RL).
     * <p/>
     * 
     * @param event
     */
    void onEvent(Event event);

    String getTaskName();

    boolean needsAuthentication();

}
