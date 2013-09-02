package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.user.GroupImpl;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.FileItemContentsAccessCheck;

import org.junit.BeforeClass;
import org.junit.Test;

public class FileItemAccessCheckTest
{

    private static final Group TEST_GROUP = new GroupImpl("testGroup");

    // There are 2592 test loops.
    private static final int TEST_COUNT = 2592;

    // test loops that should have the officer give a positive decision are in this array.
    // (see also src/test/resources/output/fileItemAccessCheck.txt)
    private static final int[] POSITIVE_STATES = {1304, 1316, 1328, 1340, 1352, 1364, 1365, 1376, 1377, 1412, 1424, 1436, 1448, 1460, 1472, 1473, 1474, 1484,
            1485, 1486, 1520, 1532, 1544, 1556, 1568, 1580, 1581, 1592, 1593, 1595, 1628, 1640, 1652, 1664, 1676, 1688, 1689, 1690, 1700, 1701, 1702, 1703};
    private static List<Integer> POSITIVE_STATES_LIST = new ArrayList<Integer>();

    private boolean verbose = Tester.isVerbose();

    private StringBuilder explanations;
    int testCounter;

    @BeforeClass
    public static void beforeClass()
    {
        for (int i : POSITIVE_STATES)
        {
            POSITIVE_STATES_LIST.add(i);
        }
    }

    @Test
    public void evaluate() throws Exception
    {
        explanations = new StringBuilder();
        testCounter = 0;

        TestDataset dataset = new TestDataset(); // null state
        evaluateGroups(dataset);

        for (DatasetState datasetState : DatasetState.values())
        {
            dataset.datasetState = datasetState;
            evaluateGroups(dataset);
        }

        assertEquals(TEST_COUNT, testCounter);
    }

    private void evaluateGroups(TestDataset dataset)
    {
        dataset.datasetGroup = null;
        evaluatePermission(dataset);

        dataset.datasetGroup = TEST_GROUP.getId();
        evaluatePermission(dataset);
    }

    private void evaluatePermission(TestDataset dataset)
    {
        dataset.permissionGranted = false;
        evaluateUserState(dataset);

        dataset.permissionGranted = true;
        evaluateUserState(dataset);
    }

    private void evaluateUserState(TestDataset dataset)
    {
        EasyUser anonUser = EasyUserAnonymous.getInstance();
        evaluateUserGroup(anonUser, dataset);

        EasyUser user = new EasyUserImpl("testUser");
        for (State userState : State.values())
        {
            user.setState(userState);
            user.leaveGroup(TEST_GROUP);
            evaluateUserGroup(user, dataset);
        }
    }

    private void evaluateUserGroup(EasyUser user, TestDataset dataset)
    {
        assertFalse(user.isMemberOf(TEST_GROUP));
        evaluateDatasetDescendancy(user, dataset);

        if (!user.isAnonymous())
        {
            user.joinGroup(TEST_GROUP);
            evaluateDatasetDescendancy(user, dataset);
        }
    }

    private void evaluateDatasetDescendancy(EasyUser user, TestDataset dataset)
    {
        FileItem fileItem = new FileItemImpl("file:testFileItem");
        evaluateFileItemAccess(user, dataset, fileItem);

        fileItem.setDatasetId(dataset.getDmoStoreId());
        evaluateFileItemAccess(user, dataset, fileItem);
    }

    private void evaluateFileItemAccess(EasyUser user, TestDataset dataset, FileItem fileItem)
    {
        evaluate(user, dataset, fileItem); // null accessCat

        for (AccessibleTo accessibleTo : AccessibleTo.values())
        {
            fileItem.setAccessibleTo(accessibleTo);
            evaluate(user, dataset, fileItem);
        }
    }

    private void evaluate(EasyUser user, TestDataset dataset, FileItem fileItem)
    {
        ContextParameters ctxParameters = new ContextParameters(user, dataset, fileItem);
        FileItemContentsAccessCheck officer = new FileItemContentsAccessCheck();

        testCounter++;
        boolean accessAllowed = officer.evaluate(ctxParameters);

        String msg = "\n-----------------------\n" + testCounter + " dataset.AdministrativeState=" + dataset.getAdministrativeState()
                + officer.explain(ctxParameters);

        explanations.append(msg);

        if (verbose)
            System.out.println(msg);

        if (accessAllowed)
        {
            assertTrue("Negative state was allowed." + msg, POSITIVE_STATES_LIST.contains(testCounter));
        }
        else
        {
            assertFalse("Positive state was not allowed." + msg, POSITIVE_STATES_LIST.contains(testCounter));
        }
    }

    class TestDataset extends DatasetImpl
    {

        private static final long serialVersionUID = 1L;

        public TestDataset()
        {
            super("easy:whatever");
        }

        DatasetState datasetState;
        String datasetGroup;
        boolean permissionGranted;

        @Override
        public DatasetState getAdministrativeState()
        {
            return datasetState;
        }

        @Override
        public Set<String> getGroupIds()
        {
            Set<String> set = new HashSet<String>();
            if (datasetGroup != null)
            {
                set.add(datasetGroup);
            }
            return set;
        }

        @Override
        public boolean isPermissionGrantedTo(EasyUser user)
        {
            return permissionGranted;
        }
    }

}
