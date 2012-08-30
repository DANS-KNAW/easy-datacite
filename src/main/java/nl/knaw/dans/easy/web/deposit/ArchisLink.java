package nl.knaw.dans.easy.web.deposit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
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
    }

    @Override
    public void onClick()
    {
        if (!loggedOnAsArcheologistOrArchivistOrAdmin())
        {
            return;
        }

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
        return !EasySession.getSessionUser().isAnonymous()// @formatter:off
                && (EasySession.getSessionUser().isMemberOfGroup(Arrays.asList(Group.ID_ARCHEOLOGY)) || EasySession.getSessionUser().hasRole(Role.ADMIN) || EasySession
                        .getSessionUser().hasRole(Role.ARCHIVIST));
        // @formatter:on
    }
}
