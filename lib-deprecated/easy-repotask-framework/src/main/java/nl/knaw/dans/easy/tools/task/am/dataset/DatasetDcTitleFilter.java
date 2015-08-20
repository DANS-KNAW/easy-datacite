package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;

public class DatasetDcTitleFilter implements DmoFilter<Dataset> {
    private boolean exactMatch;
    private Set<String> allowedTitles;

    public DatasetDcTitleFilter(String... allowedTitles) {
        this(false, allowedTitles);
    }

    public DatasetDcTitleFilter(boolean exactMatch, String... allowedTitles) {
        this.allowedTitles = new HashSet<String>(Arrays.asList(allowedTitles));
        this.exactMatch = exactMatch;
    }

    @Override
    public boolean accept(Dataset dataset) {
        List<String> titles = dataset.getEasyMetadata().getDublinCoreMetadata().getTitle();
        for (String title : titles) {
            for (String allowed : allowedTitles) {
                if (isMatch(title, allowed)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMatch(String title, String allowed) {
        if (exactMatch)
            return title.equals(allowed);
        else
            return title.contains(allowed);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append("[").append(allowedTitles.size()).append("]");
        sb.append(" <");
        for (String title : allowedTitles) {
            sb.append(title);
            sb.append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append(">");

        return sb.toString();
    }
}
