package nl.knaw.dans.easy.domain.model;

import java.net.URI;

import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

public interface FileItemMetadata extends DatasetItemMetadata
{

    String UNIT_ID = "EASY_FILE_METADATA";

    String UNIT_LABEL = "Metadata for this FileItem";

    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/file-item-md/";

    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    String MIMETYPE_UNDEFINED = "undefined";

    CreatorRole getCreatorRole();

    boolean setCreatorRole(CreatorRole creatorRole);

    AccessibleTo getAccessibleTo();

    boolean setAccessibleTo(AccessibleTo accessibleTo);

    VisibleTo getVisibleTo();

    boolean setVisibleTo(VisibleTo visibleTo);

    String getMimeType();

    long getSize();

    AdditionalMetadata getAdditionalMetadata();

    void setAdditionalMetadata(AdditionalMetadata addmd);

}
