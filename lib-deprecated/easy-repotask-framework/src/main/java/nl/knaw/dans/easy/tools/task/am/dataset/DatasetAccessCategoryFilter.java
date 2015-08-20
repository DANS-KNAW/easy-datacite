package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;

public class DatasetAccessCategoryFilter implements DmoFilter<Dataset> {
    private Set<String> allowedCategories;

    public DatasetAccessCategoryFilter(String... allowedCategories) {
        this.allowedCategories = new HashSet<String>(Arrays.asList(allowedCategories));
    }

    @Override
    public boolean accept(Dataset dataset) {
        return allowedCategories.contains(dataset.getAccessCategory().toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(allowedCategories.size()).append("]");
        sb.append(" <");
        for (String rightsHolder : allowedCategories) {
            sb.append(rightsHolder);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
