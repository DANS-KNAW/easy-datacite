package nl.knaw.dans.easy.web.fileexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy.TriState;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.common.lang.service.exceptions.TooManyFilesException;
import nl.knaw.dans.common.lang.service.exceptions.ZipFileLengthException;
import nl.knaw.dans.common.wicket.EnumChoiceRenderer;
import nl.knaw.dans.common.wicket.components.explorer.ExplorerPanel;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem.Type;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.common.StyledModalWindow;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.DownloadStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.Script;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wickettree.ITreeProvider;

public class FileExplorer extends AbstractDatasetModelPanel {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(FileExplorer.class);

    private static final String MSG_ZIP_SIZE_TOLARGE = "download.zipSizeToLarge";
    private static final String MSG_TOO_MANY_FILES = "download.tooManyFiles";

    final private ExplorerPanel explorer;
    final private ITreeProvider<ITreeItem> treeProvider;
    final private ArrayList<ITreeItem> selectedFiles = new ArrayList<ITreeItem>();
    final private ArrayList<ITreeItem> selectedFolders = new ArrayList<ITreeItem>();
    private boolean archivistView = false;
    private boolean depositorView = false;
    private boolean showTools = false;
    private boolean published = false;

    @SpringBean(name = "itemService")
    private ItemService itemService;

