package nl.knaw.dans.easy.web.view.dataset;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.UUID;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.SecuredStreamingService;

import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class AudioVideoPanel extends Panel implements IHeaderContributor {
    private static final Logger log = LoggerFactory.getLogger(AudioVideoPanel.class);

    @SpringBean(name = "securedStreamingService")
    private SecuredStreamingService securedStreamingService;

    @SpringBean(name = "audioVideoPlayerUrl")
    private String playerUrl;

    @SpringBean(name = "audioVideoVersion")
    private String version;

    @SpringBean(name = "audioVideoInstallerUrl")
    private String installerUrl;

    private String presentationPath;

    public AudioVideoPanel(String id, String presentationPath) {
        super(id);
        this.presentationPath = presentationPath;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String ticket = UUID.randomUUID().toString();
        if (available(presentationPath, playerUrl, version, installerUrl) && registerTicketForPresentation(ticket, presentationPath)) {
            response.renderString( //
            "<script>\n" + //
                    " var presentation = \"" + presentationPath + "\";\n" + //
                    " var ticket = \"" + ticket + "\";\n" + //
                    " var playerUrl = \"" + playerUrl + "\";\n" + //
                    " var version = \"" + version + "\";\n" + //
                    " var installerUrl = \"" + installerUrl + "\";\n" + //
                    "</script>");

        }
    }

    private boolean available(String... vars) {
        for (String v : vars) {
            if (isBlank(v))
                return false;
        }
        return true;
    }

    private boolean registerTicketForPresentation(String ticket, String presentationPath) {
        try {
            securedStreamingService.addSecurityTicketToResource(ticket, presentationPath);
            return true;
        }
        catch (ServiceException e) {
            log.error("Could not register ticket for presention '{}'", presentationPath);
            return false;
        }
    }
}
