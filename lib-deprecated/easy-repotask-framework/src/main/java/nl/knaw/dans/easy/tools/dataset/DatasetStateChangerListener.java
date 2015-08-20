package nl.knaw.dans.easy.tools.dataset;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;

public interface DatasetStateChangerListener {

    void onDatasetNotFound(String storeId);

    void onDatasetStateChanged(Dataset dataset, DatasetState oldState, DatasetState newState, String oldDobState, String newDobState);

}
