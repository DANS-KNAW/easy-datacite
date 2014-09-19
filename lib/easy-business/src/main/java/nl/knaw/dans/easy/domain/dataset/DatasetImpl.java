package nl.knaw.dans.easy.domain.dataset;

import static nl.knaw.dans.common.lang.dataset.AccessCategory.ANONYMOUS_ACCESS;
import static nl.knaw.dans.common.lang.dataset.AccessCategory.GROUP_ACCESS;
import static nl.knaw.dans.common.lang.dataset.AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS;
import static nl.knaw.dans.common.lang.dataset.AccessCategory.REQUEST_PERMISSION;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoRecursiveItem;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItem;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.reposearch.HasSearchBeans;
import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.data.store.EmdMetadataUnitXMLBeanAdapter;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.exceptions.DeserializationException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.DatasetItemMetadata;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.xml.exc.XMLDeserializationException;

import org.joda.time.DateTime;

public class DatasetImpl extends AbstractDmoRecursiveItem implements Dataset, HasSearchBeans {
    private static final long serialVersionUID = -343629864711069451L;

    private AdministrativeMetadata administrativeMetadata;

    private EasyMetadata easyMetadata;

    private PermissionSequenceList permissionSequenceList;

    private ItemContainerMetadataImpl itemContainerMetadata;

    private Set<Group> groups;

    private LicenseUnit licenseUnit;

    private AdditionalLicenseUnit additionalLicenseUnit;

    public DatasetImpl(String storeId) {
        super(storeId);
    }

    public DatasetImpl(String storeId, MetadataFormat metadataFormat) {
        super(storeId);
        easyMetadata = EasyMetadataFactory.newEasyMetadata(metadataFormat);
    }

    public DatasetImpl(String storeId, EasyMetadata easyMetadata) {
        super(storeId);
        this.easyMetadata = easyMetadata;
    }

    public DmoNamespace getDmoNamespace() {
        return NAMESPACE;
    }

    public DatasetState getAdministrativeState() {
        return getAdministrativeMetadata().getAdministrativeState();
    }

    public AdministrativeMetadata getAdministrativeMetadata() {
        if (administrativeMetadata == null) {
            administrativeMetadata = new AdministrativeMetadataImpl();
        }
        return administrativeMetadata;
    }

    /**
     * NOT PART OF PUBLIC API - only used by DatasetConverter.
     * 
     * @param administrativeMetadata
     *        the thing to set
     */
    public void setAdministrativeMetadata(AdministrativeMetadata administrativeMetadata) {
        this.administrativeMetadata = administrativeMetadata;
    }

    @Override
    public List<BinaryUnit> getBinaryUnits() {
        List<BinaryUnit> binUnits = super.getBinaryUnits();
        if (licenseUnit != null) {
            binUnits.add(licenseUnit);
        }
        if (additionalLicenseUnit != null) {
            binUnits.add(additionalLicenseUnit);
        }
        return binUnits;
    }

    public IsoDate getDateSubmitted() {
        List<IsoDate> list = getEasyMetadata().getEmdDate().getEasDateSubmitted();
        if (list.size() == 0) {
            list.add(new IsoDate());
        }
        return list.get(0);
    }

    public void setLicenseContent(byte[] content) {
        if (content == null)
            licenseUnit = null;
        else
            licenseUnit = new LicenseUnit(content);
    }

    public void setAdditionalLicenseContent(File file) {
        additionalLicenseUnit = new AdditionalLicenseUnit(file);
    }

    public AccessCategory getAccessCategory() {
        AccessCategory accessCategory = getEasyMetadata().getEmdRights().getAccessCategory();
        if (accessCategory == null) {
            accessCategory = DEFAULT_ACCESS_CATEGORY;
            getEasyMetadata().getEmdRights().setAccessCategory(accessCategory);
        }
        return accessCategory;
    }

    public DateTime getDateAvailable() {
        DateTime dateAvailable = null;
        List<IsoDate> lda = getEasyMetadata().getEmdDate().getEasAvailable();
        if (lda.size() > 0) {
            dateAvailable = lda.get(0).getValue();
        }
        return dateAvailable;
    }

    public boolean isUnderEmbargo(DateTime atDate) {
        boolean underEmbargo = false;
        DateTime dateAvailable = getDateAvailable();
        if (dateAvailable != null) {
            underEmbargo = atDate.isBefore(dateAvailable);
        }
        return underEmbargo;
    }

