package nl.knaw.dans.easy.web.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.knaw.dans.common.wicket.rest.PageDescription;
import nl.knaw.dans.common.wicket.rest.RESTcascadePage;
import nl.knaw.dans.easy.web.rest.dataset.RESTdatasetsPage;

public class RESTstartPage extends RESTcascadePage
{

    public static final String NAME = "rest";
    public static final String RESOURCE_KEY = "rest";

    private static final int LEVEL = 1;

    private static Map<String, PageDescription> CHILDREN;

    public RESTstartPage()
    {
        super();
    }

    @Override
    public Map<String, PageDescription> getChildren()
    {
        if (CHILDREN == null)
        {
            CHILDREN = new LinkedHashMap<String, PageDescription>();

            String name = RESTdatasetsPage.NAME;
            String resourceKey = RESTdatasetsPage.RESOURCE_KEY;
            PageDescription description = new PageDescription(name, resourceKey, RESTdatasetsPage.class);
            CHILDREN.put(name, description);
        }
        return CHILDREN;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getResourceKey()
    {
        return RESOURCE_KEY;
    }

    @Override
    public int getLevel()
    {
        return LEVEL;
    }

    @Override
    public boolean isStartPage()
    {
        return true;
    }

}
