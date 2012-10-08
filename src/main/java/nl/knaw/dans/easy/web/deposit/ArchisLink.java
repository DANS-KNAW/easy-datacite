package nl.knaw.dans.easy.web.deposit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.AbortException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ArchisLink extends Link<String>
{
    private static final Logger logger = LoggerFactory.getLogger(ArchisLink.class);

    private static final long serialVersionUID = 6730630139772319892L;

    private final String number;

    public ArchisLink(final String id, final String labelWicketId, final String number)
    {
        super(id);
        this.number = number;
        add(new Label(labelWicketId, number));
        setEnabled(loggedOnAsArcheologistOrArchivistOrAdmin());
    }

    @Override
    public void onClick()
    {
        final WebResponse response = (WebResponse) getResponse();
        response.setAttachmentHeader(number + ".pdf");
        response.setContentType("application/pdf");
        try
        {
            final URL url = new URL(ArchisCollector.ARCHIS_PDF + number);
            response.write(url.openStream());
            throw new AbortException();
        }
        catch (final MalformedURLException e)
        {
            logger.error("Malformed URL: ", e);
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (final IOException e)
        {
            logger.error("IOException: ", e);
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_BAD_GATEWAY);
        }

    }

    private boolean loggedOnAsArcheologistOrArchivistOrAdmin()
    {
        final EasyUser user = EasySession.getSessionUser();
        if (user.isAnonymous())
            return false;
        if (user.hasRole(Role.ARCHIVIST))
            return true;
        if (user.hasRole(Role.ADMIN))
            return true;
        if (user.isMemberOfGroup(Arrays.asList(Group.ID_ARCHEOLOGY)))
            return true;
        return false;
    }
}
