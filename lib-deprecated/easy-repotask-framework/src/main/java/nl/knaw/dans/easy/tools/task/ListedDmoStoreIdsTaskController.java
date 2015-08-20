package nl.knaw.dans.easy.tools.task;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class ListedDmoStoreIdsTaskController extends AbstractTaskController {
    private String input;

    public ListedDmoStoreIdsTaskController(String input) {
        super();
        this.input = input;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        if (new File(input).isFile())
            processFile(joint);
        else
            processLine(joint, input);
    }

    private void processFile(JointMap joint) throws FatalTaskException {
        try {
            RandomAccessFile raf;
            raf = new RandomAccessFile(input, "r");
            try {
                String line;
                while (null != (line = raf.readLine()))
                    processLine(joint, line);
            }
            finally {
                raf.close();
                closeTasks();
            }
        }
        catch (IOException e) {
            log.error("Task {} failed to read {}: {}", getTaskName(), input, e.getMessage());
            throw new FatalTaskException(e, this);
        }
    }

    private void processLine(JointMap joint, String line) throws FatalTaskException {
        String[] items = line.split("(\\s|,)+");
        for (String item : items) {
            if (item != null && item.length() != 0) {
                joint.clearCycleState();
                joint.setDmoStoreId(new DmoStoreId(item));
                executeSteps(joint);
            }
        }
    }
}