    public boolean isUnderEmbargo() {
        return isUnderEmbargo(new DateTime().plusMinutes(1));
    }

    public Set<Group> getGroups() {
        if (groups == null) {
            groups = new HashSet<Group>(RepoAccess.getDelegator().getGroups(getAdministrativeMetadata().getGroupIds()));
        }
        return groups;
    }

    public boolean addGroup(Group group) {
        return getAdministrativeMetadata().addGroupId(group.getId());
    }

    public boolean removeGroup(Group group) {
        return getAdministrativeMetadata().removeGroupId(group.getId());
    }

    public MetadataFormat getMetadataFormat() {
        return getEasyMetadata().getEmdOther().getEasApplicationSpecific().getMetadataFormat();
    }

    public List<AccessCategory> getChildVisibility() {
        return getDatasetItemContainerMetadata().getChildVisibility();
    }

    public List<AccessCategory> getChildAccessibility() {
        return getDatasetItemContainerMetadata().getChildAccessibility();
    }

    public boolean hasPermissionRestrictedItems() {
        return getChildAccessibility().contains(AccessCategory.REQUEST_PERMISSION);
    }

    public boolean hasGroupRestrictedItems() {
        return getChildAccessibility().contains(AccessCategory.GROUP_ACCESS);
    }

    //@formatter:off
    /* 
     * Return values
     *  0 = dataset is not published
     *  1 = dataset is published and user is anonymous
     *  3 = dataset is published and user is known
     *  7 = dataset is published and user is known and is member of one or more dataset-specific groups
     * 11 = dataset is published and user is known and was granted permission
     * 15 = dataset is published and user is known and was granted permission and is member of one or more dataset-specific groups
     */
    //@formatter:on
    public int getAccessProfileFor(EasyUser user) {
        List<AccessCategory> categories = new ArrayList<AccessCategory>();
        if (DatasetState.PUBLISHED.equals(getAdministrativeState())) {
            categories.add(AccessCategory.ANONYMOUS_ACCESS); // 1 published dataset
            if (user != null && user.isActive() && !user.isAnonymous()) {
                categories.add(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS); // 2 known users
                if (user.isMemberOfGroup(getGroupIds())) {
                    categories.add(AccessCategory.GROUP_ACCESS); // 4 member of group
                }
                if (isPermissionGrantedTo(user)) {
                    categories.add(AccessCategory.REQUEST_PERMISSION); // 8 granted permission
                }
            }
        }

        return AccessCategory.UTIL.getBitMask(categories);
    }

    public Set<String> getGroupIds() {
        return getAdministrativeMetadata().getGroupIds();
    }

    public boolean isPermissionGrantedTo(EasyUser user) {
        boolean anonymous = user == null || user.isAnonymous();
        return !anonymous && getPermissionSequenceList().isGrantedTo(user);
    }

    public boolean isGroupAccessGrantedTo(EasyUser user) {
        boolean anonymous = user == null || user.isAnonymous();
        return !anonymous && user.isMemberOfGroup(getAdministrativeMetadata().getGroupIds());
    }

    public EasyMetadata getEasyMetadata() {
        if (easyMetadata == null) {
            easyMetadata = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
        }
        return easyMetadata;
    }

    /**
     * NOT PART OF PUBLIC API - only used by DatasetConverter.
     * 
     * @param emd
     *        the thing to set
     */
    public void setEasyMetadata(final EasyMetadata emd) {
        this.easyMetadata = emd;
    }

    @Override
    public void setEasyMetadata(final String xml) {
        try {
            easyMetadata = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
        }
        catch (XMLDeserializationException e) {
            throw new DeserializationException("Could not deserialize EASY Metadata");
        }
    }

    public PermissionSequenceList getPermissionSequenceList() {
        if (permissionSequenceList == null) {
            permissionSequenceList = new PermissionSequenceListImpl();
        }
        return permissionSequenceList;
    }

    /**
     * NOT PART OF PUBLIC API - only used by DatasetConverter.
     * 
     * @param sequenceList
     *        the thing to set
     */
    public void setPermissionSequenceList(final PermissionSequenceList sequenceList) {
        this.permissionSequenceList = sequenceList;
    }

