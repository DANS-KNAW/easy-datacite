package nl.knaw.dans.easy.web.editabletexts;

import java.io.Serializable;
import java.util.regex.Pattern;

import nl.knaw.dans.common.lang.mail.MailComposer;
import nl.knaw.dans.common.wicket.components.editablepanel.EditablePanel;

/**
 * <p>
 * TinyMCE messes up URLs that contain place holders for the {@link MailComposer} because it thinks they
 * are relative URLs. The setting TinyMCESettings#setRelativeUrls is <code>false</code> for EASY, because
 * setting it to <code>true</code> causes TinyMCE to try and make <em>all</em> URLs relative, which
 * causes bugs with jump-off-pages.
 * <p>
 * In short, we fix this when saving the editable text by replacing /ui/~ with ~. Of course, this means
 * there is now a dependency to the context root.
 * <p>
 * See: EASY-291: Dependency naar context-root vanuit editable text vanwege TinyMCE-probleem met
 * place-holder bevattende URLs.
 */
public class CorrectTinyMceUrlMessUpProcessor implements EditablePanel.Processor, Serializable
{
    private static final long serialVersionUID = 5653486250159930621L;
    private static final Pattern contextRootBeforePlaceHolder = Pattern.compile("/ui/~");

    @Override
    public String process(String content)
    {
        return contextRootBeforePlaceHolder.matcher(content).replaceAll("~");
    }

}
