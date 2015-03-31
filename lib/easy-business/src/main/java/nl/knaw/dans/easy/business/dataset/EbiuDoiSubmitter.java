package nl.knaw.dans.easy.business.dataset;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.DataciteService;
import nl.knaw.dans.easy.DataciteServiceException;

public class EbiuDoiSubmitter implements SubmissionProcessor {

    private final DataciteService dataciteService;

    public EbiuDoiSubmitter(DataciteService dataciteService) {
        this.dataciteService = dataciteService;
    }

    @Override
    public boolean process(DatasetSubmissionImpl submission) {
        // datasets may be published implicitly when submitted with a batch process
        // in that case we should submit the DOI to datacite
        if (submission.getDataset().getAdministrativeState() != DatasetState.PUBLISHED)
            return true;
        else {
            try {
                dataciteService.create(submission.getDataset().getEasyMetadata());
                return true;
            }
            catch (DataciteServiceException e) {
                return false;
            }
        }
    }

    @Override
    public boolean continueAfterFailure() {
        return false;
    }

}
