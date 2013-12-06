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
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodedAuthz extends AbstractEasyService implements Authz
{
    public static final String NO_SIGNATURE_OFFICER_PROPOSITION = "NO SECURITYOFFICER SET FOR THIS SIGNATURE! ADJUST SIGNATURE IN CODEDAUTHZ.JAVA!";

    private static final Logger logger = LoggerFactory.getLogger(CodedAuthz.class);

    private Map<String, SecurityOfficer> rules;

    private Object syncRules = new Object();

    private SecurityOfficer enableToLoggedInUserRule;
    private SecurityOfficer enableToNormalUserRule;
    private SecurityOfficer enableToArchivistRule;
    private SecurityOfficer enableToAdminRule;
    private SecurityOfficer enableToArchivistOrAdminRule;
    private SecurityOfficer enableToDepositorOfDatasetRule;
    private SecurityOfficer enableToDepositorOrArchivistRule;
    private SecurityOfficer enableToDepositorOrArchivistIfDraftRule;
    private SecurityOfficer enableToDepositorOrArchivistOrAdminRule;
    private SecurityOfficer visibleToArchivistEnableToAdminRule;
    private SecurityOfficer visibleToDepositorEnableToArchivistRule;
    private SecurityOfficer permissionRequestRequiredRule;
    private SecurityOfficer editProtectedUserAttributesRule;

    private SecurityOfficer viewDatasetRule;
    private SecurityOfficer submitDatasetRule;
    private SecurityOfficer unsubmitDatasetRule;
    private SecurityOfficer publishDatasetRule;
    private SecurityOfficer unpublishDatasetRule;
    private SecurityOfficer maintainDatasetRule;
    private SecurityOfficer republishDatasetRule;
    private SecurityOfficer deleteDatasetRule;
    private SecurityOfficer restoreDatasetRule;
    private SecurityOfficer purgeDatasetRule;

    private SecurityOfficer updateItemRule;
    private SecurityOfficer downloadRule;
    private SecurityOfficer fileItemDescriptionAccessRule;
    private SecurityOfficer fileItemContentsAccessRule;
    private SecurityOfficer freelyAvailableContentRule;

    private SecurityOfficer jumpoffDmoNameSpaceRule;

    private SecurityOfficer userByIdRule;

    private SecurityOfficer noSecurityOfficer;

    /** Spring bean property */
    private SystemReadOnlyStatus systemReadOnlyStatus;
    private AbstractCheck isSystemInUpdateModeCheck;

    @Override
    public String getServiceDescription()
    {
        return "Provides rules for authorization.";
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException
    {
        getRules();
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
        return getRules().containsKey(pageName);
    }

    // List<String> missing = new ArrayList<String>();

    public boolean hasSecurityOfficer(String item)
    {
        boolean hasOfficer = getRules().containsKey(item);
        // if (logger.isDebugEnabled() && !hasOfficer && !missing.contains(item) &&
        // item.matches("nl.knaw.dans.easy.web.*Page2?(:[^_]*)?"))
        // {
        // missing.add(item);
        // logger.debug("No SecurityOfficer set for signature: " + item);
        // }
        return hasOfficer;
    }

    public SecurityOfficer getSecurityOfficer(final String signature)
    {
        if (logger.isDebugEnabled())
            logger.debug("Getting SecurityOfficer for '" + signature + "'");

        SecurityOfficer officer = null;
        officer = getRules().get(signature);
        if (officer == null)
        {
            logger.warn("No SecurityOfficer set for signature '" + signature + "'. Returning default SecurityOfficer");
            officer = createDefaultOfficer(signature);
        }
        if (signature.equals("nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage"))
            logger.info(officer.getProposition());

        return addSystemInUpdateModeCheck(signature, officer);
    }

    private AbstractCheck createDefaultOfficer(final String signature)
    {
        return new AbstractCheck()
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

    private SecurityOfficer addSystemInUpdateModeCheck(final String signature, SecurityOfficer officer)
    {
        // first return what is allowed in read-only mode

        if (signature.matches("nl.knaw.dans.easy.web.search.pages.\\w+SearchResultPage"))
            return officer;
        if (signature.matches("nl.knaw.dans.easy.web.search.AdvancedSearchPage:.*"))
            return officer;
        if (signature.equals("nl.knaw.dans.easy.web.main.AbstractEasyNavPage:managementBarPanel"))
            return officer;
        // if (signature.equals("nl.knaw.dans.easy.web.permission.PermissionRequestPage"))
        // return officer;
        if (signature.matches("nl.knaw.dans.easy.web.admin.User\\w*"))
            return officer;
        if (signature.matches("nl.knaw.dans.easy.web.admin.UserDetailsPage:userDetailsPanel:switchPanel:userInfoForm:\\w*"))
            return officer;
        if (signature.equals("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel"))
            return officer;
        if (signature.equals("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel"))
            return officer;
        if (signature.matches("\\w* nl.knaw.dans.easy.business.*"))
            return officer;
        if (signature.matches(".*:systemIsReadOnly"))
            return officer;

        // finally add a check to what is not allowed in read-only mode

        if (isSystemInUpdateModeCheck == null)
            throw new IllegalStateException("systemReadOnlyStatus not configured");
        return new And(officer, isSystemInUpdateModeCheck);
    }

    /**
     * Get all security rules.
     * 
     * @return all security rules
     */
    protected Map<String, SecurityOfficer> getRules()
    {
        synchronized (syncRules)
        {
            if (rules == null)
            {
                rules = Collections.synchronizedMap(createRules());
            }
            return rules;
        }
    }

    private Map<String, SecurityOfficer> createRules()
    {
        Map<String, SecurityOfficer> newRules = new LinkedHashMap<String, SecurityOfficer>()
        {
            // Some Wicket components are used on many pages via an abstract WebPage
            // a rule without the page as qualifier defines a default rule

            private static final long serialVersionUID = 1L;

            public boolean containsKey(Object key)
            {
                // if not found with the page as qualifier, try without
                return super.containsKey(key) || super.containsKey(stripWicketPageQualifier(key));
            }

            public SecurityOfficer get(Object key)
            {
                // if not found with the page as qualifier, try without
                if (super.containsKey(key))
                    return super.get(key);
                else
                    return super.get(stripWicketPageQualifier(key));
            }

            private String stripWicketPageQualifier(Object key)
            {
                return key.toString().replaceAll("^[^:]+:", ":");
            }
        };

        // easy navigation
        newRules.put(":systemIsReadOnly", getEnableToAdminRule());
        newRules.put(":managementBarPanel", getEnableToArchivistOrAdminRule());
        newRules.put(":managementBarPanel2", getEnableToArchivistOrAdminRule());

        // pages
        newRules.put("nl.knaw.dans.easy.web.admin.UsersOverviewPage", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.admin.UserDetailsPage", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.admin.EditableContentPage", getEnableToArchivistOrAdminRule());

        newRules.put("nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage", getEnableToLoggedInUserRule());
        newRules.put("nl.knaw.dans.easy.web.search.pages.MyWorkSearchResultPage", getEnableToArchivistRule());
        newRules.put("nl.knaw.dans.easy.web.search.pages.OurWorkSearchResultPage", getEnableToArchivistRule());
        newRules.put("nl.knaw.dans.easy.web.search.pages.AllWorkSearchResultPage", getEnableToArchivistRule());
        newRules.put("nl.knaw.dans.easy.web.search.pages.SearchAllSearchResultPage", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.search.pages.TrashCanSearchResultPage", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.permission.PermissionReplyPage", getEnableToDepositorOrArchivistRule());
        newRules.put("nl.knaw.dans.easy.web.permission.PermissionRequestPage", getEnableToLoggedInUserRule());

        newRules.put("nl.knaw.dans.easy.web.deposit.DepositIntroPage", getEnableToLoggedInUserRule());
        newRules.put("nl.knaw.dans.easy.web.deposit.DepositPage", getEnableToLoggedInUserRule());

        // nl.knaw.dans.easy.web.admin.UserDetailsPage components
        newRules.put("nl.knaw.dans.easy.web.admin.UserDetailsPage:userDetailsPanel:switchPanel:userInfoForm:state", getEditProtectedUserAttributesRule());
        newRules.put("nl.knaw.dans.easy.web.admin.UserDetailsPage:userDetailsPanel:switchPanel:userInfoForm:roles", getEditProtectedUserAttributesRule());
        newRules.put(":userDetailsPanel:switchPanel:editLink", getNoSecurityOfficer());
        newRules.put(":userInfoPanel:switchPanel:editLink", getNoSecurityOfficer());
        newRules.put(":userInfoPanel:switchPanel:changePasswordLink", getNoSecurityOfficer());

        // advanced search
        newRules.put("nl.knaw.dans.easy.web.search.AdvancedSearchPage:advancedSearchForm:depositorOptions", getEnableToNormalUserRule());
        newRules.put("nl.knaw.dans.easy.web.search.AdvancedSearchPage:advancedSearchForm:archivistOptions", getEnableToArchivistRule());

        // nl.knaw.dans.easy.web.view.dataset.DatasetViewPage components
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:reuseLink", getEnableToDepositorOfDatasetRule());

        // info segment panel
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel", getEnableToLoggedInUserRule());

        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:changeDepositorLink", getEnableToArchivistRule());

        // status panel
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel", getEnableToDepositorOrArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:continueDeposit", getSubmitDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:deleteDataset", getDeleteDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:restoreDeleted", getRestoreDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:unsubmit", getUnsubmitDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:publish", getPublishDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:unpublish", getUnpublishDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:maintain", getMaintainDatasetRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:republish", getRepublishDatasetRule());

        // PublicationProgresPanel
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:pubProgressPanel", getEnableToDepositorOrArchivistRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:pubProgressPanel:assignToForm",
                getEnableToArchivistRule());

        // JumpoffPanel
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:toggleEditorButton", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:addButton", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:editButton", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:deleteButton", getEnableToArchivistOrAdminRule());

        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:viewEditJumpoffPanel:editForm",
                getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:viewEditJumpoffPanel:editForm",
                getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:jumpoffPanel:viewEditJumpoffPanel:jumpoffMetadataPanel",
                getEnableToArchivistOrAdminRule());

        // Description tab
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:editLink", getEnableToArchivistRule());

        // file explorer tab
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:fe:deleteLink", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:fe:uploadLink", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:fe:importLink", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:fe:rightsForm", getEnableToArchivistOrAdminRule());

        // Metadata download buttons are visible to all users now
        // rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:downloadPanel",
        // getEnableToArchivistOrAdminRule());

        // Administration tab !! the order of tabs is not constant. tabs:3 could be the permissions
        // tab
        // rules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:tabs-container:tabs:3:link",
        // getEnableToArchivistRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:workflowForm", getEnableToArchivistOrAdminRule());
        newRules.put("nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:tabs:panel:licenseUploadPanel", getEnableToArchivistOrAdminRule());

        // WorkDispatchers
        // ===============
        // ItemWorkDispatcher
        newRules.put(
                "void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.addDirectoryContents(EasyUser, Dataset, DatasetItemContainer, File, FileFilter, UnitOfWork, ItemIngesterDelegator, WorkListener[])",
                getEnableToDepositorOrArchivistIfDraftRule());

        newRules.put(
                "void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.updateObjects(EasyUser, Dataset, List, UpdateInfo, ItemFilters, UnitOfWork, WorkListener[])",
                getUpdateItemRule());
        newRules.put(
                "void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.updateFileItemMetadata(EasyUser, Dataset, ResourceMetadataList, AdditionalMetadataUpdateStrategy, WorkListener[])",
                getEnableToArchivistRule());

        newRules.put("void nl.knaw.dans.easy.business.item.ItemWorkDispatcher.saveDescriptiveMetadata(EasyUser, UnitOfWork, Dataset, Map, WorkListener[])",
                getEnableToArchivistRule()); // TODO but not if published or deleted?
        newRules.put("FileItemDescription nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileItemDescription(EasyUser, Dataset, FileItem)",
                getFileItemDescriptionAccessRule());

        newRules.put("FileItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileItem(EasyUser, Dataset, DmoStoreId)", getFileItemContentsAccessRule());
        newRules.put("FileItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileItemByPath(EasyUser, Dataset, String)", getNoSecurityOfficer());

        newRules.put("FolderItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFolderItem(EasyUser, Dataset, DmoStoreId)", getNoSecurityOfficer());
        newRules.put("FolderItem nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFolderItemByPath(EasyUser, Dataset, String)", getNoSecurityOfficer());

        newRules.put("URL nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getFileContentURL(EasyUser, Dataset, FileItem)", getFreelyAvailableContentRule());

        // Not that strong, because FileItem is represented by id, due to mishap in FileItemVO
        // design.
        newRules.put("URL nl.knaw.dans.easy.business.item.ItemWorkDispatcher.getDescriptiveMetadataURL(EasyUser, Dataset, DmoStoreId)", getViewDatasetRule());

        // DownloadWorkDispatcher
        newRules.put("FileContentWrapper nl.knaw.dans.easy.business.item.DownloadWorkDispatcher.prepareFileContent(EasyUser, Dataset, DmoStoreId)",
                getDownloadRule());
        newRules.put("ZipFileContentWrapper nl.knaw.dans.easy.business.item.DownloadWorkDispatcher.prepareZippedContent(EasyUser, Dataset, Collection)",
                getDownloadRule());

        // DatasetWorkDispatcher
        newRules.put("DataModelObject nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getDataModelObject(EasyUser, DmoStoreId)", getViewDatasetRule());

        newRules.put("byte[] nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getObjectXml(EasyUser, Dataset)",
                getEnableToDepositorOrArchivistOrAdminRule());
        newRules.put("Dataset nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.cloneDataset(EasyUser, Dataset)", getEnableToLoggedInUserRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.saveEasyMetadata(EasyUser, Dataset, WorkListener[])",
                getEnableToDepositorOrArchivistIfDraftRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.saveAdministrativeMetadata(EasyUser, Dataset, WorkListener[])",
                getEnableToArchivistOrAdminRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.submitDataset(EasyUser, Dataset, DatasetSubmission, WorkListener[])",
                getSubmitDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.unsubmitDataset(EasyUser, Dataset, boolean)", getUnsubmitDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.publishDataset(EasyUser, Dataset, boolean, boolean)",
                getPublishDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.unpublishDataset(EasyUser, Dataset, boolean)", getUnpublishDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.maintainDataset(EasyUser, Dataset, boolean)", getMaintainDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.republishDataset(EasyUser, Dataset, boolean, boolean)",
                getRepublishDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.changeDepositor(EasyUser, Dataset, EasyUser, boolean, boolean)",
                getEnableToArchivistRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.deleteDataset(EasyUser, Dataset)", getDeleteDatasetRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.restoreDataset(EasyUser, Dataset)", getEnableToAdminRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.purgeDataset(EasyUser, Dataset, WorkListener[])", getPurgeDatasetRule());

        newRules.put(
                "void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.savePermissionRequest(EasyUser, Dataset, PermissionRequestModel, WorkListener[])",
                getEnableToLoggedInUserRule());
        newRules.put(
                "void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.savePermissionReply(EasyUser, Dataset, PermissionReplyModel, WorkListener[])",
                getEnableToDepositorOfDatasetRule());
        newRules.put("DownloadHistory nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getDownloadHistoryFor(EasyUser, Dataset, DateTime)",
                getEnableToLoggedInUserRule());
        newRules.put("URL nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.getUnitMetadataURL(EasyUser, Dataset, UnitMetadata)",
                getEnableToArchivistOrAdminRule());
        newRules.put("void nl.knaw.dans.easy.business.dataset.DatasetWorkDispatcher.deleteAdditionalLicense(EasyUser, DmoStoreId, DsUnitId, DateTime, String)",
                getEnableToArchivistOrAdminRule());

        // JumpoffWorkDispatcher
        newRules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.saveJumpoffDmo(EasyUser, JumpoffDmo, DataModelObject)",
                getEnableToArchivistOrAdminRule());
        newRules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.deleteJumpoff(EasyUser, JumpoffDmo, DataModelObject, String)",
                getEnableToArchivistOrAdminRule());
        newRules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.deleteUnit(EasyUser, DmoStoreId, DsUnitId, String)",
                getEnableToArchivistOrAdminRule());
        newRules.put("void nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.toggleEditorMode(EasyUser, JumpoffDmo)", getEnableToArchivistOrAdminRule());
        newRules.put("List nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.retrieveUnitMetadata(EasyUser, DmoStoreId, DsUnitId)",
                getJumpoffDmoNameSpaceRule());
        newRules.put("URL nl.knaw.dans.easy.business.jumpoff.JumpoffWorkDispatcher.retrieveURL(DmoStoreId, DsUnitId)", getJumpoffDmoNameSpaceRule());

        // EasyUserService
        // used to be getUserByIdRule() but changed to loggedInUser for activity log panel
        newRules.put("EasyUser nl.knaw.dans.easy.business.services.EasyUserService.getUserById(EasyUser, String)", getEnableToLoggedInUserRule());

        // new! operations annotated with @SecuredOperation
        newRules.put("nl.knaw.dans.easy.servicelayer.services.CollectionService.updateCollectionMemberships", getEnableToArchivistOrAdminRule());
        return newRules;
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
            fileItemDescriptionAccessRule = new Or( //
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
            userByIdRule = new Or(new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN), new IsSelfCheck());
        }
        return userByIdRule;
    }

    public SystemReadOnlyStatus getSystemReadOnlyStatus()
    {
        return systemReadOnlyStatus;
    }

    public void setSystemReadOnlyStatus(SystemReadOnlyStatus readOnlyStatus)
    {
        this.systemReadOnlyStatus = readOnlyStatus;
        isSystemInUpdateModeCheck = new IsSystemInUpdateModeCheck(systemReadOnlyStatus);
    }
}
