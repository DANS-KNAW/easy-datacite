package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.SecuredStreamingService;
import nl.knaw.dans.easy.web.template.Style;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import static nl.knaw.dans.easy.web.template.Style.AUDIO_VIDEO_HEADER_CONTRIBUTION;

@SuppressWarnings("serial")
public class AudioVideoPanel extends Panel {
    private static final Logger log = LoggerFactory.getLogger(AudioVideoPanel.class);

    @SpringBean(name = "securedStreamingService")
    private SecuredStreamingService securedStreamingService;

    @SpringBean(name = "audioVideoPlayerUrl")
    private String audioVideoPlayerUrl;

    public AudioVideoPanel(String id, String presentationPath) {
        super(id);
        String ticket = UUID.randomUUID().toString();
        String source = "";
        if (registerTicketForPresentation(presentationPath, ticket)) {
            try {
                source = new URL(audioVideoPlayerUrl + "?uri=" + presentationPath + "&ticket=" + ticket).toString();
            }
            catch (MalformedURLException e) {
                log.error("Video URL turned out invalid A/V Player URL = {}, presentation path = {}", audioVideoPlayerUrl, presentationPath);
            }
        }
        add(new PlayerFrame("audioVideoPlayer", source));
    }

    private boolean registerTicketForPresentation(String presentationPath, String ticket) {
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
