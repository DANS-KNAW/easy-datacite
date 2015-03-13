package nl.knaw.dans.easy.business.dataset;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.BRI_RESOLVER;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.DOI_RESOLVER;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DOI;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_PID;

import java.io.IOException;
import java.net.URI;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.business.dataset.PidClient.Type;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataPidGenerator implements SubmissionProcessor {
    public static final String PID_ERROR = "deposit.pid_error";
    private static final Logger logger = LoggerFactory.getLogger(DatasetIngester.class);
    private PidClient pidClient;

    public MetadataPidGenerator(PidClient pidClient) {
        this.pidClient = pidClient;
    }

    public boolean continueAfterFailure() {
        return false;
    }

    public boolean process(final DatasetSubmissionImpl submission) {
        Dataset dataset = submission.getDataset();
        try {
            generateURN(dataset);
            generateDOI(dataset);
            return true;
        }
        catch (final Exception exception) {
            submission.getGlobalErrorMessages().add(PID_ERROR);
            logger.error("Can't generate PIDs " + exception.getMessage() + " for " + submission.getDatasetId(), exception);
            return false;
        }
    }

    private void generateDOI(Dataset dataset) throws IOException, RepositoryException {
        if (dataset.getDansManagedDoi() != null)
            return;
        String doi = pidClient.getPid(Type.doi);
        addToDcIdentifier(dataset, createBasicIdentifier(doi, DOI_RESOLVER, SCHEME_DOI));
        getRelationsFrom(dataset).setDansManagedDOI(doi);
        logger.debug("Generated new DOI. doi=" + doi);
    }

    private void generateURN(Dataset dataset) throws IOException, RepositoryException {
        if (dataset.getPersistentIdentifier() != null)
            return;
        String urn = pidClient.getPid(Type.urn);
        addToDcIdentifier(dataset, createBasicIdentifier(urn, BRI_RESOLVER, SCHEME_PID));
        getRelationsFrom(dataset).setPersistentIdentifier(urn);
        logger.debug("Generated new Pid. pid=" + urn);
    }

    private DatasetRelations getRelationsFrom(Dataset dataset) {
        return (DatasetRelations) dataset.getRelations();
    }

    private void addToDcIdentifier(Dataset dataset, BasicIdentifier basicIdentifier) {
        dataset.getEasyMetadata().getEmdIdentifier().getDcIdentifier().add(basicIdentifier);
    }

    private BasicIdentifier createBasicIdentifier(String pid, String resolver, String scheme) {
        BasicIdentifier bi = new BasicIdentifier(pid);
        bi.setIdentificationSystem(URI.create(resolver));
        bi.setScheme(scheme);
        return bi;
    }
}
