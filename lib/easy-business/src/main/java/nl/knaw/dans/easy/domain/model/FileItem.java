package nl.knaw.dans.easy.domain.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.dom4j.Element;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.types.CommonFileItem;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

public interface FileItem extends DatasetItem, CommonFileItem {
    DmoNamespace NAMESPACE = new DmoNamespace("easy-file");

    void setFile(File file) throws IOException;

    File getFile();

    FileItemMetadata getFileItemMetadata();

    AdditionalMetadata getAdditionalMetadata();

    void setAdditionalMetadata(AdditionalMetadata additionalMetadata);

    CreatorRole getCreatorRole();

    void setCreatorRole(CreatorRole creatorRole);

    String getStreamingSurrogateUrl();

    void setStreamingSurrogateUrl(String streamingSurrogateUrl);

    boolean isCreatedByArchivist();

    boolean isCreatedByDepositor();

    VisibleTo getVisibleTo();

    AccessibleTo getAccessibleTo();

    void setVisibleTo(VisibleTo visibleTo);

    void setAccessibleTo(AccessibleTo accessibleTo);

    boolean isAccessibleFor(int userProfile);

    AccessCategory getReadAccessCategory();

    AccessCategory getViewAccessCategory();

    void setDescriptiveMetadata(Element content);

    boolean hasDescriptiveMetadata();

    DescriptiveMetadata getDescriptiveMetadata();

    void setFileDataUrl(String url);
}
