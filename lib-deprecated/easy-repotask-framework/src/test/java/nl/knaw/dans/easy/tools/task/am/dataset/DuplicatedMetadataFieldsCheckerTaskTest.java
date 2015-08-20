package nl.knaw.dans.easy.tools.task.am.dataset;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdDescription;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RL.class)
public class DuplicatedMetadataFieldsCheckerTaskTest {
    private Dataset datasetMock;
    private EasyMetadata emdMock;
    private EmdTitle emdTitleMock;
    private EmdDescription emdDescMock;
    private DuplicatedMetadataFieldsCheckerTask taskUnderTest;
    private JointMap jointMap;

    @Before
    public void setUp() {
        PowerMock.mockStatic(RL.class);
        emdMock = PowerMock.createMock(EasyMetadata.class);
        emdTitleMock = PowerMock.createMock(EmdTitle.class);
        emdDescMock = PowerMock.createMock(EmdDescription.class);
        datasetMock = PowerMock.createMock(Dataset.class);
        taskUnderTest = new DuplicatedMetadataFieldsCheckerTask();
        jointMap = new JointMap();
    }

    @Test
    public void testCopiedAltTitle() throws Exception {
        jointMap.setDataset(createCopiedAltTitleDataset());
        final Capture<Event> event = createRLErrorCapture();

        assertEquals("Copied alternative title is found. StoreId: easy-dataset:1. Submitted on 2012-07-24T00:00:00.000+0200", event.getValue().getMessages()
                .get(0));
    }

    @Test
    public void testTitleIsOk() throws Exception {
        jointMap.setDataset(createOkTitleDataset());
        final Capture<Event> event = createRLInfoCapture();
        assertEquals("Title is ok.", event.getValue().getMessages().get(0));
    }

    /**
     * @return
     * @throws TaskException
     * @throws TaskCycleException
     * @throws FatalTaskException
     */
    private Capture<Event> createRLInfoCapture() throws TaskException, TaskCycleException, FatalTaskException {
        final Capture<Event> event = new Capture<Event>();
        RL.info(capture(event));
        expectLastCall();
        PowerMock.replayAll();
        taskUnderTest.run(jointMap);
        return event;
    }

    @Test
    public void testCopiedTitle() throws Exception {
        jointMap.setDataset(createCopiedTitle());
        final Capture<Event> event = createRLErrorCapture();
        assertEquals("Duplicated title is found. StoreId: easy-dataset:1. Submitted on 2012-07-24T00:00:00.000+0200", event.getValue().getMessages().get(0));
    }

    @Test
    public void testEmptyTitle() throws Exception {
        jointMap.setDataset(createEmptyTitle());
        final Capture<Event> event = createRLErrorCapture();
        assertEquals("Title is null or empty for the storeId: easy-dataset:1. Submitted on 2012-07-24T00:00:00.000+0200", event.getValue().getMessages().get(0));
    }

    /**
     * @return
     * @throws TaskException
     * @throws TaskCycleException
     * @throws FatalTaskException
     */
    private Capture<Event> createRLErrorCapture() throws TaskException, TaskCycleException, FatalTaskException {
        final Capture<Event> event = new Capture<Event>();
        RL.error(capture(event));
        expectLastCall();
        PowerMock.replayAll();
        taskUnderTest.run(jointMap);
        return event;
    }

    @Test
    public void testCopiedDescription() throws Exception {
        jointMap.setDataset(createCopiedDescription());
        final Capture<Event> event = createRLErrorCapture();
        assertEquals("Duplicated description is found for the storeId: easy-dataset:1. Submitted on 2012-07-24T00:00:00.000+0200", event.getValue()
                .getMessages().get(0));
    }

    private Dataset createDataset() {
        expect(datasetMock.getStoreId()).andStubReturn("easy-dataset:1");
        expect(datasetMock.getDateSubmitted()).andStubReturn(new IsoDate("2012-07-24"));
        expect(datasetMock.getEasyMetadata()).andStubReturn(emdMock);
        expect(emdMock.getEmdTitle()).andStubReturn(emdTitleMock);
        expect(emdMock.getEmdDescription()).andStubReturn(emdDescMock);
        return datasetMock;
    }

    private Dataset createCopiedAltTitleDataset() {
        Dataset dataset = createDataset();
        List<BasicString> dcTitle = createDcTitle();
        List<BasicString> dcAltTitle = createDcAlternativeTitle();
        BasicString bsAlternativeTitle = new BasicString("alternativeTitle");
        dcAltTitle.add(bsAlternativeTitle);
        dcTitle.add(bsAlternativeTitle);
        dcTitle.add(bsAlternativeTitle);
        expectMockDcObject(dcTitle, dcAltTitle, createDcDescription());
        return dataset;
    }

    /**
     * @return
     */
    private List<BasicString> createDcTitle() {
        List<BasicString> dcTitle = new ArrayList<BasicString>();
        BasicString bsTitle = new BasicString("Title");
        dcTitle.add(bsTitle);
        return dcTitle;
    }

    /**
     * @return
     */
    private List<BasicString> createDcDescription() {
        List<BasicString> dcDesc = new ArrayList<BasicString>();
        BasicString bsDesc = new BasicString("Desc");
        dcDesc.add(bsDesc);
        return dcDesc;
    }

    private Dataset createOkTitleDataset() {
        Dataset dataset = createDataset();
        List<BasicString> dcTitle = createDcTitle();

        expectMockDcObject(dcTitle, createDcAlternativeTitle(), createDcDescription());
        return dataset;
    }

    /**
     * @return
     */
    private List<BasicString> createDcAlternativeTitle() {
        List<BasicString> dcAltTitle = new ArrayList<BasicString>();
        BasicString bsAlternativeTitle = new BasicString("alternativeTitle");
        dcAltTitle.add(bsAlternativeTitle);
        return dcAltTitle;
    }

    private Dataset createCopiedTitle() {
        Dataset dataset = createDataset();
        List<BasicString> dcTitle = createDcTitle();
        BasicString bsTitle = new BasicString("Title");
        dcTitle.add(bsTitle);
        dcTitle.add(bsTitle);

        expectMockDcObject(dcTitle, null, createDcDescription());
        return dataset;
    }

    private Dataset createEmptyTitle() {
        Dataset dataset = createDataset();

        expectMockDcObject(null, null, createDcDescription());
        return dataset;
    }

    private Dataset createCopiedDescription() {
        Dataset dataset = createDataset();
        List<BasicString> dcTitle = createDcTitle();

        List<BasicString> dcDesc = new ArrayList<BasicString>();
        BasicString bsDesc = new BasicString("Desc");
        dcDesc.add(bsDesc);
        dcDesc.add(bsDesc);

        expectMockDcObject(dcTitle, null, dcDesc);
        return dataset;
    }

    /**
     * @param dcTitle
     * @param dcDesc
     */
    private void expectMockDcObject(List<BasicString> dcTitle, List<BasicString> dcAltTitle, List<BasicString> dcDesc) {
        expect(emdTitleMock.getDcTitle()).andStubReturn(dcTitle);
        expect(emdTitleMock.getTermsAlternative()).andStubReturn(dcAltTitle);
        expect(emdDescMock.getDcDescription()).andStubReturn(dcDesc);
    }

}
