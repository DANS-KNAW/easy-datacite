package nl.knaw.dans.easy.web.template;

import nl.knaw.dans.easy.servicelayer.SystemReadonlyStatus;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SystemReadonlyLink extends Link<Page>
{
    @SpringBean(name = "systemReadonlyStatus")
    private SystemReadonlyStatus systemReadonlyStatus;

    public SystemReadonlyLink()
    {
        // make sure each page uses the same ID so CodedAuthz will find a security officer
        super("systemIsReadOnly");
        add(new Label("readOnly", createReadOnlyModel()));
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void onClick()
    {
        systemReadonlyStatus.setReadOnly(!systemReadonlyStatus.getReadOnly());
        setResponsePage(this.getPage());
    }

    private LoadableDetachableModel<String> createReadOnlyModel()
    {
        return new LoadableDetachableModel<String>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected String load()
            {
                if (systemReadonlyStatus.getReadOnly())
                    return getLocalizer().getString("adminSwitch.readOnly", getPage(), "[SYSTEM IS IN READ ONLY MODE]");
                else
                    return getLocalizer().getString("adminSwitch.readWrite", getPage(), "[system allows read and write]");
            }
        };
    }
}
