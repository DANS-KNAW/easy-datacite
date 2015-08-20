package nl.knaw.dans.easy.tools.dataset;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.model.Dataset;

public class DefaultDatasetPurgerListener implements DatasetPurgerListener {

    @Override
    public void onSubordinatePurged(DataModelObject dmo) {

    }

    @Override
    public void onDatasetPurged(Dataset dataset) {

    }

    @Override
    public void onDatasetNotFound(String storeId) {

    }

    @Override
    public void onIdMapRemoval(String storeId) {

    }

}
