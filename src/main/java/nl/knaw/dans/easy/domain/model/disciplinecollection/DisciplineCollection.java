package nl.knaw.dans.easy.domain.model.disciplinecollection;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;

public interface DisciplineCollection
{

    public static final DmoNamespace NAME_SPACE = new DmoNamespace("discipline-collection");

    /**
     * Returns the root discipline in the discipline hierarchy.
     * @return the root discipline
     * @throws ObjectNotFoundException thrown when the root discipline
     * was not found. This is a serious exception that should be handled
     * properly. In this case something has gone terribly wrong and an
     * administrator should be warned.
     * @throws DomainException wrapper exception
     */
    DisciplineContainer getRootDiscipline() throws DomainException, ObjectNotFoundException;

    /**
     * Gets a discipline by its store id.
     * @param disciplineId
     * @return the found discipline object 
     * @throws ObjectNotFoundException thrown when no discipline could be found with
     * this sid 
     * @throws DomainException wrapper exception
     */
    DisciplineContainer getDisciplineBySid(DmoStoreId disciplineId) throws ObjectNotFoundException, DomainException;

    /**
     * Case insensitive search for a discipline by name. The name is the default
     * English name.
     * @param disciplineName the name to search for
     * @return the found discipline object
     * @throws ObjectNotFoundException thrown when no discipline could be found with 
     * this particular name 
     * @throws DomainException wrapper exception
     */
    DisciplineContainer getDisciplineByName(String disciplineName) throws ObjectNotFoundException, DomainException;
}
