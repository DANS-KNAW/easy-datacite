package nl.knaw.dans.easy.ebiu;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;

public abstract class AbstractTask implements Task {

    private boolean needsAuthentication;
    private boolean finalStepInCycle;

    /**
     * Does nothing, subclasses may override.
     */
    @Override
    public void onEvent(Event event) {}

    /**
     * Does nothing, subclasses may override.
     */
    @Override
    public void close() throws TaskException, TaskCycleException, FatalTaskException {}

    @Override
    public String getTaskName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean needsAuthentication() {
        return needsAuthentication;
    }

    public void setNeedsAuthentication(boolean needsAuthentication) {
        this.needsAuthentication = needsAuthentication;
    }

    public boolean isFinalStepInCycle() {
        return finalStepInCycle;
    }

    public void setFinalStepInCycle(boolean finalStepInCycle) {
        this.finalStepInCycle = finalStepInCycle;
    }

}
