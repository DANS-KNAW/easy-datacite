package nl.knaw.dans.easy.task;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EmdTitle;

public class GetuigenVerhalenCollectionAssignmentTask extends AbstractCollectionAssignmentTask {
    // id depends on the collection xml
    static final DmoStoreId COLLECTION_STORE_ID_FOR_GETUIGENVERHALEN = DmoStoreId.newDmoStoreId("easy-collection:3");

    GetuigenVerhalenCollectionAssignmentTask() {
        super();
    }

    GetuigenVerhalenCollectionAssignmentTask(boolean inTestMode) {
        super(inTestMode);
    }

    @Override
    public DmoStoreId getCollectionStoreId() {
        return COLLECTION_STORE_ID_FOR_GETUIGENVERHALEN;
    }

    @Override
    protected boolean shouldBeAssignedToCollectionWithFormatCheck(Dataset dataset) {
        return (hasGetuigenVerhalenInTitle(dataset) && !hasArchaeologyAsAudience(dataset) && !hasArchaeologyAsMetadataFormat(dataset));
    }

    @Override
    protected boolean shouldBeAssignedToCollection(Dataset dataset) {
        return (hasGetuigenVerhalenInTitle(dataset) && !hasArchaeologyAsAudience(dataset));
    }

    boolean hasGetuigenVerhalenInTitle(Dataset dataset) {
        EmdTitle emdTitle = dataset.getEasyMetadata().getEmdTitle();
        String preferredTitle = emdTitle.getPreferredTitle();// Not sure if that is OK, maybe all titles
                                                             // should be checked?

        // "Getuigen Verhalen" should be in it
        // if it becomes more complicated we should use regexp's
        return preferredTitle.contains("Getuigen Verhalen");
    }
}
