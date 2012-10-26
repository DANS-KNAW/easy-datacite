package nl.knaw.dans.easy.web.permission;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.DateTime;

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

public class PermissionReplyViewPanel extends AbstractEasyPanel
{
    private static final long serialVersionUID = 7165279397951054593L;

    private static final String STATUS_RESOURCE_KEY = "permission.reply.status";
    private static final String EXPLANATION_RESOURCE_KEY = "permission.reply.explanation";

    private static final String DATE_TIME_FORMAT = "DateAndTimeFormat";

    private static final String BACK_WID = "back";
    private static final String EXPLANATION_WID = "explanation";
    private static final String STATUS_WID = "status";

    private static final String GRANTED = PermissionSequence.State.Granted.toString();

    protected final AbstractEasyPage fromPage;
    private final PermissionReplyModel prmReply;

    public PermissionReplyViewPanel(String wicketId, final AbstractEasyPage fromPage, final DatasetModel datasetModel, final PermissionSequence sequence)
    {
        super(wicketId, datasetModel);
        this.fromPage = fromPage;

        prmReply = getDataset().getPermissionSequenceList().getPermissionReply(sequence.getRequesterId());

        // add the form
        //add(new PermissionReplyForm("form", fromPage, datasetModel, sequence));
        addMotivation(sequence);
        addPersonalInfo(sequence.getRequester());

        final PropertyModel statusModel = new PropertyModel(prmReply, "state");
        final IModel explanationModel = new PropertyModel(prmReply, "explanation");
        add(new Label("status", statusModel));
        add(new MultiLineLabel("explanation", explanationModel));

        add(new Link(BACK_WID)
        {
            private static final long serialVersionUID = -6091186801938439734L;

            @Override
            public void onClick()
            {
                pageBack();
            }
        });
    }

    // TODO put the personal info on a separate Panel
    private void addPersonalInfo(final EasyUser requester)
    {

        add(new Label("userId", requester.getId()));
        add(new Label("email", requester.getEmail()));

        add(new Label("name", requester.getDisplayName()));
        add(new Label("function", requester.getFunction()));
        add(new Label("telephone", requester.getTelephone()));
        add(new Label("discipline1", requester.getDiscipline1()));
        add(new Label("discipline2", requester.getDiscipline2()));
        add(new Label("discipline3", requester.getDiscipline3()));
        add(new Label("dai", requester.getDai()));

        add(new Label("organization", requester.getOrganization()));
        add(new Label("department", requester.getDepartment()));
        add(new Label("address", requester.getAddress()));
        add(new Label("postalCode", requester.getPostalCode()));
        add(new Label("city", requester.getCity()));
        add(new Label("country", requester.getCountry()));
    }

    private void addMotivation(final PermissionSequence sequence)
    {
        final DateTime requestDate = sequence.getLastRequestDate();
        add(new Label("title", sequence.getRequestTitle()));
        add(new MultiLineLabel("theme", sequence.getRequestTheme()));
        add(new DateTimeLabel("date", getString(DATE_TIME_FORMAT), new Model(requestDate)));
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
