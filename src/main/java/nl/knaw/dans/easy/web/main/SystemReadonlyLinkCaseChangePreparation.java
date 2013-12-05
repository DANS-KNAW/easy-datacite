package nl.knaw.dans.easy.web.main;

import nl.knaw.dans.easy.servicelayer.SystemReadonlyStatus;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SystemReadonlyLinkCaseChangePreparation extends Link<Page>
{
    private static final long serialVersionUID = 1L;
    public static final String WICKET_ID_LINK = "systemIsReadOnly";
    public static final String WICKET_ID_LABEL = "readOnly";

    @SpringBean(name = "systemReadonlyStatus")
    private SystemReadonlyStatus systemReadonlyStatus;

    /**
     * Creates a toggle for a system administrator to set the system in read only mode for a safe
     * shutdown. Requires some HTML like:
     * 
     * <pre>&lt;a wicket:id="systemIsReadOnly">&lt;span wicket:id="readOnly">read/write?&lt;/span>&lt;/a>
     * 
     * <pre>
     */
    public SystemReadonlyLinkCaseChangePreparation()
    {
        super(WICKET_ID_LINK);
        add(new Label(WICKET_ID_LABEL, createReadOnlyModel()));
    }

    @Override
    public void onBeforeRender()
    {
        // TODO rather: if (! CodedAuthz.hasSecurityOfficer(getPath()))
        // but the synchronized clause disrupts the stack and throws an IllegalStateException
        if (!(getParent() instanceof WebPage))
            throw new SecurityException(getClass().getName() + " must be added directly to a WebPage");
        super.onBeforeRender();
    }

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
