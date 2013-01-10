package nl.knaw.dans.easy.web.editabletexts;

import nl.knaw.dans.common.wicket.components.editablepanel.CompositeProcessor;
import nl.knaw.dans.common.wicket.components.editablepanel.EditablePanel;
import nl.knaw.dans.common.wicket.components.jumpoff.DansTinyMCESettings;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.wicketutil.HomeDirBasedTextFileModel;
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
        super(id, new HomeDirBasedTextFileModel(contentPath), getTinyMceSettings(contentPath));
        setSessionContext(new EditablePanel.SessionContext()
        {
            @Override
            public boolean isEditModeAllowed()
            {
                return (EasySession.getSessionUser().hasRole(Role.ADMIN, Role.ARCHIVIST));
            }
        });
    }

    private static TinyMCESettings getTinyMceSettings(final String contentPath)
    {
        return contentPath.toUpperCase().endsWith(".TXT") ? null : new DansTinyMCESettings();
    }
}
