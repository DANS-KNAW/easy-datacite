package nl.knaw.dans.easy.web.editabletexts;

import java.io.Serializable;

import nl.knaw.dans.common.lang.mail.MailComposer;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.common.wicket.components.editablepanel.EditablePanel;

class MailComposerProcessor implements EditablePanel.Processor, Serializable {
    private static final long serialVersionUID = -7514592002301370946L;

    private Object[] placeholders;
    private transient MailComposer composer;

    MailComposerProcessor(final Object... placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public String process(final String content) {
        try {
            return getComposer().compose(content);
        }
        catch (final MailComposerException e) {
            throw new RuntimeException(String.format("Template processing error: '%s'", e.getMessage()));
        }
    }

    private MailComposer getComposer() {
        if (composer == null) {
            composer = new MailComposer(placeholders);
        }

        return composer;
    }
}
