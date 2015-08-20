package nl.knaw.dans.easy.tools.search;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.reposearch.ReindexException;
import nl.knaw.dans.common.lang.reposearch.Reindexer;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

public class DatasetReindexer extends AbstractTask {

    public DatasetReindexer() {

    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        SearchEngine engine = Data.getSearchEngine();
        DmoStore store = Data.getEasyStore();
        List<String> contentModelList = new ArrayList<String>();
        contentModelList.add("easy-model:EDM1DATASET");
        Reindexer reindexer = new Reindexer(store, engine, System.out);

        try {
            reindexer.reindexByContentModel(contentModelList, null);
        }
        catch (ReindexException e) {
            throw new FatalTaskException(e, this);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

}
