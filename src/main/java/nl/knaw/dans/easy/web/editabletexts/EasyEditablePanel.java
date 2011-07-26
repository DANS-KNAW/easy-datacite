package nl.knaw.dans.easy.web.editabletexts;

import java.io.File;
import java.io.FileNotFoundException;

import nl.knaw.dans.common.wicket.components.editablepanel.CompositeProcessor;
import nl.knaw.dans.common.wicket.components.editablepanel.EditablePanel;
import nl.knaw.dans.common.wicket.components.jumpoff.DansTinyMCESettings;
import nl.knaw.dans.common.wicket.model.TextFileModel;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.util.EasyHome;
import nl.knaw.dans.easy.web.EasySession;
import wicket.contrib.tinymce.settings.TinyMCESettings;

public class EasyEditablePanel extends EditablePanel
{
    private static final long serialVersionUID = 7660924727415870315L;

    public EasyEditablePanel(final String id, final String contentPath, final Object... placeholders)
    {
        this(id, contentPath);
        setViewModeProcessor(new CompositeProcessor( //
                new PlainTextViewProcessor(contentPath.toUpperCase().endsWith(".TXT")), //
                new MailComposerProcessor(placeholders))); //
        setEditModeProcessor(new CompositeProcessor( //
                new TildeRestorerProcessor(), //
                new CorrectTinyMceUrlMessUpProcessor()));
    }

    @SuppressWarnings("serial")
    public EasyEditablePanel(final String id, final String contentPath)
    {
        super(id, new TextFileModel(getContentFile(contentPath)), getTinyMceSettings(contentPath));
        setSessionContext(new EditablePanel.SessionContext()
        {
            @Override
            public boolean isEditModeAllowed()
            {
                return (EasySession.getSessionUser().hasRole(Role.ADMIN, Role.ARCHIVIST));
            }
        });

    }

    private static File getContentFile(final String contentPathInEasyHomeDirectory)
    {
        try
        {
            return new File(EasyHome.getLocation(), contentPathInEasyHomeDirectory);
        }
        catch (final FileNotFoundException e)
        {
            throw new RuntimeException(String.format("Could not read file from EASY_HOME directory: '%s'", contentPathInEasyHomeDirectory));
        }
    }

    private static TinyMCESettings getTinyMceSettings(final String contentPath)
    {
        return contentPath.toUpperCase().endsWith(".TXT") ? null : new DansTinyMCESettings();
    }
}
