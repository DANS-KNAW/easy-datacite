package nl.knaw.dans.easy.security;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.AbstractEasyService;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo.Action;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodedAuthz extends AbstractEasyService implements Authz
{

    public static final String           NO_SIGNATURE_OFFICER_PROPOSITION = "NO SECURITYOFFICER SET FOR THIS SIGNATURE! ADJUST SIGNATURE IN CODEDAUTHZ.JAVA!";

    private static final Logger          logger                           = LoggerFactory.getLogger(CodedAuthz.class);

    private Map<String, SecurityOfficer> rules;

    private Object                       syncRules                        = new Object();

    private SecurityOfficer              enableToLoggedInUserRule;
    private SecurityOfficer              enableToNormalUserRule;
    private SecurityOfficer              enableToArchivistRule;
    private SecurityOfficer              enableToAdminRule;
    private SecurityOfficer              enableToArchivistOrAdminRule;
    private SecurityOfficer              enableToDepositorOfDatasetRule;
    private SecurityOfficer              enableToDepositorOrArchivistRule;
    private SecurityOfficer              enableToDepositorOrArchivistIfDraftRule;
    private SecurityOfficer              enableToDepositorOrArchivistOrAdminRule;
    private SecurityOfficer              visibleToArchivistEnableToAdminRule;
    private SecurityOfficer              visibleToDepositorEnableToArchivistRule;
    private SecurityOfficer              permissionRequestRequiredRule;
    private SecurityOfficer              editProtectedUserAttributesRule;

    private SecurityOfficer              viewDatasetRule;
    private SecurityOfficer              submitDatasetRule;
    private SecurityOfficer              unsubmitDatasetRule;
    private SecurityOfficer              publishDatasetRule;
    private SecurityOfficer              unpublishDatasetRule;
    private SecurityOfficer              maintainDatasetRule;
    private SecurityOfficer              republishDatasetRule;
    private SecurityOfficer              deleteDatasetRule;
    private SecurityOfficer              restoreDatasetRule;
    private SecurityOfficer              purgeDatasetRule;

    private SecurityOfficer              updateItemRule;
    private SecurityOfficer              downloadRule;
    private SecurityOfficer              fileItemDescriptionAccessRule;
    private SecurityOfficer              fileItemContentsAccessRule;
    private SecurityOfficer              freelyAvailableContentRule;

    private SecurityOfficer              jumpoffDmoNameSpaceRule;
    
    private SecurityOfficer              userByIdRule;

    private SecurityOfficer              noSecurityOfficer;
    private SecurityOfficer              dummyOfficer;

    @Override
    public String getServiceDescription()
    {
        return "Provides rules for authorization.";
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException
    {
        synchronized (syncRules)
        {
            getRules();
        }
    }

    /**
     * Expected behavior if called from isInstantiationAuthorized(Class componentClass):
     * <ul>
     * <li>[isProtectedPage] AND [SessionUser == null] ==> throw
     * RestartResponseAtInterceptPageException(LoginPage.class)</li>
     * <li>[isProtectedPage] AND [SessionUser != null] ==> instantiate page</li>
     * <li>NOT[isProtectedPage] ==> instantiate page</li>
     * </ul>
     */
    public boolean isProtectedPage(String pageName)
    {
        synchronized (syncRules)
        {
            return getRules().containsKey(pageName);
        }
    }

    public boolean hasSecurityOfficer(String item)
    {
        synchronized (syncRules)
        {
            return getRules().containsKey(item);
        }
    }

    public SecurityOfficer getSecurityOfficer(final String signature)
    {
        if (logger.isDebugEnabled())
            logger.debug("Getting SecurityOfficer for '" + signature + "'");

        SecurityOfficer officer = null;
        synchronized (syncRules)
        {
            officer = getRules().get(signature);
        }
        if (officer == null)
        {
            logger.warn("No SecurityOfficer set for signature '" + signature + "'. Returning default SecurityOfficer");
            officer = new AbstractCheck()
            {
                public boolean evaluate(ContextParameters ctxParameters)
                {
                    return false;
                }

                public String getProposition()
                {
                    return NO_SIGNATURE_OFFICER_PROPOSITION;
                }

                public String explain(ContextParameters ctxParameters)
                {
                    return "\nNo SecurityOfficer set for signature '" + signature + "'";
                }

                @Override
                public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
                {
                    hints.add(NO_SIGNATURE_OFFICER_PROPOSITION);
                    return false;
                }
            };
        }
        if (signature.equals("nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage"))
            logger.info(officer.getProposition());
        return officer;
    }

    /**
     * Get all security rules - THIS METHOD IS NOT THREADSAFE -.
     * 
     * @return all security rules
     */
    protected Map<String, SecurityOfficer> getRules()
    {
        if (rules == null)
        {
            rules = Collections.synchronizedMap(new LinkedHashMap<String, SecurityOfficer>());

            // easy navigation
            rules.put("nl.knaw.dans.easy.web.main.AbstractEasyNavPage:managementBarPanel", getEnableToArchivistOrAdminRule());

            // pages
            rules.put("nl.knaw.dans.easy.web.admin.UsersOverviewPage", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.admin.UserDetailsPage", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.admin.EditableContentPage", getEnableToArchivistOrAdminRule());

            rules.put("nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage", getEnableToLoggedInUserRule());
            rules.put("nl.knaw.dans.easy.web.search.pages.MyWorkSearchResultPage", getEnableToArchivistRule());
            rules.put("nl.knaw.dans.easy.web.search.pages.OurWorkSearchResultPage", getEnableToArchivistRule());
            rules.put("nl.knaw.dans.easy.web.search.pages.AllWorkSearchResultPage", getEnableToArchivistRule());
            rules.put("nl.knaw.dans.easy.web.search.pages.SearchAllSearchResultPage", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.search.pages.TrashCanSearchResultPage", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.permission.PermissionReplyPage", getEnableToDepositorOrArchivistRule());
            rules.put("nl.knaw.dans.easy.web.permission.PermissionRequestPage", getEnableToLoggedInUserRule());

            rules.put("nl.knaw.dans.easy.web.jamon.EasyJamonAdminPage", getEnableToAdminRule());

            rules.put("nl.knaw.dans.easy.web.deposit.DepositIntroPage", getEnableToLoggedInUserRule());
            rules.put("nl.knaw.dans.easy.web.deposit.DepositPage", getEnableToLoggedInUserRule());
            // rules.put("nl.knaw.dans.easy.web.template.fileexplorer.FileExplorerPage:PermissionRequestPanel",
            // getPermissionRequestRequiredRule());

            // nl.knaw.dans.easy.web.admin.UserDetailsPage components
            rules.put("nl.knaw.dans.easy.web.admin.UserDetailsPage:userDetailsPanel:switchPanel:userInfoForm:state", getEditProtectedUserAttributesRule());
            rules.put("nl.knaw.dans.easy.web.admin.UserDetailsPage:userDetailsPanel:switchPanel:userInfoForm:roles", getEditProtectedUserAttributesRule());

            // advanced search
            rules.put("nl.knaw.dans.easy.web.search.AdvancedSearchPage:advancedSearchForm:depositorOptions", getEnableToNormalUserRule());
            rules.put("nl.knaw.dans.easy.web.search.AdvancedSearchPage:advancedSearchForm:archivistOptions", getEnableToArchivistRule());

            // nl.knaw.dans.easy.web.view.dataset.DatasetViewPage components
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:reuseLink", getEnableToDepositorOfDatasetRule());

            // info segment panel
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel", getEnableToLoggedInUserRule());

            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:changeDepositorLink", getEnableToArchivistRule());

            // status panel
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel", getEnableToDepositorOrArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:continueDeposit", getSubmitDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:deleteDataset", getDeleteDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:restoreDeleted", getRestoreDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:unsubmit", getUnsubmitDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:publish", getPublishDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:unpublish", getUnpublishDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:maintain", getMaintainDatasetRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:republish", getRepublishDatasetRule());

            // PublicationProgresPanel
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:pubProgressPanel", getEnableToDepositorOrArchivistRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:pubProgressPanel:assignToForm",
                    getEnableToArchivistRule());

            // JumpoffPanel
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:toggleEditorButton", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:addButton", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:editButton", getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:deleteButton", getEnableToArchivistOrAdminRule());
            
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:viewEditJumpoffPanel:editForm",
                    getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:viewEditJumpoffPanel:editForm",
                    getEnableToArchivistOrAdminRule());
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:viewEditJumpoffPanel:jumpoffMetadataPanel", getEnableToArchivistOrAdminRule());

            // Description tab
            rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:editLink", getEnableToArchivistRule());
            
            // Metadata download buttons are visible to all users now
            // rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:downloadPanel", getEnableToArchivistOrAdminRule());

            // Administration tab !! the order of tabs is not constant. tabs:3 could be the permissions
            // tab
            // rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:tabs-container:tabs:3:link",
            // getEnableToArchivistRule());

            // WorkDispatchers
            // ===============
            // ItemWorkDispatcher
            rules.put(
                    "void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.addDirectoryContents(EasyUser, Dataset, DatasetItemContainer, File, FileFilter, UnitOfWork, ItemIngesterDelegator, WorkListener[])",
                    getEnableToDepositorOrArchivistIfDraftRule());
            
            rules.put(
                    "void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.updateObjects(EasyUser, Dataset, List, UpdateInfo, ItemFilters, UnitOfWork, WorkListener[])",
                    getUpdateItemRule());
            rules.put("void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.updateFileItemMetadata(EasyUser, Dataset, ResourceMetadataList, AdditionalMetadataUpdateStrategy, WorkListener[])",
                    getEnableToArchivistRule());
            
            rules.put("void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.saveDescriptiveMetadata(EasyUser, UnitOfWork, Dataset, Map, WorkListener[])",
                    getEnableToArchivistRule()); // TODO but not if published or deleted?
            rules.put("FileItemDescription nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileItemDescription(EasyUser, Dataset, FileItem)", getFileItemDescriptionAccessRule());
            
            rules.put("FileItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileItem(EasyUser, Dataset, DmoStoreId)", getFileItemContentsAccessRule());
            rules.put("FileItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileItemByPath(EasyUser, Dataset, String)", getNoSecurityOfficer());
            
            rules.put("FolderItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFolderItem(EasyUser, Dataset, DmoStoreId)", getNoSecurityOfficer());
            rules.put("FolderItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFolderItemByPath(EasyUser, Dataset, String)", getNoSecurityOfficer());
            
            rules.put("URL nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileContentURL(EasyUser, Dataset, FileItem)", getFreelyAvailableContentRule());
            
            
            // Not that strong, because FileItem is represented by id, due to mishap in FileItemVO design.
            rules.put("URL nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getDescriptiveMetadataURL(EasyUser, Dataset, DmoStoreId)", getViewDatasetRule());
            
            // DownloadWorkDispatcher
            rules.put("FileContentWrapper nl.knaw.dans.easy.business.item.DownloadWorkDispatcher.prepareFileContent(EasyUser, Dataset, DmoStoreId)",
                    getDownloadRule());
            rules.put("ZipFileContentWrapper nl.knaw.dans.easy.business.item.DownloadWorkDispatcher.prepareZippedContent(EasyUser, Dataset, Collection)",
                    getDownloadRule());

            // DatasetWorkDispatcher
            rules.put("DataModelObject nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getDataModelObject(EasyUser, DmoStoreId)", getViewDatasetRule()); 
                                                                                                                                                             
            rules.put("byte[] nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getObjectXml(EasyUser, Dataset)",
                    getEnableToDepositorOrArchivistOrAdminRule());
            rules.put("Dataset nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.cloneDataset(EasyUser, Dataset)", getEnableToLoggedInUserRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.saveEasyMetadata(EasyUser, Dataset, WorkListener[])",
                    getEnableToDepositorOrArchivistIfDraftRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.saveAdministrativeMetadata(EasyUser, Dataset, WorkListener[])",
                    getEnableToArchivistOrAdminRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.submitDataset(EasyUser, Dataset, DatasetSubmission, WorkListener[])",
                    getSubmitDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.unsubmitDataset(EasyUser, Dataset, boolean)", getUnsubmitDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.publishDataset(EasyUser, Dataset, boolean, boolean)",
                    getPublishDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.unpublishDataset(EasyUser, Dataset, boolean)", getUnpublishDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.maintainDataset(EasyUser, Dataset, boolean)", getMaintainDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.republishDataset(EasyUser, Dataset, boolean, boolean)",
                    getRepublishDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.changeDepositor(EasyUser, Dataset, EasyUser, boolean, boolean)",
                    getEnableToArchivistRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.deleteDataset(EasyUser, Dataset)", getDeleteDatasetRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.restoreDataset(EasyUser, Dataset)", getEnableToAdminRule());
            rules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.purgeDataset(EasyUser, Dataset, WorkListener[])", getPurgeDatasetRule());

            rules.put(
                    "void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.savePermissionRequest(EasyUser, Dataset, PermissionRequestModel, WorkListener[])",
                    getEnableToLoggedInUserRule());
            rules.put(
                    "void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.savePermissionReply(EasyUser, Dataset, PermissionReplyModel, WorkListener[])",
                    getEnableToDepositorOfDatasetRule());
            rules.put("DownloadHistory nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getDownloadHistoryFor(EasyUser, Dataset, DateTime)",
            		getEnableToLoggedInUserRule());
            rules.put("URL nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getUnitMetadataURL(EasyUser, Dataset, UnitMetadata)",
                    getEnableToArchivistOrAdminRule());

            // JumpoffWorkDispatcher
            rules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.saveJumpoffDmo(EasyUser, JumpoffDmo, DataModelObject)",
                    getEnableToArchivistOrAdminRule());
            rules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.deleteJumpoff(EasyUser, JumpoffDmo, DataModelObject, String)",
                    getEnableToArchivistOrAdminRule());
            rules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.deleteUnit(EasyUser, DmoStoreId, DsUnitId, String)",
                    getEnableToArchivistOrAdminRule());
            rules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.toggleEditorMode(EasyUser, JumpoffDmo)",
                    getEnableToArchivistOrAdminRule());
            rules.put("List nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.retrieveUnitMetadata(EasyUser, DmoStoreId, DsUnitId)",
                    getJumpoffDmoNameSpaceRule());
            rules.put("URL nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.retrieveURL(DmoStoreId, DsUnitId)", getJumpoffDmoNameSpaceRule());

            // EasyUserService
            // used to be getUserByIdRule() but changed to loggedInUser for activity log panel
            rules.put("EasyUser nl.knaw.dans.easy.business.services.EasyUserService.getUserById(EasyUser, String)", 
            		getEnableToLoggedInUserRule());
            
            // new! operations annotated with @SecuredOperation
            rules.put("nl.knaw.dans.easy.servicelayer.services.CollectionService.updateCollectionMemberships", 
                    getEnableToArchivistOrAdminRule());
        }
        return rules;
    }

    protected SecurityOfficer getNoSecurityOfficer()
    {
        if (noSecurityOfficer == null)
        {
            noSecurityOfficer = new NoSecurityOfficer();
            logger.debug("Created rule: " + noSecurityOfficer.getProposition());
        }
        return noSecurityOfficer;
    }

    protected SecurityOfficer getDummyOfficer()
    {
        if (dummyOfficer == null)
        {
            logger.warn("Still using dummyOfficer!");
            dummyOfficer = new DummySecurityOfficer();
        }
        return dummyOfficer;
    }

    /**
     * Rule that grants rights to logged in users.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * [SessionUser has role USER or ARCHIVIST or ADMIN]
     * </pre>
     * 
     * @return Rule that grants rights to logged in users
     */
    protected SecurityOfficer getEnableToLoggedInUserRule()
    {
        if (enableToLoggedInUserRule == null)
        {
            enableToLoggedInUserRule = new HasRoleCheck(Role.values());
            logger.debug("Created rule: " + enableToLoggedInUserRule.getProposition());
        }
        return enableToLoggedInUserRule;
    }

    /**
     * Rule that grants rights to logged in users.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * [SessionUser has role USER or ARCHIVIST or ADMIN]
     * </pre>
     * 
     * @return Rule that grants rights to logged in users
     */
    protected SecurityOfficer getEnableToNormalUserRule()
    {
        if (enableToNormalUserRule == null)
        {
            enableToNormalUserRule = new HasRoleCheck(Role.USER);
            logger.debug("Created rule: " + enableToNormalUserRule.getProposition());
        }
        return enableToNormalUserRule;
    }

    /**
     * Rule that grants rights to archivists.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * [SessionUser has role ARCHIVIST]
     * </pre>
     * 
     * @return Rule that grants rights to archivists
     */
    protected SecurityOfficer getEnableToArchivistRule()
    {
        if (enableToArchivistRule == null)
        {
            enableToArchivistRule = new HasRoleCheck(Role.ARCHIVIST);
            logger.debug("Created rule: " + enableToArchivistRule.getProposition());
        }
        return enableToArchivistRule;
    }

    /**
     * Rule that grants rights to administrators.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * [SessionUser has role ADMIN]
     * </pre>
     * 
     * @return Rule that grants rights to administrators
     */
    protected SecurityOfficer getEnableToAdminRule()
    {
        if (enableToAdminRule == null)
        {
            enableToAdminRule = new HasRoleCheck(Role.ADMIN);
            logger.debug("Created rule: " + enableToAdminRule.getProposition());
        }
        return enableToAdminRule;
    }

    /**
     * Rule that grants rights to archivists and administrators.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * [SessionUser has role ARCHIVIST or ADMIN]
     * </pre>
     * 
     * @return Rule that grants rights to archivists and administrators
     */
    protected SecurityOfficer getEnableToArchivistOrAdminRule()
    {
        if (enableToArchivistOrAdminRule == null)
        {
            enableToArchivistOrAdminRule = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);
            logger.debug("Created rule: " + enableToArchivistOrAdminRule.getProposition());
        }
        return enableToArchivistOrAdminRule;
    }

    protected SecurityOfficer getEnableToDepositorOfDatasetRule()
    {
        if (enableToDepositorOfDatasetRule == null)
        {
            enableToDepositorOfDatasetRule = new IsDepositorOfDatasetCheck();
            logger.debug("Created rule: " + enableToDepositorOfDatasetRule.getProposition());
        }
        return enableToDepositorOfDatasetRule;
    }

    /**
     * Rule that grants rights to the depositor of a dataset and to archivists.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * ([SessionUser is depositor of dataset] OR [SessionUser has role ARCHIVIST])
     * </pre>
     * 
     * @return Rule that grants rights to the depositor of a dataset and to archivists
     */
    protected SecurityOfficer getEnableToDepositorOrArchivistRule()
    {
        if (enableToDepositorOrArchivistRule == null)
        {
            enableToDepositorOrArchivistRule = new Or( //
                    new IsDepositorOfDatasetCheck(), //
                    new HasRoleCheck(Role.ARCHIVIST));
            logger.debug("Created rule: " + enableToDepositorOrArchivistRule.getProposition());
        }
        return enableToDepositorOrArchivistRule;
    }

    public SecurityOfficer getViewDatasetRule()
    {
        if (viewDatasetRule == null)
        {
            SecurityOfficer isPublic = new DatasetStateCheck(DatasetState.PUBLISHED);
            SecurityOfficer isDepositor = new And( //
                    new IsDepositorOfDatasetCheck(), //
                    new DatasetStateCheck(DatasetState.DRAFT, DatasetState.SUBMITTED, DatasetState.MAINTENANCE));
            SecurityOfficer isArchivistOrAdmin = new HasRoleCheck(Role.ADMIN, Role.ARCHIVIST);
            viewDatasetRule = new Or(isPublic, isDepositor, isArchivistOrAdmin);
            logger.debug("Created viewDatasetRule: " + viewDatasetRule.getProposition());
        }
        return viewDatasetRule;
    }

    /**
     * Rule that grants rights to the depositor of a dataset or an archivist if the dataset status is
     * draft.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * ([Dataset state is DRAFT] AND [SessionUser is depositor of dataset]) OR [SessionUser has role ARCHIVIST]
     * </pre>
     * 
     * @return Rule that grants rights to the depositor of a dataset or an archivist if the dataset
     *         status is draft
     */
    protected SecurityOfficer getEnableToDepositorOrArchivistIfDraftRule()
    {
        if (enableToDepositorOrArchivistIfDraftRule == null)
        {
            enableToDepositorOrArchivistIfDraftRule = new Or( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new And( //
                            new DatasetStateCheck(DatasetState.DRAFT), //
                            new IsDepositorOfDatasetCheck()));
            logger.debug("Created rule: " + enableToDepositorOrArchivistIfDraftRule.getProposition());
        }
        return enableToDepositorOrArchivistIfDraftRule;
    }

    protected SecurityOfficer getEnableToDepositorOrArchivistOrAdminRule()
    {
        if (enableToDepositorOrArchivistOrAdminRule == null)
        {
            enableToDepositorOrArchivistOrAdminRule = new Or( //
                    new IsDepositorOfDatasetCheck(), //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN));
            logger.debug("Created rule: " + enableToDepositorOrArchivistOrAdminRule.getProposition());
        }
        return enableToDepositorOrArchivistOrAdminRule;
    }

    protected SecurityOfficer getSubmitDatasetRule()
    {
        if (submitDatasetRule == null)
        {
            submitDatasetRule = new And( //
                    new IsDepositorOfDatasetCheck(), //
                    new DatasetStateCheck(DatasetState.DRAFT));
            logger.debug("Created rule: " + submitDatasetRule.getProposition());
        }
        return submitDatasetRule;
    }

    protected SecurityOfficer getUnsubmitDatasetRule()
    {
        if (unsubmitDatasetRule == null)
        {
            unsubmitDatasetRule = new And( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new DatasetStateCheck(DatasetState.SUBMITTED));
            logger.debug("Created rule: " + unsubmitDatasetRule.getProposition());
        }
        return unsubmitDatasetRule;
    }

    protected SecurityOfficer getPublishDatasetRule()
    {
        // if submitted and archivist -> disabled & visible = v
        // if submitted and archivist and workflow progress complete -> enabled & visible = e

        if (publishDatasetRule == null)
        {
            SecurityOfficer v = new And( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new DatasetStateCheck(DatasetState.SUBMITTED));
            SecurityOfficer e = new And( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new DatasetStateCheck(DatasetState.SUBMITTED), //
                    new WorkflowCheck());
            publishDatasetRule = new SplitAnswer(v, e);

            logger.debug("Created rule: " + publishDatasetRule.getProposition());
        }
        return publishDatasetRule;
    }

    protected SecurityOfficer getUnpublishDatasetRule()
    {
        if (unpublishDatasetRule == null)
        {
            unpublishDatasetRule = new And( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new DatasetStateCheck(DatasetState.PUBLISHED));
            logger.debug("Created rule: " + unpublishDatasetRule.getProposition());
        }
        return unpublishDatasetRule;
    }

    protected SecurityOfficer getMaintainDatasetRule()
    {
        if (maintainDatasetRule == null)
        {
            maintainDatasetRule = new And( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new DatasetStateCheck(DatasetState.PUBLISHED));
            logger.debug("Created rule: " + maintainDatasetRule.getProposition());
        }
        return maintainDatasetRule;
    }

    protected SecurityOfficer getRepublishDatasetRule()
    {
        if (republishDatasetRule == null)
        {
            republishDatasetRule = new And( //
                    new HasRoleCheck(Role.ARCHIVIST), //
                    new DatasetStateCheck(DatasetState.MAINTENANCE), //
                    new WorkflowCheck());
            logger.debug("Created rule: " + republishDatasetRule.getProposition());
        }
        return republishDatasetRule;
    }

    public SecurityOfficer getUpdateItemRule()
    {
        if (updateItemRule == null)
        {
            SecurityOfficer archivist = new HasRoleCheck(Role.ARCHIVIST);
            SecurityOfficer depositor = new IsDepositorOfDatasetCheck();
            SecurityOfficer datasetState = new DatasetStateCheck(DatasetState.DRAFT);
            SecurityOfficer actions = new UpdateActionCheck(Action.DELETE, Action.RENAME);
            SecurityOfficer depositorActions = new And(new And(depositor, datasetState), actions);
            updateItemRule = new Or(archivist, depositorActions);
            logger.debug("Created rule: " + updateItemRule.getProposition());
        }
        return updateItemRule;
    }

    /**
     * Gets rule for deleting datasets.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * (([SessionUser is depositor of dataset] AND [Dataset state is DRAFT]) OR ([SessionUser has role ARCHIVIST] AND [Dataset state is draft or submitted or published]))
     * </pre>
     * 
     * @return rule for deleting datasets
     */
    protected SecurityOfficer getDeleteDatasetRule()
    {
        if (deleteDatasetRule == null)
        {
            SecurityOfficer a = new IsDepositorOfDatasetCheck();
            SecurityOfficer b = new DatasetStateCheck(DatasetState.DRAFT);
            SecurityOfficer p = new And(a, b);

            SecurityOfficer c = new HasRoleCheck(Role.ARCHIVIST);
            SecurityOfficer d = new DatasetStateCheck(DatasetState.DRAFT, DatasetState.SUBMITTED, DatasetState.PUBLISHED, DatasetState.MAINTENANCE);
            SecurityOfficer q = new And(c, d);

            deleteDatasetRule = new Or(p, q);
            logger.debug("Created rule: " + deleteDatasetRule.getProposition());
        }
        return deleteDatasetRule;
    }

    protected SecurityOfficer getPurgeDatasetRule()
    {
        if (purgeDatasetRule == null)
        {
            purgeDatasetRule = new And( //
                    new HasRoleCheck(Role.ADMIN), //
                    new DatasetStateCheck(DatasetState.DELETED));
            logger.debug("Created rule: " + purgeDatasetRule.getProposition());
        }
        return purgeDatasetRule;
    }

    /**
     * Gets rule for restoring datasets.
     * <p/>
     * Proposition is
     * 
     * <pre>
     * ([Dataset state is DELETED] AND [SessionUser has role ADMIN])
     * </pre>
     * 
     * @return rule for restoring datasets
     */
    protected SecurityOfficer getRestoreDatasetRule()
    {
        if (restoreDatasetRule == null)
        {
            restoreDatasetRule = new And( //
                    new HasRoleCheck(Role.ADMIN), //
                    new DatasetStateCheck(DatasetState.DELETED));
            logger.debug("Created rule: " + restoreDatasetRule.getProposition());
        }
        return restoreDatasetRule;
    }

    protected SecurityOfficer getDownloadRule()
    {
        if (downloadRule == null)
        {
            SecurityOfficer accessableDataset = new And( //
                    new DatasetStateCheck(DatasetState.PUBLISHED), //
                    new EmbargoFreeCheck());
            SecurityOfficer allowedRoles = new Or( //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN), //
                    new IsDepositorOfDatasetCheck());
            downloadRule = new Or(accessableDataset, allowedRoles);
        }
        return downloadRule;
    }
    
    protected SecurityOfficer getFileItemDescriptionAccessRule()
    {
        if (fileItemDescriptionAccessRule == null)
        {
            fileItemDescriptionAccessRule = new Or(  //
                    new DatasetStateCheck(DatasetState.PUBLISHED), //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN), //
                    new IsDepositorOfFileItemCheck());
            logger.debug("Created rule: " + fileItemDescriptionAccessRule.getProposition());
        }
        return fileItemDescriptionAccessRule;
    }

    protected SecurityOfficer getFileItemContentsAccessRule()
    {
        if (fileItemContentsAccessRule == null)
        {
            fileItemContentsAccessRule = new Or( //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN), //
                    new IsDepositorOfFileItemCheck(), //
                    new FileItemContentsAccessCheck());
            logger.debug("Created rule: " + fileItemContentsAccessRule.getProposition());
        }
        return fileItemContentsAccessRule;
    }

    protected SecurityOfficer getFreelyAvailableContentRule()
    {
        if (freelyAvailableContentRule == null)
        {
            freelyAvailableContentRule = new Or( //
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN), //
                    new FreelyAvailableContentCheck());
            logger.debug("Created rule: " + freelyAvailableContentRule.getProposition());
        }
        return freelyAvailableContentRule;
    }

    /**
     * Rule that grants the right to archivists to see a thing and the right to administrators to see
     * *and* act upon a thing.
     * <p/>
     * Implications and propositions:
     * 
     * <pre>
     * ComponentVisisble &lt;== [SessionUser has role ARCHIVIST or ADMIN]
     * EnableAllowed     &lt;== [SessionUser has role ADMIN]
     * </pre>
     * 
     * @return rule that grants the right to archivists to see a thing and the right to administrators to
     *         see *and* act upon a thing
     */
    protected SecurityOfficer getVisibleToArchivistEnableToAdminRule()
    {
        if (visibleToArchivistEnableToAdminRule == null)
        {
            SecurityOfficer v = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);
            SecurityOfficer e = new HasRoleCheck(Role.ADMIN);
            visibleToArchivistEnableToAdminRule = new SplitAnswer(v, e);
            logger.debug("Created rule: " + visibleToArchivistEnableToAdminRule.getProposition());
        }
        return visibleToArchivistEnableToAdminRule;
    }

    protected SecurityOfficer getVisibleToDepositorEnableToArchivistRule()
    {
        if (visibleToDepositorEnableToArchivistRule == null)
        {
            SecurityOfficer v = new Or(new IsDepositorOfDatasetCheck(), new HasRoleCheck(Role.ARCHIVIST));
            SecurityOfficer e = new HasRoleCheck(Role.ARCHIVIST);
            visibleToDepositorEnableToArchivistRule = new SplitAnswer(v, e);
            logger.debug("Created rule: " + visibleToDepositorEnableToArchivistRule.getProposition());
        }
        return visibleToDepositorEnableToArchivistRule;
    }

    /**
     * Rule that warns a user that permission is required to see/access (some of) the files in the
     * dataset.
     * 
     * @return Rule that warns a user that permission is required to see/access (some of) the files in
     *         the dataset.
     */
    protected SecurityOfficer getPermissionRequestRequiredRule()
    {
        if (permissionRequestRequiredRule == null)
        {
            final SecurityOfficer powerUser = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);
            final SecurityOfficer ordinaryUser = new Not(new Or(powerUser, new IsDepositorOfDatasetCheck()));
            final SecurityOfficer specialDataset = new HasPermissionRestrictedItemsCheck();
            permissionRequestRequiredRule = new And(ordinaryUser, specialDataset);
            logger.debug("Created rule: " + permissionRequestRequiredRule.getProposition());
        }
        return permissionRequestRequiredRule;
    }

    /**
     * Rule that grants the right to archivists to see user attributes and the right to administrators to
     * see *and* act upon those attributes, as long as the administrator is not the user that is being
     * edited.
     * <p/>
     * Implications and propositions:
     * 
     * <pre>
     * ComponentVisisble &lt;== [SessionUser has role ARCHIVIST or ADMIN]
     * EnableAllowed     &lt;== ([SessionUser has role ADMIN] AND NOT([SessionUser is user under edit]))
     * </pre>
     * 
     * @return rule on editing (certain) attributes of users
     */
    protected SecurityOfficer getEditProtectedUserAttributesRule()
    {
        if (editProtectedUserAttributesRule == null)
        {
            SecurityOfficer v = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);

            SecurityOfficer a = new HasRoleCheck(Role.ADMIN);
            SecurityOfficer b = new IsSelfCheck();
            SecurityOfficer e = new And(a, new Not(b));

            editProtectedUserAttributesRule = new SplitAnswer(v, e);
            logger.debug("Created rule: " + editProtectedUserAttributesRule.getProposition());
        }
        return editProtectedUserAttributesRule;
    }

    protected SecurityOfficer getJumpoffDmoNameSpaceRule()
    {
        if (jumpoffDmoNameSpaceRule == null)
        {
            jumpoffDmoNameSpaceRule = new DmoNamespaceCheck(JumpoffDmo.NAMESPACE);
            logger.debug("Created rule: " + jumpoffDmoNameSpaceRule.getProposition());
        }
        return jumpoffDmoNameSpaceRule;
    }
    
    protected SecurityOfficer getUserByIdRule()
    {
        if (userByIdRule == null)
        {
            userByIdRule = new Or(
                    new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN),
                    new IsSelfCheck());
        }
        return userByIdRule;
    }

}