    public FileExplorer(String id, final DatasetModel datasetModel) {
        super(id, datasetModel);

        add(Script.JQUERY_UI_CONTRIBUTION);
        add(Script.FILEEXPLORER_RESIZE_CONTRIBUTION);

        // check if archivist or depsitor view should be enabled
        if (EasySession.getSessionUser().hasRole(Role.ARCHIVIST)) {
            archivistView = true;
        } else if (datasetModel.getObject().hasDepositor(EasySession.getSessionUser())) {
            depositorView = true;
        }
        showTools = archivistView || (depositorView && datasetModel.getObject().getAdministrativeState().equals(DatasetState.DRAFT));
        published = datasetModel.getObject().getAdministrativeState().equals(DatasetState.PUBLISHED);

        // initialize a ModalWindow for file details
        final ModalWindow modalFileDetailsWindow = new StyledModalWindow("modalFileDetails", "File details", 400);
        add(modalFileDetailsWindow);

        // initialize a ModalWindow for downloads
        final ModalWindow modalDownloadWindow = new StyledModalWindow("modalDownload", "Notice: Download", 400);
        add(modalDownloadWindow);

        // initialize a ModalWindow for delete files
        final ModalWindow modalDeleteWindow = new StyledModalWindow("modalDelete", "Delete file(s)/folder(s)", 450);
        add(modalDeleteWindow);

        // initialize a ModalWindow for importing file metadata
        final ModalWindow modalImportWindow = new StyledModalWindow("modalImport", "Import file metadata", 450);
        add(modalImportWindow);

        // initialize a ModalWindow for uploads
        final ModalWindow modalUploadWindow = new StyledModalWindow("modalUpload", "Upload files", 450);
        modalUploadWindow.setCloseButtonCallback(createCloseButtonCallback());
        add(modalUploadWindow);

        // initialize a ModalWindow for messages
        final ModalWindow modalMessageWindow = new StyledModalWindow("modalMessage", "Message", 450);
        add(modalMessageWindow);

        Form<Void> filterForm = new Form<Void>("filterForm");
        final HashMap<Enum<?>, CheckBox> filterMap = new HashMap<Enum<?>, CheckBox>();
        createFilterCheckboxes(filterForm, filterMap, CreatorRole.values(), "creator");
        createFilterCheckboxes(filterForm, filterMap, VisibleTo.values(), "visible");
        createFilterCheckboxes(filterForm, filterMap, AccessibleTo.values(), "access");
        filterForm.setVisible(archivistView);
        filterForm.add(createFilterSubmitLink(filterMap));
        add(filterForm);

        treeProvider = new TreeItemProvider(datasetModel.getDmoStoreId(), filterMap);
        explorer = createExplorerPanel();

        // create legend
        final Label legend = new Label("legend");
        legend.setVisible(false);
        add(legend);

        // initialize columns, archivist has 6 columns, depositor has 5 columns, normal user has 4
        // columns
        IColumn<?>[] columns;
        if (archivistView) {
            columns = new IColumn[6];
        } else if (depositorView) {
            columns = new IColumn[5];
            // also show legend in depositor's case
            legend.setVisible(true);
        } else {
            columns = new IColumn[4];
        }

        columns[0] = createFileFolderColumn();

        // a small check to see if this dataset has an Additional License
        final boolean hasAdditionalLicense = Util.getAdditionalLicenseResource(datasetModel) != null;

        columns[1] = createNameColumn(datasetModel, modalDownloadWindow, hasAdditionalLicense);
        columns[2] = createSizeColumn();

        if (archivistView) {
            columns[3] = new PropertyColumn<Void>(new Model<String>("Creator"), "creator", "creator");
            columns[4] = new PropertyColumn<Void>(new Model<String>("Visible to"), "visibleTo", "visibleTo");
            columns[5] = new PropertyColumn<Void>(new Model<String>("Accessible to"), "accessibleTo", "accessibleTo");
        } else if (depositorView) {
            columns[3] = new PropertyColumn<Void>(new Model<String>("Visible to"), "visibleTo", "visibleTo");
            columns[4] = new PropertyColumn<Void>(new Model<String>("Accessible to"), "accessibleTo", "accessibleTo");
        } else {
            columns[3] = createAccessibleToColumn(datasetModel);
        }
        explorer.setColumns(columns);
        explorer.setOutputMarkupId(true);
        add(explorer);

        add(creatFileDetailsLinks(datasetModel, modalFileDetailsWindow));
        add(createDownloadLink(datasetModel, modalDownloadWindow, modalMessageWindow, hasAdditionalLicense));
        add(createUploadLink(datasetModel, modalUploadWindow));
        add(createImportLink(datasetModel, modalImportWindow));
        add(createDeleteLink(datasetModel, modalDeleteWindow));

        ArrayList<VisibleTo> visibleToList = new ArrayList<VisibleTo>(Arrays.asList(VisibleTo.values()));
        visibleToList.remove(VisibleTo.RESTRICTED_REQUEST); // GK: milco requested to turn off the
                                                            // possibility of group and request on the
                                                            // visibility of files
        visibleToList.remove(VisibleTo.RESTRICTED_GROUP);
        final EnumChoiceRenderer<VisibleTo> visibleToRenderer = new EnumChoiceRenderer<VisibleTo>(this, "Rights");
        final DropDownChoice<VisibleTo> viewRights = new DropDownChoice<VisibleTo>("viewRights", new Model<VisibleTo>(), visibleToList, visibleToRenderer);
        final EnumChoiceRenderer<AccessibleTo> accessibleToRenderer = new EnumChoiceRenderer<AccessibleTo>(this, "Rights");
        final DropDownChoice<AccessibleTo> accessRights = new DropDownChoice<AccessibleTo>("accessRights", new Model<AccessibleTo>(),
                Arrays.asList(AccessibleTo.values()), accessibleToRenderer);
        viewRights.setNullValid(true);
        accessRights.setNullValid(true);

        Form<Void> rightsForm = createRightsForm();

        // GK: these behaviours make sure AJAX round trips are made to the server whenever the value
        // changes.
        // Should AjaxFormComponentUpdatingBehavior be used instead maybe?
        viewRights.add(getAjaxFormSubmitBehavior(rightsForm));
        accessRights.add(getAjaxFormSubmitBehavior(rightsForm));

        rightsForm.add(viewRights);
        rightsForm.add(accessRights);
        rightsForm.add(createApplyFileRightsLink(datasetModel, viewRights, accessRights));

        add(rightsForm);
    }

