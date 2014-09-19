package nl.knaw.dans.easy.rest.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.rest.util.DisciplineConverter;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * A resource for addressing the disciplines that are present in EASY.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
@Path("disciplines")
public class DisciplineResource extends Resource {

    /**
     * Returns a complete list of all disciplines.
     * 
     * @return A response containing the complete list of disciplines.
     */
    @GET
    public Response getDisciplines() {
        try {
            return responseXmlOrJson(DisciplineConverter.getDisciplineList(Integer.MAX_VALUE));
        }
        catch (ObjectNotFoundException e) {
            return notFound();
        }
        catch (DomainException e) {
            return internalServerError(e);
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (RepositoryException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns a list that contains the root disciplines.
     * 
     * @return A response containing the list of root disciplines.
     */
    @GET
    @Path("/roots")
    public Response getRootDisciplines() {
        try {
            return responseXmlOrJson(DisciplineConverter.getDisciplineList(0));
        }
        catch (ObjectNotFoundException e) {
            return notFound();
        }
        catch (DomainException e) {
            return internalServerError(e);
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (RepositoryException e) {
            return internalServerError(e);
        }
    }

    /**
     * Get a specific discipline by sid.
     * 
     * @param sid
     *        Store ID of the discipline.
     * @return A response containing the data about the given discipline.
     */
    @GET
    @Path("/{sid}")
    public Response getDisciplineBySid(@PathParam("sid") String sid) {
        try {
            DisciplineContainer discipline = Services.getDisciplineService().getDisciplineById(new DmoStoreId(sid));
            return responseXmlOrJson(DisciplineConverter.getDiscipline(discipline, 0));
        }
        catch (ObjectNotFoundException e) {
            return notFound();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (DomainException e) {
            return internalServerError(e);
        }
        catch (RepositoryException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns a list of subdisciplines of the given discipline.
     * 
     * @param sid
     *        Store ID of the parent discipline.
     * @return A response containing a list of subdisciplines.
     */
    @GET
    @Path("/{sid}/subdisciplines")
    public Response getSubDisciplines(@PathParam("sid") String sid) {
        try {
            List<DisciplineContainer> disciplines = Services.getDisciplineService().getDisciplineById(new DmoStoreId(sid)).getSubDisciplines();
            return responseXmlOrJson(DisciplineConverter.getDisciplineList(disciplines, 0));
        }
        catch (ObjectNotFoundException e) {
            return notFound();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (DomainException e) {
            return internalServerError(e);
        }
        catch (RepositoryException e) {
            return internalServerError(e);
        }
    }

}
