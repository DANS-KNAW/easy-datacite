package nl.knaw.dans.common.wicket.components.jumpoff;

import java.io.File;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;

public abstract class SimpleUploadPanel extends Panel
{
    private static final long serialVersionUID = -3255093961248274439L;

    /**
     * List view for files in upload folder.
     */
    private class FileListView extends ListView<ResourceRef>
    {
        private static final long serialVersionUID = 8632880113438585573L;

        public FileListView(String name, final IModel<List<ResourceRef>> files)
        {
            super(name, files);
        }

        @Override
        protected void populateItem(ListItem<ResourceRef> listItem)
        {
            final ResourceRef rr = listItem.getModelObject();
            listItem.add(new Label("filename", rr.getFilename()));
            listItem.add(new Label("mimeType", rr.getMimeType()));
            listItem.add(new Label("href", rr.getHref()));
            listItem.add(new ExternalLink("view", rr.getHref()));
            Link<Void> deleteLink = new Link<Void>("delete")
            {
                private static final long serialVersionUID = 1776911700200182352L;

                @Override
                public void onClick()
                {
                    onDelete(rr);
                }

            };
            deleteLink.add(new SimpleAttributeModifier("onclick", "return confirm('Are you sure you want to delete " + rr.getFilename() + "?');"));
            deleteLink.setVisible(!rr.isReferenced());
            listItem.add(deleteLink);
        }
    }

    /**
     * Form for uploads.
     */
    private class FileUploadForm extends Form<Void>
    {
        private static final long serialVersionUID = -4112925958579197849L;
        private FileUploadField fileUploadField;

        public FileUploadForm(String name)
        {
            super(name);
            setMultiPart(true);
            add(fileUploadField = new FileUploadField("fileInput"));

            setMaxSize(Bytes.gigabytes(2L));

        }

        @Override
        protected void onSubmit()
        {
            final FileUpload upload = fileUploadField.getFileUpload();
            if (upload != null)
            {
                File newFile = new File(getUploadFolder(), upload.getClientFileName());
                checkFileExists(newFile);
                try
                {
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                    onUpload(newFile);
                    SimpleUploadPanel.this.info("saved file: " + upload.getClientFileName());
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file");
                }
            }
        }
    }

    private final FileListView fileListView;

    public SimpleUploadPanel(String id, String containerId)
    {
        super(id);

        //Folder uploadFolder = getUploadFolder();
        final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
        add(uploadFeedback);

        // Add simple upload form, which is hooked up to its feedback panel by
        // virtue of that panel being nested in the form.
        final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload");
        add(simpleUploadForm);

        // Add folder view
        add(new Label("container", containerId));
        fileListView = new FileListView("fileList", new LoadableDetachableModel<List<ResourceRef>>()
        {
            private static final long serialVersionUID = 1407827349630811991L;

            @Override
            protected List<ResourceRef> load()
            {
                return getUploadedResources();
            }
        });
        add(fileListView);

    }

    private void checkFileExists(File newFile)
    {
        if (newFile.exists())
        {
            if (!Files.remove(newFile))
            {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    private Folder getUploadFolder()
    {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File uploadDir = new File(tmpDir, Session.get().getId());
        uploadDir.mkdirs();
        Folder folder = new Folder(uploadDir);
        return folder;//((UploadApplication)Application.get()).getUploadFolder();
    }

    protected abstract void onUpload(File file);

    protected abstract void onDelete(ResourceRef resourceRef);

    protected abstract List<ResourceRef> getUploadedResources();

}
