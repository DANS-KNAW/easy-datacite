package nl.knaw.dans.common.lang.reposearch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import nl.knaw.dans.common.lang.file.SidListFile;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.search.SearchEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class to get a command line reindexing tool setup.
 * 
 * @author lobo
 */
public abstract class ReindexerCmdLine
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexerCmdLine.class);

    public static final String STORE_NAME = "store-name";

    public static final String STORE_URL = "store-url";

    public static final String STORE_USER = "store-user";

    public static final String STORE_PASSWORD = "store-password";

    public static final String SOLR_URL = "search-engine-url";

    public static final String EXCLUDE_FILE = "exclude-file";

    public static final String OUTPUT_SIDLIST_FILE = "output-sidlist-file";

    public static final String OUTPUT_ERROR_FILE = "output-error-file";

    protected static final String CONTENT_MODELS = "content-models";

    private Reindexer reindex;

    private OptionParser parser;

    public ReindexerCmdLine()
    {
        initParser();
    }

    protected void initParser()
    {
        parser = new OptionParser()
        {
            {
                accepts(CONTENT_MODELS, "A comma separated list of content models that need to be reindexed.").withRequiredArg().ofType(String.class);

                accepts(STORE_NAME, "The name of the store.").withRequiredArg().ofType(String.class);

                accepts(STORE_URL, "The store URL.").withRequiredArg().ofType(String.class);

                accepts(STORE_USER, "The store user name.").withRequiredArg().ofType(String.class);

                accepts(STORE_PASSWORD, "The store password.").withRequiredArg().ofType(String.class);

                accepts(SOLR_URL, "The URL of the search engine.").withRequiredArg().ofType(String.class);

                accepts(EXCLUDE_FILE, "A SidList filename with the store IDs of the object to exclude from reindexing.").withRequiredArg().ofType(File.class);

                accepts(OUTPUT_SIDLIST_FILE, "A filename to which the list of store IDs is written of the objects that were reindexed.").withRequiredArg()
                        .ofType(File.class).defaultsTo(new File("reindexed.csv"));

                accepts(OUTPUT_ERROR_FILE, "A filename to which the list of encountered errors is written.").withRequiredArg().ofType(File.class)
                        .defaultsTo(new File("error.log"));

                acceptsAll(Arrays.asList("h", "?"), "show help");
            }
        };
    }

    /**
     * @return the option parser. You can extend the options through this parser object.
     */
    public OptionParser getParser()
    {
        return parser;
    }

    /**
     * Execute the command line tool version of the reindexer tool. Run with -? to see a list of all
     * parameters.
     */
    public void execute(String[] args)
    {
        OptionParser optionParser = getParser();

        // PARSE OPTIONS AND/OR SHOW HELP
        OptionSet options = null;
        try
        {
            try
            {
                options = optionParser.parse(args);

                if (options.has("?") || options.has("h") || args.length == 0)
                {
                    optionParser.printHelpOn(System.out);
                    return;
                }
            }
            catch (OptionException e)
            {
                System.out.println(e.getMessage() + "\n\n");
                optionParser.printHelpOn(System.out);
                return;
            }
        }
        catch (IOException e)
        {
            LOGGER.error("IOException during parsing options", e);
            return;
        }

        String storeName = (String) options.valueOf(STORE_NAME);
        String fedoraUrl = (String) options.valueOf(STORE_URL);
        String fedoraUser = (String) options.valueOf(STORE_USER);
        String fedoraPasswd = (String) options.valueOf(STORE_PASSWORD);
        String solrUrl = (String) options.valueOf(SOLR_URL);
        File excludeSidListFile = (File) options.valueOf(EXCLUDE_FILE);
        File outputSidListFile = (File) options.valueOf(OUTPUT_SIDLIST_FILE);
        File outputErrorFile = (File) options.valueOf(OUTPUT_ERROR_FILE);
        String contentModels = (String) options.valueOf(CONTENT_MODELS);
        List<String> contentModelList = Arrays.asList(contentModels.split(","));

        System.out.print("---------------------------------------------\n" + "---------- Starting Easy Reindexer ----------\n"
                + "---------------------------------------------\n" + "store name: " + storeName + "\n" + "store url: " + fedoraUrl + "\n" + "store username: "
                + fedoraUser + "\n" + "store password: " + fedoraPasswd + "\n" + "search engine url: " + solrUrl + "\n" + "content models: " + contentModels
                + "\n" + "---------------------------------------------\n");

        // GET STORE & SEARCH ENGINE
        DmoStore store = getStore(storeName, fedoraUrl, fedoraUser, fedoraPasswd, options);
        SearchEngine searchEngine = getSearchEngine(solrUrl, options);

        // READ EXCLUDE LIST
        List<DmoStoreId> excludeSidList = null;
        try
        {
            if (excludeSidListFile != null)
            {
                if (!excludeSidListFile.exists())
                {
                    LOGGER.error("SidList file " + excludeSidListFile.getAbsolutePath() + " does not exist.");
                    return;
                }
                excludeSidList = SidListFile.readSidList(excludeSidListFile);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Error reading SidList file " + excludeSidListFile.getAbsolutePath(), e);
            return;
        }

        // START REINDEXING
        ReindexReport report = null;
        try
        {
            System.out.print("Starting reindex process..\n");

            reindex = new Reindexer(store, searchEngine, System.out);
            report = reindex.reindexByContentModel(contentModelList, excludeSidList);
        }
        catch (ReindexException reindexException)
        {
            LOGGER.error("Fatal runtime error during reindexing. Reindexing aborted. Currently no rollback!", reindexException);
            report = reindexException.getReindexReport();
        }
        catch (Throwable t)
        {
            LOGGER.error("Fatal runtime error during reindexing. Reindexing aborted. Currently no rollback!", t);
            return;
        }

        // WRITE TO OUTPUT
        if (report != null)
        {
            if (!report.getReindexed().isEmpty() && outputSidListFile != null)
            {
                try
                {
                    if (!outputSidListFile.exists())
                        outputSidListFile.createNewFile();
                    report.writeReindexSidList(outputSidListFile);
                }
                catch (IOException e)
                {
                    LOGGER.error("Error writing to output sid list file " + outputSidListFile.getAbsolutePath(), e);
                }
            }

            if (!report.getErrors().isEmpty() && outputErrorFile != null)
            {
                try
                {
                    if (!outputErrorFile.exists())
                        outputErrorFile.createNewFile();
                    report.writeErrors(outputErrorFile);
                }
                catch (IOException e)
                {
                    LOGGER.error("Error writing to error file " + outputErrorFile.getAbsolutePath(), e);
                }
            }

            System.out.print("---------------------------------------------\n" + "Reindexed " + report.getReindexed().size() + " objects (see "
                    + outputSidListFile.getAbsolutePath() + ")\n" + "Got " + report.getErrors().size() + " errors (see " + outputErrorFile.getAbsolutePath()
                    + ")\n");
        }

    }

    protected abstract SearchEngine getSearchEngine(String solrUrl, OptionSet options);

    protected abstract DmoStore getStore(String storeName, String storeUrl, String storeUser, String storePasswd, OptionSet options);

}
