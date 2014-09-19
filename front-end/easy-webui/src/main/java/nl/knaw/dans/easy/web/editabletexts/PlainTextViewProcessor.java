package nl.knaw.dans.easy.web.editabletexts;

import java.io.Serializable;

import nl.knaw.dans.common.wicket.components.editablepanel.EditablePanel;

@SuppressWarnings("serial")
class PlainTextViewProcessor implements EditablePanel.Processor, Serializable {
    private final boolean isPlainText;

    PlainTextViewProcessor(boolean isPlainText) {
        this.isPlainText = isPlainText;
    }

    @Override
    public String process(String content) {
        if (isPlainText) {
            return "<pre>" + content + "</pre>";
        } else {
            return content;
        }
    }
}