    @Override
    public String getLabel() {
        String label = super.getLabel();
        if (label == null || EmdTitle.NO_TITLE.equals(label)) {
            label = getPreferredTitle();
            super.setLabel(label);
        }
        return label;
    }

    @Override
    public String getOwnerId() {
        return getAdministrativeMetadata().getDepositorId();
    }

    @Override
    public void setOwnerId(String ownerId) {
        getAdministrativeMetadata().setDepositorId(ownerId);
        super.setOwnerId(ownerId);
    }

    public boolean isDeletable() {
        return getTotalFileCount() == 0 && getTotalFolderCount() == 0 && DatasetState.DELETED.equals(getAdministrativeMetadata().getAdministrativeState());
    }

    public String getPreferredTitle() {
        return getEasyMetadata().getPreferredTitle();
    }

    public boolean hasDepositor(EasyUser user) {
        boolean anonymous = user == null || user.isAnonymous();
        return !anonymous && hasDepositor(user.getId());
    }

    public boolean hasDepositor(String userId) {
        boolean userIsDepositor = false;
        if (userId != null) {
            userIsDepositor = userId.equals(getAdministrativeMetadata().getDepositorId());
        }
        return userIsDepositor;
    }

    public EasyUser getDepositor() {
        return getAdministrativeMetadata().getDepositor();
    }

    public List<MetadataUnit> getMetadataUnits() {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();

        EasyMetadata emd = getEasyMetadata();
        DublinCoreMetadata dcmd = emd.getDublinCoreMetadata();

        metadataUnits.add(dcmd);
        metadataUnits.add(new EmdMetadataUnitXMLBeanAdapter(emd));
        metadataUnits.add(getDatasetItemContainerMetadata());
        metadataUnits.add(getAdministrativeMetadata());
        metadataUnits.add(getPermissionSequenceList());

        return metadataUnits;
    }

    public DublinCoreMetadata getDublinCoreMetadata() {
        return getEasyMetadata().getDublinCoreMetadata();
    }

    public DatasetItemMetadata getDatasetItemMetadata() {
        return getDatasetItemContainerMetadata();
    }

    public ItemContainerMetadataImpl getDatasetItemContainerMetadata() {
        if (itemContainerMetadata == null) {
            itemContainerMetadata = new ItemContainerMetadataImpl(getDmoStoreId());
        }

        // in case storeId was not known at time of instantiation of ItemContainerMetadataImpl.
        itemContainerMetadata.setDmoStoreId(getDmoStoreId());

        return itemContainerMetadata;
    }

    /**
     * NOT PART OF PUBLIC API - only use by DatasetConverter.
     * 
     * @param itemContainerMetadata
     *        the thing to set
     */
    public void setItemContainerMetadata(ItemContainerMetadataImpl itemContainerMetadata) {
        this.itemContainerMetadata = itemContainerMetadata;
    }

    @Override
    public void addChild(DmoContainerItem containerItem) throws RepositoryException {
        super.addChild(containerItem);

        onChildAdded((DatasetItem) containerItem);
    }

    public void onChildAdded(DatasetItem item) {
        getDatasetItemContainerMetadata().onChildAdded(item);
    }

    @Override
    public void removeChild(DmoContainerItem containerItem) throws RepositoryException {
        super.removeChild(containerItem);

        onChildRemoved((DatasetItem) containerItem);
    }

    public void onChildRemoved(DatasetItem item) {
        getDatasetItemContainerMetadata().onChildRemoved(item);
    }

    public void onDescendantStateChange(CreatorRole oldCreatorRole, CreatorRole newCreatorRole) {
        getDatasetItemContainerMetadata().onChildStateChange(oldCreatorRole, newCreatorRole);
    }

    public void onDescendantStateChange(String oldStreamingPath, String newStreamingPath) {
        getDatasetItemContainerMetadata().onChildStateChange(oldStreamingPath, newStreamingPath);
    }

    public void onDescendantStateChange(VisibleTo oldVisibleTo, VisibleTo newVisibleTo) {
        getDatasetItemContainerMetadata().onChildStateChange(oldVisibleTo, newVisibleTo);
    }

    public void onDescendantStateChange(AccessibleTo oldAccessibleTo, AccessibleTo newAccessibleTo) {
        getDatasetItemContainerMetadata().onChildStateChange(oldAccessibleTo, newAccessibleTo);
    }

