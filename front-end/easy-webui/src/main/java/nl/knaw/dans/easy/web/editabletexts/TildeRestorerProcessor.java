package nl.knaw.dans.easy.web.editabletexts;

import java.io.Serializable;

import nl.knaw.dans.common.wicket.components.editablepanel.EditablePanel;

/**
 * Preprocessor that restores the tildes (<code>~</code>) that where replaced with HTML entities by TinyMCE.
 */
@SuppressWarnings("serial")
public class TildeRestorerProcessor implements EditablePanel.Processor, Serializable {
    @Override
    public String process(String content) {
        return content.replace("&#126;", "~");
    }

}
