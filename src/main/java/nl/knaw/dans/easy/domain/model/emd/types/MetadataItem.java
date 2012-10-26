package nl.knaw.dans.easy.domain.model.emd.types;

import java.io.Serializable;

public interface MetadataItem extends Serializable
{

    boolean isComplete();

    String getSchemeId();

}