    public int getChildFileCount() {
        return getDatasetItemContainerMetadata().getChildFileCount();
    }

    public int getChildFolderCount() {
        return getDatasetItemContainerMetadata().getChildFolderCount();
    }

    public int getTotalFileCount() {
        return getDatasetItemContainerMetadata().getTotalFileCount();
    }

    public int getTotalFolderCount() {
        return getDatasetItemContainerMetadata().getTotalFolderCount();
    }

    public int getCreatorRoleFileCount(CreatorRole creatorRole) {
        return getDatasetItemContainerMetadata().getCreatorRoleFileCount(creatorRole);
    }

    public int getVisibleToFileCount(VisibleTo visibleTo) {
        return getDatasetItemContainerMetadata().getVissibleToFileCount(visibleTo);
    }

    public int getAccessibleToFileCount(AccessibleTo accessibleTo) {
        return getDatasetItemContainerMetadata().getAccessibleToFileCount(accessibleTo);
    }

    // ??
    public Collection<IndexDocument> getIndexDocuments() {
        return null;
    }

    @SuppressWarnings("serial")
    public Collection<Object> getSearchBeans() {
        final EasyDatasetSB searchBean = new EasyDatasetSB();
        EasyMetadata emd = getEasyMetadata();
        AdministrativeMetadata amd = getAdministrativeMetadata();

        searchBean.setStoreId(getStoreId());
        // TODO: implement repository naming
        searchBean.setStoreName("eof12");
        searchBean.setDublinCore(emd.getDublinCoreMetadata());
        searchBean.setDepositorId(amd.getDepositorId());
        searchBean.setState(amd.getAdministrativeState());
        searchBean.setWorkflowProgress(amd.getWorkflowData().getWorkflow().countRequiredStepsCompleted());
        searchBean.setAssigneeId(amd.getWorkflowData().getAssigneeId());
        searchBean.setAudience(emd.getEmdAudience().getValues());
        searchBean.setPermissionStatusList(getPermissionSequenceList().getSearchInfoList());
        searchBean.setAccessCategory(getAccessCategory());

        searchBean.setDateCreated(emd.getEmdDate().getDateCreated());
        searchBean.setDateCreatedFormatted(emd.getEmdDate().getFormattedDateCreated());
        searchBean.setDateAvailable(emd.getEmdDate().getDateAvailable());
        searchBean.setDateAvailableFormatted(emd.getEmdDate().getFormattedDateAvailable());

        // set state change dates
        searchBean.setDateDeleted(amd.getDateOfLastChangeTo(DatasetState.DELETED));
        searchBean.setDatePublished(amd.getDateOfLastChangeTo(DatasetState.PUBLISHED));
        searchBean.setDateSubmitted(amd.getDateOfLastChangeTo(DatasetState.SUBMITTED));
        if (amd.getAdministrativeState().equals(DatasetState.DRAFT)) {
            searchBean.setDateDraftSaved(this.getLastModified());
        }

        // set archaeology-specific fields
        searchBean.setArchaeologyDcSubject(emd.getEmdSubject().getArchaeologyDcSubjectValues());
        searchBean.setArchaeologyDctermsTemporal(emd.getEmdCoverage().getArchaeologyTermsTemporalValues());

        // set DAI's of creators and contributors
        searchBean.setDaiCreators(emd.getEmdCreator().getDigitalAuthorIds());
        searchBean.setDaiContributors(emd.getEmdContributor().getDigitalAuthorIds());

        // set collections
        List<String> collectionMemberships = new ArrayList<String>();
        collectionMemberships.addAll(DmoStoreId.asStrings(getRelations().getCollectionMemberships(ECollection.EasyCollection.namespace)));
        searchBean.setCollections(collectionMemberships);

        return new ArrayList<Object>(1) {
            {
                add(searchBean);
            }
        };
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> contentModels = super.getContentModels();
        contentModels.add(Constants.CM_DATASET_1);
        contentModels.add(Constants.CM_OAI_ITEM_1);
        return contentModels;
    }

    public boolean isHierarchical() {
        return true;
    }

    public void onDescendantAdded(DatasetItem item) {
        getDatasetItemContainerMetadata().addDescendant(item);
    }

    public void onDescendantRemoved(DatasetItem item) {
        getDatasetItemContainerMetadata().onDescendantRemoved(item);
    }

