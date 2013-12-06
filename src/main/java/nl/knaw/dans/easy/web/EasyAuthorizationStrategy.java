package nl.knaw.dans.easy.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.easy.web.authn.login.LoginPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.wicket.SecureEasyPageLink;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class EasyAuthorizationStrategy implements IAuthorizationStrategy
{
    private static Logger LOGGER = LoggerFactory.getLogger(EasyAuthorizationStrategy.class);

    public boolean isActionAuthorized(final Component component, final Action action)
    {
        final List<String> items = new ArrayList<String>();
        AbstractEasyPage easyPage = null;
        if (component instanceof SecureEasyPageLink)
        {
            final SecureEasyPageLink protectedLink = (SecureEasyPageLink) component;
            final String item = protectedLink.getTarget().getName();
            items.add(item);
        }
        else
        {
            final Page page = component.findParent(Page.class);
            if (page instanceof AbstractEasyPage)
            {
                easyPage = (AbstractEasyPage) page;
            }
            String item = page == null ? "" : component.getPage().getClass().getName() + ":" + component.getPageRelativePath();
            items.add(item);

            if (page != null)
            {
                Class<?> superPage = component.getPage().getClass().getSuperclass();
                while (superPage != null && ClassUtil.instanceOf(superPage, AbstractEasyPage.class))
                {
                    item = superPage.getName() + ":" + component.getPageRelativePath();
                    items.add(item);
                    superPage = superPage.getSuperclass();
                }
            }
        }

        for (final String item : items)
        {
            if (Security.getAuthz().hasSecurityOfficer(item))
            {
                final SecurityOfficer officer = Security.getAuthz().getSecurityOfficer(item);
                final EasySession session = EasySession.get();
                ContextParameters ctxParameters;
                if (easyPage != null)
                {
                    ctxParameters = easyPage.getContextParameters();
                }
                else
                {
                    ctxParameters = session.getContextParameters();
                }
                if (Component.RENDER.equals(action))
                {
                    return officer.isComponentVisible(ctxParameters);
                }
                else if (Component.ENABLE.equals(action))
                {
                    return officer.isEnableAllowed(ctxParameters);
                }
                return true;
            }
        }
        if (component instanceof ComponentWithSecurityOfficer)
        {
            LOGGER.error(component.getClass().getName() + " should have a SecurityOfficer for at least one of "
                    + Arrays.deepToString(items.toArray()));
            return false;
        }
        return true;
    }

    public boolean isInstantiationAuthorized(@SuppressWarnings("rawtypes")
    final Class componentClass)
    {
        if (WebPage.class.isAssignableFrom(componentClass))
        {
            if (Security.getAuthz().isProtectedPage(componentClass.getName()))
            {
                final boolean sessionExists = Session.exists();
                final EasySession session = EasySession.get();
                if (session.getUser().isAnonymous())
                {
                    LOGGER.info("Redirecting user (" + EasyWicketApplication.getUserIpAddress() + ") with session ("
                            + (sessionExists ? session.getId() : "null") + ") to login page. User is not allowed to access page " + componentClass.getName());

                    throw new RestartResponseAtInterceptPageException(LoginPage.class);
                }
                else
                {
                    SecurityOfficer officer = Security.getAuthz().getSecurityOfficer(componentClass.getName());
                    return officer.isEnableAllowed(EasySession.get().getContextParameters());

                }
            }
        }
        return true;
    }
}
