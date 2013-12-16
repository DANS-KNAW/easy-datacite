package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DownloadActivityLogPanelTest extends ActivityLogFixture
{
    private static final String ANONYMOUS_DOWNLOAD_LINE = "2013-12-13T00:00:00.000+01:00;anonymous; ; ; ;null;\n";
    private static final String PANEL = "panel";
    private static final String PANEL_DOWNLOAD_CSV = PANEL+":"+DownloadActivityLogPanel.DOWNLOAD_CSV;
    @Test
    public void noDLH() throws Exception
    {
        downloadList = null;
        expectInvisible(new EasyUserImpl(Role.ARCHIVIST));
    }

    @Test
    public void emptyDLH() throws Exception
    {
        expectInvisible(new EasyUserImpl(Role.ARCHIVIST));
    }

    @Test
    public void byUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expectInvisible(new EasyUserImpl(Role.USER));
    }

    @Test
    public void byAdmin() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expectInvisible(new EasyUserImpl(Role.ADMIN));
    }

    @Test
    public void withoutUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withAnonymous() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, EasyUserAnonymous.getInstance(), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void userWantsNoActionLog() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withKnownUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);
        expect("2013-12-13T00:00:00.000+01:00;userid;email;organization;function;null;\n");
    }

    @Test
    public void withNotFoundUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockNotFoundUser(), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withEmptyUserValues() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUserWithEmptyValues(), DOWNLOAD_DATE_TIME);
        expect("2013-12-13T00:00:00.000+01:00;userid;null;null;null;null;\n");
    }

    @Test
    public void withEmptyDownloaderID() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, new EasyUserImpl(""), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withNotFoundUserService() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockNotFoundUserService(), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withNotFoundDatasetService() throws Exception
    {
        EasyMock.expect(datasetService.getDownloadHistoryFor(isA(EasyUser.class), isA(Dataset.class), isA(DateTime.class))).andStubThrow(new ServiceException(""));
        expectInvisible(new EasyUserImpl(Role.USER));
    }

    private void expectInvisible(final EasyUserImpl easyUser) throws Exception
    {
        final WicketTester tester = run(easyUser);
        tester.assertInvisible(PANEL);
        tester.assertInvisible(PANEL_DOWNLOAD_CSV);
    }

    private void expect(final String lines) throws Exception
    {
        final WicketTester tester = run(new EasyUserImpl(Role.ARCHIVIST));
        tester.assertVisible(PANEL);
        tester.assertVisible(PANEL_DOWNLOAD_CSV);
        tester.assertEnabled(PANEL_DOWNLOAD_CSV);
        tester.clickLink(PANEL_DOWNLOAD_CSV);
        assertThat(tester.getServletResponse().getDocument(), is(lines));
    }

    private WicketTester run(final EasyUser easyUser) throws Exception
    {
        final Dataset dataset = mockDataset();

        PowerMock.replayAll();
        final EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);

        final WicketTester tester = new WicketTester(application);
        tester.startPanel(new ITestPanelSource()
        {
            private static final long serialVersionUID = 1L;

            public Panel getTestPanel(final String panelId)
            {
                return new DownloadActivityLogPanel(panelId, dataset, easyUser);
            }
        });
        return tester;
    }
}
