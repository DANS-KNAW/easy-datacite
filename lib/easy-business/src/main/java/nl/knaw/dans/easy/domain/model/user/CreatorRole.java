package nl.knaw.dans.easy.domain.model.user;

import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;

/**
 * Enumerates who the creator of a dataset was.
 * 
 * @author akmi
 */
public enum CreatorRole implements FileItemVOAttribute
{
    ARCHIVIST, DEPOSITOR

}
