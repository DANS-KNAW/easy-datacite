package nl.knaw.dans.easy.web.fileexplorer2;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.unzip.UnzipPostProcess;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.upload.postprocess.ingest.IngestPostProcess;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModalUpload extends Panel
{
    private static final long serialVersionUID = 1L;

    public ModalUpload(final ModalWindow window, final DatasetModel dataset, final ITreeItem folder)
    {
        super(window.getContentId());

        add(new Label("folder", "'" + folder.getName() + "'"));

        add(buildUploadPanel(dataset, folder.getId()));

        add(new IndicatingAjaxLink<Void>("close")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onCustomCloseButtonClick(target);
                window.close(target);
            }

        });
    }

    abstract protected void onCustomCloseButtonClick(AjaxRequestTarget target);

    private WebMarkupContainer buildUploadPanel(final DatasetModel model, final String folderSid)
    {
        final WebMarkupContainer uploadPanelContainer = new WebMarkupContainer("uploadPanel");

        final EasyUpload easyUpload = new EasyUpload("easyUploadPanel")
        {

            private static final long serialVersionUID = -8966163487482572856L;

            @Override
            public IUploadPostProcess createPostProcess(final Class<? extends IUploadPostProcess> pclass)
            {

                final IUploadPostProcess rtn = super.createPostProcess(pclass);

                if (rtn instanceof IngestPostProcess)
                {
                    ((IngestPostProcess) rtn).setParentSid(folderSid);
                    ((IngestPostProcess) rtn).setModel(model);
                }

                return rtn;
            }
        };
        // uploadPanelContainer.easyUpload.setVisible((sid != null && !sid.trim().equals("")));
        easyUpload.registerPostProcess(UnzipPostProcess.class);
        easyUpload.registerPostProcess(IngestPostProcess.class);
        // deposit ingest post process takes care of the files, so let's delete the files
        // after they have been ingested

        uploadPanelContainer.add(easyUpload);

        final WebMarkupContainer uploadPanelHolder = new WebMarkupContainer("depositUploadPanelbuttonsPanel");
        uploadPanelHolder.add(new SimpleAttributeModifier("style", "display: none"));
        uploadPanelContainer.add(uploadPanelHolder);
        return uploadPanelContainer;
    }
}
