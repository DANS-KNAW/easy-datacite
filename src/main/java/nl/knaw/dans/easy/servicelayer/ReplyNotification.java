package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;

public class ReplyNotification extends DatasetNotification implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final String        TEMPLATE_LOCATION = "permission/%sReplyNotification";
    private final PermissionReplyModel replyModel;
    private final PermissionSequence   sequence;
    private final String               templateLocation;

    public ReplyNotification(final Dataset dataset, final PermissionSequence sequence, final PermissionReplyModel replyModel)
    {
        super(dataset, sequence.getRequester(), sequence, replyModel);
        this.sequence = sequence;
        this.replyModel = replyModel;
        templateLocation = String.format(TEMPLATE_LOCATION, replyModel.getState().toString());
    }

    @Override
    String getTemplateLocation()
    {
        return templateLocation;
    }

    public String getRequesterName()
    {
        return sequence.getRequester().getDisplayName();
    }
    
    public String getDepositorName()
    {
        return getDataset().getDepositor().getDisplayName();
    }

    public String getDepositorId()
    {
        return getDataset().getDepositor().getId();
    }

    public String getDatasetTitle()
    {
        return getDataset().getPreferredTitle();
    }

    public String getRequestLink()
    {
        return replyModel.getRequestLink();
    }

    public String getExplanation()
    {
        return replyModel.getExplanation();
    }

    public String getDatasetLink()
    {
        return replyModel.getDatasetLink();
    }

}
