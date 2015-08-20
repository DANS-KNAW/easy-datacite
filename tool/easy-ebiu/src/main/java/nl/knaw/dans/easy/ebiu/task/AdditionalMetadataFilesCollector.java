package nl.knaw.dans.easy.ebiu.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.FileOntology;
import nl.knaw.dans.easy.domain.model.FileOntology.MetadataFormat;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;

/**
 * Collects the information about additional files that contain metadata on the dataset The information is read from a properties file that can specify for each
 * metadata format the path to the file. Example: PAKBON=pakbon.xml Note that we can only have one file per format.
 */
public class AdditionalMetadataFilesCollector extends AbstractTask {
    public static final String DEFAULT_RELATIVE_PATH = "metadata/additional-metadatafiles.properties";

    private final String relativePath;

    public AdditionalMetadataFilesCollector() {
        super();
        relativePath = DEFAULT_RELATIVE_PATH;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        HashMap<MetadataFormat, String> additionalMetadataFiles = loadAdditionalMetadataFiles(joint);
        joint.setAdditionalMetadataFiles(additionalMetadataFiles);
    }

    HashMap<FileOntology.MetadataFormat, String> loadAdditionalMetadataFiles(JointMap joint) throws TaskCycleException {
        HashMap<FileOntology.MetadataFormat, String> additionalMetadataFiles = new HashMap<FileOntology.MetadataFormat, String>();

        Properties properties = new Properties();
        try {
            File currentDirectory = joint.getCurrentDirectory();
            File propertiesFile = new File(currentDirectory, relativePath);
            RL.info(new Event(getTaskName(), "Collecting additional metadatafiles list from: " + propertiesFile.getAbsolutePath()));
            properties.load(new FileInputStream(propertiesFile));
            RL.info(new Event(getTaskName(), "Number of additional metadatafiles listed: " + properties.keySet().size()));
        }
        catch (FileNotFoundException e) {
            // No problem, just no additional metadatafiles
            RL.info(new Event(getTaskName(), "No additional metadatafiles list found"));
        }
        catch (IOException e) {
            e.printStackTrace();
            RL.error(new Event(getTaskName(), e, "Failure reading additional metadatafiles list", e.getMessage()));
            throw new TaskCycleException("Failure reading additional metadatafiles list", e, this);
        }

        for (Object k : properties.keySet()) {
            String key = (String) k;
            try {
                MetadataFormat metadataFormat = FileOntology.MetadataFormat.valueOf(key);
                additionalMetadataFiles.put(metadataFormat, properties.getProperty(key));
            }
            catch (IllegalArgumentException e) {
                RL.error(new Event(getTaskName(), e, "Wrong metadata format: " + key, e.getMessage()));
                throw new TaskCycleException("Failure reading additional metadatafiles list", e, this);
            }
        }

        return additionalMetadataFiles;
    }
}
