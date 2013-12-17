package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DownloadActivityLogPanelTest extends ActivityLogFixture implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final String ANONYMOUS_DOWNLOAD_LINE = "2013-12-13T00:00:00.000+01:00;anonymous; ; ; ;null;\n";
    private static final String PANEL = "panel";
    private static final String PANEL_DOWNLOAD_CSV = PANEL + ":" + DownloadActivityLogPanel.DOWNLOAD_CSV;

    @Test
    public void noDLH() throws Exception
    {
        expectInvisible(null, new EasyUserImpl(Role.ARCHIVIST));
    }

    @Test
    public void emptyDLH() throws Exception
    {
        expectInvisible(createDownloadList(), new EasyUserImpl(Role.ARCHIVIST));
    }

    @Test
    public void byUser() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expectInvisible(downloadList, new EasyUserImpl(Role.USER));
    }

    @Test
    public void byAdmin() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expectInvisible(downloadList, new EasyUserImpl(Role.ADMIN));
    }

    @Test
    public void withoutUser() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expect(downloadList, ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withAnonymous() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, EasyUserAnonymous.getInstance(), DOWNLOAD_DATE_TIME);
        expect(downloadList, ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void userWantsNoActionLog() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);
        expect(downloadList, ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withKnownUser() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);
        expect(downloadList, "2013-12-13T00:00:00.000+01:00;userid;email;organization;function;null;\n");
    }

    @Test
    public void withNotFoundUser() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockNotFoundUser(), DOWNLOAD_DATE_TIME);
        expect(downloadList, ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withEmptyUserValues() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUserWithEmptyValues(), DOWNLOAD_DATE_TIME);
        expect(downloadList, "2013-12-13T00:00:00.000+01:00;userid;null;null;null;null;\n");
    }

    @Test
    public void withEmptyDownloaderID() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, new EasyUserImpl(""), DOWNLOAD_DATE_TIME);
        expect(downloadList, ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withNotFoundUserService() throws Exception
    {
        DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockNotFoundUserService(), DOWNLOAD_DATE_TIME);
        expect(downloadList, ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withNotFoundDatasetService() throws Exception
    {
        EasyMock.expect(datasetService.getDownloadHistoryFor(isA(EasyUser.class), isA(Dataset.class), isA(DateTime.class))).andStubThrow(
                new ServiceException(""));
        expectInvisible(null, new EasyUserImpl(Role.USER));
    }

    @Test
    public void feb2013issue560() throws Exception
    {
        expect(mockDownloadList36028(), "2013-02-05T14:40:06.700+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
                + "2013-02-05T14:47:22.715+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-05T18:44:51.846+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
                + "2013-02-11T10:55:19.434+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-11T10:55:31.976+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-11T11:01:16.151+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
                + "2013-02-12T13:51:29.008+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
                + "2013-02-21T14:34:03.198+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
                + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
                + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
                + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-23T11:29:40.653+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-23T11:33:00.059+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n");
    }

    private void expectInvisible(DownloadList downloadList, final EasyUserImpl easyUser) throws Exception
    {
        final WicketTester tester = run(downloadList, easyUser);
        tester.assertInvisible(PANEL);
        tester.assertInvisible(PANEL_DOWNLOAD_CSV);
    }

    private void expect(DownloadList downloadList, final String lines) throws Exception
    {
        final WicketTester tester = run(downloadList, new EasyUserImpl(Role.ARCHIVIST));
        tester.assertVisible(PANEL);
        tester.assertVisible(PANEL_DOWNLOAD_CSV);
        tester.assertEnabled(PANEL_DOWNLOAD_CSV);
        tester.clickLink(PANEL_DOWNLOAD_CSV);
        assertThat(tester.getServletResponse().getDocument(), is(lines));
    }

    private WicketTester run(DownloadList downloadList, final EasyUser easyUser) throws Exception
    {
        final Dataset dataset = mockDataset(downloadList);
        final Session session = mockSessionFor_Component_isActionAuthourized();
        PowerMock.replayAll();

        final WicketTester tester = createWicketTester();
        tester.startPanel(new ITestPanelSource()
        {
            private static final long serialVersionUID = 1L;

            public Panel getTestPanel(final String panelId)
            {
                return new DownloadActivityLogPanel(panelId, dataset, easyUser)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Session getSession()
                    {
                        return session; 
                    }
                }
                ;
            }
        });
        return tester;
    }
}
