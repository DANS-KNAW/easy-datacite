package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.semanticdesktop.aperture.util.FileUtil;

public class DatasetViewPageTest {
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
        Dataset dataset = mockDataset();
        applicationContext.setDatasetService(mockDatasetService(dataset));
        EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertLabel(LABEL_PATH, dataset.getEasyMetadata().getEmdIdentifier().getPersistentIdentifier());
    }

    @Test
    public void withDOI() throws Exception {
        Dataset dataset = mockDataset();
        dataset.getEasyMetadata().getEmdIdentifier().add(mockDOI());
        applicationContext.setDatasetService(mockDatasetService(dataset));
        EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertLabel(LABEL_PATH, dataset.getEasyMetadata().getEmdIdentifier().getDansManagedDoi());
    }

    private BasicIdentifier mockDOI() {
        BasicIdentifier doi = new BasicIdentifier("10.5072/dans-1234-abcd");
        doi.setScheme(EmdConstants.SCHEME_DOI);
        return doi;
    }

    private DatasetService mockDatasetService(final Dataset dataset) throws Exception {
        final DatasetService datasetService = PowerMock.createMock(DatasetService.class);
        expect(datasetService.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).andStubReturn(dataset);
        return datasetService;
    }

    private Dataset mockDataset() throws Exception {
        datasetStoreId = new DmoStoreId(Dataset.NAMESPACE, "1");
        final DatasetImpl dataset = new DatasetImpl(datasetStoreId.toString()) {
            private static final long serialVersionUID = 1L;

            @Override
            public List<DisciplineContainer> getParentDisciplines() {
                return Collections.emptyList();
            }
        };
        dataset.setState(State.Submitted.toString());
        dataset.setEasyMetadata(FileUtil.readWholeFileAsUTF8("src/test/resources/41602-EMD.xml"));
        dataset.setAuthzStrategy(new EasyItemContainerAuthzStrategy() {
            // need a subclass because the constructors are protected
            private static final long serialVersionUID = 1L;
        });

        // needed twice because considered dirty
        final EasyUserImpl depositor = createDepositor();
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        dataset.getAdministrativeMetadata().setDepositor(depositor);
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
