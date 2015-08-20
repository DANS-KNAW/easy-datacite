package nl.knaw.dans.easy.task;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.task.InputTaskController.Converter;

public class InputConverter implements Converter {
    public static class Input {
        private DmoStoreId datasetStoreId;
        private String storeIDs;

        private Input(String line) {
            this.storeIDs = line;
            String[] storeIDs = line.split("\t");
            this.datasetStoreId = new DmoStoreId(storeIDs[0]);
        }

        public DmoStoreId getDatasetStoreId() {
            return datasetStoreId;
        }

        public String toString() {
            return storeIDs;
        }
    }

    @Override
    public Input convert(String line) throws TaskException, TaskCycleException, FatalTaskException {
        return new Input(line);
    }
}
