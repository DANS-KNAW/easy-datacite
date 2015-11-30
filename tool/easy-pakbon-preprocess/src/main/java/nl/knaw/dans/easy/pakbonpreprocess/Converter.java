package nl.knaw.dans.easy.pakbonpreprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;

import nl.knaw.dans.easy.pakbonpreprocess.exceptions.ConversionException;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;
import nl.knaw.dans.platform.language.pakbon.Pakbon2EmdTransformer;
import nl.knaw.dans.platform.language.pakbon.PakbonValidator;
import nl.knaw.dans.easy.domain.model.FileOntology;

import org.apache.commons.io.FileUtils;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts datasets using the information in the 'pakbon' file.
 * 
 * @author paulboon
 */
public class Converter {
    private static final Logger logger = LoggerFactory.getLogger(Converter.class);
    private static final Logger reporter = LoggerFactory.getLogger("Reporter");

    private static final String METADATA_DIR = "metadata";
    private static final String FILEDATA_DIR = "filedata";
    private static final String EMD_FILENAME = "easymetadata.xml";
    private static final String PAKBON_FILENAME = "pakbon.xml"; // as we store it

    /*
     * The PakbonValidator does a http request to the SIKB server which is not always desirable. Especially when unittesting. Maybe we could have other kinds of
     * validators (offline xsd schema?). It is be better to make the implementation of the validater configurable via a setter and have the Converter work with
     * a PakbonValidator interface.
     */
    private PakbonValidator pakbonValidator;

    private Pakbon2EmdTransformer transformer = new Pakbon2EmdTransformer();

    private File amdFile;

    /**
     * Construct converter that uses the given validator
     * 
     * @param pakbonValidator
     *        validator to use before converting
     * @param amdFile
     *        fixed metadata file for each dataset
     */
    public Converter(PakbonValidator pakbonValidator, File amdFile) {
        super();

        this.pakbonValidator = pakbonValidator;
        this.amdFile = amdFile;
    }

    /**
     * Convert all datasets (in sub directories) in the given directory that have a valid pakbon file. The expected directory structure for batch ingest/update:
     * 
     * <pre>
     *  ingest/ 
     *  + {dataset-directory1}/ 
     *  ... 
     *  + {dataset-directoryN}/
     *    + {pakbon.xml}
     *    +- filedata/
     *    +- metadata/
     *       + easymetadata.xml
     *       + administrative-metadata.xml
     *       + resource-metadata-list.xml
     * </pre>
     * 
     * Assume that in each datasetdirectory there is one pakbon file with the extension xml.
     * 
     * @param directory
     *        The directory to scan for datasets
     */
    public void batchConvert(File directory) {
        logger.info("Pakbon batch convertion from directory: " + directory.getAbsolutePath());

        List<File> datasetdirectorys = findDatasetDirectories(directory);
        long totalNumDirs = datasetdirectorys.size();
        logger.info("Numnber of potential dataset directories Found: " + totalNumDirs);
        long count = 0;
        for (File datasetdirectory : datasetdirectorys) {
            count++;
            logger.info("Trying to convert dataset " + count + " of " + totalNumDirs + " : " + datasetdirectory.getAbsolutePath());
            convert(datasetdirectory);
        }
    }

    /**
     * Convert the datasset when pakbon is found.
     * 
     * @param datasetDir
     *        The dataset directory
     */
    public void convert(File datasetDir) {
        List<File> pakbonFiles = findPakbonFiles(datasetDir);
        if (pakbonFiles.isEmpty()) {
            logger.warn("Not converting, because no pakbon file has been found!");
            reporter.warn("{}; Finding pakbon file FAILED", datasetDir);
            return;
        }

        File pakbonFile = pakbonFiles.get(0); // just the first one!

        logger.info("Found pakbon file: " + pakbonFile.getAbsolutePath());
        try {
            logger.info("Validating...");

            if (!validatePakbon(pakbonFile)) {
                logger.error("Validation failed!");
                logger.warn("Not converting, because pakbon file was invalid");
                reporter.error("{}; Validation FAILED", pakbonFile);
                return;
            }

            logger.info("Valid file, start converting...");

            convertDatasetWithValidPakbon(pakbonFile);

            logger.info("Conversion succeeded.");
            reporter.info("{}; OK", pakbonFile);
        }
        catch (ConversionException e) {
            logger.error("Conversion failed!", e);
            reporter.error("{}; FAILED", pakbonFile);
        }
    }

