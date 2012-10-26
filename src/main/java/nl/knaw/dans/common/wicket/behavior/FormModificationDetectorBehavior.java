package nl.knaw.dans.common.wicket.behavior;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WicketEventReference;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

/**
 * Detects form modification. This behavior can be added to a {@link Form}. It compares form elements onLoad and
 * onbeforeUnload. If there are differences a confirm-message is displayed. After a submit on the form, comparison
 * parameters are renewed.
 * <p/>
 * Sometimes a model object is dynamically updated from different parts of the page and saved to the back-end storage on
 * events other than the classical form-submission. In that case, comparison parameters can be renewed by calling the
 * function {@link #FORM_MODIFICATIONS_SAVED_JS} on an ajax round trip.
 * <p>Example</p>
 * Here is an ajax-event handler that updates the
 * save-button and the the state of comparison parameters on the clients machine:
 *
 * <pre>
 * public void handleAjaxEvent(AjaxRequestTarget target)
 * {
 *     // update button-looks
 *     target.addComponent(saveButton);
 *
 *     // send message that form-elements are now saved to the back-end
 *     target.appendJavascript(FormModificationDetectorBehavior.FORM_MODIFICATIONS_SAVED_JS);
 * }
 * </pre>
 *
 * Sometimes you want to get rid of form modification detection behavior altogether, like for instance after some other
 * procedure on the page bumped into an error. In that case append {@link #DISABLE_DETECTOR_JS} to the target.
 * <p/>
 * The message displayed can be influenced by overriding {@link #getDisplayMessage()}.
 * <p/>
 * See also <a
 * href="http://www.hunlock.com/blogs/Mastering_The_Back_Button_With_Javascript">http://www.hunlock.com/blogs
 * /Mastering_The_Back_Button_With_Javascript</a>
 *
 * @author Sep 2, 2009 Class and js adapted from
 *         http://www.jroller.com/karthikg/entry/modelling_client_side_form_modifications
 */
public class FormModificationDetectorBehavior extends AbstractBehavior
{

    public static final String FORM_MODIFICATIONS_SAVED_JS = "setFormOnRender();";

    public static final String DISABLE_DETECTOR_JS = "setDetectFormModification(false);";

    private static final long serialVersionUID = -4529319874092963800L;

    // Wicket calls this method just after a behavior is bound to
    // a Component and that's nice as we need access to the Form
    // component.

    public void bind(Component component)
    {
        if (!(component instanceof Form))
        {
            throw new WicketRuntimeException(getClass() + " behavior can be bound only to a Form component");
        }
        Form form = (Form) component;

        // This will make sure that the Form emits the
        // HTML 'ID' attribure when rendering. We require the 'ID' in our JS file

        form.setOutputMarkupId(true);

        // Since we require the Form "id" and the 'message' in our JavaScript
        // template, we will use Wicket's TextTemplateHeaderContributor that allows us to
        // perform variable substitutions at runtime. Keep the JS file in the same package location
        // as the behavior.

        // load dependencies
        form.getWebPage().add(JavascriptPackageResource.getHeaderContribution(WicketEventReference.INSTANCE));
        form.getWebPage().add(JavascriptPackageResource.getHeaderContribution(WicketAjaxReference.INSTANCE));

        form.getWebPage().add(
                TextTemplateHeaderContributor.forJavaScript(FormModificationDetectorBehavior.class, "j-script/FormModificationDetector.js",
                        new FormModificationDetectorModel(form.getMarkupId())));
    }

    // TextTemplateHeaderContributor expects to be supplied with a Map
    // as the backing Model. It will use the map to perform variable substitution.

    class FormModificationDetectorModel extends AbstractReadOnlyModel
    {

        private static final long serialVersionUID = -7377791430281874480L;
        private Map<String, String> variables;
        private String formMarkupid;

        FormModificationDetectorModel(String formMarkupid)
        {
            this.formMarkupid = formMarkupid;
        }

        // return the Map as the model object
        public Object getObject()
        {
            if (variables == null)
            {
                // Use Wicket's built-in MiniMap when the
                // number of Map entries are known upfront.
                // A nice way of controlling Wicket's memory usage.
                // Also, while we are on this subject, you might also
                // want to look at the MicroMap class

                variables = new MiniMap(2);

                // provide runtime values for the 'form_id' and 'message'
                variables.put("form_id", formMarkupid);
                variables.put("message", getDisplayMessage());
            }
            return variables;
        }
    };

    /**
     * Get the display message.
     *
     * @return the message displayed in a confirm dialog
     */
    protected String getDisplayMessage()
    {
        return "The form on this page has unsaved changes.";
    }
}
