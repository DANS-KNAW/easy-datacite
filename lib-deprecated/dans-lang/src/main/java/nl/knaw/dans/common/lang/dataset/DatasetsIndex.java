package nl.knaw.dans.common.lang.dataset;

import nl.knaw.dans.common.lang.reposearch.RepoSearchBean;
import nl.knaw.dans.common.lang.search.Index;

public class DatasetsIndex implements Index
{
    private static final long serialVersionUID = 3851983243086212027L;

    public static final String NAME = "datasets";

    public static final String PRIMARY_KEY = RepoSearchBean.SID_FIELD;

    public String getName()
    {
        return NAME;
    }

    public String getPrimaryKey()
    {
        return PRIMARY_KEY;
    }

}
