package nl.knaw.dans.easy.business.md.amd;

import nl.knaw.dans.easy.xml.AdditionalMetadata;

/**
 * 
 *
 */
public interface AdditionalMetadataUpdateStrategy
{
    
    void update(AdditionalMetadataOwner owner, AdditionalMetadata newAmd) throws AdditionalMetadataUpdateException;

}
