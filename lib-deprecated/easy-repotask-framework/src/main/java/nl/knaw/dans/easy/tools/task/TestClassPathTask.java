package nl.knaw.dans.easy.tools.task;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class TestClassPathTask extends AbstractTask {

    List<String> classes;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        try {
            for (String className : classes) {
                Class.forName(className);
            }
        }
        catch (ClassNotFoundException e) {
            RL.error(new Event(getTaskName(), e.getMessage()));
        }
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
}
