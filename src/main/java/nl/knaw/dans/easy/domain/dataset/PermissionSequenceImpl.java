package nl.knaw.dans.easy.domain.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.easy.domain.model.PermissionConversation;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestSearchInfo;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionConversation.Type;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.joda.time.DateTime;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PermissionSequenceImpl extends AbstractTimestampedJiBXObject<PermissionSequence> implements PermissionSequence
{

    private static final long serialVersionUID = 1826412349612885182L;
    
    private final String requesterId;
    private EasyUser requester;
    private State state;
    
    private DateTime lastRequestDate;
    private DateTime lastReplyDate;
    private String requestTitle;
    private String requestTheme;
    private String replyText;
    
    private boolean acceptConditionsOfUse;
    
    private DateTime stateLastModified;
    
    private List<PermissionConversation> backLog = new ArrayList<PermissionConversation>();

    
    @SuppressWarnings("unused")
    private PermissionSequenceImpl()
    {
        requesterId = null;
    }
    
    public PermissionSequenceImpl(EasyUser requester)
    {
        this.requesterId = requester.getId();
        this.requester = requester;
    }
    
    /** only use for migration **/
    public PermissionSequenceImpl(String requesterId)
    {
        this.requesterId = requesterId;
    }
    
    public State getState()
    {
        return state;
    }
    
    public void setState(State state)
    {
        if (evaluateDirty(this.state, state))
        {
            stateLastModified = new DateTime();
        }
        this.state = state;
    }
    
    public DateTime getLastStateChange()
    {
        return stateLastModified;
    }
    
    public boolean isAcceptingConditionsOfUse()
    {
        return acceptConditionsOfUse;
    }
    
    public void setAcceptingConditionsOfUse(boolean acceptingConditionsOfUse)
    {
        this.acceptConditionsOfUse = acceptingConditionsOfUse;
    }
    
    public String getRequesterId()
    {
        return requesterId;
    }
    
    public EasyUser getRequester()
    {
        if (requester == null && requesterId != null)
        {
            requester = RepoAccess.getDelegator().getUser(requesterId);
        }
        return requester;
    }  
    
    public String getRequestTitle()
    {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle)
    {
        this.requestTitle = requestTitle;
        
    }

    public String getRequestTheme()
    {
        return requestTheme;
    }

    public void setRequestTheme(String requestTheme)
    {
        this.requestTheme = requestTheme;
    }

    public String getReplyText()
    {
        return replyText;
    }

    public void setReplyText(String replyText)
    {
        this.replyText = replyText;
    }

    public DateTime getLastRequestDate()
    {
        return lastRequestDate;
    }

    public DateTime getLastReplyDate()
    {
        return lastReplyDate;
    }
    
    public boolean isGranted()
    {
        return State.Granted.equals(state);
    }
    
    public PermissionRequestSearchInfo getSearchInfo()
    {
    	PermissionRequestSearchInfo result = new PermissionRequestSearchInfo();
    	result.setRequesterId(requesterId);
    	result.setState(state);
    	result.setStateLastModified(stateLastModified);
    	return result;
    }
   
    public PermissionRequestModel getRequestModel()
    {
        PermissionRequestModel request = new PermissionRequestModel();
        request.setAcceptingConditionsOfUse(acceptConditionsOfUse);
        request.setRequestTheme(requestTheme);
        request.setRequestTitle(requestTitle);
        return request;
    }
    
    public PermissionReplyModel getReplyModel()
    {
        PermissionReplyModel reply = new PermissionReplyModel(requesterId);
        reply.setExplanation(replyText);
        reply.setState(state);
        return reply;
    }
    
    public void updateRequest(PermissionRequestModel request)
    {
        logCurrentRequest();
        setAcceptingConditionsOfUse(request.isAcceptingConditionsOfUse());
        setRequestTitle(request.getRequestTitle());
        setRequestTheme(request.getRequestTheme());
        lastRequestDate = new DateTime();

        setState(State.Submitted);
    }

    public void updateReply(PermissionReplyModel reply)
    {
        if (!requesterId.equals(reply.getRequesterId()))
        {
            throw new IllegalArgumentException("The reply model with requesterId " + reply.getRequesterId() 
                    + " does not belong to the sequence with requesterId " + requesterId);
        }
        logCurrentReply();
        setReplyText(reply.getExplanation());
        setState(reply.getState());
        lastReplyDate = new DateTime();
    }

    public List<PermissionConversation> getBackLog()
    {
        return backLog;
    }
    
    private void logCurrentRequest()
    {
        if (lastRequestDate != null)
        {
            PermissionConversationImpl conversation = new PermissionConversationImpl(Type.REQUEST);
            conversation.setDate(lastRequestDate);
            conversation.setRequestTitle(requestTitle);
            conversation.setRequestTheme(requestTheme);
            conversation.setState(state);
            backLog.add(conversation);
        }
    }
    
    private void logCurrentReply()
    {
        if (lastReplyDate != null)
        {
            PermissionConversationImpl conversation = new PermissionConversationImpl(Type.REPLY);
            conversation.setDate(lastReplyDate);
            conversation.setReplyText(replyText);
            conversation.setState(state);
            backLog.add(conversation);
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return new HashCodeBuilder(-1670986549, 382131205)
            .appendSuper(super.hashCode())
            .append(this.stateLastModified)
            .append(this.requestTheme)
            .append(this.lastReplyDate)
            .append(this.backLog)
            .append(this.requesterId)
            .append(this.lastRequestDate)
            .append(this.acceptConditionsOfUse)
            .append(this.state)
            .append(this.replyText)
            .append(this.requestTitle)
            .toHashCode();
    }
    
    // only use for Migration!!
    // TODO remove method after migration.
    public void stripDates()
    {
        lastReplyDate = null;
        lastRequestDate = null;
        for (PermissionConversation pc : backLog)
        {
            ((PermissionConversationImpl) pc).setDate(null);
        }
    }
    
    
    
}
