package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class DatasetDcCreatorFilter implements DmoFilter<Dataset> {
    private Set<String> allowedCreators;

    public DatasetDcCreatorFilter(String... allowedCreators) {
        this.allowedCreators = new HashSet<String>(Arrays.asList(allowedCreators));
    }

    @Override
    public boolean accept(Dataset dataset) {
        boolean contains = false;
        // Get all the creators of this dataset
        List<BasicString> creators = dataset.getEasyMetadata().getEmdCreator().getDcCreator();
        // For every creator
        for (BasicString creator : creators) {
            // Check if it is the one we are looking for
            if (allowedCreators.contains(creator.getValue())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(allowedCreators.size()).append("]");
        sb.append(" <");
        for (String creator : allowedCreators) {
            sb.append(creator);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
