package nl.knaw.dans.easy.business.dataset;

import java.io.ByteArrayOutputStream;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.LicenseCreatorWrapper;
import nl.knaw.dans.easy.servicelayer.LicenseCreatorWrapper.LicenseCreatorWrapperException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataLicenseGenerator implements SubmissionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MetadataLicenseGenerator.class);

    public boolean continueAfterFailure() {
        return false;
    }

    public boolean process(final DatasetSubmissionImpl submission) {
        final Dataset dataset = submission.getDataset();
        final EasyUser depositor = submission.getSessionUser();
        String datasetId = submission.getDatasetId();
        return createLicense(dataset, depositor, datasetId);
    }

    private static boolean createLicense(final Dataset dataset, final EasyUser depositor, String datasetId) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(LicenseCreatorWrapper.ESTIMATED_PDF_SIZE);
        try {
            new LicenseCreatorWrapper(depositor, dataset, false).createPdf(outputStream);
        }
        catch (final LicenseCreatorWrapperException exception) {
            logger.error("failed to create license document for " + datasetId, exception);
            return false;
        }
        dataset.setLicenseContent(outputStream.toByteArray());
        return true;
    }

}
