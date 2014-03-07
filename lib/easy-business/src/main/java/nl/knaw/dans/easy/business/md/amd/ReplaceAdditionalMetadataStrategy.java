package nl.knaw.dans.easy.business.md.amd;

import nl.knaw.dans.easy.xml.AdditionalMetadata;

/**
 * Replaces {@link AdditionalMetadata} completely.
 */
public class ReplaceAdditionalMetadataStrategy implements AdditionalMetadataUpdateStrategy
{

    public void update(AdditionalMetadataOwner owner, AdditionalMetadata additionalMetadata)
    {
        owner.setAdditionalMetadata(additionalMetadata);
    }

}
