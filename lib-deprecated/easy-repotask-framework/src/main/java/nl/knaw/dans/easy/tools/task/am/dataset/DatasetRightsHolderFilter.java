package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class DatasetRightsHolderFilter implements DmoFilter<Dataset> {
    private Set<String> allowedRightsHolders;

    public DatasetRightsHolderFilter(String... allowedRightsHolders) {
        this.allowedRightsHolders = new HashSet<String>(Arrays.asList(allowedRightsHolders));
    }

    @Override
    public boolean accept(Dataset dataset) {
        boolean contains = false;
        // Get all the Rights holders of this dataset
        List<BasicString> rightsHolders = dataset.getEasyMetadata().getEmdRights().getTermsRightsHolder();
        // For every Rights holder
        for (BasicString holder : rightsHolders) {
            // Check if it is the one we are looking for
            if (allowedRightsHolders.contains(holder.getValue())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(allowedRightsHolders.size()).append("]");
        sb.append(" <");
        for (String rightsHolder : allowedRightsHolders) {
            sb.append(rightsHolder);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
