package nl.knaw.dans.easy.domain.dataset;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.StateChangeDate;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public class AdministrativeMetadataImpl extends AbstractTimestampedJiBXObject<AdministrativeMetadata> implements AdministrativeMetadata {

    /**
     * The version - when newly instantiated. The actual version of an instance as read from an xml-stream might be obtained by {@link #getVersion()}.
     */
    public static final String VERSION = "0.1";

    private static final long serialVersionUID = -4016139778061431329L;

    private boolean versionable;

    private String version;
    private DatasetState administrativeState = DatasetState.DRAFT;
    private DatasetState previousState;

    private transient DateTime lastStateChange = new DateTime();
    private transient DateTime previousStateChange;

    private String depositorId;
    private EasyUser depositor;

    private Set<String> groupIds = new HashSet<String>();

    private WorkflowData workflowData;

    private List<StateChangeDate> stateChangeDates = new ArrayList<StateChangeDate>();

    public AdministrativeMetadataImpl() {
        workflowData = new WorkflowDataImpl();
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        if (version == null) {
            version = VERSION;
        }
        return version;
    }

    public String getUnitFormat() {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI() {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId() {
        return UNIT_ID;
    }

    public String getUnitLabel() {
        return UNIT_LABEL;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public void setVersionable(boolean versionable) {
        evaluateDirty(this.versionable, versionable);
        this.versionable = versionable;
    }

    public String getDepositorId() {
        return depositorId;
    }

    public void setDepositorId(String depositorId) {
        if (evaluateDirty(this.depositorId, depositorId)) {
            depositor = null;
        }
        this.depositorId = depositorId;
    }

    public void setDepositor(EasyUser depositor) {
        this.depositor = depositor;
        setDepositorId(depositor == null ? null : depositor.getId());
    }

    public EasyUser getDepositor() {
        if (depositor == null && depositorId != null) {
            depositor = RepoAccess.getDelegator().getUser(depositorId);
        }
        return depositor;
    }

    public DatasetState getAdministrativeState() {
        return administrativeState;
    }

    public void setAdministrativeState(DatasetState administrativeState) {
        if (!evaluateDirty(this.administrativeState, administrativeState))
            return;

        if (administrativeState.equals(previousState)) {
            // TODO: remove this. It has become redundant since all state changes are saved
            // in stateChangeDates
            lastStateChange = previousStateChange;
            previousState = this.administrativeState;
        } else {
            // TODO: remove this. It has become redundant since all state changes are saved
            // in stateChangeDates
            previousState = this.administrativeState;
            previousStateChange = lastStateChange;
            lastStateChange = new DateTime();
        }

        // store change date
        StateChangeDate stateChangeDate = new StateChangeDate(previousState, administrativeState, new DateTime());
        stateChangeDates.add(stateChangeDate);

        this.administrativeState = administrativeState;
    }

    public DatasetState getPreviousAdministrativeState() {
        return previousState;
    }

    public DateTime getLastStateChange() {
        return lastStateChange;
    }

    // only for migration!!
    public void setLastStateChanged(DateTime lastChange) {
        lastStateChange = lastChange;
    }

    public WorkflowData getWorkflowData() {
        return workflowData;
    }

    public Set<String> getGroupIds() {
        return groupIds;
    }

    public boolean addGroupId(String groupId) {
        return groupIds.add(groupId);
    }

    public boolean removeGroupId(String groupId) {
        return groupIds.remove(groupId);
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || getWorkflowData().isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        if (!dirty) {
            getWorkflowData().setDirty(dirty);
        }
        super.setDirty(dirty);
    }

    public List<StateChangeDate> getStateChangeDates() {
        return stateChangeDates;
    }

    public void setStateChangeDates(List<StateChangeDate> stateChangeDates) {
        this.stateChangeDates = stateChangeDates;
    }

    public DateTime getDateOfFirstChangeTo(DatasetState state) {
        DateTime result = null;
        for (StateChangeDate stateChangeDate : stateChangeDates) {
            if (stateChangeDate.getToState().equals(state) && (result == null || result.isAfter(stateChangeDate.getChangeDate())))
                result = stateChangeDate.getChangeDate();
        }
        return result;
    }

    public DateTime getDateOfLastChangeTo(DatasetState state) {
        DateTime result = null;
        for (StateChangeDate stateChangeDate : stateChangeDates) {
            if (stateChangeDate.getToState().equals(state) && (result == null || result.isBefore(stateChangeDate.getChangeDate())))
                result = stateChangeDate.getChangeDate();
        }
        return result;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(-284466127, 1724715273).appendSuper(super.hashCode()).append(this.administrativeState).append(this.versionable)
                .append(this.depositorId).append(this.groupIds).append(this.workflowData).append(this.stateChangeDates).toHashCode();
    }
}
