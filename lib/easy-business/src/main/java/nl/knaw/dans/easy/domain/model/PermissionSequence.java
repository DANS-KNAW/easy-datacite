package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.joda.time.DateTime;

public interface PermissionSequence extends Serializable, XMLBean
{

    public enum State
    {
        Submitted, Returned, Granted, Denied
    }

    State getState();

    DateTime getLastStateChange();

    boolean isAcceptingConditionsOfUse();

    String getRequesterId();

    EasyUser getRequester();

    String getRequestTitle();

    String getRequestTheme();

    String getReplyText();

    DateTime getLastRequestDate();

    DateTime getLastReplyDate();

    PermissionRequestModel getRequestModel();

    PermissionReplyModel getReplyModel();

    boolean isGranted();

    List<PermissionConversation> getBackLog();

    PermissionRequestSearchInfo getSearchInfo();
}