    private AjaxSubmitLink createFilterSubmitLink(final HashMap<Enum<?>, CheckBox> filterMap) {
        return new AjaxSubmitLink("filtersSubmitLink") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // in filterMap we can lookup wich filters are enabled/disabled
                // we reload the treeProvider, clear the tableProvider
                // and update the explorer
                ((TreeItemProvider) treeProvider).setFilters(filterMap);
                selectedFiles.clear();
                selectedFolders.clear();
                ((TreeItemProvider) treeProvider).reload();
                explorer.getTableProvider().getList().clear();
                ITreeItem root = treeProvider.getRoots().next();
                explorer.getContent().setSelected(treeProvider.model(root));
                explorer.initialExpand(root);
                explorer.getBreadcrumbPanel().update(target, explorer.getContent().getSelected().getObject());
                target.addComponent(explorer);
            }
        };
    }

    private ExplorerPanel createExplorerPanel() {
        return new ExplorerPanel("explorer", null, treeProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            public void selectedFolderChanged(IModel<ITreeItem> selected) {
                selectedFiles.clear();
                selectedFolders.clear();
            }

            @Override
            public void selectAllClicked(AjaxRequestTarget target) {
                selectedFiles.clear();
                selectedFolders.clear();
                ArrayList<ITreeItem> items = this.getContent().getSelected().getObject().getChildrenWithFiles();
                for (ITreeItem item : items) {
                    if (item.getType().equals(Type.FILE)) {
                        selectedFiles.add(item);
                    } else {
                        selectedFolders.add(item);
                    }
                }
                target.addComponent(this);
            }

            @Override
            public void selectNoneClicked(AjaxRequestTarget target) {
                selectedFiles.clear();
                selectedFolders.clear();
                target.addComponent(this);
            }
        };
    }

    private AbstractColumn<Void> createFileFolderColumn() {
        return new AbstractColumn<Void>(new Model<String>("")) {
            private static final long serialVersionUID = 1L;

            public void populateItem(Item cellItem, String componentId, final IModel rowModel) {
                final ITreeItem item = ((ITreeItem) (rowModel.getObject()));
                Model<Boolean> checked = new Model<Boolean>();
                checked.setObject(selectedFiles.contains(item) || selectedFolders.contains(item)); // selection
                                                                                                   // memoization
                cellItem.add(createFileCheckBoxPanel(componentId, item, checked));
            }
        };
    }

    private CheckboxPanel createFileCheckBoxPanel(String componentId, final ITreeItem item, Model<Boolean> checked) {
        return new CheckboxPanel(componentId, checked) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSelectionChange(AjaxRequestTarget target) {
                ArrayList<ITreeItem> list;
                if (item.getType().equals(Type.FILE)) {
                    list = selectedFiles;
                } else {
                    list = selectedFolders;
                }
                if (list.contains(item)) {
                    list.remove(item);
                } else {
                    list.add(item);
                }
            }
        };
    }

    private AbstractColumn<Void> createSizeColumn() {
        return new AbstractColumn<Void>(new Model<String>("Size"), "size") {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item cellItem, String componentId, final IModel rowModel) {
                cellItem.add(new SizePanel(componentId, ((TreeItem) (rowModel.getObject())).getSizeAsString()));
            }
        };
    }

    private AbstractColumn<Void> createNameColumn(final DatasetModel datasetModel, final ModalWindow modalDownloadWindow, final boolean hasAdditionalLicense) {
        return new AbstractColumn<Void>(new Model<String>("Name"), "name") {
            private static final long serialVersionUID = 1L;

            public void populateItem(Item cellItem, String componentId, final IModel rowModel) {
                try {
                    cellItem.add(createNamePanel(datasetModel, modalDownloadWindow, hasAdditionalLicense, componentId, rowModel));
                }
                catch (ServiceRuntimeException e) {
                    logger.error("Error creating direct download link for single file.", e);
                }
                catch (ServiceException e) {
                    logger.error("Error creating direct download link for single file.", e);
                }
            }
        };
    }

    private NamePanel createNamePanel(final DatasetModel datasetModel, final ModalWindow modalDownloadWindow, final boolean hasAdditionalLicense,
            String componentId, final IModel rowModel) throws ServiceException
    {
        return new NamePanel(componentId, rowModel, explorer.getTree(), datasetModel, explorer.getContent(), hasAdditionalLicense, new AjaxLink<Void>("link") {
            private static final long serialVersionUID = 1L;

            public void onClick(AjaxRequestTarget target) {
                // show download popup
                ITreeItem treeItem = (ITreeItem) rowModel.getObject();
                modalDownloadWindow.setContent(new ModalDownload(modalDownloadWindow, treeItem, datasetModel));
                modalDownloadWindow.show(target);
            }
        });
    }

    private Form<Void> createRightsForm() {
        return new Form<Void>("rightsForm") {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return archivistView && !published;
            }
        };
    }

    private IndicatingAjaxLink<Void> createApplyFileRightsLink(final DatasetModel datasetModel, final DropDownChoice<VisibleTo> viewRights,
            final DropDownChoice<AccessibleTo> accessRights)
    {
        return new IndicatingAjaxLink<Void>("applyFileRightsLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                List<ITreeItem> items = new ArrayList<ITreeItem>();
                items.addAll(selectedFiles);
                items.addAll(selectedFolders);
                List<DmoStoreId> sidList = new ArrayList<DmoStoreId>();
                for (ITreeItem item : items) {
                    sidList.add(new DmoStoreId(item.getId()));
                }
                try {
                    VisibleTo newVisibleTo = viewRights.getModelObject();
                    AccessibleTo newAccessibleTo = accessRights.getModelObject();
                    itemService.updateObjects(EasySession.getSessionUser(), datasetModel.getObject(), sidList, new UpdateInfo(newVisibleTo, newAccessibleTo,
                            null, false));
                }
                catch (ServiceRuntimeException e) {
                    logger.error("Error applying file rights.", e);
                }
                catch (ServiceException e) {
                    logger.error("Error applying file rights: ", e);
                }
                IModel<ITreeItem> selected = explorer.getContent().getSelected();
                selected.getObject().getChildren().clear();
                selected.getObject().getChildrenWithFiles().clear();
                explorer.getContent().setSelectedAndUpdate(selected, explorer.getTree(), target);
            }

            @Override
            public boolean isVisible() {
                return archivistView && !published;
            }
        };
    }

    private AbstractColumn<Void> createAccessibleToColumn(final DatasetModel datasetModel) {
        return new AbstractColumn<Void>(new Model<String>("Accessible"), "accessibleTo") {
            private static final long serialVersionUID = 1L;

            public void populateItem(Item cellItem, String componentId, final IModel rowModel) {
                TreeItem item = (TreeItem) (rowModel.getObject());
                AuthzStrategy strategy = item.getItemVO().getAuthzStrategy();
                if (item.getType().equals(Type.FILE)) {
                    AuthzMessage message = strategy.getSingleReadMessage();
                    cellItem.add(new Label(componentId, new StringResourceModel(message.getMessageCode(), datasetModel)));
                } else {
                    cellItem.add(new Label(componentId, new ResourceModel(strategy.canChildrenBeRead().toString())));
                }
            }
        };
    }

    private IndicatingAjaxLink<Void> createDeleteLink(final DatasetModel datasetModel, final ModalWindow modalDeleteWindow) {
        return new IndicatingAjaxLink<Void>("deleteLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ArrayList<ITreeItem> items = new ArrayList<ITreeItem>();
                items.addAll(selectedFiles);
                items.addAll(selectedFolders);
                modalDeleteWindow.setContent(createModalDeleteContent(datasetModel, modalDeleteWindow, items));
                modalDeleteWindow.show(target);
            }

            @Override
            public boolean isVisible() {
                return showTools && !published;
            }
        };
    }

    private ModalDelete createModalDeleteContent(final DatasetModel datasetModel, final ModalWindow modalDeleteWindow, ArrayList<ITreeItem> items) {
        return new ModalDelete(modalDeleteWindow, items, datasetModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateAfterDelete(AjaxRequestTarget target) {
                ITreeItem currentFolder = (ITreeItem) explorer.getContent().getSelected().getObject();
                for (ITreeItem file : selectedFiles) {
                    currentFolder.removeChild(file);
                }
                for (ITreeItem folder : selectedFolders) {
                    currentFolder.removeChild(folder);
                }
                selectedFiles.clear();
                selectedFolders.clear();
                target.addComponent(explorer);
            }
        };
    }

    private AjaxLink<Void> createImportLink(final DatasetModel datasetModel, final ModalWindow modalImportWindow) {
        return new AjaxLink<Void>("importLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalImportWindow.setContent(new ModalImport(modalImportWindow, datasetModel));
                modalImportWindow.show(target);
            }

            @Override
            public boolean isVisible() {
                return archivistView && !published;
            }
        };
    }

    private IndicatingAjaxLink<Void> createUploadLink(final DatasetModel datasetModel, final ModalWindow modalUploadWindow) {
        return new IndicatingAjaxLink<Void>("uploadLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalUploadWindow.setContent(createModalUploadContent(datasetModel, modalUploadWindow));
                modalUploadWindow.show(target);
            }

            @Override
            public boolean isVisible() {
                return showTools && !published;
            }
        };
    }

    private ModalUpload createModalUploadContent(final DatasetModel datasetModel, final ModalWindow modalUploadWindow) {
        return new ModalUpload(modalUploadWindow, datasetModel, explorer.getContent().getSelected().getObject()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onCustomCloseButtonClick(AjaxRequestTarget target) {
                refreshCurrentFolder(target);
            }
        };
    }

    private IndicatingAjaxLink<Void> creatFileDetailsLinks(final DatasetModel datasetModel, final ModalWindow modalFileDetailsWindow) {
        return new IndicatingAjaxLink<Void>("fileDetailsLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalFileDetailsWindow.setContent(new ModalFileDetails(modalFileDetailsWindow, selectedFiles, datasetModel));
                modalFileDetailsWindow.show(target);
            }
        };
    }

    private IndicatingAjaxLink<Void> createDownloadLink(final DatasetModel datasetModel, final ModalWindow modalDownloadWindow,
            final ModalWindow modalMessageWindow, final boolean hasAdditionalLicense)
    {
        return new IndicatingAjaxLink<Void>("downloadLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ArrayList<RequestedItem> requestedItems = getSelectionAsRequestedItems();
                Dataset dataset = datasetModel.getObject();
                EasyUser sessionUser = EasySession.getSessionUser();

                if (isAnonymousDownloadAllowed(dataset)) {
                    streamDownloadDirectly(modalMessageWindow, target, requestedItems, dataset, sessionUser);
                } else {
                    boolean canAllChildrenBeRead = dataset.getAuthzStrategy().canChildrenBeRead().equals(TriState.ALL);
                    boolean requestedEqualsSelected = requestedItems.size() == (selectedFiles.size() + selectedFolders.size());
                    if (requestedItems.size() != 0 && (canAllChildrenBeRead || requestedEqualsSelected) && !hasAdditionalLicense
                            && sessionUser.hasAcceptedGeneralConditions())
                    {
                        streamDownloadDirectly(modalMessageWindow, target, requestedItems, dataset, sessionUser);
                    } else {
                        showDownloadPopup(datasetModel, modalDownloadWindow, target);
                    }
                }
            }
        };
    }

    private boolean isAnonymousDownloadAllowed(Dataset dataset) {
        if (selectedFiles.isEmpty() && selectedFolders.isEmpty()) {
            // check complete dataset
            ArrayList<ITreeItem> items = ((TreeItemProvider) treeProvider).getRoot().getChildrenWithFiles();
            return isAllAccessible(items);
        } else {
            // check all selected items
            return (isAllAccessible(selectedFiles) && isAllAccessible(selectedFolders));
        }
    }

    private boolean isAllAccessible(List<ITreeItem> items) {
        boolean allAccessible = true;
        ListIterator<ITreeItem> iter = items.listIterator();
        while (allAccessible && iter.hasNext()) {
            TreeItem item = (TreeItem) iter.next();
            AuthzStrategy strategy = item.getItemVO().getAuthzStrategy();
            // @formatter:off
            allAccessible = (item.getAccessibleTo().equalsIgnoreCase(AccessibleTo.ANONYMOUS.toString()))
                     && ((item.getType().equals(Type.FILE) && strategy.canUnitBeRead(EasyFile.UNIT_ID))
                             || (item.getType().equals(Type.FOLDER) && strategy.canChildrenBeRead().equals(TriState.ALL)));
            // @formatter:on
        }
        return allAccessible;
    }

    private void showDownloadPopup(final DatasetModel datasetModel, final ModalWindow modalDownloadWindow, AjaxRequestTarget target) {
        ArrayList<ITreeItem> items;
        if (selectedFiles.size() > 0 || selectedFolders.size() > 0) {
            // download whatever is selected
            items = new ArrayList<ITreeItem>();
            items.addAll(selectedFiles);
            items.addAll(selectedFolders);
        } else {
            // nothing is selected so download everything in current dataset
            items = ((TreeItemProvider) treeProvider).getRoot().getChildrenWithFiles();
        }
        modalDownloadWindow.setContent(new ModalDownload(modalDownloadWindow, items, datasetModel));
        modalDownloadWindow.show(target);
    }

    private void streamDownloadDirectly(final ModalWindow modalMessageWindow, AjaxRequestTarget target, ArrayList<RequestedItem> requestedItems,
            Dataset dataset, EasyUser sessionUser)
    {
        try {
            final ZipFileContentWrapper zfcw = itemService.getZippedContent(sessionUser, dataset, requestedItems);
            final AJAXDownload download = AJAXDownload.create(zfcw);
            add(download);
            download.initiate(target);
            // register this download action
            itemService.registerDownload(getSessionUser(), dataset, zfcw.getDownloadedItemVOs());
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.DOWNLOAD_DATASET_REQUEST, new DatasetStatistics(dataset), new DownloadStatistics(zfcw),
                    new DisciplineStatistics(dataset));
        }
        catch (TooManyFilesException e) {
            logger.info("Too many files requested for download (" + e.getAmount() + "). Limit is " + e.getLimit() + " files.", e.getMessage());
            // download can't be handled so show a message
            modalMessageWindow.setContent(new ModalPopup(modalMessageWindow, new StringResourceModel(MSG_TOO_MANY_FILES, this,
                    new Model<TooManyFilesException>(e)).getObject()));
            modalMessageWindow.show(target);
        }
        catch (ZipFileLengthException e) {
            logger.info("File size too large for download!", e.getMessage());
            // download can't be handled so show a message
            modalMessageWindow.setContent(new ModalPopup(modalMessageWindow, new StringResourceModel(MSG_ZIP_SIZE_TOLARGE, this,
                    new Model<ZipFileLengthException>(e)).getObject()));
            modalMessageWindow.show(target);
        }
        catch (ServiceRuntimeException e) {
            logger.error("Error creating direct download link for zip file.", e);
        }
        catch (ServiceException e) {
            logger.error("Error creating direct download link for zip file.", e);
        }
    }

    // returns the current selection as a List of RequestedItems
    // if nothing is selected the complete dataset will be offered
    private ArrayList<RequestedItem> getSelectionAsRequestedItems() {
        ArrayList<RequestedItem> result = new ArrayList<RequestedItem>();

        if (selectedFiles.isEmpty() && selectedFolders.isEmpty()) {
            // prepare complete dataset
            ArrayList<ITreeItem> items = ((TreeItemProvider) treeProvider).getRoot().getChildrenWithFiles();
            for (ITreeItem item : items) {
                TreeItem concreteItem = (TreeItem) item;
                AuthzStrategy strategy = concreteItem.getItemVO().getAuthzStrategy();
                if (item.getType().equals(Type.FILE) && strategy.canUnitBeRead(EasyFile.UNIT_ID)) {
                    result.add(new RequestedItem(item.getId()));
                } else if (item.getType().equals(Type.FOLDER) && !strategy.canChildrenBeRead().equals(TriState.NONE)) {
                    result.add(new RequestedItem(item.getId()));
                }
            }
        } else {
            // prepare selected items
            for (ITreeItem file : selectedFiles) {
                TreeItem concreteItem = (TreeItem) file;
                AuthzStrategy strategy = concreteItem.getItemVO().getAuthzStrategy();
                if (strategy.canUnitBeRead(EasyFile.UNIT_ID)) {
                    result.add(new RequestedItem(file.getId()));
                }
            }

            for (ITreeItem folder : selectedFolders) {
                TreeItem concreteItem = (TreeItem) folder;
                AuthzStrategy strategy = concreteItem.getItemVO().getAuthzStrategy();
                if (!strategy.canChildrenBeRead().equals(TriState.NONE)) {
                    result.add(new RequestedItem(folder.getId()));
                }
            }
        }

        return result;
    }

    private CloseButtonCallback createCloseButtonCallback() {
        return new CloseButtonCallback() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                // refresh fileExplorer when upload modal is closed by user
                refreshCurrentFolder(target);
                return true;
            }
        };
    }

    private void refreshCurrentFolder(AjaxRequestTarget target) {
        explorer.getContent().getSelected().getObject().getChildren().clear();
        explorer.getContent().getSelected().getObject().getChildrenWithFiles().clear();
        explorer.getContent().setSelectedAndUpdate(explorer.getContent().getSelected(), explorer.getTree(), target);
    }

    private void createFilterCheckboxes(Form<Void> form, HashMap<Enum<?>, CheckBox> map, Enum<?>[] values, String idPrefix) {
        for (Enum<?> access : values) {
            CheckBox cb = new CheckBox(idPrefix + access, new Model<Boolean>(true));
            form.add(cb);
            map.put(access, cb);
        }
    }

    private AjaxFormSubmitBehavior getAjaxFormSubmitBehavior(Form<Void> form) {
        return new AjaxFormSubmitBehavior(form, "onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {}

            @Override
            protected void onError(AjaxRequestTarget target) {}
        };
    }
}
