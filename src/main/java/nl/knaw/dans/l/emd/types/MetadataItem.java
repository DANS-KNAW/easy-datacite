package nl.knaw.dans.l.emd.types;

import java.io.Serializable;

public interface MetadataItem extends Serializable
{

    boolean isComplete();

    String getSchemeId();

}
