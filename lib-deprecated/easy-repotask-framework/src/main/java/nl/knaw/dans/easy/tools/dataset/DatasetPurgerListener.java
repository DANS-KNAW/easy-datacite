package nl.knaw.dans.easy.tools.dataset;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.model.Dataset;

public interface DatasetPurgerListener {

    void onSubordinatePurged(DataModelObject dmo);

    void onDatasetPurged(Dataset dataset);

    void onDatasetNotFound(String storeId);

    void onIdMapRemoval(String storeId);

}
