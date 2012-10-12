package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.ApplicationUser;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.protocol.https.RequireHttps;

@RequireHttps
public class RegistrationPage extends AbstractEasyNavPage implements EasyResources
{
    public static final String EDITABLE_REGISTRATION_TEMPLATE = "/editable/Registration.template";

    private static final String REGISTRATION_FORM = "registrationForm";

    public RegistrationPage()
    {
        setStatelessHint(true);
        add(new RegistrationForm(REGISTRATION_FORM));
    }

    public RegistrationPage(String federationUserId, String institute)
    {
        add(new RegistrationForm(REGISTRATION_FORM, new ApplicationUser(), federationUserId, institute));
    }
}