    public Set<DmoCollection> getCollections() {
        HashSet<DmoCollection> c = new HashSet<DmoCollection>();
        c.add(DisciplineCollectionImpl.getInstance());
        c.add(DatasetItemCollection.getInstance());
        return c;
    }

    public void addFileOrFolder(DatasetItem item) throws DomainException {
        try {
            super.addChild((DmoContainerItem) item);
        }
        catch (RepositoryException e) {
            throw new DomainException(e);
        }
    }

    /**
     * Retrieves a list of discipline objects by the audience metadata field.
     * 
     * @throws DomainException
     * @throws ObjectNotFoundException
     */
    public List<DisciplineContainer> getParentDisciplines() throws ObjectNotFoundException, DomainException {
        if (easyMetadata == null)
            return Collections.emptyList();

        List<BasicString> audienceList = easyMetadata.getEmdAudience().getTermsAudience();
        List<DisciplineContainer> disciplines = new ArrayList<DisciplineContainer>(audienceList.size());
        for (BasicString audience : audienceList) {
            DisciplineContainer discipline;
            discipline = DisciplineCollectionImpl.getInstance().getDisciplineBySid(new DmoStoreId(audience.getValue()));
            if (discipline == null)
                throw new IllegalStateException("Audience field with value " + audience.getValue() + " is not a valid discipline sid");
            disciplines.add(discipline);
        }
        return disciplines;
    }

    public List<DisciplineContainer> getLeafDisciplines() throws ObjectNotFoundException, DomainException {
        List<DisciplineContainer> allDisciplines = getParentDisciplines();
        List<DisciplineContainer> leafDisciplines = new ArrayList<DisciplineContainer>();

        for (DisciplineContainer dc : allDisciplines) {
            if (!isRepresentedByChild(dc, allDisciplines)) {
                leafDisciplines.add(dc);
            }
        }
        return leafDisciplines;
    }

    private boolean isRepresentedByChild(DisciplineContainer dc, List<DisciplineContainer> dcList) throws DomainException {
        boolean represented = false;
        for (DisciplineContainer dcChild : dc.getSubDisciplines()) {
            // cannot trust dcList.contains(dcChild). equals on DisciplineContainer not fully
            // implemented.
            boolean dcListContainsChild = false;
            String childId = dcChild.getStoreId();
            for (DisciplineContainer dcL : dcList) {
                if (childId.equals(dcL.getStoreId())) {
                    dcListContainsChild = true;
                    break;
                }

            }
            represented = dcListContainsChild || isRepresentedByChild(dcChild, dcList);
            if (represented)
                break;
        }
        return represented;
    }

    @Override
    protected Relations newRelationsObject() {
        return new DatasetRelations(this);
    }

    // Awaiting generics in relations, which is awaiting clearing discipline multi-inheritance obstacles.
    @Override
    public DatasetRelations getRelations() {
        return (DatasetRelations) super.getRelations();
    }

    public boolean hasVisibleItems(final EasyUser user) {
        List<AccessCategory> childVisibility = getChildVisibility();
        if (user == null || user.isAnonymous())
            return childVisibility.contains(ANONYMOUS_ACCESS);
        if (childVisibility.contains(ANONYMOUS_ACCESS) || childVisibility.contains(OPEN_ACCESS_FOR_REGISTERED_USERS))
            return true;
        if (childVisibility.contains(GROUP_ACCESS) && isGroupAccessGrantedTo(user))
            return true;
        if (childVisibility.contains(REQUEST_PERMISSION) && isPermissionGrantedTo(user))
            return true;

        return false;
    }

    public String getPersistentIdentifier() {
        return getPid();
    }

    @Override
    public String getEncodedPersistentIdentifier() {
        try {
            return URLEncoder.encode(getPid(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // happens either never or always
            return getPid();
        }
    }

    private String getPid() {
        BasicIdentifier biPid = getEasyMetadata().getEmdIdentifier().getIdentifier(EmdConstants.SCHEME_PID);
        return biPid == null ? null : biPid.getValue();
    }

    @Override
    public String getAutzStrategyName() {
        return "nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy";
    }

    @Override
    public void replaceEasyMetadata(String xml) throws DomainException {
        try {
            easyMetadata = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
        }
        catch (XMLDeserializationException e) {
            throw new DomainException("desarialisation problem of Easy Metadata: " + e.getMessage(), e);
        }
    }
}
