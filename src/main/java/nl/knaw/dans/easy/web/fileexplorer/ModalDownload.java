package nl.knaw.dans.easy.web.fileexplorer;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy.TriState;
import nl.knaw.dans.common.lang.service.exceptions.FileSizeException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.common.lang.service.exceptions.TooManyFilesException;
import nl.knaw.dans.common.lang.service.exceptions.ZipFileLengthException;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem.Type;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.DownloadStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.view.dataset.UnitMetaDataResource;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModalDownload extends Panel
{
    private static final long serialVersionUID = 1L;

    // TODO: GK: refactor this class to make it smaller and more readable

    private static final Logger logger = LoggerFactory.getLogger(ModalDownload.class);

    public static final String EDITABLE_DOWNLOAD_DIALOG_TEMPLATE = "/pages/DownloadDialog.template";

    private static final String MSG_NO_SELECTION = "download.noSelection";
    private static final String MSG_NO_ACCESS = "download.noAccess";
    private static final String MSG_SOME_ACCESS = "download.someAccess";
    private static final String MSG_READY = "download.ready";
    private static final String MSG_ACCEPT = "download.accept";
    private static final String MSG_ACCEPT_ADDITIONAL = "download.acceptAdditional";
    private static final String MSG_DONT_SHOW = "download.dontShowAgain";
    private static final String MSG_ZIP_SIZE_TOLARGE = "download.zipSizeToLarge";
    private static final String MSG_FILE_SIZE_TOLARGE = "download.fileSizeToLarge";
    private static final String MSG_TOO_MANY_FILES = "download.tooManyFiles";

    public ModalDownload(final ModalWindow window, ITreeItem item, DatasetModel datasetModel)
    {
        super(window.getContentId());

        List<ITreeItem> items = new ArrayList<ITreeItem>();
        items.add(item);
        addContent(window, items, datasetModel, false);
    }

    public ModalDownload(final ModalWindow window, List<ITreeItem> items, DatasetModel datasetModel)
    {
        super(window.getContentId());

        addContent(window, items, datasetModel, true);
    }

    private void addContent(final ModalWindow window, List<ITreeItem> items, final DatasetModel datasetModel, boolean zipped)
    {
        add(new DisableDefaultConfirmBehavior());

        StringResourceModel message = null;

        boolean downloadAllowed = false;
        boolean hasInaccessible = false;

        EasyEditablePanel editableMessage = new EasyEditablePanel("editablePanel", EDITABLE_DOWNLOAD_DIALOG_TEMPLATE);
        add(editableMessage);

        ArrayList<RequestedItem> requestedItems = new ArrayList<RequestedItem>();
        for (ITreeItem item : items)
        {
            TreeItem concreteItem = (TreeItem) item;
            AuthzStrategy strategy = concreteItem.getItemVO().getAuthzStrategy();
            if (item.getType().equals(Type.FILE) && strategy.canUnitBeRead(EasyFile.UNIT_ID) || item.getType().equals(Type.FOLDER)
                    && !strategy.canChildrenBeRead().equals(TriState.NONE))
            {
                requestedItems.add(new RequestedItem(item.getId()));
                if (strategy.canChildrenBeRead().equals(TriState.SOME))
                {
                    hasInaccessible = true;
                }
            }
            else
            {
                hasInaccessible = true;
            }
        }

        if (items.size() == 0)
        {
            message = new StringResourceModel(MSG_NO_SELECTION, this, null);
            downloadAllowed = false;
        }
        else if (requestedItems.size() == 0)
        {
            message = new StringResourceModel(MSG_NO_ACCESS, this, null);
            downloadAllowed = false;
        }
        else if (hasInaccessible)
        {
            message = new StringResourceModel(MSG_SOME_ACCESS, this, null);
            downloadAllowed = true;
        }
        else
        {
            message = new StringResourceModel(MSG_READY, this, null);
            downloadAllowed = true;
        }

        editableMessage.setVisible(downloadAllowed);

        AbstractLink link = null;
        try
        {

            if (downloadAllowed)
            {
                final Dataset dataset = datasetModel.getObject();
                if (!zipped && requestedItems.size() == 1 && requestedItems.get(0).isFile())
                {
                    // SINGLE FILE DOWNLOAD
                    try
                    {
                        final FileContentWrapper fcw = Services.getItemService().getContent(EasySession.getSessionUser(), dataset,
                                new DmoStoreId(requestedItems.get(0).getStoreId()));

                        link = createSingleDownloadLink(window, dataset, fcw);
                    }
                    catch (FileSizeException e)
                    {
                        logger.info("File size is to large: " + e.getAmount() + ", while the limit is " + e.getLimit() + " Bytes");
                        // file exceeds size limit
                        message = new StringResourceModel(MSG_FILE_SIZE_TOLARGE, this, new Model<FileSizeException>(e));
                        downloadAllowed = false;
                    }
                }
                else
                {
                    // ZIP FILE DOWNLOAD
                    try
                    {
                        final ZipFileContentWrapper zfcw = Services.getItemService().getZippedContent(EasySession.getSessionUser(), dataset, requestedItems);
                        link = createZipDownloadLink(window, dataset, zfcw);
                    }
                    catch (TooManyFilesException e)
                    {
                        logger.info("Too many files requested for download (" + e.getAmount() + "). Limit is " + e.getLimit() + " files.", e.getMessage());
                        // zip exceeds number of files limit
                        message = new StringResourceModel(MSG_TOO_MANY_FILES, this, new Model<TooManyFilesException>(e));
                        downloadAllowed = false;
                    }
                    catch (ZipFileLengthException e)
                    {
                        logger.info("Zip size is to large: " + e.getAmount() + ", while the limit is " + e.getLimit() + " Bytes");
                        // zip exceeds size limit
                        message = new StringResourceModel(MSG_ZIP_SIZE_TOLARGE, this, new Model<ZipFileLengthException>(e));
                        downloadAllowed = false;
                    }
                }
            }
        }
        catch (ServiceRuntimeException e)
        {
            logger.error("Error while creating download link.", e);
        }
        catch (ServiceException e)
        {
            logger.error("Error while creating download link.", e);
        }

        final Label msg = new Label("message", message);
        msg.setEscapeModelStrings(false);
        add(msg);

        if (link == null)
            link = new ExternalLink("downloadLink", "#");

        final Model<AbstractLink> linkModel = new Model<AbstractLink>(link);
        link.setVisible(downloadAllowed);
        link.setEnabled(false);
        link.setOutputMarkupId(true);
        add(link);

        IndicatingAjaxLink<Void> cancel = createCancelLink(window);

        add(cancel);

        final Model<Boolean> conditionsAccepted = new Model<Boolean>(EasySession.getSessionUser().hasAcceptedGeneralConditions());
        final UnitMetaDataResource additionalLicenseResource = Util.getAdditionalLicenseResource(datasetModel);
        final Component additionalLicense = new ResourceLink<UnitMetaDataResource>("additionalLicense", additionalLicenseResource);
        final boolean hasAdditionalLicense = additionalLicenseResource != null;
        additionalLicense.setVisible(downloadAllowed && hasAdditionalLicense);
        add(additionalLicense);
        final Model<Boolean> additionalAccepted = new Model<Boolean>(!hasAdditionalLicense);

        link.setEnabled(conditionsAccepted.getObject() && additionalAccepted.getObject());

        final AjaxCheckBox dontShowBox = createDontShowAgainLink(conditionsAccepted);

        final Label dontShowBoxLabel = new Label("dontShowAgainLabel", new ResourceModel(MSG_DONT_SHOW));
        dontShowBox.setVisible(!EasySession.getSessionUser().isAnonymous() && downloadAllowed && !EasySession.getSessionUser().hasAcceptedGeneralConditions());
        dontShowBoxLabel.setVisible(downloadAllowed && !EasySession.getSessionUser().hasAcceptedGeneralConditions());
        add(dontShowBox);
        add(dontShowBoxLabel);

        final AjaxCheckBox acceptBox = createAcceptCheckBox(linkModel, conditionsAccepted, additionalAccepted, dontShowBox);

        final Label acceptBoxLabel = new Label("acceptLabel", new ResourceModel(MSG_ACCEPT));
        acceptBox.setVisible(downloadAllowed && !EasySession.getSessionUser().hasAcceptedGeneralConditions());
        acceptBoxLabel.setVisible(downloadAllowed && !EasySession.getSessionUser().hasAcceptedGeneralConditions());
        add(acceptBox);
        add(acceptBoxLabel);

        final AjaxCheckBox acceptAdditionalBox = createAcceptAdditionalCheckBox(linkModel, conditionsAccepted, additionalAccepted);
        add(acceptAdditionalBox);
        final Label acceptAdditionalBoxLabel = new Label("acceptAdditionalLabel", new ResourceModel(MSG_ACCEPT_ADDITIONAL));
        add(acceptAdditionalBoxLabel);
    }

    private AjaxCheckBox createAcceptAdditionalCheckBox(final Model<AbstractLink> linkModel, final Model<Boolean> conditionsAccepted,
            final Model<Boolean> additionalAccepted)
    {
        return new AjaxCheckBox("acceptAdditional", new Model<Boolean>(false))
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                additionalAccepted.setObject(this.getModelObject());
                linkModel.getObject().setEnabled(conditionsAccepted.getObject() && additionalAccepted.getObject());
                target.addComponent(linkModel.getObject());
            }
        };
    }

    private AjaxCheckBox createAcceptCheckBox(final Model<AbstractLink> linkModel, final Model<Boolean> conditionsAccepted,
            final Model<Boolean> additionalAccepted, final AjaxCheckBox dontShowBox)
    {
        return new AjaxCheckBox("accept", new Model<Boolean>(false))
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                conditionsAccepted.setObject(this.getModelObject());
                linkModel.getObject().setEnabled(conditionsAccepted.getObject() && additionalAccepted.getObject());
                target.addComponent(linkModel.getObject());
                boolean dontShow = dontShowBox.getModelObject() && this.getModelObject();
                dontShowBox.setModel(new Model<Boolean>(dontShow));
                if (dontShow)
                {
                    EasyUser user = EasySession.getSessionUser();
                    user.setAcceptedGeneralConditions(this.getModelObject() && conditionsAccepted.getObject());
                    try
                    {
                        Services.getUserService().update(user, EasySession.getSessionUser());
                    }
                    catch (ServiceException e)
                    {
                        logger.error("Error while updating don't show again.", e);
                    }
                }
            }
        };
    }

    private AjaxCheckBox createDontShowAgainLink(final Model<Boolean> conditionsAccepted)
    {
        return new AjaxCheckBox("dontShowAgain", new Model<Boolean>(false))
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                EasyUser user = EasySession.getSessionUser();
                user.setAcceptedGeneralConditions(this.getModelObject() && conditionsAccepted.getObject());
                try
                {
                    Services.getUserService().update(user, EasySession.getSessionUser());
                }
                catch (ServiceException e)
                {
                    logger.error("Error while updating don't show again.", e);
                }
            }
        };
    }

    private IndicatingAjaxLink<Void> createCancelLink(final ModalWindow window)
    {
        return new IndicatingAjaxLink<Void>("cancel")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }

        };
    }

    private IndicatingAjaxLink<Void> createZipDownloadLink(final ModalWindow window, final Dataset dataset, final ZipFileContentWrapper zfcw)
    {
        return new IndicatingAjaxLink<Void>("downloadLink")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                final AJAXDownload download = createZippedAjaxDownload(zfcw);
                window.getParent().add(download);
                download.initiate(target);
                // register this download action
                Services.getItemService().registerDownload(EasySession.getSessionUser(), dataset, zfcw.getDownloadedItemVOs());
                // close download popup
                window.close(target);
                StatisticsLogger.getInstance().logEvent(StatisticsEvent.DOWNLOAD_DATASET_REQUEST, new DatasetStatistics(dataset), new DownloadStatistics(zfcw),
                        new DisciplineStatistics(dataset));
            }
        };
    }

    private AJAXDownload createZippedAjaxDownload(final ZipFileContentWrapper zfcw)
    {
        return new AJAXDownload()
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected IResourceStream getResourceStream()
            {
                return new FileResourceStream(zfcw.getZipFile());
            }

            @Override
            protected String getFileName()
            {
                return zfcw.getFilename();
            }
        };
    }

    private IndicatingAjaxLink<Void> createSingleDownloadLink(final ModalWindow window, final Dataset dataset, final FileContentWrapper fcw)
    {
        return new IndicatingAjaxLink<Void>("downloadLink")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                final AJAXDownload download = createSingleAjaxDownload(fcw);
                window.getParent().add(download);
                download.initiate(target);
                // register this download action
                List<ItemVO> downloadList = new ArrayList<ItemVO>();
                downloadList.add(fcw.getFileItemVO());
                Services.getItemService().registerDownload(EasySession.getSessionUser(), dataset, downloadList);
                // close download popup
                window.close(target);
                StatisticsLogger.getInstance().logEvent(StatisticsEvent.DOWNLOAD_FILE_REQUEST, new DatasetStatistics(dataset), new DownloadStatistics(fcw),
                        new DisciplineStatistics(dataset));
            }
        };
    }

    private AJAXDownload createSingleAjaxDownload(final FileContentWrapper fcw)
    {
        return new AJAXDownload()
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected IResourceStream getResourceStream()
            {
                return new UrlResourceStream(fcw.getURL());
            }

            @Override
            protected String getFileName()
            {
                return fcw.getFileName();
            }
        };
    }

    /*
     * GK: Deze functie zorgt ervoor dat de browser geen confirm box toont als men op de download link
     * klikt van de modalBox
     */
    private class DisableDefaultConfirmBehavior extends AbstractBehavior implements IHeaderContributor
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void renderHead(IHeaderResponse response)
        {
            response.renderOnDomReadyJavascript("Wicket.Window.unloadConfirmation = false");
        }
    }
}
