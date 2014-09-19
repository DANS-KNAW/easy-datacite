package nl.knaw.dans.common.wicket.components.editablepanel;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;

import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * A panel that displays content from a model and provides the ability to edit this content.
 */
public class EditablePanel extends Panel {
    private static final long serialVersionUID = 3363588497570146613L;

    private final Form<String> form;
    private final EditPanel editPanel;
    private final ViewPanel viewPanel;
    private final PreprocessingModelDecorator decoratedViewModel;
    private final PostprocessingModelDecorator decoratedEditModel;
    private boolean inEditMode;
    private SessionContext context;

    /**
     * Object that pre- or post-processes the contents of the model before it is retrieved or written to the model object.
     */
    public interface Processor {
        String process(String content);
    }

    /**
     * The session context in which this editable panel operates.
     */
    public interface SessionContext extends Serializable {
        boolean isEditModeAllowed();
    }

    /**
     * Decorator that processes IModel content before returning it to the caller.
     */
    @SuppressWarnings("serial")
    private static class PreprocessingModelDecorator extends DefaultIModelDecorator<String> {
        private Processor processor;

        private PreprocessingModelDecorator(final IModel<String> decoratee) {
            super(decoratee);
        }

        private void setProcessor(final Processor processor) {
            this.processor = processor;
        }

        @Override
        public String getObject() {
            return processor == null ? super.getObject() : processor.process(decoratee.getObject());
        }
    }

    /**
     * Decorator that processes the new IModel object after it was changed by the caller.
     */
    @SuppressWarnings("serial")
    private static class PostprocessingModelDecorator extends DefaultIModelDecorator<String> {
        private Processor processor;

        private PostprocessingModelDecorator(final IModel<String> decoratee) {
            super(decoratee);
        }

        private void setProcessor(final Processor processor) {
            this.processor = processor;
        }

        @Override
        public void setObject(String object) {
            if (processor != null) {
                decoratee.setObject(processor.process(object));
            } else {
                decoratee.setObject(object);
            }
        }
    }

    /**
     * Initializes a new <code>EditablePanel</code>. The content can be edited using a text area. For rich text editing use
     * {@link #EditablePanel(String, IModel, TinyMCESettings)}.
     * 
     * @param id
     *        the component ID
     * @param model
     *        the component model
     */
    public EditablePanel(final String id, final IModel<String> model) {
        this(id, model, null);
    }

    /**
     * Initializes a new <code>EditablePanel</code>. The content editor can be either a plain text area or an instance of the Tiny MCE rich text editor. If no
     * <code>tinyMceSettings</code> are provided the text area is used.
     * 
     * @param id
     *        the component ID
     * @param model
     *        the component model
     * @param tinyMceSettings
     *        if Tiny MCE is to be used for editing the text: the settings for the editor
     * @param allowEdit
     *        if <code>true</code> the panel content can be edited if <code>false</code> the panel is read-only
     */
    public EditablePanel(final String id, final IModel<String> model, final TinyMCESettings tinyMceSettings) {
        super(id);
        form = new Form<String>("form");
        decoratedViewModel = new PreprocessingModelDecorator(model);
        decoratedEditModel = new PostprocessingModelDecorator(model);
        viewPanel = new ViewPanel("content", decoratedViewModel);
        editPanel = new EditPanel("content", decoratedEditModel, tinyMceSettings);
        form.add(viewPanel);
        form.add(createModeLink());
        form.add(createCancelLink());
        add(form);
    }

    /**
     * Sets the {@link Processor} for the view mode. The processor is called to process the content just after it is retrieved from the underlying model.
     * 
     * @param processor
     *        the <code>Processor</code> to set
     */
    public void setViewModeProcessor(final Processor processor) {
        decoratedViewModel.setProcessor(processor);
    }

    /**
     * Sets the {@link Processor} for the edit mode. The processor is called to process the content just before the content is set on the underlying model.
     * 
     * @param processor
     *        the <code>Processor</code> to set
     */
    public void setEditModeProcessor(final Processor processor) {
        decoratedEditModel.setProcessor(processor);
    }

    @SuppressWarnings("serial")
    private SubmitLink createModeLink() {
        final SubmitLink modeLink = new SubmitLink("modeLink") {
            @Override
            public void onSubmit() {
                inEditMode = !inEditMode;
                setContentPanel();
            }

            @Override
            public boolean isVisible() {
                return context.isEditModeAllowed();
            }
        };

        modeLink.add(new Label("modeLinkLabel", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                final ComponentStringResourceLoader resources = new ComponentStringResourceLoader();
                return resources.loadStringResource(EditablePanel.this, inEditMode ? "displayLinkLabel" : "editLinkLabel");
            }
        }));

        return modeLink;
    }

    @SuppressWarnings("serial")
    private Link<String> createCancelLink() {
        return new Link<String>("cancelLink") {
            @Override
            public void onClick() {
                inEditMode = false;
                setContentPanel();
            };

            @Override
            public boolean isVisible() {
                return inEditMode;
            }
        };
    }

    private void setContentPanel() {
        if (inEditMode && context.isEditModeAllowed()) {
            form.addOrReplace(editPanel);
        } else {
            form.addOrReplace(viewPanel);
        }
    }

    /**
     * Sets whether this <code>EditablePanel</code> shows a link to toggle between edit and view mode. If <code>allowEditMode</code> is <code>false</code> the
     * <code>EditablePanel</code> effectively behaves like a regular panel, i.e. it just displays its contents.
     * 
     * @param allowEditMode
     *        <code>true</code> to allow editing of the panel, <code>false</code> otherwise
     */
    public void setSessionContext(final SessionContext context) {
        this.context = context;
        setContentPanel();
    }
}
