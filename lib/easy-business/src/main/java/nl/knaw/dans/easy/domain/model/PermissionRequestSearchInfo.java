package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;

import org.joda.time.DateTime;

public class PermissionRequestSearchInfo implements Serializable {
    private static final long serialVersionUID = -6544742065836247081L;

    private static final String STR_SEPARATOR = " ";

    private String requesterId;

    private State state;

    private DateTime stateLastModified;

    public PermissionRequestSearchInfo() {

    }

    public void fromString(String fromString) throws DomainException {
        int s1 = fromString.indexOf(STR_SEPARATOR);
        int s2 = fromString.indexOf(STR_SEPARATOR, s1 + 1);

        if (s1 < 0 || s2 < 0)
            throw new DomainException("Could not convert " + fromString + " to PermissionRequestSearchInfo. Missing separators.");

        setRequesterId(fromString.substring(0, s1));
        setState(State.valueOf(fromString.substring(s1 + 1, s2)));
        String substring = fromString.substring(s2 + 1);
        if (!"null".equals(substring))
            setStateLastModified(new DateTime(substring));
    }

    @Override
    public String toString() {
        return getRequesterId() + STR_SEPARATOR + getState() + STR_SEPARATOR + getStateLastModified();
    }

    public void setStateLastModified(DateTime stateLastModified) {
        this.stateLastModified = stateLastModified;
    }

    public DateTime getStateLastModified() {
        return stateLastModified;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterId() {
        return requesterId;
    }
}
