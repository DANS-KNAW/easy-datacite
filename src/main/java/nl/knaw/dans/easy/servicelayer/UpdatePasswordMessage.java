package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class UpdatePasswordMessage extends AbstractNotification implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String url;

    public UpdatePasswordMessage(final EasyUser receiver, final ForgottenPasswordMessenger messenger)
    {
        super(receiver);
        url = messenger.getUpdateURL() + "/" + messenger.getUserIdParamKey() + "/" + receiver.getId() + "/";
    }

    public String getURL()
    {
        return url;
    }

    String getTemplateLocation()
    {
        return "authn/UpdatePasswordMail";
    }

}
