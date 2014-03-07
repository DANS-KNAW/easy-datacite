package nl.knaw.dans.common.lang.reposearch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The reindexer can be used to get the dmo store and the search engine into a synchronized state. Note:
 * this class has not been properly tested nor is it complete.
 * 
 * @author lobo
 */
public class Reindexer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Reindexer.class);

    private DmoStore store;

    private SearchEngine searchEngine;

    private PrintStream stdOut;

    private int commitFrequency = 20;

    public Reindexer(DmoStore store, SearchEngine searchEngine, PrintStream stdOut)
    {
        this.store = store;
        this.searchEngine = searchEngine;
        this.stdOut = stdOut;
    }

    public ReindexReport reindexByContentModel(List<String> contentModelList, List<DmoStoreId> excludeSids) throws ReindexException, RepositoryException
    {
        List<DmoStoreId> sids = new ArrayList<DmoStoreId>();
        for (String contentModel : contentModelList)
        {
            LOGGER.info("Getting sid list for all data model object with content model " + contentModel);
            sids.addAll(store.getSidsByContentModel(new DmoStoreId(contentModel)));
        }
        LOGGER.info("Store returned " + sids.size() + " sids");

        return reindex(sids, excludeSids);
    }

    public ReindexReport reindex(List<DmoStoreId> sids, List<DmoStoreId> excludeSids) throws ReindexException
    {
        if (excludeSids == null)
            excludeSids = Collections.emptyList();

        List<DmoStoreId> reindexedList = new ArrayList<DmoStoreId>(sids.size() - excludeSids.size());
        List<ReindexError> errors = new ArrayList<ReindexError>();
        ReindexReport report = new ReindexReport(reindexedList, errors);

        LOGGER.debug("Now starting reindex process");
        int count = 0;
        for (DmoStoreId sid : sids)
        {
            if (excludeSids != null)
            {
                if (excludeSids.contains(sid))
                    continue; // skip
            }

            LOGGER.debug("Getting object " + sid + " from store");

            DataModelObject dmo = null;
            try
            {
                dmo = store.retrieve(sid);
            }
            catch (Exception e)
            {
                LOGGER.warn("Error while getting object " + sid + " from store. Reindex will continue.", e);
                errors.add(new ReindexError(e, sid, "retrieve"));
                continue;
            }

            LOGGER.debug("Successfully got object " + sid + " from store");

            if (commitFrequency > 1)
            {
                searchEngine.beginTransaction();
            }

            if (dmo instanceof HasSearchBeans)
            {
                // get search documents
                Collection<? extends Object> sbeans = ((HasSearchBeans) dmo).getSearchBeans();
                if (sbeans != null)
                {
                    for (Object sb : sbeans)
                    {
                        if (sb instanceof RepoSearchBean)
                            ((RepoSearchBean) sb).setPropertiesByDmo(dmo);
                    }

                    try
                    {
                        searchEngine.indexBeans(sbeans);
                    }
                    catch (SearchEngineException e)
                    {
                        LOGGER.warn("Error while reindexing object " + sid + ". Reindex will continue.", e);
                        errors.add(new ReindexError(e, sid, "reindex"));
                        continue;
                    }

                    reindexedList.add(dmo.getDmoStoreId());
                    LOGGER.debug("Reindexed " + sid);

                    count++;
                    int div = Math.min(50, sids.size() / 10);
                    if (div > 0 && count % (div) == 0) // java.lang.ArithmeticException: / by zero
                    {
                        String msg = "Reindexed " + count + " objects.";
                        stdOut.println(msg);
                    }
                    if (commitFrequency > 1)
                    {
                        if (count % commitFrequency == 0)
                        {
                            try
                            {
                                searchEngine.commit();
                                searchEngine.beginTransaction();
                            }
                            catch (SearchEngineException e)
                            {
                                LOGGER.error("Reindexer caught error while commiting. Stopping.", e);
                                throw new ReindexException(e, report);
                            }
                        }
                    }
                }
                else
                {
                    LOGGER.debug(sid + " had nothing to reindex");
                }
            }
        }

        if (commitFrequency > 1)
        {
            try
            {
                searchEngine.commit();
            }
            catch (SearchEngineException e)
            {
                LOGGER.error("Reindexer caught error while commiting. Stopping.", e);
                throw new ReindexException(e, report);
            }
        }

        LOGGER.debug("Reindex process completed");

        return report;
    }

    public void setCommitFrequency(int commitFrequency)
    {
        this.commitFrequency = commitFrequency;
    }

    public int getCommitFrequency()
    {
        return commitFrequency;
    }
}
