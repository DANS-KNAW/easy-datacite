package nl.knaw.dans.easy.pakbonpreprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import nl.knaw.dans.easy.pakbonpreprocess.exceptions.ConfigurationException;
import nl.knaw.dans.platform.language.pakbon.PakbonValidator;
import nl.knaw.dans.platform.language.pakbon.PakbonValidatorCredentials;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commandline tool to perform the conversion(s) needed to do a batch ingest of datasets given that you have a 'pakbon' file per dataset.
 * 
 * @author paulboon
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String EASY_PAKBON_HOME_ENV = "EASY_PAKBON_HOME";
    private static final String EASY_PAKBON_CONF_DIR = "cfg";
    private static final String EASY_PAKBON_CONF = "cfg/easy-pakbon-preprocess-config.properties";
    private static final String EASY_PAKBON_VALIDATION_USERNAME_PROP = "pakbon.validator.sikb.username";
    private static final String EASY_PAKBON_VALIDATION_PASSWD_PROP = "pakbon.validator.sikb.passwd";
    private static final String DEFAULT_DIR = ".";
    private static final String EASY_PAKBON_AMDFILE_PROP = "pakbon.amdFile";
    private static final String AMD_FILENAME = "administrative-metadata.xml";

    private static Properties configProperties;

    /**
     * The main
     * 
     * @param args
     *        The commandline arguments
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        try {
            configProperties = retrieveConfigurationProperties();
        }
        catch (ConfigurationException ce) {
            logger.error("Unable to retrieve configuration: " + ce.getMessage());
            return; // exit the program
        }

        // get administartive metadata file
        File amdFile = new File(configProperties.getProperty(EASY_PAKBON_AMDFILE_PROP));

        // handle input arguments
        CommandLineParser parser = new BasicParser();
        Options options = buildOptions();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // evaluate the options

            if (line.hasOption("help")) {
                final String DESCRIPTION = "\ndescription: Converts datasets using the information in the 'pakbon' file.\n "
                        + "Usefull to do before a batch ingest or update of the dataset, given that you have a 'pakbon' file for it.\n";
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("easy-pakbon-preprocess", null, options, DESCRIPTION, true);
                return; // exit the program
            }

            File dir = new File(DEFAULT_DIR);
            if (line.hasOption("dir")) {
                dir = new File(line.getOptionValue("dir"));
                // NOTE could check input
                // if (!dir.isDirectory())
                // if (!dir.canRead())
            }

            logger.info("Started...");

            PakbonValidator pakbonValidator;
            try {
                pakbonValidator = createPakbonValidator();
            }
            catch (ConfigurationException vce) {
                logger.error("Unable to create validator: " + vce.getMessage());
                return; // exit the program
            }

            Converter converter = new Converter(pakbonValidator, amdFile);
            if (line.hasOption("batch")) {
                logger.info("Batch convert scanning dir: " + dir.getAbsolutePath());
                converter.batchConvert(dir);
            } else {
                logger.info("Convert dir: " + dir.getAbsolutePath());
                converter.convert(dir);
            }

            logger.info("Done.");
        }
        catch (ParseException pe) {
            logger.error("Unable execute command: " + pe.getMessage());
            return; // exit the program
        }
    }

    /**
     * Create the PakbonValidator
     * 
     * @return The PakbonValidator
     * @throws ConfigurationException
     */
    private static PakbonValidator createPakbonValidator() throws ConfigurationException {
        // get credentials for validator
        String sikbUsername = configProperties.getProperty(EASY_PAKBON_VALIDATION_USERNAME_PROP);
        String sikbPasswd = configProperties.getProperty(EASY_PAKBON_VALIDATION_PASSWD_PROP);

        if (sikbUsername == null) {
            throw new ConfigurationException("Missing property: " + EASY_PAKBON_VALIDATION_USERNAME_PROP);
        }
        if (sikbPasswd == null) {
            throw new ConfigurationException("Missing property: " + EASY_PAKBON_VALIDATION_PASSWD_PROP);
        }

        // construct validator
        return new PakbonValidator(new PakbonValidatorCredentials(sikbUsername, sikbPasswd));
    }

    /**
     * Retrieve the configuration
     * 
     * @return The properties
     * @throws ConfigurationException
     */
    private static Properties retrieveConfigurationProperties() throws ConfigurationException {
        String homeDirEnv = System.getenv(EASY_PAKBON_HOME_ENV);
        // should be set
        if (homeDirEnv == null) {
            // logger.error("Environment variable must be set: " + EASY_PAKBON_HOME_ENV);
            throw new ConfigurationException("Environment variable must be set: " + EASY_PAKBON_HOME_ENV);
        }

        // set Defaults
        Properties propDefault = new Properties();
        // add default adm file
        propDefault.setProperty(EASY_PAKBON_AMDFILE_PROP, homeDirEnv + "/" + EASY_PAKBON_CONF_DIR + "/" + AMD_FILENAME);

        Properties properties = new Properties(propDefault);

        // get the home dir for this app
        // File pakbonHomeDir = new File(homeDirEnv);
        File configFile = new File(homeDirEnv, EASY_PAKBON_CONF);
        // get credential strings from properties
        try {
            // load a properties file
            properties.load(new FileInputStream(configFile));
            logger.info("Loaded configuration from file: " + configFile.getAbsolutePath());
        }
        catch (IOException ex) {
            logger.error("Could not load configuration file: " + configFile.getAbsolutePath());
            // logger.error(ex.getMessage());
            throw new ConfigurationException(ex);
        }

        return properties;
    }

    /**
     * Construct the application (command-line) options.
     * 
     * @return The options
     */
    private static Options buildOptions() {
        Options options = new Options();

        // setup the options

        @SuppressWarnings("static-access")
        // @formatter:off
        Option convertDir = OptionBuilder
				.withArgName("directory")
				.hasArg()
				.isRequired() //!
				.withLongOpt("dir")
				.withDescription(
						"Directory with pakbon and dataset to convert. \n" +
						"With the batch option the dir is 'scanned' for dataset folders with pakbon files.")
				.create("dir"); 
        // @formatter:on
        options.addOption(convertDir);

        Option batch = new Option("b", "batch", false, "Batch convert by 'scanning' the given dir for dataset folders.");
        options.addOption(batch);

        Option help = new Option("h", "help", false, "print this help message");
        options.addOption(help);

        return options;
    }
}
