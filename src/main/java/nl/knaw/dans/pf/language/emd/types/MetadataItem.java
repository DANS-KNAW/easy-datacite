package nl.knaw.dans.pf.language.emd.types;

import java.io.Serializable;

public interface MetadataItem extends Serializable
{

    boolean isComplete();

    String getSchemeId();

}
