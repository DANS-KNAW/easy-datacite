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

import java.util.UUID;

import static nl.knaw.dans.easy.web.template.Style.AUDIO_VIDEO_HEADER_CONTRIBUTION;

@SuppressWarnings("serial")
public class AudioVideoPanel extends Panel {
    private static final Logger log = LoggerFactory.getLogger(AudioVideoPanel.class);

    @SpringBean(name = "securedStreamingService")
    private SecuredStreamingService securedStreamingService;

    public AudioVideoPanel(String id, String presentationPath) {
        super(id);
        log.debug("presentationPath = " + presentationPath);
        Component component = createScriptComponent(presentationPath);
        if (StringUtils.isBlank(presentationPath))
            component.setVisible(false);
        else
            registerTicketForPresentation(presentationPath);
        add(AUDIO_VIDEO_HEADER_CONTRIBUTION);
        add(component);
    }

    private Component createScriptComponent(String presentationPath) {
        // @formatter:off
        final String script =
                "        $(document).ready(function(){\n" +
                "            $('.presentation').krusty({\n" +
                "                'uri': '" + presentationPath + "',\n" +
                "                'quality': '360p'\n" +
                "            });\n" +
                "        })\n";
        // @formatter:on
        return new Label("presentationScript") {
            @Override
            protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
                // a plain label would replace the quotes with &#039;
                replaceComponentTagBody(markupStream, openTag, script);
            }
        };
    }

    private boolean registerTicketForPresentation(String presentationPath) {
        String ticket = UUID.randomUUID().toString();
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