    /**
     * Use the pakbon validator to test if the pakbon file is valid.
     * 
     * @param pakbonFile
     *        The pakbon file to validate
     * @return The validation status, true if valid false if invalid.
     * @throws ConversionException
     */
    private boolean validatePakbon(File pakbonFile) throws ConversionException {
        ValidateXmlResponse response = null;
        try {
            response = pakbonValidator.validateXml(pakbonFile);
        }
        catch (ValidatorException e) {
            throw new ConversionException("Could not validate pakbon file: " + pakbonFile.getAbsolutePath(), e);
        }
        catch (SOAPException e) {
            throw new ConversionException("Could not validate pakbon file: " + pakbonFile.getAbsolutePath(), e);
        }
        catch (IOException e) {
            throw new ConversionException("Could not validate pakbon file: " + pakbonFile.getAbsolutePath(), e);
        }

        // validation succeeded, but the file might be invalid

        if (response != null && response.getValidation().getMessages() != null) {
            for (ValidationMessage msg : response.getValidation().getMessages()) {
                logger.info("Validation message: " + msg.getMessage());
            }
        }

        if (response == null) {
            // Not sure if this is an error and we should throw an exception instead
            return false;
        } else {
            return response.getValidation().getValidXml();
        }
    }

    /**
     * Convert the dataset with the pakbon file information. Note that there is no validation here! Also note that files are overwritten and in case of an
     * exception there is no 'rollback' of files written already.
     * 
     * @param pakbonFile
     *        The pakbon file to use for conversion
     * @throws ConversionException
     */
    private void convertDatasetWithValidPakbon(File pakbonFile) throws ConversionException {
        try {
            logger.info("Loading file: " + pakbonFile.getAbsolutePath());

            byte[] bytes = transformer.transform(pakbonFile);
            // NOTE that the xslt used by transform specifies that the output is UTF-8
            // So if you want a string for logging : String emdStr = new String(bytes, "UTF-8");

            logger.info("Transformed file: " + pakbonFile.getAbsolutePath());
            File metadatadirectory = findMetadataDirectory(pakbonFile);
            File emdFile = new File(metadatadirectory, EMD_FILENAME);
            try {
                FileUtils.writeByteArrayToFile(emdFile, bytes);
                logger.info("Saved EASY Metadata file to: " + emdFile.getAbsolutePath());
            }
            catch (IOException e1) {
                throw new ConversionException("Could not save EASY Metadata file to: " + emdFile.getAbsolutePath(), e1);
            }

            copyAdministrativeMetadatafile(amdFile, metadatadirectory);

            copyPakbonfile(pakbonFile);

            savePakbonfileMetadataRelation(pakbonFile, metadatadirectory);
        }
        catch (TransformerException e) {
            throw new ConversionException("Could not transform file: " + pakbonFile.getAbsolutePath(), e);
        }
    }

    private void copyAdministrativeMetadatafile(File amdFile, File metadatadirectory) throws ConversionException {
        File admDstFile = new File(metadatadirectory, amdFile.getName());
        try {
            FileUtils.copyFile(amdFile, admDstFile);
            logger.info("Copied administrative metadata file to: " + admDstFile.getAbsolutePath());
        }
        catch (IOException e) {
            throw new ConversionException("Could not copy administrative metadata file to: " + admDstFile.getAbsolutePath(), e);
        }

    }

