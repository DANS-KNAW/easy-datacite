package nl.knaw.dans.easy.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.dataset.DatasetFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class DatasetRelationsTest {

    @BeforeClass
    public static void beforeClass() {
        AbstractDmoFactory.register(Dataset.NAMESPACE, new DatasetFactory());
    }

    @Test
    public void getOAISetMembershipsEmpty() throws Exception {
        Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
        DatasetRelations relations = dataset.getRelations();
        Set<DmoStoreId> storeIds = relations.getOAISetMemberships();
        assertTrue(storeIds.isEmpty());
    }

    @Test
    public void getOAISetMemberships() throws Exception {
        Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
        DatasetRelations relations = dataset.getRelations();
        relations.addOAISetMembership(new DmoStoreId("foo-bar:1"));
        relations.addOAISetMembership(new DmoStoreId("foo-bar:2"));
        relations.addOAISetMembership(new DmoStoreId("baz:1"));

        Set<DmoStoreId> storeIds = relations.getOAISetMemberships();
        assertEquals(3, storeIds.size());

        storeIds = relations.getOAISetMemberships(new DmoNamespace("foo-bar"));
        assertEquals(2, storeIds.size());

        storeIds = relations.getOAISetMemberships(new DmoNamespace("baz"));
        assertEquals(1, storeIds.size());

        storeIds = relations.getOAISetMemberships(new DmoNamespace("foo"));
        assertEquals(0, storeIds.size());

        relations.removeOAISetMembership(new DmoStoreId("foo-bar:2"));

        storeIds = relations.getOAISetMemberships();
        assertEquals(2, storeIds.size());

        storeIds = relations.getOAISetMemberships(new DmoNamespace("foo-bar"));
        assertEquals(1, storeIds.size());

        storeIds = relations.getOAISetMemberships(new DmoNamespace("baz"));
        assertEquals(1, storeIds.size());

        storeIds = relations.getOAISetMemberships(new DmoNamespace("foo"));
        assertEquals(0, storeIds.size());
    }

    @Test
    public void getCollectionMembershipsEmpty() throws Exception {
        Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
        DatasetRelations relations = dataset.getRelations();
        Set<DmoStoreId> storeIds = relations.getCollectionMemberships();
        assertTrue(storeIds.isEmpty());
    }

    @Test
    public void getCollectionMemberships() throws Exception {
        Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
        DatasetRelations relations = dataset.getRelations();
        relations.addCollectionMembership(new DmoStoreId(ECollection.EasyCollection.namespace, "1"));
        relations.addCollectionMembership(new DmoStoreId(ECollection.EasyCollection.namespace, "2"));
        relations.addCollectionMembership(new DmoStoreId(ECollection.EasyResearchArea.namespace, "1"));

        Set<DmoStoreId> storeIds = relations.getCollectionMemberships();
        assertEquals(3, storeIds.size());

        storeIds = relations.getCollectionMemberships(ECollection.EasyCollection.namespace);
        assertEquals(2, storeIds.size());

        storeIds = relations.getCollectionMemberships(ECollection.EasyResearchArea.namespace);
        assertEquals(1, storeIds.size());

        storeIds = relations.getCollectionMemberships(ECollection.EasyInterestArea.namespace);
        assertEquals(0, storeIds.size());

        relations.removeCollectionMembership(new DmoStoreId(ECollection.EasyCollection.namespace, "2"));

        storeIds = relations.getCollectionMemberships();
        assertEquals(2, storeIds.size());

        storeIds = relations.getCollectionMemberships(ECollection.EasyCollection.namespace);
        assertEquals(1, storeIds.size());

        storeIds = relations.getCollectionMemberships(ECollection.EasyResearchArea.namespace);
        assertEquals(1, storeIds.size());

        storeIds = relations.getCollectionMemberships(ECollection.EasyInterestArea.namespace);
        assertEquals(0, storeIds.size());
    }

    @Test
    public void isCollectionMember() throws Exception {
        Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
        DatasetRelations relations = dataset.getRelations();
        relations.addCollectionMembership(new DmoStoreId(ECollection.EasyCollection.namespace, "1"));
        relations.addCollectionMembership(new DmoStoreId(ECollection.EasyCollection.namespace, "2"));
        relations.addCollectionMembership(new DmoStoreId(ECollection.EasyResearchArea.namespace, "1"));

        assertFalse(relations.isCollectionMember(new DmoStoreId(ECollection.EasyResearchArea.namespace, "2")));
        assertTrue(relations.isCollectionMember(new DmoStoreId(ECollection.EasyCollection.namespace, "2")));
    }

    @Test
    public void isOAISetMember() throws Exception {
        Dataset dataset = (Dataset) AbstractDmoFactory.newDmo(Dataset.NAMESPACE);
        DatasetRelations relations = dataset.getRelations();
        relations.addOAISetMembership(new DmoStoreId("foo-bar:1"));
        relations.addOAISetMembership(new DmoStoreId("foo-bar:2"));
        relations.addOAISetMembership(new DmoStoreId("baz:1"));

        assertFalse(relations.isOAISetMember(new DmoStoreId("baz:2")));
        assertTrue(relations.isOAISetMember(new DmoStoreId("foo-bar:2")));
    }

}
