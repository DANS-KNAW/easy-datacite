package nl.knaw.dans.easy.web;

import java.io.Serializable;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;

public class RedirectData implements Serializable
{
    private static final long serialVersionUID = 2650092271045175148L;

    private final Class<? extends Page> pageClass;
    private PageParameters pageParameters;

    public RedirectData(Class<? extends Page> pageClass)
    {
        this(pageClass, new PageParameters());
    }

    public RedirectData(Class<? extends Page> pageClass, PageParameters pageParameters)
    {
        this.pageClass = pageClass;
        this.pageParameters = pageParameters;
    }

    public Class<? extends Page> getPageClass()
    {
        return pageClass;
    }

    public PageParameters getPageParameters()
    {
        return pageParameters;
    }

    public void setPageParameters(PageParameters pageParameters)
    {
        this.pageParameters = pageParameters;
    }
}
