package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoffLink extends Link
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LogoffLink.class);

    public LogoffLink(final String wicketId)
    {
        super(wicketId);
    }

    @Override
    public void onClick()
    {
        logger.debug("Logoutlink is clicked");
        StatisticsLogger.getInstance().logEvent(StatisticsEvent.USER_LOGOUT);
        EasySession.get().setLoggedOff();
        setRedirect(true);
        setResponsePage(Application.get().getHomePage());
    }

    @Override
    public boolean isVisible()
    {
        return isUserLoggedOn();
    }

    private static boolean isUserLoggedOn()
    {
        return !EasySession.getSessionUser().isAnonymous();
    }

    @Override
    public boolean getStatelessHint() // NOPMD: Wicket method.
    {
        return true;
    }
}
