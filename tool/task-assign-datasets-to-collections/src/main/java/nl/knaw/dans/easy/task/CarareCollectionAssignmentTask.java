package nl.knaw.dans.easy.task;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdCoverage;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.Spatial;

public class CarareCollectionAssignmentTask extends AbstractCollectionAssignmentTask {
    // id depends on the collection xml
    public static final DmoStoreId COLLECTION_STORE_ID_FOR_CARARE = DmoStoreId.newDmoStoreId("easy-collection:4");

    CarareCollectionAssignmentTask() {
        super();
    }

    CarareCollectionAssignmentTask(boolean inTestMode) {
        super(inTestMode);
    }

    @Override
    public DmoStoreId getCollectionStoreId() {
        return COLLECTION_STORE_ID_FOR_CARARE;
    }

    @Override
    protected boolean shouldBeAssignedToCollection(Dataset dataset) {
        return (isOpen(dataset) && hasArchaeologyAsMetadataFormat(dataset) && hasAtLeastDcmiTypeText(dataset) && (hasSpatialPoint(dataset) || hasSpatialBox(dataset)));
    }

    private boolean isOpen(Dataset dataset) {
        return AccessCategory.isOpen(dataset.getAccessCategory());
    }

    private boolean hasArchaeologyAsMetadataFormat(Dataset dataset) {
        EasyMetadata emd = dataset.getEasyMetadata();
        MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();

        return (MetadataFormat.ARCHAEOLOGY.equals(mdFormat));
    }

    private boolean hasSpatialPoint(Dataset dataset) {
        boolean result = false;

        EasyMetadata emd = dataset.getEasyMetadata();
        EmdCoverage emdCoverage = emd.getEmdCoverage();
        List<Spatial> easSpatials = emdCoverage.getEasSpatial();

        for (Spatial spatial : easSpatials) {

            if (spatial.getPoint() != null && spatial.getPoint().isComplete()) {
                // NOTE: should I check the content of scheme, x, y to be not empty, non-whitespace etc.
                result = true;
                break; // done, because found one!
            }
        }

        return result;
    }

    private boolean hasSpatialBox(Dataset dataset) {
        boolean result = false;

        EasyMetadata emd = dataset.getEasyMetadata();
        EmdCoverage emdCoverage = emd.getEmdCoverage();
        List<Spatial> easSpatials = emdCoverage.getEasSpatial();

        for (Spatial spatial : easSpatials) {

            if (spatial.getBox() != null && spatial.getBox().isComplete()) {
                // NOTE: should I check the content of scheme, north, east,... etc. to be not empty,
                // non-whitespace etc.
                result = true;
                break; // done, because found one!
            }
        }

        return result;
    }

    private boolean hasAtLeastDcmiTypeText(Dataset dataset) {
        boolean hasAtLeastDcmiTypeText = false;
        for (BasicString bs : dataset.getEasyMetadata().getEmdType().getDcType()) {
            if ("DCMI".equals(bs.getScheme()) && "common.dc.type".equals(bs.getSchemeId()) && "Text".equals(bs.getValue())) {
                hasAtLeastDcmiTypeText = true;
                break;
            }
        }
        return hasAtLeastDcmiTypeText;
    }

}
