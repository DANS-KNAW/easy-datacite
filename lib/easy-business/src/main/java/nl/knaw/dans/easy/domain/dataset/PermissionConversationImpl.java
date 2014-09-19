package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.easy.domain.model.PermissionConversation;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;

import org.joda.time.DateTime;

public class PermissionConversationImpl extends AbstractTimestampedJiBXObject<PermissionConversationImpl> implements PermissionConversation {

    private static final long serialVersionUID = -6945866936979010472L;

    private final Type type;

    private State state;

    private DateTime date;
    private String requestTitle;
    private String requestTheme;
    private String replyText;

    @SuppressWarnings("unused")
    private PermissionConversationImpl() {
        this.type = null;
    }

    public PermissionConversationImpl(Type type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    protected void setState(State state) {
        this.state = state;
    }

    public DateTime getDate() {
        return date;
    }

    protected void setDate(DateTime date) {
        this.date = date;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    protected void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequestTheme() {
        return requestTheme;
    }

    protected void setRequestTheme(String requestTheme) {
        this.requestTheme = requestTheme;
    }

    public String getReplyText() {
        return replyText;
    }

    protected void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public Type getType() {
        return type;
    }

}
