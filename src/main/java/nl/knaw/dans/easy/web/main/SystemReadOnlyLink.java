package nl.knaw.dans.easy.web.main;

import nl.knaw.dans.easy.security.Authz;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SystemReadOnlyLink extends Link<Page>
{
    private static final long serialVersionUID = 1L;
    public static final String WICKET_ID_LINK = "systemIsReadOnly";
    public static final String WICKET_ID_LABEL = "readOnly";

    @SpringBean(name = "systemReadOnlyStatus")
    private SystemReadOnlyStatus systemReadOnlyStatus;

    @SpringBean(name = "authz")
    private Authz authz;

    /**
     * Creates a toggle for a system administrator to set the system in read only mode for a safe
     * shutdown. Requires some HTML like:
     * 
     * <pre>&lt;a wicket:id="systemIsReadOnly">&lt;span wicket:id="readOnly">read/write?&lt;/span>&lt;/a>
     * 
     * <pre>
     */
    public SystemReadOnlyLink()
    {
        super(WICKET_ID_LINK);
        add(new Label(WICKET_ID_LABEL, createReadOnlyModel()));
    }

    @Override
    public void onBeforeRender()
    {
        if (! authz.hasSecurityOfficer(getPath()))
            throw new SecurityException(getClass().getName() + " no rule defined for "+getPath());
        super.onBeforeRender();
    }

    @Override
    public void onClick()
    {
        systemReadOnlyStatus.setReadOnly(!systemReadOnlyStatus.getReadOnly());
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
                if (systemReadOnlyStatus.getReadOnly())
                    return getLocalizer().getString("adminSwitch.readOnly", getPage(), "[SYSTEM IS IN READ ONLY MODE]");
                else
                    return getLocalizer().getString("adminSwitch.readWrite", getPage(), "[system allows read and write]");
            }
        };
    }
}
