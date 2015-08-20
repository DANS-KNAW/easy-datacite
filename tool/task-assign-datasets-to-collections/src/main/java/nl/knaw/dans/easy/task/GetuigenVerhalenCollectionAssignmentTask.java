package nl.knaw.dans.easy.task;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdAudience;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

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
    protected boolean shouldBeAssignedToCollection(Dataset dataset) {
        return (hasGetuigenVerhalenInTitle(dataset) && !hasArchaeologyAsAudience(dataset) && !hasArchaeologyAsMetadataFormat(dataset));
    }

    boolean hasGetuigenVerhalenInTitle(Dataset dataset) {
        EmdTitle emdTitle = dataset.getEasyMetadata().getEmdTitle();
        String preferredTitle = emdTitle.getPreferredTitle();// Not sure if that is OK, maybe all titles
                                                             // should be checked?

        // "Getuigen Verhalen" should be in it
        // if it becomes more complicated we should use regexp's
        return preferredTitle.contains("Getuigen Verhalen");
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
