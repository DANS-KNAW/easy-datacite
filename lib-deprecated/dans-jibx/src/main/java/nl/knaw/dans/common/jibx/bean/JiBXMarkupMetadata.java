package nl.knaw.dans.common.jibx.bean;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.bean.MarkupMetadata;

import org.joda.time.DateTime;

public class JiBXMarkupMetadata extends AbstractJiBXObject<MarkupMetadata> implements MarkupMetadata
{

    private static final long serialVersionUID = -1156624217408639919L;

    private String lastEditedBy;

    private DateTime lastEdited;

    private MarkupVersionID markupVersionID;

    /* used by JiBX */
    protected JiBXMarkupMetadata()
    {

    }

    protected JiBXMarkupMetadata(MarkupVersionID markupVersionID)
    {
        this.markupVersionID = markupVersionID;
    }

    public MarkupVersionID getMarkupVersionID()
    {
        return markupVersionID;
    }

    public String getLastEditedBy()
    {
        return lastEditedBy;
    }

    public void setLastEditedBy(String lastEditedBy)
    {
        this.lastEditedBy = lastEditedBy;
        setLastEdited(new DateTime());
    }

    public DateTime getLastEdited()
    {
        return lastEdited;
    }

    public void setLastEdited(DateTime lastEdited)
    {
        this.lastEdited = lastEdited;
    }

}
