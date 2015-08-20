package nl.knaw.dans.easy.tools.task;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.util.RepoUtil;

public class InputTaskController extends AbstractTaskController {
    private File input;
    private Converter converter;

    public static interface Converter {
        /**
         * Adds data to the JointMap from a line read from input file.
         * 
         * @param line
         * @throws TaskException
         * @throws TaskCycleException
         * @throws FatalTaskException
         * @return will be accessible by sub-tasks as JointMap.getInput(), should override toString()
         */
        public Object convert(String line) throws TaskException, TaskCycleException, FatalTaskException;
    }

    public InputTaskController(File input, Converter converter) {
        super();
        this.input = input;
        this.converter = converter;
        if (!input.isFile())
            throw new IllegalArgumentException(input.getAbsolutePath() + " is not a file");
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        try {
            RepoUtil.checkListenersActive();
        }
        catch (NoListenerException e) {
            throw new FatalTaskException("No searchEngine", e, this);
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(input, "r");
            String line;
            try {
                while (null != (line = raf.readLine())) {
                    joint.clearCycleState();
                    joint.setInput(converter.convert(line));
                    executeSteps(joint);
                }
            }
            finally {
                raf.close();
                closeTasks();
            }
        }
        catch (IOException e) {
            log.error("Task {}: failure reading {}: {}", getTaskName(), input, e.getMessage());
            throw new FatalTaskException(e, this);
        }
    }
}
