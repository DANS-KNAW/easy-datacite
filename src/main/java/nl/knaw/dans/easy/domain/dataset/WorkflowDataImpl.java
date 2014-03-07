package nl.knaw.dans.easy.domain.dataset;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;
import nl.knaw.dans.easy.domain.workflow.WorkflowFactory;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;

import org.dom4j.Element;

public class WorkflowDataImpl extends AbstractTimestampedJiBXObject<WorkflowData> implements WorkflowData
{

    /**
     * The version - when newly instantiated. The actual version of an instance as read from an
     * xml-stream might be obtained by {@link #getVersion()}.
     */
    public static final String VERSION = "0.1";

    private static final long serialVersionUID = -4946639050379250401L;

    private String version;
    private String assigneeId = NOT_ASSIGNED;
    private EasyUser assignee;
    private WorkflowStep workflow;

    private boolean dirty;

    protected WorkflowDataImpl()
    {

    }

    /**
     * {@inheritDoc}
     */
    public String getVersion()
    {
        if (version == null)
        {
            version = VERSION;
        }
        return version;
    }

    public void setAssigneeId(String assigneeId)
    {
        String assigneeIdToSet = assigneeId == null ? NOT_ASSIGNED : assigneeId;
        if (evaluateDirty(this.assigneeId, assigneeIdToSet))
        {
            assignee = null;
            this.assigneeId = assigneeIdToSet;
        }

    }

    public String getAssigneeId()
    {
        return assigneeId;
    }

    public EasyUser getAssignee() // throws ObjectNotFoundException, DataAccessException
    {
        if (assignee == null && !NOT_ASSIGNED.equals(assigneeId))
        {
            assignee = RepoAccess.getDelegator().getUser(assigneeId);
        }
        return assignee;
    }

    public void setAssignee(EasyUser assignee)
    {
        this.assignee = assignee;
        setAssigneeId(assignee == null ? null : assignee.getId());
    }

    public WorkflowStep getWorkflow()
    {
        if (workflow == null)
        {
            workflow = WorkflowFactory.newDatasetWorkflow();
        }
        return workflow;
    }

    public void setWorkflow(WorkflowStep root)
    {
        this.workflow = root;
    }

    protected Element getWorkflowElement() throws XMLSerializationException
    {
        return workflow == null ? null : workflow.asElement();
    }

    protected void setWorkflowElement(Element workflowElement) throws XMLDeserializationException
    {
        if (workflowElement != null)
        {
            workflow = (WorkflowStep) JiBXObjectFactory.unmarshal(WorkflowStep.class, workflowElement);
        }
    }

    public boolean isDirty()
    {
        return dirty || (workflow != null && getWorkflow().isDirty());
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
        if (!dirty && workflow != null)
        {
            getWorkflow().setDirty(dirty);
        }
    }

    protected boolean evaluateDirty(Object obj1, Object obj2)
    {
        boolean dirty = false;
        if (obj2 == null)
        {
            dirty = obj1 != null;
        }
        else
        {
            dirty = !obj2.equals(obj1);
        }
        if (dirty)
            setDirty(true);
        return dirty;
    }

}
