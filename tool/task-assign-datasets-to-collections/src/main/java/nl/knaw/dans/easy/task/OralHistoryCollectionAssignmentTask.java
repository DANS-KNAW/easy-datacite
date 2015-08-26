package nl.knaw.dans.easy.task;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class OralHistoryCollectionAssignmentTask extends AbstractCollectionAssignmentTask {

    public static final DmoStoreId COLLECTION_STORE_ID = DmoStoreId.newDmoStoreId("easy-collection:1");
    public static final String SUBJECT_ORAL_HISTORY = "oral history";
    public static final String TITLE_IPNV = "IPNV";

    public OralHistoryCollectionAssignmentTask() {
        super();
    }

    public OralHistoryCollectionAssignmentTask(boolean inTestMode) {
        super(inTestMode);
    }

    @Override
    public DmoStoreId getCollectionStoreId() {
        return COLLECTION_STORE_ID;
    }

    @Override
    protected boolean shouldBeAssignedToCollection(Dataset dataset) {
        return subjectHasOralHistory(dataset) || titleContainsIPNV(dataset);
    }

    private boolean titleContainsIPNV(Dataset dataset) {
        EasyMetadata emd = dataset.getEasyMetadata();
        List<BasicString> titles = emd.getEmdTitle().getDcTitle();
        titles.addAll(emd.getEmdTitle().getTermsAlternative());
        for (BasicString bs : titles) {
            String value = bs.getValue();
            if (value != null && value.contains(TITLE_IPNV)) {
                return true;
            }
        }
        return false;
    }

    private boolean subjectHasOralHistory(Dataset dataset) {
        EasyMetadata emd = dataset.getEasyMetadata();
        List<BasicString> subjects = emd.getEmdSubject().getDcSubject();
        for (BasicString bs : subjects) {
            String value = bs.getValue();
            if (value != null && value.toLowerCase().startsWith(SUBJECT_ORAL_HISTORY)) {
                return true;
            }
        }
        return false;
    }

}
