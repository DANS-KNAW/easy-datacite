package nl.knaw.dans.easy.web.permission;

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.IPageLink;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

/**
 * Panel showing the permission request status for the session user of a dataset.
 */
public class PermissionSatusPanel extends AbstractEasyPanel
{
    private static final DateTime UNKNOWN_DATE_TIME = new DateTime("0000-01-01");
    private static final long serialVersionUID = 1L;

    private static enum LinkType
    {
        REQUEST, REVIEW, VIEW
    };

    private static final String DATE_TIME_FORMAT = "DateAndTimeFormat";

    private static final String PANEL_LABEL = "permissionRequestLabel";

    private static final String STATUS = "permissionRequestStatus";
    private static final String STATUS_DATE = "permissionRequestStatusDate";

    private static final String LINK = "permissionRequestLink";
    private static final String LINK_TEXT = "permissionRequestLinkText";
    // private static final String LINK_TYPE = "permissionRequestLinkType";

    private boolean initiated;

    private final Dataset dataset;
    private final EasyUser sessionUser;

    private PermissionSequence getSequence()
    {
        final PermissionSequenceList sequenceList = dataset.getPermissionSequenceList();
        if (sequenceList == null || sessionUser == null || !sequenceList.hasSequenceFor(sessionUser))
            return null;
        return sequenceList.getSequenceFor(sessionUser);
    }

    private DateTime getStatusDate()
    {
        final PermissionSequence sequence = getSequence();
        if (sequence == null)
            return null;
        return sequence.getLastStateChange();
    }

    private State getRequestState()
    {
        final PermissionSequence sequence = getSequence();
        if (sequence == null)
            return null;
        return sequence.getState();
    }

    private LinkType getLinkType()
    {
        final State requestState = getRequestState();
        if (requestState == null)
            return LinkType.REQUEST;
        switch (requestState)
        {
        case Returned:
            return LinkType.REVIEW;
        case Denied:
            return LinkType.VIEW;
        case Granted:
            return LinkType.VIEW;
        case Submitted:
            return LinkType.VIEW;
        default:
            return null;
        }
    }

    private void addComponents()
    {
        add(new Label(PANEL_LABEL, new Model<String>(getString(PANEL_LABEL))));

        add(new Label(STATUS, new Model<String>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject()
            {
                return getString(STATUS + "." + getRequestState());
            }
        }));

        add(new DateTimeLabel(STATUS_DATE, getString(DATE_TIME_FORMAT), new Model<DateTime>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public DateTime getObject()
            {
                final DateTime statusDate = getStatusDate();
                if (statusDate != null)
                    return statusDate;
                if ("".equals(getString(STATUS + "." + null)))
                    return null;
                return new DateTime();
            }
        }));

        // prepare to return back to this page
        final AbstractEasyPage currentPage = (AbstractEasyPage) getPage();

        IPageLink permissionRequestPage = new IPageLink()
        {
            private static final long serialVersionUID = 1L;

            public Page getPage()
            {
                return new PermissionRequestPage(new DatasetModel(dataset), currentPage);
            }

            public Class<PermissionRequestPage> getPageIdentity()
            {
                return PermissionRequestPage.class;
            }
        };

        @SuppressWarnings("unchecked")
        final MarkupContainer pageLink = new PageLink(LINK, permissionRequestPage);
        add(pageLink);

        pageLink.add(new Label(LINK_TEXT, new Model<String>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject()
            {
                return getString(LINK_TEXT + "." + getLinkType());
            }

        }));
    }

    public PermissionSatusPanel(String wicketId, final Dataset dataset)
    {
        super(wicketId);
        this.dataset = dataset;
        sessionUser = getSessionUser();
        if (dataset == null || sessionUser == null || sessionUser.isAnonymous() || !sessionUser.isActive())
            setVisible(false);
        else if (sessionUser.hasRole(EasyUser.Role.ADMIN) || sessionUser.hasRole(EasyUser.Role.ARCHIVIST))
            setVisible(false);
        else if (dataset.hasDepositor(sessionUser))
            setVisible(false);
        else if (!dataset.hasPermissionRestrictedItems())
            setVisible(false);
    }

    public boolean isInitiated()
    {
        return initiated;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            setOutputMarkupId(true);
            addComponents();
            initiated = true;
        }
        super.onBeforeRender();
    }

}
