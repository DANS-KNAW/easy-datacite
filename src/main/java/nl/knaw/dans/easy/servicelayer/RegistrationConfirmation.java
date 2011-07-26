package nl.knaw.dans.easy.servicelayer;

import nl.knaw.dans.easy.domain.authn.Registration;

public class RegistrationConfirmation extends AbstractNotification
{
    public RegistrationConfirmation(final Registration registration)
    {
        super(registration.getUser(), registration);
    }

    @Override
    String getTemplateLocation()
    {
        return "authn/RegistrationMail";
    }

}
