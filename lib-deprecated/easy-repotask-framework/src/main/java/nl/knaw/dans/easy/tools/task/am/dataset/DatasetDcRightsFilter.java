package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class DatasetDcRightsFilter implements DmoFilter<Dataset> {
    private Set<String> allowedDcRights;

    public DatasetDcRightsFilter(String... allowedDcRights) {
        this.allowedDcRights = new HashSet<String>(Arrays.asList(allowedDcRights));
    }

    @Override
    public boolean accept(Dataset dmo) {
        return acceptDcRights(dmo) || acceptRightsHolder(dmo);
    }

    private boolean acceptDcRights(Dataset dmo) {
        boolean contains = false;
        // Get all the dc:rights of this dataset
        List<BasicString> dcRights = dmo.getEasyMetadata().getEmdRights().getDcRights();
        // For every dc:rights
        for (BasicString element : dcRights) {
            // Check if it is the one we are looking for
            if (allowedDcRights.contains(element.getValue())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private boolean acceptRightsHolder(Dataset dmo) {
        boolean contains = false;
        // Get all the Rights holders of this dataset
        List<BasicString> rightsHolders = dmo.getEasyMetadata().getEmdRights().getTermsRightsHolder();
        // For every Rights holder
        for (BasicString holder : rightsHolders) {
            // Check if it is the one we are looking for
            if (allowedDcRights.contains(holder.getValue())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(allowedDcRights.size()).append("]");
        sb.append(" <");
        for (String rightsValue : allowedDcRights) {
            sb.append(rightsValue);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