    private void copyPakbonfile(File pakbonFile) throws ConversionException {
        File filedatadirectory = findFiledataDirectory(pakbonFile);
        File pakbonDstFile = new File(filedatadirectory, pakbonFile.getName());// PAKBON_FILENAME);
        try {
            FileUtils.copyFile(pakbonFile, pakbonDstFile);
            logger.info("Copied pakbon file to: " + pakbonDstFile.getAbsolutePath());
        }
        catch (IOException e) {
            throw new ConversionException("Could not copy pakbon file to: " + pakbonDstFile.getAbsolutePath(), e);
        }
    }

    private void savePakbonfileMetadataRelation(File pakbonFile, File metadatadirectory) throws ConversionException {
        Properties properties = new Properties();
        File propertiesFile = new File(metadatadirectory, "additional-metadatafiles.properties");
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
            }
            catch (IOException e) {
                throw new ConversionException("Could not load existing file: " + propertiesFile.getAbsolutePath(), e);
            }
        }
        properties.put(FileOntology.MetadataFormat.PAKBON.toString(), pakbonFile.getName());
        try {
            properties.store(new FileOutputStream(propertiesFile), "additional-metadatafiles");
        }
        catch (IOException e) {
            throw new ConversionException("Could not store updated file: " + propertiesFile.getAbsolutePath(), e);
        }
    }

    /**
     * Construct the File object representing the easy metadata (emd) diractory given the pakbon file.
     * 
     * @param pakbonFile
     *        The pakbon file to find the metadata directory for
     * @return
     */
    private File findMetadataDirectory(File pakbonFile) {
        // assume pakbon file is at the root of the dataset directory
        File datasetDir = pakbonFile.getParentFile();
        File metadataDir = new File(datasetDir, METADATA_DIR);

        return metadataDir;
    }

    /**
     * Construct the File object representing the easy filedata directory given the pakbon file.
     * 
     * @param pakbonFile
     *        The pakbon file to find the filedata directory for
     * @return
     */
    private File findFiledataDirectory(File pakbonFile) {
        // assume pakbon file is at the root of the dataset directory
        File datasetDir = pakbonFile.getParentFile();
        File dataDir = new File(datasetDir, FILEDATA_DIR);

        return dataDir;
    }

    /**
     * @param directory
     *        The dataset directory
     * @return The pakbon files that have been found, a list with just one file or empty!
     */
    private List<File> findPakbonFiles(File datasetdirectory) {
        ArrayList<File> foundFiles = new ArrayList<File>();

        List<File> xmlFiles = findXmlFiles(datasetdirectory);

        if (!xmlFiles.isEmpty()) {
            // could also check that it is readable and has the right content?
            // just always take the first, but we could do something smarter
            foundFiles.add(xmlFiles.get(0));
            // logger.info("Found pakbon file: " + xmlFiles.get(0));
            if (xmlFiles.size() > 1) {
                logger.warn("Found multiple files!");
                for (int i = 1; i < xmlFiles.size(); i++) {
                    logger.warn("Ignoring: " + xmlFiles.get(i).getAbsolutePath());
                }
            }
        }

        return foundFiles;
    }

    /**
     * Find all xml files in the given directory. It is not recursive, just the files directly under the directory.
     * 
     * @param directory
     *        The directory where xml files should be found
     * @return The files found
     */
    private List<File> findXmlFiles(File directory) {
        ArrayList<File> foundFiles = new ArrayList<File>();

        // filter for xml extension

        // just get the first xml extension file... for now
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File directory, String name) {
                // case insensitive
                return name.toLowerCase().endsWith(".xml");
            }
        });

        // copy into the list
        Collections.addAll(foundFiles, files);

        return foundFiles;
    }

    /**
     * All subdirectories are assumed to be dataset directories. Note that we do NOT test for the existence of subdirs 'filedata' and 'metadata'.
     * 
     * @param directory
     *        The directory that should have dataset (sub)directories
     * @return
     */
    private List<File> findDatasetDirectories(File directory) {
        ArrayList<File> foundDirs = new ArrayList<File>();

        File[] subDirs = directory.listFiles(new FilenameFilter() {
            public boolean accept(File directory, String name) {
                return new File(directory, name).isDirectory();
            }
        });

        // copy into the list
        Collections.addAll(foundDirs, subDirs);

        return foundDirs;
    }
}
