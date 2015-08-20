package nl.knaw.dans.easy.tools.dataset;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;

public class DefaultDatasetStateChangerListener implements DatasetStateChangerListener {

    @Override
    public void onDatasetNotFound(String storeId) {

    }

    @Override
    public void onDatasetStateChanged(Dataset dataset, DatasetState oldState, DatasetState newState, String oldDobState, String newDobState) {

    }

}
