package nl.knaw.dans.easy.tools.dataset;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.easy.tools.dmo.DmoIterator;

public class DatasetIterator extends DmoIterator<Dataset> {

    public DatasetIterator() {
        super(Dataset.NAMESPACE);
    }

    public DatasetIterator(DmoFilter<Dataset>... datasetFilters) {
        super(Dataset.NAMESPACE, datasetFilters);
    }

}
