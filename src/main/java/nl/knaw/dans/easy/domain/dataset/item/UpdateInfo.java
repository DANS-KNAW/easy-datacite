package nl.knaw.dans.easy.domain.dataset.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;

public class UpdateInfo implements Serializable
{
    /**
     * Possible actions contained in UpdateInfo. (archivist) or ((draft/unsaved and depositor) and
     * (allowedUpdateAction Delete or Rename))
     * 
     * @author ecco Nov 17, 2009
     */
    public enum Action
    {
        UPDATE_VISIBILITY, UPDATE_ACCESSIBILITY, RENAME, DELETE

    }

    private static final long serialVersionUID = 6403363641645381493L;

    private VisibleTo visibleTo;
    private AccessibleTo accessibleTo;
    private String name;
    private boolean deleted;

    public UpdateInfo()
    {

    }

    public UpdateInfo(VisibleTo visibleTo, AccessibleTo accessibleTo, String name, boolean delete)
    {
        this.visibleTo = visibleTo;
        this.accessibleTo = accessibleTo;
        this.name = name;
        this.deleted = delete;
    }

    public boolean hasVisibleToUpdate()
    {
        return visibleTo != null;
    }

    public VisibleTo getVisibleTo()
    {
        return visibleTo;
    }

    public void updateVisibleTo(VisibleTo visibleTo)
    {
        this.visibleTo = visibleTo;
    }

    public boolean hasAccessibleToUpdate()
    {
        return accessibleTo != null;
    }

    public AccessibleTo getAccessibleTo()
    {
        return accessibleTo;
    }

    public void updateAccessibleTo(AccessibleTo accessibleTo)
    {
        this.accessibleTo = accessibleTo;
    }

    public boolean hasNameUpdate()
    {
        return name != null;
    }

    public String getName()
    {
        return name;
    }

    public void updateName(String name)
    {
        this.name = name;
    }

    public boolean isRegisteredDeleted()
    {
        return deleted;
    }

    public void registerDeleted(boolean delete)
    {
        this.deleted = delete;
    }

    public boolean hasPropagatingUpdates()
    {
        return hasAccessibleToUpdate() || hasVisibleToUpdate() || isRegisteredDeleted();
    }

    public List<Action> getActions()
    {
        List<Action> actions = new ArrayList<Action>();
        if (hasVisibleToUpdate())
        {
            actions.add(Action.UPDATE_VISIBILITY);
        }
        if (hasAccessibleToUpdate())
        {
            actions.add(Action.UPDATE_ACCESSIBILITY);
        }
        if (hasNameUpdate())
        {
            actions.add(Action.RENAME);
        }
        if (isRegisteredDeleted())
        {
            actions.add(Action.DELETE);
        }
        return actions;
    }

    public String getAction()
    {
        StringBuilder sb = new StringBuilder();
        if (hasVisibleToUpdate())
        {
            sb.append("Updating visible to " + getVisibleTo() + " ");
        }
        if (hasAccessibleToUpdate())
        {
            sb.append("Updating accessible to " + getAccessibleTo() + " ");
        }
        if (hasNameUpdate())
        {
            sb.append("Updating name to " + getName() + " ");
        }
        if (isRegisteredDeleted())
        {
            sb.append("Deleting ");
        }
        if (sb.length() == 0)
        {
            sb.append("No action ");
        }
        return sb.toString();
    }

}
