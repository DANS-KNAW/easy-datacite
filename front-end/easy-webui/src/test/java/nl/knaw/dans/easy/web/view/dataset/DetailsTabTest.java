package nl.knaw.dans.easy.web.view.dataset;

import static nl.knaw.dans.easy.domain.model.PermissionSequence.State.Submitted;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.DatasetProxy;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticdesktop.aperture.util.FileUtil;

public class DetailsTabTest {

    private final DmoStoreId ROOT_DISCIPLINE_ID = new DmoStoreId(DisciplineContainer.NAMESPACE, "root");
    private final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private EasyApplicationContextMock applicationContext;
    private FileStoreMocker fileStoreMocker;
    private Dataset dataset;

    @Before
    public void mockApplicationContext() throws Exception {

        applicationContext = new EasyApplicationContextMock();
        fileStoreMocker = new FileStoreMocker();
        List<DisciplineContainer> parentDisciplines = Collections.<DisciplineContainer> emptyList();
        dataset = new DatasetProxy(DATASET_STORE_ID.toString(), createDepositor(), Submitted, parentDisciplines);
        loadEmd("41602");

        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.expectAuthenticatedAsVisitor();
        applicationContext.expectDisciplineObject(ROOT_DISCIPLINE_ID, new ArrayList<DisciplineContainer>());
        applicationContext.expectDataset(DATASET_STORE_ID, dataset);
        applicationContext.setDepositService(new EasyDepositService());
    }

    private EasyUserImpl createDepositor() {
        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        return depositor;
    }

    @After
    public void reset() throws Exception {
        TestUtil.cleanup();
        fileStoreMocker.close();
    }

    @Test
    public void sociologyWithDOI() throws Exception {
        mockDOI();
        startDetailsTab().dumpPage();
    }

    @Test
    public void sociologyWithoutDOI() throws Exception {
        startDetailsTab().dumpPage();
    }

    @Test
    public void historyWithDOI() throws Exception {
        mockDOI();
        mockMetadataFormat(MetadataFormat.HISTORY);
        startDetailsTab().dumpPage();
    }

    @Test
    public void historyWithoutDOI() throws Exception {
        mockMetadataFormat(MetadataFormat.HISTORY);
        startDetailsTab().dumpPage();
    }

    @Test
    public void langlitWithDOI() throws Exception {
        mockDOI();
        mockMetadataFormat(MetadataFormat.LANGUAGE_LITERATURE);
        startDetailsTab().dumpPage();
    }

    @Test
    public void langlitWithoutDOI() throws Exception {
        mockMetadataFormat(MetadataFormat.LANGUAGE_LITERATURE);
        startDetailsTab().dumpPage();
    }

    @Test
    public void lifeWithDOI() throws Exception {
        mockDOI();
        mockMetadataFormat(MetadataFormat.LIFESCIENCE);
        startDetailsTab().dumpPage();
    }

    @Test
    public void lifeWithoutDOI() throws Exception {
        mockMetadataFormat(MetadataFormat.LIFESCIENCE);
        startDetailsTab().dumpPage();
    }

    @Test
    public void archaeologyWithDOI() throws Exception {

        // annotation springbean injection did not work in RelationViewPanel$CustomPanel
        new Services().setDepositService(applicationContext.getDepositService());

        mockDOI();
        loadEmd("60916");
        startDetailsTab().dumpPage();
    }

    @Test
    public void archaeologyWithoutDOI() throws Exception {

        // annotation springbean injection did not work in RelationViewPanel$CustomPanel
        new Services().setDepositService(applicationContext.getDepositService());

        loadEmd("60916");
        startDetailsTab().dumpPage();
    }

    @Test
    public void otherWithDOI() throws Exception {
        mockDOI();
        mockMetadataFormat(MetadataFormat.UNSPECIFIED);
        startDetailsTab().dumpPage();
    }

    @Test
    public void otherWithoutDOI() throws Exception {
        mockMetadataFormat(MetadataFormat.UNSPECIFIED);
        startDetailsTab().dumpPage();
    }

    private void loadEmd(String string) throws IOException {
        dataset.setEasyMetadata(FileUtil.readWholeFileAsUTF8("src/test/resources/" + string + "-EMD.xml"));
    }

    private void mockDOI() {
        dataset.getEasyMetadata().getEmdIdentifier().add(new BasicIdentifier("10.5072/dans-1234-abcd", "en", EmdConstants.SCHEME_DOI));
    }

    private void mockMetadataFormat(MetadataFormat history) {
        dataset.getEasyMetadata().getEmdOther().getEasApplicationSpecific().setMetadataFormat(history);
    }

    private EasyWicketTester startDetailsTab() {
        replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        final PageParameters pageParameters = new PageParameters();
        pageParameters.add(DatasetViewPage.PM_DATASET_ID, DATASET_STORE_ID.getStoreId());
        pageParameters.add("tab", "1");
        tester.startPage(DatasetViewPage.class, pageParameters);
        return tester;
    }
}
