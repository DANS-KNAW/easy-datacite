package nl.knaw.dans.common.wicket.components.jumpoff;

import java.io.File;
import java.util.List;

import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.xml.HtmlValidator;
import nl.knaw.dans.common.wicket.model.DMOModel;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import wicket.contrib.tinymce.TinyMceBehavior;

public abstract class JumpoffEditPanel extends Panel
{

    public static final String VALIDATE_BUTTON = "validate";
    public static final String VIEW_BUTTON = "view";
    public static final String CANCEL_BUTTON = "cancel";
    public static final String SUBMIT_BUTTON = "submit";
    private static final long serialVersionUID = -886878277947316135L;

    private TextArea<String> editor;
    private boolean initiated;

    public JumpoffEditPanel(final String id, final DMOModel<JumpoffDmo> model)
    {
        super(id, model);
    }

    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        @SuppressWarnings("rawtypes")
        Form form = new Form("editForm");

        editor = new TextArea<String>("editor", new Model<String>(getJumpoffDmo().getMarkupUnit().getHtml()));

        if (getJumpoffDmo().isInHtmlMode())
        {
            editor.add(new TinyMceBehavior(new DansTinyMCESettings()));
        }
        editor.setEscapeModelStrings(false);
        form.add(editor);

        form.add(new SubmitLink(VALIDATE_BUTTON)
        {
            private static final long serialVersionUID = 1778637177139377551L;

            @Override
            public void onSubmit()
            {
                validate();
            }

        });

        form.add(new SubmitLink(SUBMIT_BUTTON)
        {
            private static final long serialVersionUID = 918379270241898255L;

            @Override
            public void onSubmit()
            {
                setContents();
                onSaveButtonClicked();
            }
        });
        form.add(new SubmitLink(VIEW_BUTTON)
        {
            private static final long serialVersionUID = -2463272546185236888L;

            public void onSubmit()
            {
                setContents();
                onViewButtonClicked();
            }
        });
        form.add(new SubmitLink(CANCEL_BUTTON)
        {

            private static final long serialVersionUID = -491610584666052457L;

            public void onSubmit()
            {
                onCancelButtonClicked();
            }
        });

        add(form);
        // has no effect
        //form.add(new FormModificationDetectorBehavior());

        add(new SimpleUploadPanel("upload", getJumpoffDmo().getStoreId())
        {

            private static final long serialVersionUID = -3161938397225277632L;

            @Override
            protected void onUpload(File file)
            {
                getJumpoffDmo().addFile(file);
                onFileUpload();
            }

            @Override
            protected void onDelete(ResourceRef resourceRef)
            {
                JumpoffEditPanel.this.onDelete(resourceRef);
            }

            @Override
            protected List<ResourceRef> getUploadedResources()
            {
                return JumpoffEditPanel.this.getUploadedResources();
            }

        });
    }

    public abstract void onSaveButtonClicked();

    public abstract void onViewButtonClicked();

    public abstract void onCancelButtonClicked();

    public abstract void onFileUpload();

    public abstract void onDelete(ResourceRef resourceRef);

    public abstract List<ResourceRef> getUploadedResources();

    private JumpoffDmo getJumpoffDmo()
    {
        return (JumpoffDmo) getDefaultModelObject();
    }

    private void validate()
    {
        String content = editor.getDefaultModelObjectAsString();
        HtmlValidator validator = new HtmlValidator();
        validator.tidyHtml(content, true);
        for (String msg : validator.getErrorMessages())
        {
            error(msg);
        }
        for (String msg : validator.getWarningMessages())
        {
            warn(msg);
        }
        for (String msg : validator.getInfoMessages())
        {
            info(msg);
        }
        for (String msg : validator.getSummeryMessages())
        {
            info(msg);
        }
    }

    private void setContents()
    {
        String content = editor.getDefaultModelObjectAsString();
        getJumpoffDmo().getMarkupUnit().setHtml(content);
    }

}
