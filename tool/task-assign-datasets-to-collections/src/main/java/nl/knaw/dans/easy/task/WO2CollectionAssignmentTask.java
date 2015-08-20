package nl.knaw.dans.easy.task;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdAudience;
import nl.knaw.dans.pf.language.emd.EmdSubject;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class WO2CollectionAssignmentTask extends AbstractCollectionAssignmentTask {
    // id depends on the collection xml
    static final DmoStoreId COLLECTION_STORE_ID_FOR_WO2 = DmoStoreId.newDmoStoreId("easy-collection:2");

    WO2CollectionAssignmentTask() {
        super();
    }

    WO2CollectionAssignmentTask(boolean inTestMode) {
        super(inTestMode);
    }

    @Override
    public DmoStoreId getCollectionStoreId() {
        return COLLECTION_STORE_ID_FOR_WO2;
    }

    @Override
    protected boolean shouldBeAssignedToCollection(Dataset dataset) {
        return (hasWO2AsSubject(dataset) && !hasArchaeologyAsAudience(dataset) && !hasArchaeologyAsMetadataFormat(dataset));
    }

    boolean hasWO2AsSubject(Dataset dataset) {
        EmdSubject subject = dataset.getEasyMetadata().getEmdSubject();
        List<BasicString> dcSubjects = subject.getDcSubject();

        for (BasicString dcSubject : dcSubjects) {
            String value = dcSubject.getValue().trim(); // Just in case
            if (0 == value.compareToIgnoreCase("tweede wereldoorlog") || 0 == value.compareToIgnoreCase("wo2"))
                return true; // found one, so done
        }
        // not found

        return false;
    }

    boolean hasArchaeologyAsAudience(Dataset dataset) {
        final String ARCHAEOLOGY_DISCIPLINE_ID = "easy-discipline:2";
        EmdAudience emdAudience = dataset.getEasyMetadata().getEmdAudience();

        List<BasicString> disciplines = emdAudience.getDisciplines();
        for (BasicString discipline : disciplines) {
            String value = discipline.getValue();
            if (value.contentEquals(ARCHAEOLOGY_DISCIPLINE_ID))
                return true; // found so done
        }
        // not found

        return false;
    }

    boolean hasArchaeologyAsMetadataFormat(Dataset dataset) {
        EasyMetadata emd = dataset.getEasyMetadata();
        MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();

        return (MetadataFormat.ARCHAEOLOGY.equals(mdFormat));
    }

}
