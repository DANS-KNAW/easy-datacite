package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

public class DisciplineConverterTest {
	private DisciplineCollectionService disciplineServiceMock;
	private DisciplineContainer rootDiscipline;

	@Before
	public void setUp() throws ServiceException, RepositoryException,
			DomainException {
		setUpServices();

		setUpDisciplines();
	}

	private void setUpServices() {
		Services services = new Services();

		disciplineServiceMock = mock(DisciplineCollectionService.class);
		services.setDisciplineService(disciplineServiceMock);
	}

	private void setUpDisciplines() throws ServiceException,
			RepositoryException, DomainException {
		DisciplineContainer child1 = setUpChildDiscipline1();
		DisciplineContainer child2 = setUpChildDiscipline2();
		DisciplineContainer parent = setUpParentDiscipline(child1, child2);
		List<DisciplineContainer> rootChildren = new ArrayList<DisciplineContainer>();
		rootChildren.add(parent);
		rootDiscipline = mock(DisciplineContainer.class);
		when(rootDiscipline.getSubDisciplines()).thenReturn(rootChildren);
		when(disciplineServiceMock.getRootDiscipline()).thenReturn(
				rootDiscipline);
	}

	private DisciplineContainer setUpChildDiscipline1() throws DomainException,
			RepositoryException {
		DisciplineContainer child = mock(DisciplineContainer.class);
		when(child.getStoreId()).thenReturn("easy-discipline:2");
		when(child.getName()).thenReturn("child name");
		when(child.getSubDisciplines()).thenReturn(
				new ArrayList<DisciplineContainer>());
		Set<DmoStoreId> parents = new HashSet<DmoStoreId>();
		parents.add(new DmoStoreId("easy-discipline:1"));
		when(child.getParentSids()).thenReturn(parents);
		when(child.getCollections()).thenReturn(new HashSet<DmoCollection>());
		return child;
	}

	private DisciplineContainer setUpChildDiscipline2() throws DomainException,
			RepositoryException {
		DisciplineContainer child2 = mock(DisciplineContainer.class);
		when(child2.getStoreId()).thenReturn("easy-discipline:3");
		when(child2.getName()).thenReturn("child2 name");
		when(child2.getSubDisciplines()).thenReturn(null);
		when(child2.getParentSids()).thenReturn(null);
		when(child2.getCollections()).thenReturn(null);
		return child2;
	}

	private DisciplineContainer setUpParentDiscipline(
			DisciplineContainer child1, DisciplineContainer child2)
			throws DomainException, RepositoryException {
		DisciplineContainer parent = mock(DisciplineContainer.class);
		when(parent.getStoreId()).thenReturn("easy-discipline:1");
		when(parent.getName()).thenReturn("parent name");
		List<DisciplineContainer> parentChildren = new ArrayList<DisciplineContainer>();
		parentChildren.add(child1);
		parentChildren.add(child2);
		when(parent.getSubDisciplines()).thenReturn(parentChildren);
		when(parent.getParentSids()).thenReturn(new HashSet<DmoStoreId>());

		Set<DmoCollection> collections = new HashSet<DmoCollection>();
		DmoCollection collection = mock(DmoCollection.class);
		when(collection.getStoreId()).thenReturn("easy-collection:1");
		when(collection.getLabel()).thenReturn("collection label");
		collections.add(collection);
		when(parent.getCollections()).thenReturn(collections);

		return parent;
	}

	@Test(expected = AssertionError.class)
	public void notInstantiable() {
		new DisciplineConverter();
	}

	@Test
	public void getRootDisciplines() throws DomainException, ServiceException,
			RepositoryException {
		String xml = DisciplineConverter.getDisciplineList(0);
		String expectedXml = "<disciplines>" +
				"<discipline>" +
				"<id>easy-discipline:1</id>" +
				"<name>parent name</name>" +
				"<collections>" +
				"<collection>" +
				"<id>easy-collection:1</id>" +
				"<label>collection label</label>" +
				"</collection>" +
				"</collections>" +
				"</discipline>" +
				"</disciplines>";
		assertEquals(expectedXml, xml);
	}

	@Test
	public void getDisciplineList() throws DomainException, ServiceException,
			RepositoryException {
		String xml = DisciplineConverter.getDisciplineList(2);
		String expectedXml = "<disciplines>" +
				"<discipline>" +
				"<id>easy-discipline:1</id>" +
				"<name>parent name</name>" +
				"<collections>" +
				"<collection>" +
				"<id>easy-collection:1</id>" +
				"<label>collection label</label>" +
				"</collection>" +
				"</collections>" +
				"<subDisciplines>" +
				"<discipline>" +
				"<id>easy-discipline:2</id>" +
				"<name>child name</name>" +
				"<parents>" +
				"<id>easy-discipline:1</id>" +
				"</parents>" +
				"</discipline>" +
				"<discipline>" +
				"<id>easy-discipline:3</id>" +
				"<name>child2 name</name>" +
				"</discipline>" +
				"</subDisciplines>" +
				"</discipline>" +
				"</disciplines>";
		assertEquals(xml, expectedXml);
	}

}
