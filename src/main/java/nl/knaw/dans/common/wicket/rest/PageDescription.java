package nl.knaw.dans.common.wicket.rest;

import java.io.Serializable;

public class PageDescription implements Serializable
{

    private static final long serialVersionUID = -6728631697551628327L;
    private final String name;
    private final String resourceKey;
    private final Class<? extends RESTpage> pageClass;
    
    public PageDescription(String name, String resourceKey, Class<? extends RESTpage> pageClass)
    {
        this.name = name;
        this.resourceKey = resourceKey;
        this.pageClass = pageClass;
    }

    public String getName()
    {
        return name;
    }

    public String getResourceKey()
    {
        return resourceKey;
    }

    public Class<? extends RESTpage> getPageClass()
    {
        return pageClass;
    }

}
