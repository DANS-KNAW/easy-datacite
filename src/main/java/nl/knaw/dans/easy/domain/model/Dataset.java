package nl.knaw.dans.easy.domain.model;

import java.io.File;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.joda.time.DateTime;

public interface Dataset extends DatasetItemContainer
{
    AccessCategory DEFAULT_ACCESS_CATEGORY = AccessCategory.OPEN_ACCESS;

    String NAME_SPACE_VALUE = "easy-dataset";

    DmoNamespace NAMESPACE = new DmoNamespace(NAME_SPACE_VALUE);

    EasyMetadata getEasyMetadata();

    AdministrativeMetadata getAdministrativeMetadata();

    DatasetRelations getRelations();

    DatasetState getAdministrativeState();

    PermissionSequenceList getPermissionSequenceList();

    String getPreferredTitle();

    boolean hasDepositor(EasyUser user);

    boolean hasDepositor(String userId);

    EasyUser getDepositor();

    int getAccessProfileFor(EasyUser user);

    /**
     * Get the AccessCategory the depositor set on the dataset when depositing.
     * 
     * @return AccessCategory the depositor set on the dataset when depositing
     */
    AccessCategory getAccessCategory();

    DateTime getDateAvailable();

    boolean isUnderEmbargo(DateTime atDate);

    boolean isUnderEmbargo();

    List<AccessCategory> getChildVisibility();

    List<AccessCategory> getChildAccessibility();

    boolean hasPermissionRestrictedItems();

    boolean hasGroupRestrictedItems();

    Set<Group> getGroups();

    boolean addGroup(Group group);

    boolean removeGroup(Group group);

    MetadataFormat getMetadataFormat();

    List<DisciplineContainer> getParentDisciplines() throws DomainException, ObjectNotFoundException;

    List<DisciplineContainer> getLeafDisciplines() throws ObjectNotFoundException, DomainException;

    boolean isPermissionGrantedTo(EasyUser user);

    boolean isGroupAccessGrantedTo(EasyUser user);

    boolean hasVisibleItems(EasyUser user);

    String getPersistentIdentifier();

    String getEncodedPersistentIdentifier();

    void setLicenseContent(byte[] content);

    void setEasyMetadata(String xml);

    IsoDate getDateSubmitted();

    void setAdditionalLicenseContent(File file);

    /**
     * Usually the meta data is changed by making changes to the object. This method allows to replace
     * the entire object. For example for general spelling corrections at data stream level.
     * 
     * @param xml an UTF-8 encoded XML string
     * @throws DomainException 
     */
    void replaceEasyMetadata(String xml) throws DomainException;
}
