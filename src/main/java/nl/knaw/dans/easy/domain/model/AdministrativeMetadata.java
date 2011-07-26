package nl.knaw.dans.easy.domain.model;

import java.net.URI;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.joda.time.DateTime;

public interface AdministrativeMetadata extends MetadataUnitXMLBean
{
    String UNIT_ID = "AMD";
    
    String UNIT_LABEL = "Administrative metadata for this dataset";
    
    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/";
    
    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    
    String getDepositorId();

    void setDepositorId(String depositorId);

    void setDepositor(EasyUser depositor);

    EasyUser getDepositor();
    

    DatasetState getAdministrativeState();

    void setAdministrativeState(DatasetState administrativeState);
    
    DatasetState getPreviousAdministrativeState();
    

    DateTime getLastStateChange();
    
    List<StateChangeDate> getStateChangeDates();
    
    DateTime getDateOfFirstChangeTo(DatasetState state);

    Set<String> getGroupIds();
    
    boolean addGroupId(String groupId);
    
    boolean removeGroupId(String groupId);

    DateTime getDateOfLastChangeTo(DatasetState state);
    
    WorkflowData getWorkflowData();
}
