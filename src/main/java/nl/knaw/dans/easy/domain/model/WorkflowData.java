package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.xml.MinimalXMLBean;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;

public interface WorkflowData extends Serializable, MinimalXMLBean
{

    public static final String NOT_ASSIGNED = "NOT_ASSIGNED";

    void setAssigneeId(String assigneeId);

    String getAssigneeId();

    EasyUser getAssignee() throws ObjectNotInStoreException, RepositoryException;

    void setAssignee(EasyUser assignee);

    WorkflowStep getWorkflow();

    boolean isDirty();

    void setDirty(boolean dirty);
}
