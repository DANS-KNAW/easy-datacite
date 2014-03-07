package nl.knaw.dans.easy.business.item;

import nl.knaw.dans.easy.domain.model.FileItem;

public interface ItemIngesterDelegator
{

    void setFileRights(FileItem fileItem);

    void addAdditionalMetadata(FileItem fileItem);

    void addAdditionalRDF(FileItem fileItem);

}
