package nl.knaw.dans.easy.web.permission;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.wicket.components.PossiblyDisabledTextArea;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

// Note maybe a AbstractDatasetModelPanel is better?
//
public class PermissionRequestViewPanel extends AbstractEasyPanel
{
    private static final long        serialVersionUID    = -5400472018731834517L;

    private static final Logger      logger              = LoggerFactory.getLogger(PermissionRequestViewPanel.class);

    private static final String      STATUS_RESOURCE_KEY = "permission.request.status.value.";

    private static final String      THEME_WID           = "theme";
    private static final String      TITLE_WID           = "title";
    private static final String      BACK_WID          = "back";

    protected final AbstractEasyPage fromPage;

    public PermissionRequestViewPanel(String wicketId, final AbstractEasyPage fromPage, final DatasetModel datasetModel)
    {
        super(wicketId, datasetModel);
        this.fromPage = fromPage;

        final EasyUser sessionUser = getSessionUser();
        final boolean initialRequest = !getDataset().getPermissionSequenceList().hasSequenceFor(sessionUser);
        final PermissionSequence userSequence = getDataset().getPermissionSequenceList().getSequenceFor(sessionUser);

        final PermissionSequence.State status = initialRequest ? null : userSequence.getState();
        final String explanation = initialRequest ? null : userSequence.getReplyText();
        final boolean editMode = initialRequest || (State.Returned.equals(status));

        PermissionRequestModel prmRequest = getDataset().getPermissionSequenceList().getPermissionRequest(sessionUser);

        final IModel titleModel = new PropertyModel(prmRequest, PermissionRequestModel.REQUEST_TITLE);
        final IModel themeModel = new PropertyModel(prmRequest, PermissionRequestModel.REQUEST_THEME);
        final IModel statusModel = new ResourceModel(STATUS_RESOURCE_KEY + status, "" + status);

        add(new Label("status.value", statusModel).setVisible(status != null));
        
        // In submitted state; Don't show the explanation of a reply that 'returned' the previous request
        boolean isExplanationVisible = (explanation != null) && (!State.Submitted.equals(status));
        add(new MultiLineLabel("explanation.value", explanation).setVisible(isExplanationVisible));
        
        add(new Label(TITLE_WID, titleModel));

        // Note: maybe a MultiLineLabel is better
        add(new PossiblyDisabledTextArea(THEME_WID, themeModel, editMode));

        // TODO make it into a back link
        add(new Link(BACK_WID)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                pageBack();
            }
        });
    }

    protected void pageBack() throws RestartResponseException
    {
        if (fromPage == null)
            throw new RestartResponseException(HomePage.class);
        fromPage.refresh(); // TODO just a refresh of the panel?
        setResponsePage(fromPage);
    }

    protected Dataset getDataset()
    {
        return (Dataset) getModel().getObject();
    }
}
