package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class DisciplineResourceTest extends RestTest {
    private DisciplineCollectionService disciplineServiceMock;
    private DisciplineContainer rootDiscipline;

    @Before
    public void setUp() {
        Services services = new Services();

        disciplineServiceMock = mock(DisciplineCollectionService.class);
        services.setDisciplineService(disciplineServiceMock);
    }

    @Test
    public void getAllDisciplines() throws ServiceException, RepositoryException, DomainException {
        setUpDisciplines();

        assertResponseCode("disciplines", 200);
    }

    private void setUpDisciplines() throws ServiceException, RepositoryException, DomainException {
        List<DisciplineContainer> rootChildren = new ArrayList<DisciplineContainer>();
        rootDiscipline = mock(DisciplineContainer.class);
        when(rootDiscipline.getSubDisciplines()).thenReturn(rootChildren);
        when(disciplineServiceMock.getRootDiscipline()).thenReturn(rootDiscipline);
    }

    @Test
    public void getAllDisciplinesNotFound() throws ObjectNotFoundException, ServiceException {
        setUpException(ObjectNotFoundException.class);

        assertResponseCode("disciplines", 404);
    }

    @SuppressWarnings("unchecked")
    private void setUpException(Class<? extends Throwable> t) throws ObjectNotFoundException, ServiceException {
        when(disciplineServiceMock.getRootDiscipline()).thenThrow(t);
        when(disciplineServiceMock.getDisciplineById(isA(DmoStoreId.class))).thenThrow(t);
    }

    private void assertResponseCode(String path, int expectedResponse) {
        WebResource webResource = resource().path(path);
        ClientResponse response = webResource.get(ClientResponse.class);

        assertEquals(expectedResponse, response.getStatus());
    }

    @Test
    public void getAllDisciplinesDomainProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(DomainException.class);

        assertResponseCode("disciplines", 500);
    }

    @Test
    public void getAllDisciplinesServiceProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(ServiceException.class);

        assertResponseCode("disciplines", 500);
    }

    @Test
    public void getAllDisciplinesRepositoryProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(RepositoryException.class);

        assertResponseCode("disciplines", 500);
    }

    @Test
    public void getRootDisciplines() throws ServiceException, RepositoryException, DomainException {
        setUpDisciplines();

        assertResponseCode("disciplines/roots", 200);
    }

    @Test
    public void getRootDisciplinesNotFound() throws ObjectNotFoundException, ServiceException {
        setUpException(ObjectNotFoundException.class);

        assertResponseCode("disciplines/roots", 404);
    }

    @Test
    public void getRootDisciplinesDomainProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(DomainException.class);

        assertResponseCode("disciplines/roots", 500);
    }

    @Test
    public void getRootDisciplinesServiceProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(ServiceException.class);

        assertResponseCode("disciplines/roots", 500);
    }

    @Test
    public void getRootDisciplinesRepositoryProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(RepositoryException.class);

        assertResponseCode("disciplines/roots", 500);
    }

    @Test
    public void getDisciplineBySid() throws ServiceException, DomainException {
        setUpGetDisciplineById();

        assertResponseCode("disciplines/easy-discipline:1", 200);
    }

    private void setUpGetDisciplineById() throws ServiceException, DomainException {
        List<DisciplineContainer> rootChildren = new ArrayList<DisciplineContainer>();
        rootDiscipline = mock(DisciplineContainer.class);
        when(rootDiscipline.getSubDisciplines()).thenReturn(rootChildren);
        when(disciplineServiceMock.getDisciplineById(isA(DmoStoreId.class))).thenReturn(rootDiscipline);
    }

    @Test
    public void getDisciplineBySidNotFound() throws ServiceException, DomainException {
        setUpException(ObjectNotFoundException.class);

        assertResponseCode("disciplines/easy-discipline:1", 404);
    }

    @Test
    public void getDisciplineBySidServiceProblem() throws ServiceException, DomainException {
        setUpException(ServiceException.class);

        assertResponseCode("disciplines/easy-discipline:1", 500);
    }

    @Test
    public void getDisciplineBySidDomainProblem() throws ServiceException, DomainException {
        setUpException(DomainException.class);

        assertResponseCode("disciplines/easy-discipline:1", 500);
    }

    @Test
    public void getDisciplineBySidRepositoryProblem() throws ServiceException, DomainException {
        setUpException(RepositoryException.class);

        assertResponseCode("disciplines/easy-discipline:1", 500);
    }

    @Test
    public void getSubDisciplines() throws ServiceException, DomainException {
        setUpGetDisciplineById();

        assertResponseCode("disciplines/easy-discipline:1/subdisciplines", 200);
    }

    @Test
    public void getSubDisciplinesNotFound() throws ObjectNotFoundException, ServiceException {
        setUpException(ObjectNotFoundException.class);

        assertResponseCode("disciplines/easy-discipline:1/subdisciplines", 404);
    }

    @Test
    public void getSubDisciplinesServiceProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(ServiceException.class);

        assertResponseCode("disciplines/easy-discipline:1/subdisciplines", 500);
    }

    @Test
    public void getSubDisciplinesDomainProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(DomainException.class);

        assertResponseCode("disciplines/easy-discipline:1/subdisciplines", 500);
    }

    @Test
    public void getSubDisciplinesRepositoryProblem() throws ObjectNotFoundException, ServiceException {
        setUpException(RepositoryException.class);

        assertResponseCode("disciplines/easy-discipline:1/subdisciplines", 500);
    }

}
