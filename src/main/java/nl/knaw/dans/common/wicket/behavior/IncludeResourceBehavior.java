package nl.knaw.dans.common.wicket.behavior;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;

/**
 * <p>
 * Behavior that adds an included resource to component (e.g., a page). The scope is a class in the same
 * package as where the resource resides. Typically, the scope is the class object of a wicket page or
 * panel and the resource is CSS- or JavaScript file that is included in the source code in the same
 * package as the class. The HTML-template of the class can then refer to the resource without a prefixed
 * path.
 * </p>
 * <p>
 * Example:
 * <pre>
 *      JAVA FILE:
 *      
 *      public class MyPage extends Page {
 *      
 *         public MyPage() {
 *           add(new IncludeResourceBehavior(MyPage.class, "myjavascript.js");
 *           // ... etc
 *           
 *      HTML-TEMPLATE MyPage.html:
 *      
 *      &lt;html&gt;
 *      &lt;head&gt;
 *         &lt;script type="text/javascript" src="myjavascript.js"&gt;
 *         
 *      &lt;-- etc --&gt;
 * </pre>         
 * 
 */
public class IncludeResourceBehavior extends AbstractBehavior
{
    private static final long serialVersionUID = 5261628952131156563L;
    private final Class<?> scope;
    private final String resource;

    public IncludeResourceBehavior(Class<?> scope, String resource)
    {
        this.scope = scope;
        this.resource = resource;
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.renderCSSReference(new CompressedResourceReference(scope, resource));
    }
}
