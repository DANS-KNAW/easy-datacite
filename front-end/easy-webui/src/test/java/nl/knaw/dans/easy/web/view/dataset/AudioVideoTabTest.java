package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.*;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.TicketService;
import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static nl.knaw.dans.easy.domain.model.user.EasyUser.Role.ARCHIVIST;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.semanticdesktop.aperture.util.FileUtil.readWholeFileAsUTF8;

@Ignore
public class AudioVideoTabTest {

    private final DmoStoreId ROOT_DISCIPLINE_ID = new DmoStoreId(DisciplineContainer.NAMESPACE, "root");
    private final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private EasyApplicationContextMock applicationContext;
    private FileStoreMocker fileStoreMocker;
    private Dataset dataset;

    @Before
    public void mockApplicationContext() throws Exception {

        List<DisciplineContainer> parentDisciplines = Collections.emptyList();
        applicationContext = new EasyApplicationContextMock();
        dataset = new DatasetProxy(DATASET_STORE_ID.toString(), createDepositor(), DatasetState.SUBMITTED, parentDisciplines);
        dataset.setEasyMetadata(readWholeFileAsUTF8("src/test/resources/SchiedamseAV-EMD.xml"));
        fileStoreMocker = new FileStoreMocker();
        fileStoreMocker.insertRootFolder(dataset);

        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoDatasetsInToolBar();
        applicationContext.expectNoJumpoff();
        applicationContext.expectDisciplineObject(ROOT_DISCIPLINE_ID, new ArrayList<DisciplineContainer>());
        applicationContext.expectDataset(DATASET_STORE_ID, dataset);
        applicationContext.setDepositService(new EasyDepositService());
        applicationContext.putBean("audioVideoPlayerUrl", "https://localhost/dummy");
        applicationContext.putBean("securedStreamingService", new TicketService() {
            @Override
            public void setTicketServiceUrl(String baseUrl) {}

            @Override
            public void setAccessDurationInMilliseconds(long ms) {}

            @Override
            public void addSecurityTicketToResource(String ticket, String resource) throws ServiceException {}

            @Override
            public void removeSecurityTicket(String ticket) throws ServiceException {}
        });
    }

    private EasyUserImpl createArchivist() {
        final EasyUserImpl archivist = new EasyUserTestImpl("easy-user:a");
        archivist.setInitials("A.R.");
        archivist.setSurname("Chie");
        archivist.addRole(ARCHIVIST);
        return archivist;
    }

    private EasyUserImpl createDepositor() {
        final EasyUserImpl depositor = new EasyUserTestImpl("easy-user:d");
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
    public void withAVFiles() throws Exception {
        LinkedList<FileItemVO> avFiles = new LinkedList<FileItemVO>();
        avFiles.add(new FileItemVO(fileStoreMocker.insertFile(1, dataset, "some.mpg", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN)));
        applicationContext.expectAudioVideoFiles(true, avFiles);
        applicationContext.expectAuthenticatedAs(createArchivist());
        EasyWicketTester tester = startTab();
        tester.dumpPage();
        tester.assertVisible("tabs:tabs-container:tabs:6");
    }

    @Test
    public void withoutAVFiles() throws Exception {
        applicationContext.expectAudioVideoFiles(true, new LinkedList<FileItemVO>());
        applicationContext.expectAuthenticatedAs(createArchivist());
        EasyWicketTester tester = startTab();
        tester.assertInvisible("tabs:tabs-container:tabs:6");
    }

    private EasyWicketTester startTab() {
        replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        final PageParameters pageParameters = new PageParameters();
        pageParameters.add(DatasetViewPage.PM_DATASET_ID, DATASET_STORE_ID.getStoreId());
        pageParameters.add("tab", "6");
        tester.startPage(DatasetViewPage.class, pageParameters);
        return tester;
    }
}
