package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;

public class DatasetDepositorIdFilter implements DmoFilter<Dataset> {
    private Set<String> depositors;

    public DatasetDepositorIdFilter(String... depositors) {
        this.depositors = new HashSet<String>(Arrays.asList(depositors));
    }

    @Override
    public boolean accept(Dataset dataset) {
        return depositors.contains(dataset.getOwnerId());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(depositors.size()).append("]");
        sb.append(" <");
        for (String rightsHolder : depositors) {
            sb.append(rightsHolder);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
