package nl.knaw.dans.common.lang.repo.bean;

import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;

import org.joda.time.DateTime;

public interface MarkupMetadata {

    String getLastEditedBy();

    void setLastEditedBy(String editorId);

    DateTime getLastEdited();

    void setLastEdited(DateTime editTime);

    MarkupVersionID getMarkupVersionID();

}
