package nl.knaw.dans.easy.domain.model;

import nl.knaw.dans.easy.domain.model.PermissionSequence.State;

import org.joda.time.DateTime;

public interface PermissionConversation
{
    public enum Type
    {
        REQUEST,
        REPLY
    }

    State getState();

    DateTime getDate();

    String getRequestTitle();

    String getRequestTheme();

    String getReplyText();

    Type getType();

}


