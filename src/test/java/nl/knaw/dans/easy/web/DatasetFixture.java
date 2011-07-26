//package nl.knaw.dans.easy.web;
//
//import static org.easymock.EasyMock.createMock;
//import static org.easymock.EasyMock.expect;
//import static org.easymock.EasyMock.isA;
//import static org.easymock.EasyMock.replay;
//import static org.easymock.EasyMock.reset;
//import static org.hamcrest.core.Is.is;
//import static org.junit.Assert.assertThat;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Locale;
//
//import nl.knaw.dans.common.jibx.JiBXObjectFactory;
//import nl.knaw.dans.common.lang.ResourceLocator;
//import nl.knaw.dans.common.lang.ResourceNotFoundException;
//import nl.knaw.dans.common.lang.dataset.DatasetState;
//import nl.knaw.dans.common.lang.test.ClassPathHacker;
//import nl.knaw.dans.common.lang.user.User.State;
//import nl.knaw.dans.common.lang.xml.HtmlValidator;
//import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
//import nl.knaw.dans.easy.business.security.Authz;
//import nl.knaw.dans.easy.business.services.EasyDepositService;
//import nl.knaw.dans.easy.data.Data;
//import nl.knaw.dans.easy.data.DataConfigurationException;
//import nl.knaw.dans.easy.data.store.EasyStore;
//import nl.knaw.dans.easy.data.store.FileStoreAccess;
//import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
//import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
//import nl.knaw.dans.easy.domain.model.Dataset;
//import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollection;
//import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
//import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
//import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
//import nl.knaw.dans.easy.domain.model.user.EasyUser;
//import nl.knaw.dans.easy.servicelayer.ServiceLayer;
//import nl.knaw.dans.easy.servicelayer.ServiceSessionInfo;
//import nl.knaw.dans.easy.servicelayer.services.DatasetService;
//import nl.knaw.dans.easy.servicelayer.services.DepositService;
//import nl.knaw.dans.easy.web.bean.Services;
//
//import org.apache.wicket.util.tester.ITestPageSource;
//import org.apache.wicket.util.tester.TestPanelSource;
//import org.apache.wicket.util.tester.WicketTester;
//import org.easymock.EasyMock;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//
///**
// * An independent environment with mocked objects injected into services. This super test class provides helper methods to create variants of the mocked
// * objects. The default mocked objects concentrate on a session user and a dataset and are geared to test components on the dataset view page. The environment
// * for a test is completed like:
// * 
// * <pre>
// *      {@link #resetMocks()};
// *      {@link #expectDataset(Boolean)};
// *      expect... 
// *      {@link #replayMocks()};
// * </pre>
// * 
// * To overrule a method call prepared for some mocked object, overrule all prepared method calls, like for example:
// * 
// * <pre>
// *      {@link #getWicketTester()}.reset({@link #getDataset()});
// *      {@link EasyMock}.expect({@link #getDataset()}.isUnderEmbargo())).andReturn(true).anyTimes();
// *      ...
// *      {@link #getWicketTester()}.replay({@link #getDataset()});
// * </pre>
// * 
// * To perform the test, create a component and feed it to a helper method like
// * 
// * <pre>
// *      {@link String} renderedHtml = {@link #captureHtmlOf(TestPanelSource, String)};
// *      {@link WicketTester} tester = {@link #getWicketTester()}
// * </pre>
// * 
// * or perform a custom test like
// * 
// * <pre>
// *      {@link WicketTester} tester = {@link #getWicketTester()}.start...(...);
// * </pre>
// * 
// * The tester resulting from one of the two snippets above provides assertion methods. Further development of tests may require to implement more expect and
// * helper methods.
// * 
// * @author Joke Pol
// */
//public class DatasetFixture
//{
//
//    public final String                 captureFolder = "target/junit-output/" + getClass().getName() + "/";
//
//    /** TODO for permission requests we need another user */
//    private static EasyUser             mockedSessionUser;
//
//    private static EasyStore            mockedEasyStore;
//    private static Dataset              mockedDataset;
//    private static DisciplineCollection mockedDisciplineCollection;
//    private static FileStoreAccess      mockedFileStoreAccess;
//    private static Authz                mockedAuthz;
//
//    @SuppressWarnings("unused")
//    /** TODO if all implement Mockable, could "Mockable[] allMocks = {}" be used for resetMocks and replayMocks ?? */
//    private static Object[]             allMocks      = {mockedDataset, mockedSessionUser, mockedFileStoreAccess, mockedDisciplineCollection, mockedAuthz,
//            mockedEasyStore                           };
//
//    private static Authz                savedAuthz;
//    private static FileStoreAccess      savedFileStoreAccess;
//    private static DepositService       savedDepositService;
//    private static EasyStore            savedEasyStore;
//    private static WicketTester         wicketTester;
//    private static ServiceLayer         serviceLayer;
//
//    private static DatasetService       savedDatasetService;
//
//    private static DatasetService       mockedDatasetService;
//
//    /**
//     * Creates relaxed expected method calls of the mocked dataset and session user.
//     * 
//     * @param isUnderEmbargo
//     * @throws Exception
//     */
//    protected static void expectDataset(final Boolean isUnderEmbargo) throws Exception
//    {
//        final String[] fileNames = {"folder1/fileA.txt", "folder1/fileB.txt", "folder2/fileX.txt", "folder2/fileY.txt"};
//        final String datasetStoreId = "easy-dataset:123";
//
//        expect(mockedFileStoreAccess.getFilenames(datasetStoreId, true)).andReturn(Arrays.asList(fileNames)).anyTimes();
//
//        expect(mockedSessionUser.getDisplayName()).andReturn("Jan Klaasen").anyTimes();
//        expect(mockedSessionUser.getOrganization()).andReturn("Leger van de Prins").anyTimes();
//        expect(mockedSessionUser.getAddress()).andReturn("Trompetdreef 1").anyTimes();
//        expect(mockedSessionUser.getPostalCode()).andReturn("1234 AB").anyTimes();
//        expect(mockedSessionUser.getCity()).andReturn("Den Helder").anyTimes();
//        expect(mockedSessionUser.getCountry()).andReturn("Nederland").anyTimes();
//        expect(mockedSessionUser.getTelephone()).andReturn("070-1234567").anyTimes();
//        expect(mockedSessionUser.getEmail()).andReturn("jan.klaasen@lvdp.nl").anyTimes();
//        expect(mockedSessionUser.getState()).andReturn(State.ACTIVE).anyTimes();
//
//        expect(mockedDataset.isUnderEmbargo()).andReturn(isUnderEmbargo).anyTimes();
//        expect(mockedDataset.getPreferredTitle()).andReturn("A mocked dataset").anyTimes();
//        expect(mockedDataset.getStoreId()).andReturn(datasetStoreId).anyTimes();
//        expect(mockedDataset.isInvalidated()).andReturn(false).anyTimes();
//        expect(mockedDataset.hasPermissionRestrictedItems()).andReturn(false).anyTimes();
//        expect(mockedDataset.getAccessProfileFor(isA(EasyUser.class))).andReturn(0).anyTimes();
//        expect(mockedDataset.getAdministrativeState()).andReturn(DatasetState.SUBMITTED).anyTimes();
//        expect(mockedDataset.getDateAvailable()).andReturn(null).anyTimes();
//
//        // TODO isn't this a mistake in the FileExplorer.java?
//        expect(mockedDataset.getState()).andReturn(DatasetState.SUBMITTED.toString()).anyTimes();
//
//        expect(mockedDatasetService.getAdditionalLicense(isA(Dataset.class))).andReturn(null).anyTimes();
//        expect(mockedDatasetService.getDataset(datasetStoreId)).andReturn(mockedDataset).anyTimes();
//        expect(mockedDatasetService.getDataModelObject(datasetStoreId)).andReturn(mockedDataset).anyTimes();
//
//        // TODO replace DummyAutz with the next lines in other tests?
//        expect(mockedAuthz.hasSecurityOfficer(isA(String.class))).andReturn(false).anyTimes();
//        expect(mockedAuthz.isProtectedPage(isA(String.class))).andReturn(false).anyTimes();
//    }
//
//    protected static void expectDiscipline(final String disciplineName, final String disciplineId) throws Exception
//    {
//
//        final DisciplineContainerImpl disciplineContainer = new DisciplineContainerImpl(disciplineId);
//        disciplineContainer.setName(disciplineName);
//        expect(mockedDisciplineCollection.getDisciplineBySid(disciplineId)).andReturn(disciplineContainer);
//        expect(mockedEasyStore.retrieve(DisciplineCollectionImpl.EASY_DISCIPLINE_ROOT)).andReturn(null).anyTimes();
//    }
//
//    protected static void resetMocks()
//    {
//        reset(mockedDatasetService, mockedDataset, mockedSessionUser, mockedFileStoreAccess, mockedDisciplineCollection, mockedAuthz, mockedEasyStore);
//    }
//
//    protected static void replayMocks()
//    {
//        replay(mockedDatasetService, mockedDataset, mockedSessionUser, mockedFileStoreAccess, mockedDisciplineCollection, mockedAuthz, mockedEasyStore);
//    }
//
//    /** Called by the JUnit framework. */
//    @BeforeClass
//    public static void createMocks() throws Exception
//    {
//        ClassPathHacker.addFile("../easy-business/src/test/resources");
//        ClassPathHacker.addFile("../easy-webui/src/main/resources");
//        ClassPathHacker.addFile("../easy-webui/src/main/java");
//        ClassPathHacker.addFile("../easy-webui/src/test/java");
//
//        mockedDisciplineCollection = createMock(DisciplineCollection.class);
//        mockedFileStoreAccess = createMock(FileStoreAccess.class);
//        mockedAuthz = createMock(Authz.class);
//        mockedSessionUser = createMock(EasyUser.class);
//        mockedDataset = createMock(Dataset.class);
//        mockedEasyStore = createMock(EasyStore.class);
//        mockedDatasetService = createMock(DatasetService.class);
//
//        setWicketTester();
//        inject();
//    }
//
//    /** Called by the JUnit framework. */
//    @AfterClass
//    public static void uninject()
//    {
//        final Data data = new Data();
//        data.setFileStoreAccess(savedFileStoreAccess);
//        data.setEasyStore(savedEasyStore);
//        final Services services = new Services();
//        services.setAuthz(savedAuthz);
//        services.setDepositService(savedDepositService);
//        services.setDatasetService(savedDatasetService);
//    }
//
//    private static void inject()
//    {
//        serviceLayer = new ServiceLayer();
//
//        try
//        {
//            savedFileStoreAccess = Data.getFileStoreAccess();
//        }
//        catch (DataConfigurationException e)
//        {
//            //
//        }
//        new Data().setFileStoreAccess(mockedFileStoreAccess);
//
//        try
//        {
//            savedEasyStore = Data.getEasyStore();
//        }
//        catch (DataConfigurationException e)
//        {
//            //
//        }
//        new Data().setEasyStore(mockedEasyStore);
//
//        final Services services = new Services();
//
//        savedDatasetService = Services.getDatasetService();
//        services.setDatasetService(mockedDatasetService);
//
//        savedAuthz = Services.getAuthz();
//        services.setAuthz(mockedAuthz);
//
//        savedDepositService = Services.getDepositService();
//        services.setDepositService(new EasyDepositService()
//        {
//            // TODO replace with mock?
//            public ChoiceList getChoices(final String listId, final Locale locale)
//            {
//                return new ChoiceList(new ArrayList<KeyValuePair>());
//            }
//        });
//    }
//
//    /** Called by the JUnit framework. */
//    @Before
//    public void startSession()
//    {
//        serviceLayer.createSession(mockedSessionUser, new ServiceSessionInfo());
//    }
//
//    /**
//     * Captures and validates the rendered HTML code for a panel. Wrapper for {@link #captureHtml(String, int, int)}.
//     * 
//     * @param panel
//     *        see {@link WicketTester#startPanel(TestPanelSource)}
//     */
//    protected final String captureHtmlOf(final TestPanelSource panel, final String filename, final int expectedWarnings, final int expectedErrors)
//            throws IOException
//    {
//        getWicketTester().startPanel(panel);
//        return captureHtml(filename, expectedWarnings, expectedErrors);
//    }
//
//    /**
//     * Captures and validates the rendered HTML code for a panel. Wrapper for {@link #captureHtml(String, int, int)}.
//     * 
//     * @param page
//     *        see {@link WicketTester#startPage(ITestPageSource)}
//     */
//    protected final String captureHtmlOf(final ITestPageSource page, final String filename, final int expectedWarnings, final int expectedErrors)
//            throws IOException
//    {
//        getWicketTester().startPage(page);
//        return captureHtml(filename, expectedWarnings, expectedErrors);
//    }
//
//    /**
//     * Captures and validates the rendered HTML code for a component after a call to {@link #getWicketTester()}.start...
//     * 
//     * @param filename
//     *        the rendered HTML code is written to {@link #captureFolder}/filename for debugging purposes
//     * @param expectedWarnings
//     *        the expected value for {@link HtmlValidator#warningCount()}. If negative the number of warnings is not validated.
//     * @param expectedErrors
//     *        the expected value for {@link HtmlValidator#errorCount()}. If negative the number of errors is not validated.
//     * @return the rendered and cleaned up HTML code.
//     * @throws IOException
//     *         if writing the rendered HTML code fails.
//     */
//    public String captureHtml(final String filename, final int expectedWarnings, final int expectedErrors) throws IOException
//    {
//        final String rendered = getWicketTester().getServletResponse().getDocument()//
//                .replaceAll("../yui", "../../../src/main/webapp/yui")//
//                .replaceAll("../images", "../../../src/main/webapp/images")//
//                .replaceAll("../css", "../../../src/main/webapp/css");
//        new File(captureFolder).mkdirs();
//        writeHtml(filename, rendered);
//        return validateHtml(expectedWarnings, expectedErrors, rendered);
//    }
//
//    protected void writeHtml(final String filename, final String document) throws FileNotFoundException, IOException
//    {
//        final File file = new File(captureFolder + filename + ".html");
//        final FileOutputStream stream = new FileOutputStream(file);
//        try
//        {
//            stream.write(document.getBytes());
//            stream.flush();
//        }
//        finally
//        {
//            stream.close();
//        }
//    }
//
//    /**
//     * Primary objective: validation; side effect: cleaned up HTML code. Warnings and errors are written to standard error output.
//     * 
//     * @param expectedWarnings
//     *        with a negative value the number of warnings is not asserted
//     * @param expectedErrors
//     *        with a negative value the number of warnings is not asserted
//     * @param rendered
//     *        the rendered HTML code
//     * @return
//     */
//    private String validateHtml(final int expectedWarnings, final int expectedErrors, final String rendered)
//    {
//        final HtmlValidator htmlValidator = new HtmlValidator();
//
//        // TODO would like to remove white space only elements too:
//        // might make it easier for iText to convert meta data to PDF
//        htmlValidator.setHideVarious();
//
//        final String stripped = rendered.replaceAll("<.?wicket:[^>]*>", "").replaceAll("wicket:id=.[^\"]*\"", "").replaceAll("xmlns:wicket=.[^\"]*\"", "");
//        final String tidyHtml = htmlValidator.tidyHtml(stripped, false);
//        if (expectedErrors >= 0)
//            assertThat(htmlValidator.errorCount(), is(expectedErrors));
//        if (expectedWarnings >= 0)
//            assertThat(htmlValidator.warningCount(), is(expectedWarnings));
//        return tidyHtml;
//    }
//
//    protected static EasyMetadataImpl getMetadata(final String location) throws ResourceNotFoundException, XMLDeserializationException
//    {
//        final File metadataResource = ResourceLocator.getFile(location);
//        return (EasyMetadataImpl) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, metadataResource);
//    }
//
//    /**
//     * @return the mocked dataset
//     */
//    protected static Dataset getDataset()
//    {
//        return mockedDataset;
//    }
//
//    protected static EasyUser getSessionUser()
//    {
//        return mockedSessionUser;
//    }
//
//    protected static WicketTester getWicketTester()
//    {
//        return wicketTester;
//    }
//
//    private static void setWicketTester()
//    {
//        // TODO a tester per test to allow comparing results, now id's are always different
//        DatasetFixture.wicketTester = new WicketTester(new EasyWicketApplication());
//    }
//}
