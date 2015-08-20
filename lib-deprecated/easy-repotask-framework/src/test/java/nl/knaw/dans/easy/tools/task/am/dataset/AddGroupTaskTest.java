package nl.knaw.dans.easy.tools.task.am.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.util.Dialogue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({Services.class, DatasetService.class, Dataset.class, Application.class, Dialogue.class})
@RunWith(PowerMockRunner.class)
public class AddGroupTaskTest {

    private EasyUser archivist;
    private EasyUser depositor;
    private DatasetService datasetServiceMock;
    private Dataset datasetMock;

    @Before
    public void setUp() throws Exception {
        System.setProperty("easy.home", "../easy-home");

        setUpUsers();

        setUpDatasetMock();

        setUpDatasetServiceMock();

        setUpApplicationMock();

        setUpDialogueMock(true, "1");

        replayAll();
    }

    @Test
    public void testTask() throws Exception {
        // run the task and try to add group Archaeology
        AddGroupTask task = new AddGroupTask();
        task.run(null);
        Assert.assertEquals(task.getReturnCode(), 1);

        // run the task and try to add group History
        reset(true, "2");
        task = new AddGroupTask();
        task.run(null);
        Assert.assertEquals(task.getReturnCode(), 2);

        // run the task and try to add an invalid group number
        reset(true, "13");
        task = new AddGroupTask();
        task.run(null);
        Assert.assertEquals(task.getReturnCode(), -1);

        // run the task and don't confirm changing the dataset
        reset(false, "1");
        task = new AddGroupTask();
        task.run(null);
        Assert.assertEquals(task.getReturnCode(), 0);
    }

    private void reset(boolean confirm, String groupNumber) throws Exception {
        resetAll();

        setUpDatasetMock();

        setUpDatasetServiceMock();

        setUpApplicationMock();

        setUpDialogueMock(confirm, groupNumber);

        replayAll();
    }

    private void setUpUsers() {
        archivist = new EasyUserImpl("archie");
        archivist.setFirstname("Archie");
        archivist.setSurname("Archiver");
        archivist.addRole(Role.USER);
        archivist.addRole(Role.ARCHIVIST);
        archivist.setState(State.ACTIVE);

        depositor = new EasyUserImpl("depie");
        depositor.setFirstname("Depo");
        depositor.setSurname("Depositorer");
        depositor.addRole(Role.USER);
        depositor.setState(State.ACTIVE);
    }

    private void setUpDatasetMock() {
        datasetMock = PowerMock.createMock(Dataset.class);
        expect(datasetMock.getPreferredTitle()).andReturn("PowerMock").anyTimes();
        expect(datasetMock.getDepositor()).andReturn(depositor).anyTimes();
        expect(datasetMock.addGroup(isA(Group.class))).andReturn(true).anyTimes();
    }

    private void setUpDatasetServiceMock() throws Exception {
        datasetServiceMock = PowerMock.createNiceMock(DatasetService.class);

        mockStatic(Services.class);
        expect(Services.getDatasetService()).andReturn(datasetServiceMock).anyTimes();
        expect(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).andReturn(datasetMock).anyTimes();
    }

    private void setUpApplicationMock() {
        mockStatic(Application.class);
        expect(Application.authenticate()).andReturn(archivist).anyTimes();
    }

    private void setUpDialogueMock(boolean confirm, String groupNumber) throws Exception {
        mockStatic(Dialogue.class);
        expect(Dialogue.getInput(isA(String.class))).andReturn(groupNumber).anyTimes();
        expect(Dialogue.confirm(isA(String.class))).andReturn(confirm).anyTimes();
    }

    @After
    public void tearDown() {
        resetAll();
    }

}
