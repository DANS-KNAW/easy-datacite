package nl.knaw.dans.easy.web.view.dataset;

import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.DatasetProxy;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.semanticdesktop.aperture.util.FileUtil;

public class OverviewTabTest {
    private static final String LABEL_PATH = "tabs:panel:summary:pidLink:pid";
    private FileStoreMocker fileStoreMocker;
    private EasyApplicationContextMock applicationContext;
    private DmoStoreId datasetStoreId;

    @Before
    public void mockApplicationContext() throws Exception {
        applicationContext = new EasyApplicationContextMock();
        fileStoreMocker = new FileStoreMocker();
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplineChoices();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.expectAuthenticatedAsVisitor();
    }

    @After
    public void cleanup() throws Exception {
        TestUtil.cleanup();
        fileStoreMocker.close();
    }

    @Test
    public void withoutDOI() throws Exception {
        Dataset dataset = mockDataset(DatasetState.DRAFT);
        applicationContext.expectDataset(dataset.getDmoStoreId(), dataset);
        EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertLabel(LABEL_PATH, dataset.getEasyMetadata().getEmdIdentifier().getPersistentIdentifier());
    }

    @Test
    public void neitherDoiNorUrn() throws Exception {
        Dataset dataset = mockDataset(DatasetState.DRAFT);
        dataset.getEasyMetadata().getEmdIdentifier().removeAllIdentifiers(EmdConstants.SCHEME_PID);

        applicationContext.expectDataset(dataset.getDmoStoreId(), dataset);
        EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertContainsNot("identifier=null");
    }

    @Test
    public void withDOI() throws Exception {
        Dataset dataset = mockDatasetWithDoi();
        applicationContext.expectDataset(dataset.getDmoStoreId(), dataset);
        EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertLabel(LABEL_PATH, dataset.getEasyMetadata().getEmdIdentifier().getDansManagedDoi());
    }

    private Dataset mockDatasetWithDoi() throws Exception {
        BasicIdentifier doi = new BasicIdentifier("10.5072/dans-1234-abcd");
        doi.setScheme(EmdConstants.SCHEME_DOI);
        Dataset dataset = mockDataset(DatasetState.SUBMITTED);
        dataset.getEasyMetadata().getEmdIdentifier().add(doi);
        return dataset;
    }

    private Dataset mockDataset(DatasetState state) throws Exception {
        datasetStoreId = new DmoStoreId(Dataset.NAMESPACE, "1");
        List<DisciplineContainer> parentDisciplines = Collections.<DisciplineContainer> emptyList();
        DatasetProxy dataset = new DatasetProxy(datasetStoreId.toString(), createDepositor(), state, parentDisciplines);
        dataset.setEasyMetadata(FileUtil.readWholeFileAsUTF8("src/test/resources/41602-EMD.xml"));
        return dataset;
    }

    private EasyUserImpl createDepositor() {
        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        return depositor;
    }

    private EasyWicketTester startPage() {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        final PageParameters pageParameters = new PageParameters();
        pageParameters.add(DatasetViewPage.PM_DATASET_ID, datasetStoreId.getStoreId());
        pageParameters.add(DatasetViewPage.PM_VIEW_MODE, DatasetViewPage.Mode.VIEW.name());
        tester.startPage(DatasetViewPage.class, pageParameters);
        return tester;
    }
}
