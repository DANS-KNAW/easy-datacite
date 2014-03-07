package nl.knaw.dans.common.lang.repo.relations;

import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;

public interface RelationsConverter
{

    String getRdf(Relations relations) throws ObjectSerializationException;

}
