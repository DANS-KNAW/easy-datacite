package nl.knaw.dans.easy.web.fileexplorer2;

import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.view.dataset.UploadFileMetadataProcess;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class ModalImport extends Panel
{
    private static final long serialVersionUID = 1L;

    public ModalImport(final ModalWindow window, final DatasetModel dataset)
    {
        super(window.getContentId());

        addFileMetaDataUploadPanel(window, dataset);

        add(new IndicatingAjaxLink<Void>("close")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }

        });
    }

    private void addFileMetaDataUploadPanel(final ModalWindow window, final DatasetModel dataset)
    {
        final EasyUpload upload = new EasyUpload("fileMetadataUploadPanel", configureFileMetadataUpload())
        {
            private static final long serialVersionUID = 0L;

            @Override
            public IUploadPostProcess createPostProcess(final Class<? extends IUploadPostProcess> pclass)
            {
                final IUploadPostProcess result = super.createPostProcess(pclass);
                ((UploadFileMetadataProcess) result).setDataset(dataset.getObject());
                return result;
            }
        };
        upload.registerPostProcess(UploadFileMetadataProcess.class);
        add(upload);
        add(new ComponentFeedbackPanel("fileMetadataUploadPanel-componentFeedback", upload));
    }

    private EasyUploadConfig configureFileMetadataUpload()
    {
        final EasyUploadConfig config = new EasyUploadConfig();
        config.setAutoRemoveFiles(true);
        return config;
    }
}
