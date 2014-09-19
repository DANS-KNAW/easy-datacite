package nl.knaw.dans.easy.rest.util;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * A class to convert EASY disciplines to XML strings.
 * 
 * @author Georgi Khomeriki
 * @author Roshan timal
 */
public class DisciplineConverter extends SimpleXmlWriter {

    /**
     * Returns a XML String of disciplines with depth=recursionDepth.
     * 
     * @param recursionDepth
     *        Depth of recursion for subdisciplines.
     * @return A String containing a XML list of disciplines.
     * @throws DomainException
     *         Thrown if something goes wrong in the domain model.
     * @throws ServiceException
     *         Thrown if something goes wrong while retrieving subdisciplines.
     * @throws RepositoryException
     *         Thrown if something goes wrong while getting parent Sid's of a discipline.
     */
    public static String getDisciplineList(int recursionDepth) throws DomainException, ServiceException, RepositoryException {
        List<DisciplineContainer> disciplines = Services.getDisciplineService().getRootDiscipline().getSubDisciplines();

        return getDisciplineList(disciplines, recursionDepth);
    }

    /**
     * Returns a XML String of disciplines starting at disciplines in the given List. The depth of the list is given in recursionDepth.
     * 
     * @param disciplines
     *        List of 'root' disciplines.
     * @param recursionDepth
     *        Depth of recursion for subdisciplines.
     * @return A String containing a XML list of disciplines.
     * @throws DomainException
     *         Thrown if something goes wrong in the domain model.
     * @throws ServiceException
     *         Thrown if something goes wrong while retrieving subdisciplines.
     * @throws RepositoryException
     *         Thrown if something goes wrong while getting parent Sid's of a discipline.
     */
    public static String getDisciplineList(List<DisciplineContainer> disciplines, int recursionDepth) throws DomainException, ServiceException,
            RepositoryException
    {
        String xml = startNode("disciplines");
        for (DisciplineContainer discipline : disciplines) {
            xml += getDiscipline(discipline, recursionDepth);
        }
        return xml + endNode("disciplines");
    }

    /**
     * Returns a XML String of a specific discipline. Also goes on recursively (while recursionDepth > 0) to add subdisciplines.
     * 
     * @param discipline
     *        The discipline to parse to XML.
     * @param recursionDepth
     *        Depth of recursion for subdisciplines.
     * @return A String containing XMl for the given discipline.
     * @throws DomainException
     *         Thrown if something goes wrong in the domain model.
     * @throws RepositoryException
     *         Thrown if something goes wrong while getting parent Sid's of a discipline.
     */
    public static String getDiscipline(DisciplineContainer discipline, int recursionDepth) throws DomainException, RepositoryException {
        String result = startNode("discipline");
        result += addNode("id", discipline.getStoreId());
        result += addNode("name", discipline.getName());
        result += getParentsXml(discipline.getParentSids());
        result += getCollectionsXml(discipline.getCollections());
        if (recursionDepth > 0) {
            List<DisciplineContainer> subDisciplines = discipline.getSubDisciplines();
            if (subDisciplines != null && !subDisciplines.isEmpty()) {
                result += startNode("subDisciplines");
                for (DisciplineContainer subDiscipline : subDisciplines) {
                    result += getDiscipline(subDiscipline, recursionDepth - 1);
                }
                result += endNode("subDisciplines");
            }
        }
        return result + endNode("discipline");
    }

    private static String getParentsXml(Set<DmoStoreId> parentIds) {
        String result = "";
        if (parentIds != null && !parentIds.isEmpty()) {
            result += startNode("parents");
            for (DmoStoreId id : parentIds) {
                result += addNode("id", id.getStoreId());
            }
            result += endNode("parents");
        }
        return result;
    }

    private static String getCollectionsXml(Set<DmoCollection> collections) {
        String result = "";
        if (collections != null && !collections.isEmpty()) {
            result += startNode("collections");
            for (DmoCollection collection : collections) {
                result += startNode("collection");
                result += addNode("id", collection.getStoreId());
                result += addNode("label", collection.getLabel());
                result += endNode("collection");
            }
            result += endNode("collections");
        }
        return result;
    }

}
