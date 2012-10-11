package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractAuthenticationPage extends AbstractEasyNavPage
{
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractAuthenticationPage.class);

    public AbstractAuthenticationPage()
    {
        super();
    }

    public AbstractAuthenticationPage(final PageParameters paras)
    {
        super(paras);
    }

    public AbstractAuthenticationPage(final IPageMap pageMap, final PageParameters paras)
    {
        super(pageMap, paras);
    }

    public boolean signIn(Authentication authentication)
    {
        boolean signedIn;
        try
        {
            Services.getUserService().authenticate(authentication);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.INTERNAL_ERROR);
            LOGGER.error(message);
            throw new InternalWebError();
        }
        if (authentication.isCompleted())
        {
            signedIn = true;
            getEasySession().setLoggedIn(authentication);
        }
        else
        {
            signedIn = false;
        }
        return signedIn;
    }
}
