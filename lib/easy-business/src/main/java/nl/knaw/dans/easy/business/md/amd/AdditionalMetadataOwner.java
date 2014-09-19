package nl.knaw.dans.easy.business.md.amd;

import nl.knaw.dans.easy.xml.AdditionalMetadata;

public interface AdditionalMetadataOwner {

    void setAdditionalMetadata(AdditionalMetadata addmd);

    AdditionalMetadata getAdditionalMetadata();

}
