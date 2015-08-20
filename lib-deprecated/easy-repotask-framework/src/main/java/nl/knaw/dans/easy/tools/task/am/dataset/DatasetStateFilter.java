package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;

public class DatasetStateFilter implements DmoFilter<Dataset> {
    private Set<String> allowedStates;

    public DatasetStateFilter(String... allowedStates) {
        this.allowedStates = new HashSet<String>(Arrays.asList(allowedStates));
    }

    @Override
    public boolean accept(Dataset dataset) {
        return allowedStates.contains(dataset.getAdministrativeState().toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(allowedStates.size()).append("]");
        sb.append(" <");
        for (String rightsHolder : allowedStates) {
            sb.append(rightsHolder);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
