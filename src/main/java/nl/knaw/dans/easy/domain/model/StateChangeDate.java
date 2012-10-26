package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;

import nl.knaw.dans.common.lang.dataset.DatasetState;

import org.joda.time.DateTime;

public class StateChangeDate implements Serializable
{
    private static final long serialVersionUID = 13432432523466L;

    private DatasetState fromState;
    private DatasetState toState;
    private DateTime changeDate;

    public StateChangeDate()
    {

    }

    public StateChangeDate(DatasetState fromState, DatasetState toState, DateTime changeDate)
    {
        this.setFromState(fromState);
        this.setToState(toState);
        this.setChangeDate(changeDate);
    }

    public void setFromState(DatasetState fromState)
    {
        this.fromState = fromState;
    }

    public DatasetState getFromState()
    {
        return fromState;
    }

    public void setToState(DatasetState toState)
    {
        this.toState = toState;
    }

    public DatasetState getToState()
    {
        return toState;
    }

    public void setChangeDate(DateTime changeDate)
    {
        this.changeDate = changeDate;
    }

    public DateTime getChangeDate()
    {
        return changeDate;
    }

}
