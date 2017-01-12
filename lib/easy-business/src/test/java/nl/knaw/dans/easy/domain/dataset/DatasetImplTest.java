package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItem;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.collections.DmoCollectionsAccess;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.user.GroupImpl;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.Test;

public class DatasetImplTest {
    private boolean verbose = Tester.isVerbose();

    @Test
    public void dirtyChecking() {
        DatasetImpl dataset = new DatasetImpl("dummy-dataset:1");
        assertTrue(dataset.isDirty());

        if (verbose)
            Tester.printClassAndFieldHierarchy(DatasetImpl.class);
        // fields affected by dirty checking:
        // label:java.lang.String
        // ownerId:java.lang.String (override --> AdministrativeMetadata)
        // state:java.lang.String

        dataset.setLabel("foo");
        assertTrue(dataset.isDirty());
        dataset.setDirty(false);

        dataset.setState("active");
        assertTrue(dataset.isDirty());
        dataset.setDirty(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void accessCategory() {
        AccessCategory.valueOf("bla");
    }

    @Test
    public void getAccessCategory() {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        AccessCategory ac = dataset.getAccessCategory();
        assertNotNull(ac);
        assertEquals(Dataset.DEFAULT_ACCESS_CATEGORY, ac);

        dataset.getEasyMetadata().getEmdRights().setAccessCategory(AccessCategory.REQUEST_PERMISSION);
        ac = dataset.getAccessCategory();
        assertEquals(AccessCategory.REQUEST_PERMISSION, ac);
    }

    @Test
    public void getAccessProfileForUser() {
        Group history = new GroupImpl("history");
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.addGroup(history);
        EasyUser jan = new EasyUserImpl("jan");

        assertEquals(0, dataset.getAccessProfileFor(jan));

        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.MAINTENANCE);
        assertEquals(0, dataset.getAccessProfileFor(jan));

        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);
        assertEquals(1, dataset.getAccessProfileFor(jan));

        jan.setState(State.ACTIVE);
        assertEquals(3, dataset.getAccessProfileFor(jan));

        jan.joinGroup(history);
        assertEquals(7, dataset.getAccessProfileFor(jan));

        PermissionSequenceImpl sequence = new PermissionSequenceImpl(jan);
        sequence.setState(nl.knaw.dans.easy.domain.model.PermissionSequence.State.Granted);
        ((PermissionSequenceListImpl) dataset.getPermissionSequenceList()).addSequence(sequence);
        assertEquals(15, dataset.getAccessProfileFor(jan));

        jan.leaveGroup(history);
        assertEquals(11, dataset.getAccessProfileFor(jan));

        sequence.setState(nl.knaw.dans.easy.domain.model.PermissionSequence.State.Submitted);
        assertEquals(3, dataset.getAccessProfileFor(jan));
    }

    @Test
    public void getMetadataFormat() {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        assertEquals(MetadataFormat.DEFAULT, dataset.getMetadataFormat());
    }

    @Test
    public void getLabel() {
        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        EasyMetadata emd = dataset.getEasyMetadata();
        emd.getEmdTitle().getDcTitle().add(new BasicString("Title should be propagated to label of dataset"));
        assertEquals("Title should be propagated to label of dataset", dataset.getLabel());

        dataset = new DatasetImpl("dummy-dataset:2");
        assertEquals("[no title]", dataset.getLabel());
        emd = dataset.getEasyMetadata();
        emd.getEmdTitle().getDcTitle().add(new BasicString("Title should be propagated to label of dataset"));
        assertEquals("Title should be propagated to label of dataset", dataset.getLabel());
    }

