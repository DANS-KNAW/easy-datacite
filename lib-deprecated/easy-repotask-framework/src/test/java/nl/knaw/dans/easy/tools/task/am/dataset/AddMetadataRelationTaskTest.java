package nl.knaw.dans.easy.tools.task.am.dataset;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.Relation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RL.class)
public class AddMetadataRelationTaskTest {
    private static String RELATION_URI_STRING = "http://easy.dans.knaw.nl/";
    private static String RELATION_TITLE_STRING = "Electronic Archiving SYstem (EASY)";
    private static boolean RELATION_EMPHASIS = true;

    private JointMap jointMap;

    private AddMetadataRelationTask taskUnderTest;

    @Before
    public void setUp() throws Exception {
        setupJointMapWithDataset();

        Relation relation = createRelation();

        PowerMock.mockStatic(RL.class);
        taskUnderTest = new AddMetadataRelationTask(relation);

        taskUnderTest.run(jointMap);
    }

    private void setupJointMapWithDataset() {
        jointMap = new JointMap();
        Dataset dataset = new DatasetImpl("dummy-dataset:1", MetadataFormat.ARCHAEOLOGY);
        jointMap.setDataset(dataset);
    }

    private Relation createRelation() throws Exception {
        Relation relation = new Relation();
        relation.setSubjectLink(new URI(RELATION_URI_STRING));
        relation.setSubjectTitle(new BasicString(RELATION_TITLE_STRING));
        relation.setEmphasis(RELATION_EMPHASIS);
        return relation;
    }

    @Test
    public void testRelationWasAdded() throws Exception {
        Dataset datasetAfter = jointMap.getDataset();
        int numberOfRelationsAfter = datasetAfter.getEasyMetadata().getEmdRelation().getEasRelation().size();

        assertEquals(1, numberOfRelationsAfter);
    }

    @Test
    public void testRelationURIWasAdded() throws Exception {
        assertEquals(RELATION_URI_STRING, getAddedRelation().getSubjectLink().toString());
    }

    @Test
    public void testRelationTitleWasAdded() throws Exception {
        assertEquals(RELATION_TITLE_STRING, getAddedRelation().getSubjectTitle().toString());
    }

    @Test
    public void testRelationEmphasisWasAdded() throws Exception {
        assertEquals(RELATION_EMPHASIS, getAddedRelation().hasEmphasis());
    }

    private Relation getAddedRelation() {
        Dataset datasetAfter = jointMap.getDataset();
        return datasetAfter.getEasyMetadata().getEmdRelation().getEasRelation().get(0);
    }
}
