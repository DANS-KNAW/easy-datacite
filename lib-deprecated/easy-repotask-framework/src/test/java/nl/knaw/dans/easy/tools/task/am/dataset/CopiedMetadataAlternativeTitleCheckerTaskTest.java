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
import nl.knaw.dans.pf.language.emd.EasyMetadata;
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
public class CopiedMetadataAlternativeTitleCheckerTaskTest {
    private EasyMetadata emdMock;
    private EmdTitle emdTitleMock;
    private CopiedMetadataAlternativeTitleCheckerTask taskUnderTest;
    private JointMap jointMap;

    @Before
    public void setUp() {
        PowerMock.mockStatic(RL.class);

        taskUnderTest = new CopiedMetadataAlternativeTitleCheckerTask();

    }

    @Test
    public void testMultiTitleTask() throws Exception {
        jointMap = new JointMap();
        jointMap.setDataset(createDataset("main title", new String[] {"alternative title"}, true));
        final Capture<Event> event = new Capture<Event>();
        RL.error(capture(event));
        expectLastCall();
        PowerMock.replayAll();
        taskUnderTest.run(jointMap);

        assertEquals("Alternative title is copied to the title for the storeId: easy-dataset:1. Submitted on 2012-07-24T00:00:00.000+0200", event.getValue()
                .getMessages().get(0));
    }

    @Test
    public void testTitleIsOkTask() throws Exception {
        jointMap = new JointMap();
        jointMap.setDataset(createDataset("main title", new String[] {"alternative title"}, false));
        final Capture<Event> event = new Capture<Event>();
        RL.info(capture(event));
        expectLastCall();
        PowerMock.replayAll();
        taskUnderTest.run(jointMap);
        assertEquals("Title is ok.", event.getValue().getMessages().get(0));
    }

    private Dataset createDataset(String title, String[] alternativeTitles, boolean isCopied) {
        Dataset datasetMock = PowerMock.createMock(Dataset.class);
        expect(datasetMock.getStoreId()).andStubReturn("easy-dataset:1");
        expect(datasetMock.getDateSubmitted()).andStubReturn(new IsoDate("2012-07-24"));
        emdMock = PowerMock.createMock(EasyMetadata.class);
        emdTitleMock = PowerMock.createMock(EmdTitle.class);

        List<BasicString> dcTitle = new ArrayList<BasicString>();
        BasicString bsTitle = new BasicString(title);
        dcTitle.add(bsTitle);

        List<BasicString> dcAltTitle = new ArrayList<BasicString>();
        for (String alternativeTitle : alternativeTitles) {
            BasicString bsAlternativeTitle = new BasicString(alternativeTitle);
            dcAltTitle.add(bsAlternativeTitle);
            if (isCopied)
                dcTitle.add(bsAlternativeTitle);
        }
        expect(datasetMock.getEasyMetadata()).andStubReturn(emdMock);
        expect(emdMock.getEmdTitle()).andStubReturn(emdTitleMock);
        expect(emdTitleMock.getDcTitle()).andStubReturn(dcTitle);
        expect(emdTitleMock.getTermsAlternative()).andStubReturn(dcAltTitle);
        return datasetMock;
    }

}