    @Test
    public void getEncodedDansManagedDoi() {
        Dataset dataset = EasyMock.createMockBuilder(DatasetImpl.class).addMockedMethod("getDansManagedDoi").createMock();
        EasyMock.expect(dataset.getDansManagedDoi()).andReturn(new String("10.17026/dans-test-doi")).anyTimes();
        EasyMock.replay(dataset);

        assertEquals("10.17026%2Fdans-test-doi", dataset.getEncodedDansManagedDoi());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addRelationsButOnlyOneOfAKind() throws Exception {
        DmoCollectionsAccess dmoCollectionAccess = EasyMock.createMock(DmoCollectionsAccess.class);
        new Data().setCollectionAccess(dmoCollectionAccess);

        Dataset dataset = new DatasetImpl("dummy:2");
        DatasetRelations relations = (DatasetRelations) dataset.getRelations();
        assertEquals(0, relations.size());

        relations.addOAIIdentifier();
        assertEquals(1, relations.size());

        relations.addOAIIdentifier();
        assertEquals(1, relations.size());

        EasyMock.expect(dmoCollectionAccess.filterOAIEndNodes(EasyMock.isA(Set.class))).andReturn(new HashSet<DmoStoreId>()).anyTimes();
        EasyMock.replay(dmoCollectionAccess);

        relations.addOAISetMembership(new DmoStoreId("foo:bar"));
        if (verbose)
            printRelations(relations);
        assertEquals(2, relations.size());

        relations.addOAISetMembership(new DmoStoreId("foo:bar"));
        assertEquals(2, relations.size());

        EasyMock.verify(dmoCollectionAccess);
    }

    @AfterClass
    public static void afterClass() {
        // the next test class should not inherit from this one
        Data data = new Data();
        data.setCollectionAccess(null);
    }

    private void printRelations(Relations relations) {
        for (Relation relation : relations.getRelation(null, null)) {
            System.out.println(relation);
        }

    }

    @Test
    public void getLeafDisciplines() throws Exception {
        // Disasters happen if you ignore LOW COUPLING, HIGH COHESION, TESTABILITY!
        DisciplineContainer root = new DisciplineContainerImplStub("root");
        DisciplineContainer d1 = new DisciplineContainerImplStub("easy-discipline:1");
        DisciplineContainer d1_1 = new DisciplineContainerImplStub("easy-discipline:1_1");
        DisciplineContainer d1_1_1 = new DisciplineContainerImplStub("easy-discipline:1_1_1");
        DisciplineContainer d1_2 = new DisciplineContainerImplStub("easy-discipline:1_2");

        DisciplineContainer d2 = new DisciplineContainerImplStub("easy-discipline:2");
        DisciplineContainer d2_1 = new DisciplineContainerImplStub("easy-discipline:2_1");
        DisciplineContainer d2_1_1 = new DisciplineContainerImplStub("easy-discipline:2_1_1");
        DisciplineContainer d2_2 = new DisciplineContainerImplStub("easy-discipline:2_2");
        DisciplineContainer d2_2_1 = new DisciplineContainerImplStub("easy-discipline:2_2_1");

        root.addChild(d1);
        d1.addChild(d1_1);
        d1_1.addChild(d1_1_1);
        d1.addChild(d1_2);

        root.addChild(d2);
        d2.addChild(d2_1);
        d2_1.addChild(d2_1_1);
        d2.addChild(d2_2);
        d2_2.addChild(d2_2_1);

        List<DisciplineContainer> disciplines = new ArrayList<DisciplineContainer>();
        disciplines.add(d1);
        disciplines.add(d1_1);
        disciplines.add(d1_1_1);
        disciplines.add(d1_2);

        disciplines.add(d2_2_1);

        Dataset dataset = new DatasetImplProxy("foo:test", disciplines);

        // Phew! Now we can test a simple method
        List<DisciplineContainer> leafDisciplines = dataset.getLeafDisciplines();
        assertEquals(3, leafDisciplines.size());
        assertTrue(leafDisciplines.contains(d1_1_1));
        assertTrue(leafDisciplines.contains(d1_2));
        assertTrue(leafDisciplines.contains(d2_2_1));
    }

    class DisciplineContainerImplStub extends DisciplineContainerImpl {

        private List<DisciplineContainer> subDisciplines = new ArrayList<DisciplineContainer>();

        public DisciplineContainerImplStub(String storeId) {
            super(storeId);
        }

        @Override
        public void addChild(DmoContainerItem item) throws RepositoryException {
            subDisciplines.add((DisciplineContainer) item);
        }

        @Override
        public List<DisciplineContainer> getSubDisciplines() throws DomainException {
            return subDisciplines;
        }

        private static final long serialVersionUID = 1L;

    }

    class DatasetImplProxy extends DatasetImpl {

        private final List<DisciplineContainer> disciplines;

        public DatasetImplProxy(String storeId, List<DisciplineContainer> disciplines) {
            super(storeId);
            this.disciplines = disciplines;
        }

        @Override
        public List<DisciplineContainer> getParentDisciplines() throws ObjectNotFoundException, DomainException {
            return disciplines;
        }

        private static final long serialVersionUID = 1L;

    }

}
