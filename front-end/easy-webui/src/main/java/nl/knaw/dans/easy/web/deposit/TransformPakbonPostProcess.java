package nl.knaw.dans.easy.web.deposit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.statistics.UploadFileStatistics;
import nl.knaw.dans.easy.web.view.dataset.UploadSingleFilePostProcess;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.PakbonStatus;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;
import nl.knaw.dans.platform.language.pakbon.Pakbon2EmdTransformer;
import nl.knaw.dans.platform.language.pakbon.PakbonValidator;
import nl.knaw.dans.platform.language.pakbon.PakbonValidatorCredentials;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TransformPakbonPostProcess extends UploadSingleFilePostProcess {
    private static final Logger log = LoggerFactory.getLogger(TransformPakbonPostProcess.class);

    private final UploadStatus status = new UploadStatus("Initializing pakbon transform process");

    @SpringBean(name = "pakbonValidatorCredentials")
    private PakbonValidatorCredentials validatorCredentials;

    @SpringBean(name = "itemService")
    private ItemService itemService;

    @SpringBean(name = "datasetService")
    private DatasetService datasetService;

    public TransformPakbonPostProcess(DatasetModel datasetModel) {
        super(datasetModel);
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    protected void processUploadedFile(File pakbon) throws UploadPostProcessException {
        log.info("Performing Pakbon to EMD transformation on {}", pakbon.getName());
        status.setMessage("Initializing ...");
        validate(pakbon);
        updateEasyMetadata(transform(pakbon));
        ingest(pakbon);
        updatePakbonImportedStatus();
        saveEasyMetadata();
        status.setMessage("Pakbon import complete.");
    }

    private void validate(File pakbon) throws UploadPostProcessException {
        PakbonValidator validator = new PakbonValidator(validatorCredentials);
        ValidateXmlResponse response;
        try {
            response = validator.validateXml(getPakbonInputStream(pakbon));
            Validation v = response.getValidation();
            if (!v.getValidXml()) {
                int errors = v.getErrorCount();
                int warnings = v.getWarningCount();
                String msg1 = errors + warnings > 0 ? v.getMessages()[0].getMessage() : "";
                error("Not a valid Pakbon file: " + response.getValidation().getErrorCount() + " errors, " + response.getValidation().getWarningCount()
                        + " warnings. First message: " + msg1);
            }
        }
        catch (ValidatorException e) {
            error("Unable to validate Pakbon file", e);
        }
        catch (SOAPException e) {
            error("Unable to communicate with validation service", e);
        }
        catch (IOException e) {
            error("Unable to read Pakbon file to validate", e);
        }
    }

    private InputStream getPakbonInputStream(File pakbon) throws UploadPostProcessException {
        try {
            return FileUtils.openInputStream(pakbon);
        }
        catch (IOException e) {
            error("Could not open uploaded Pakbon file", e);
        }
        return null;
    }

    private String transform(File pakbon) throws UploadPostProcessException {
        try {
            byte[] result = new Pakbon2EmdTransformer().transform(pakbon);
            return new String(result, "UTF-8");
        }
        catch (IOException e) {
            error("Could not open uploaded Pakbon for transformation", e);
        }
        catch (TransformerException e) {
            // error("Error during Pakbon to EASY metadata tranformation process", e);
            error("Error during Pakbon to EASY metadata tranformation process" + "; " + e.getMessage(), e);
        }
        return null;
    }

    private void updateEasyMetadata(String emdXml) throws UploadPostProcessException {
        try {
            getDataset().replaceEasyMetadata(emdXml);
        }
        catch (DomainException e) {
            error("Error saving EASY metadata", e);
        }
    }

    private void ingest(final File pakbon) throws UploadPostProcessException {
        List<File> files = new ArrayList<File>();
        files.add(pakbon);
        try {
            DmoStoreId parentDmoStoreId = getDataset().getDmoStoreId();
            itemService.addDirectoryContents(EasySession.get().getUser(), getDataset(), parentDmoStoreId, pakbon.getParentFile(), new PakbonIngester(
                    getDataset(), pakbon));
        }
        catch (ServiceException e) {
            error("Could not ingest Pakbon file", e);
        }
        finally {
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.FILE_DEPOSIT, new DatasetStatistics(getDataset()), new UploadFileStatistics(files));
        }
    }

    private void updatePakbonImportedStatus() {
        getDataset().getEasyMetadata().getEmdOther().getEasApplicationSpecific().setPakbonStatus(PakbonStatus.IMPORTED);
    }

    private void saveEasyMetadata() throws UploadPostProcessException {
        try {
            datasetService.saveEasyMetadata(EasySession.getSessionUser(), getDataset());
        }
        catch (DataIntegrityException e) {
            error("Could not save IMPORTED status to Easy metadata", e);
        }
        catch (ServiceException e) {
            error("Could not save IMPORTED status to Easy metadata", e);
        }
    }

    private void error(String msg, Throwable t) throws UploadPostProcessException {
        status.setError(true);
        status.setMessage(msg);
        throw new UploadPostProcessException(msg, t);
    }

    private void error(String msg) throws UploadPostProcessException {
        error(msg, null);
    }

    public void cancel() {
        /*
         * Disabled cancel. It will not really cancel the action, as there is no rollback. Neither is this a long-running process, so there is really no good
         * reason to have a kind of semi-cancel.
         */
    }

    public UploadStatus getStatus() {
        return status;
    }

    public boolean needsProcessing(List<File> files) {
        return true;
    }
}
