package nl.knaw.dans.common.wicket.components.jumpoff;

import org.apache.wicket.ResourceReference;

import wicket.contrib.tinymce.settings.Button;
import wicket.contrib.tinymce.settings.ContextMenuPlugin;
import wicket.contrib.tinymce.settings.DateTimePlugin;
import wicket.contrib.tinymce.settings.DirectionalityPlugin;
import wicket.contrib.tinymce.settings.FullScreenPlugin;
import wicket.contrib.tinymce.settings.IESpellPlugin;
import wicket.contrib.tinymce.settings.MediaPlugin;
import wicket.contrib.tinymce.settings.PastePlugin;
import wicket.contrib.tinymce.settings.PreviewPlugin;
import wicket.contrib.tinymce.settings.PrintPlugin;
import wicket.contrib.tinymce.settings.SearchReplacePlugin;
import wicket.contrib.tinymce.settings.TablePlugin;
import wicket.contrib.tinymce.settings.TinyMCESettings;

public class DansTinyMCESettings extends TinyMCESettings {
    public static final String JUMPOFF_MARKUP_CSS = "JUMPOFF_MARKUP_CSS";
    private static final long serialVersionUID = -5469817405455196606L;

    public DansTinyMCESettings() {
        super(TinyMCESettings.Theme.advanced);

        initDefaultSettings();

        TablePlugin tablePlugin = new TablePlugin();
        IESpellPlugin iespellPlugin = new IESpellPlugin();
        MediaPlugin mediaPlugin = new MediaPlugin();
        PrintPlugin printPlugin = new PrintPlugin();
        FullScreenPlugin fullScreenPlugin = new FullScreenPlugin();
        DirectionalityPlugin directionalityPlugin = new DirectionalityPlugin();
        PastePlugin pastePlugin = new PastePlugin();
        SearchReplacePlugin searchReplacePlugin = new SearchReplacePlugin();
        DateTimePlugin dateTimePlugin = new DateTimePlugin();
        dateTimePlugin.setDateFormat("Date: %d-%m-%Y");
        dateTimePlugin.setTimeFormat("Time: %H:%M");
        PreviewPlugin previewPlugin = new PreviewPlugin();

        // first toolbar
        add(Button.newdocument, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
        add(printPlugin.getPrintButton(), TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
        add(Button.separator, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);

        add(Button.fontselect, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        add(Button.fontsizeselect, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        add(Button.forecolor, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        add(Button.backcolor, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);

        // second toolbar
        add(Button.cut, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(Button.copy, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(pastePlugin.getPasteButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(pastePlugin.getPasteTextButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(pastePlugin.getPasteWordButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);

        add(searchReplacePlugin.getSearchButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(searchReplacePlugin.getReplaceButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
        add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);

        add(dateTimePlugin.getDateButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
        add(dateTimePlugin.getTimeButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
        add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);

        add(previewPlugin.getPreviewButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);

        // third toolbar
        add(tablePlugin.getTableControls(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.before);
        add(iespellPlugin.getIespellButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
        add(mediaPlugin.getMediaButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
        add(Button.separator, TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
        add(Button.separator, TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);

        add(directionalityPlugin.getLtrButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
        add(directionalityPlugin.getRtlButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
        add(Button.separator, TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);

        add(fullScreenPlugin.getFullscreenButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
    }

    /**
     * Called from constructor. Subclasses may override.
     */
    protected void initDefaultSettings() {
        setToolbarAlign(TinyMCESettings.Align.left);
        setToolbarLocation(TinyMCESettings.Location.top);
        setStatusbarLocation(TinyMCESettings.Location.bottom);
        setContentCss(new ResourceReference(JUMPOFF_MARKUP_CSS));
        setRelativeUrls(false);
        addCustomSetting("entity_encoding : 'numeric'");
        addCustomSetting("forced_root_block : ''");
        addCustomSetting("extended_valid_elements : 'script[language|type]'");
        setResizing(true);
        ContextMenuPlugin contextMenuPlugin = new ContextMenuPlugin();
        register(contextMenuPlugin);
        setStatusbarLocation(null);
        disableButton(Button.help);
    }
}
