package nl.knaw.dans.common.wicket.rest;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * Top class of cascading RESTful web pages. A RESTful URL can be composed as
 * <pre>
 *    {/collection-name{/#id}{/collection-name{/#id}}}{/dissemination-type}
 * </pre>
 * where url-forms
 * <dl>
 * <dt>(1) /</dt><dd>leads to a page showing collection names</dd>
 * <dt>(2) /collection-name</dt><dd>leads to a page showing item-id's and/or sub collections and/or dissemination-types</dd>
 * <dt>(3) /collection-name/#id</dt><dd>leads to a default dissemination</dd>
 * <dt>(4) /collection-name/#id/dissemination-type</dt><dd>leads to the dissemination</dd>
 * </dl>
 * 
 * <p>&nbsp;</p>
 * This class creates a navigation bar corresponding to the url at the top of the page.
 * 
 * @see {@link RESTcascadePage}, {@link RESTdisseminationPage}
 *
 */
public abstract class RESTpage extends WebPage
{

    private static final String PATH = "/ui";

    private final String url;

    private final String[] urlFragments;

    public RESTpage()
    {
        this(new PageParameters());
    }

    public RESTpage(PageParameters parameters)
    {
        super(parameters);
        url = getRequest().getURL();
        urlFragments = url.split("/");
        if (isDisseminationPage())
        {
            ((RESTdisseminationPage) this).disseminate();
        }
        else if (urlFragments.length == getLevel())
        {
            initPage();
        }
        else if (isCascadePage() && urlFragments.length == getLevel() + 1)
        {
            doDefaultDissemination();
        }
        else
        {
            cascadeToChild();
        }
    }

    public abstract String getName();

    public abstract String getResourceKey();

    public abstract int getLevel();

    protected abstract void cascadeToChild();

    /**
     * Initiates the page.
     */
    protected void doDefaultDissemination()
    {
        initPage();
    }

    /**
     * Override this method and return true if this is the first page and only showing collections.
     * 
     * @return false
     */
    public boolean isStartPage()
    {
        return false;
    }

    public boolean isCascadePage()
    {
        return !isStartPage() && (this instanceof RESTcascadePage);
    }

    public boolean isDisseminationPage()
    {
        return this instanceof RESTdisseminationPage;
    }

    public String getUrl()
    {
        return url;
    }

    public String[] getUrlFragments()
    {
        return urlFragments;
    }

    public String getUrlFragment(int level)
    {
        return urlFragments[level];
    }

    protected String composeUrl(int index, String... names)
    {
        StringBuilder sb = new StringBuilder(PATH);
        for (int i = 0; i < index; i++)
        {
            sb.append("/") //
                    .append(getUrlFragments()[i]);
        }
        for (int i = 0; i < names.length; i++)
        {
            sb.append("/") //
                    .append(names[i]);
        }
        return sb.toString();
    }

    protected void initPage()
    {
        createNavigationBar();
    }

    protected void createNavigationBar()
    {
        List<String> fragments = Arrays.asList(getUrlFragments()).subList(0, getLevel() - 1);
        ListView<String> navigation = new ListView<String>("navigation", fragments)
        {

            private static final long serialVersionUID = -4008784545857808172L;

            @Override
            protected void populateItem(ListItem<String> item)
            {
                final String targetUrl = composeUrl(item.getIndex() + 1);
                ExternalLink eLink = new ExternalLink("fragment", targetUrl, item.getModelObject());
                item.add(eLink);
            }
        };
        add(navigation);
        add(new Label("currentStep", getName()));
    }

}
