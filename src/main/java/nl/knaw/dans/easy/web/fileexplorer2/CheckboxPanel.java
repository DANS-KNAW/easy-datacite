package nl.knaw.dans.easy.web.fileexplorer2;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class CheckboxPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    private AjaxCheckBox cb;

    public CheckboxPanel(String name, Model<Boolean> model)
    {
        super(name, model);
        Form<Void> form = new Form<Void>("form");
        cb = new AjaxCheckBox("checkbox", model)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                onSelectionChange(target);
            }
        };
        form.add(cb);
        add(form);
    }

    public void onSelectionChange(AjaxRequestTarget target)
    {
        // please mister, Override me
    }
}
