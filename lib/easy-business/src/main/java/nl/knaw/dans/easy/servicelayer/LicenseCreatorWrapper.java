package nl.knaw.dans.easy.servicelayer;

import java.io.File;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.license.BaseParameters;
import nl.knaw.dans.easy.license.FileAccessRight;
import nl.knaw.dans.easy.license.LicenseCreator;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Enumeration.Value;
import scala.collection.JavaConverters;
import scala.collection.mutable.Seq;

public class LicenseCreatorWrapper {
    private static final Logger logger = LoggerFactory.getLogger(LicenseCreatorWrapper.class);

    public static class LicenseCreatorWrapperException extends Exception {
        private static final long serialVersionUID = 1L;

        // anyone can catch, only owner can throw
        private LicenseCreatorWrapperException(final String identifyingMessage, final Throwable cause) {
            super(identifyingMessage, cause);
            logger.error(identifyingMessage, cause);
        }

        public LicenseCreatorWrapperException(final Throwable cause) {
            super(cause);
            logger.error("", cause);
        }
    }

    /** Use as initial size (in bytes) for buffers for the license (pdf file) */
    public static final int ESTIMATED_PDF_SIZE = 70 * 1024;

    private final nl.knaw.dans.easy.license.Dataset licenseDataset;
    private final LicenseCreator licenseCreator;

    public LicenseCreatorWrapper(final EasyUser depositor, final Dataset dataset, final boolean generateSample) throws LicenseCreatorWrapperException {
        licenseDataset = getLicenseDataset(depositor, dataset);

        try {
            File pdfGen = ResourceLocator.getFile("pdfgen.sh");
            File templateResourceDir = pdfGen.getParentFile();
            BaseParameters baseParameters = new BaseParameters(templateResourceDir, dataset.getStoreId(), generateSample);
            licenseCreator = LicenseCreator.apply(baseParameters);
        }
        catch (ResourceNotFoundException e) {
            throw new LicenseCreatorWrapperException("Could not find resources for license creation", e);
        }
    }

    public void createPdf(final OutputStream outputStream) throws LicenseCreatorWrapperException {
        try {
            licenseCreator.createLicense(licenseDataset, outputStream).get();
        }
        catch (Exception e) {
            throw new LicenseCreatorWrapperException("Failed to create license", e);
        }
    }

    private static nl.knaw.dans.easy.license.Dataset getLicenseDataset(final EasyUser depositor, final Dataset dataset) throws LicenseCreatorWrapperException {
        String datasetID = dataset.getStoreId();
        EasyMetadata emd = dataset.getEasyMetadata();
        Seq<String> audiences = getLicenseAudiences(emd);
        Seq<nl.knaw.dans.easy.license.FileItem> fileItems = getLicenseFileItems(dataset.getDmoStoreId());
        return new nl.knaw.dans.easy.license.Dataset(datasetID, emd, getLicenseEasyUser(depositor), audiences, fileItems);
    }

    private static nl.knaw.dans.easy.license.EasyUser getLicenseEasyUser(final EasyUser depositor) {
        // @formatter:off
        return new nl.knaw.dans.easy.license.EasyUser(
                depositor.getDisplayName(), // And not getCommonName()!
                StringUtils.defaultString(depositor.getOrganization()), 
                StringUtils.defaultString(depositor.getAddress()), 
                StringUtils.defaultString(depositor.getPostalCode()), 
                StringUtils.defaultString(depositor.getCity()), 
                StringUtils.defaultString(depositor.getCountry()),
                StringUtils.defaultString(depositor.getTelephone()), 
                depositor.getEmail());
        // @formatter:on
    }

    private static Seq<String> getLicenseAudiences(final EasyMetadata emd) throws LicenseCreatorWrapperException {
        final DisciplineCollectionService disciplineService = Services.getDisciplineService();
        if (disciplineService == null)
            throw new LicenseCreatorWrapperException("Discipline service not configured", null);

        List<String> licAudiences = new ArrayList<String>();
        for (String sid : emd.getEmdAudience().getValues()) {
            try {
                // TODO check that we return the correct strings...
                licAudiences.add(disciplineService.getDisciplineById(new DmoStoreId(sid)).getName());
            }
            catch (final IllegalArgumentException e) {
                logger.error(MessageFormat.format("Could not get discipline name for: {0}", sid), e);
                // ignore?
            }
            catch (final ObjectNotFoundException e) {
                logger.error(MessageFormat.format("Could not get discipline name for: {0}", sid), e);
                // ignore?
            }
            catch (final ServiceException e) {
                throw new LicenseCreatorWrapperException(MessageFormat.format("Discipline service error: {0}", e.getMessage()), e);
            }
        }

        return JavaConverters.asScalaBufferConverter(licAudiences).asScala();
    }

    private static Value getAccessRight(final AccessibleTo accessibleTo) {
        if (accessibleTo == null)
            throw new IllegalArgumentException("Could not map accessibleTo=null");

        Value value = null;
        if (accessibleTo.equals(AccessibleTo.ANONYMOUS)) {
            value = FileAccessRight.ANONYMOUS();
        } else if (accessibleTo.equals(AccessibleTo.KNOWN)) {
            value = FileAccessRight.KNOWN();
        } else if (accessibleTo.equals(AccessibleTo.RESTRICTED_REQUEST)) {
            value = FileAccessRight.RESTRICTED_REQUEST();
        } else if (accessibleTo.equals(AccessibleTo.RESTRICTED_GROUP)) {
            value = FileAccessRight.RESTRICTED_GROUP();
        } else if (accessibleTo.equals(AccessibleTo.NONE)) {
            value = FileAccessRight.NONE();
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Could not map accessibleTo='{0}'", accessibleTo));
        }

        return value;
    }

    private static Seq<nl.knaw.dans.easy.license.FileItem> getLicenseFileItems(final DmoStoreId sid) throws LicenseCreatorWrapperException {
        List<nl.knaw.dans.easy.license.FileItem> licFileItems = new ArrayList<nl.knaw.dans.easy.license.FileItem>();
        List<FileItemVO> fileItemVOs = getDatasetFileItemVOs(sid);
        for (FileItemVO fileItemVO : fileItemVOs) {
            AccessibleTo accessibleTo = fileItemVO.getAccessibleTo();
            Value accessRight = getAccessRight(accessibleTo);
            licFileItems.add(new nl.knaw.dans.easy.license.FileItem(fileItemVO.getPath(), accessRight, fileItemVO.getSha1Checksum()));
        }
        return JavaConverters.asScalaBufferConverter(licFileItems).asScala();
    }

    private static List<FileItemVO> getDatasetFileItemVOs(final DmoStoreId sid) throws LicenseCreatorWrapperException {
        try {
            return Data.getFileStoreAccess().getDatasetFiles(sid);
        }
        catch (StoreAccessException e) {
            throw new LicenseCreatorWrapperException("Can not find uploaded files of dataset", e);
        }
    }

}
