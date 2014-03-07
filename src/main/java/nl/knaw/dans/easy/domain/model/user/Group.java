package nl.knaw.dans.easy.domain.model.user;

import nl.knaw.dans.common.lang.user.RepoEntry;

public interface Group extends RepoEntry
{

    public enum State
    {
        ACTIVE, INACTIVE
    }

    public static final String ID_ARCHEOLOGY = "Archeology";
    public static final String ID_HISTORY = "History";

    State getState();

    void setState(State state);

    String getDescription();

    void setDescription(String description);

}
