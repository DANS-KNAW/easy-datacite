package nl.knaw.dans.common.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Sets focus on component when loaded.
 * 
 * @see <a href="http://cwiki.apache.org/WICKET/request-focus-on-a-specific-form-component.html">cwiki</a>
 * @author ecco Mar 11, 2009
 */
public class FocusOnLoadBehavior extends AbstractBehavior {

    private static final long serialVersionUID = -5255219396884691361L;
    private Component component;

    public void bind(Component component) {
        this.component = component;
        component.setOutputMarkupId(true);
    }

    public void renderHead(IHeaderResponse iHeaderResponse) {
        super.renderHead(iHeaderResponse);
        iHeaderResponse.renderOnLoadJavascript("document.getElementById('" + component.getMarkupId() + "').focus()");
    }

    public boolean isTemporary() {
        // remove the behavior after component has been rendered
        return true;
    }
}
