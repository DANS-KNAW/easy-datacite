package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.web.template.TestPanelPage;

import org.apache.wicket.Session;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class ActivityLogPanelTest extends ActivityLogFixture implements Serializable
{
    private static final EasyUserImpl ADMIN = new EasyUserImpl(Role.ADMIN);
    private static final EasyUserImpl USER = new EasyUserImpl(Role.USER);
    private static final EasyUserImpl ARCHIVIST = new EasyUserImpl(Role.ARCHIVIST);
    private static final long serialVersionUID = 1L;
    private static final String PATH_VIEW = "panel:downloadListPanel:timeViewContainer:timeView:";
    private static final String PATH_DOWNLOAD = "panel:downloadActivityLogPanel:download_csv";

    @Test
    public void noList() throws Exception
    {
        final WicketTester tester = run(null, ARCHIVIST, false, false);
        tester.assertInvisible("panel:downloadListPanel");
        tester.assertInvisible(PATH_DOWNLOAD);
    }

    @Test
    public void emptyList() throws Exception
    {
        final WicketTester tester = run(createDownloadList(), ARCHIVIST, false, false);
        tester.assertVisible("panel:downloadListPanel");
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 0, new Integer[0]);
    }

    @Test
    public void singleRow01() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, false, false);
        tester.assertVisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }
    @Test
    public void singleRow02() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, true, false);
        tester.assertVisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }
    @Test
    public void singleRow03() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, false, true);
        tester.assertVisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }
    @Test
    public void singleRow04() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, true, true);
        tester.assertVisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }

    @Test
    public void singleRow05() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, USER, false, false);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void singleRow06() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, USER, true, false);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void singleRow07() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, USER, false, true);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void singleRow08() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, USER, true, true);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }

    @Test
    public void singleRow09() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ADMIN, false, false);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void singleRow10() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ADMIN, true, false);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void singleRow11() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ADMIN, false, true);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void singleRow12() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ADMIN, true, true);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }

    @Test
    public void singleRow16() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, EasyUserAnonymous.getInstance(), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, true, true);
        tester.assertInvisible(PATH_DOWNLOAD);
        assertRows(tester, 1, (Integer) null);
        tester.assertLabel(PATH_VIEW + "0:displayName", "surname");
    }

    @Test
    public void noDownLoader() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, false, false);
        tester.assertLabel(PATH_VIEW + "0:displayName", "Anonymous");
    }

    @Test
    public void noUserFound() throws Exception
    {
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubThrow(new ObjectNotAvailableException(""));
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, false, false);
        tester.assertLabel(PATH_VIEW + "0:displayName", "");
    }

    @Test
    public void noUserService() throws Exception
    {
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubThrow(new ServiceException(""));
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, ARCHIVIST, false, false);
        tester.assertLabel(PATH_VIEW + "0:displayName", "");
    }

    @Test(expected = InternalWebError.class)
    public void noDatasetService() throws Exception
    {
        EasyMock.expect(datasetService.getDownloadHistoryFor(isA(EasyUser.class), isA(Dataset.class), isA(DateTime.class)))//
                .andStubThrow(new ServiceException(""));
        run(null, ARCHIVIST, false, false);
    }

    @Test
    public void formActions() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        final WicketTester tester = run(downloadList, ARCHIVIST, false, false);

        final FormTester formTester = tester.newFormTester("panel:choiceForm");
        formTester.select("yearChoice", 0);
        formTester.select("monthChoice", 0);

        tester.assertLabel("panel:choiceForm:yearChoice", "2013");
        tester.assertLabel("panel:choiceForm:monthChoice", "12");
        formTester.submit("submitLink");
        tester.assertLabel("panel:choiceForm:yearChoice", "2006");
        tester.assertLabel("panel:choiceForm:monthChoice", "1");
    }

    @Test
    public void archivistFeb2013issue560() throws Exception
    {
        final WicketTester tester = run(new MockedDLHL36028(userService, ARCHIVIST).getList(), ARCHIVIST, false, false);
        tester.clickLink("panel:choiceForm:submitLink");
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertVisible(PATH_DOWNLOAD);
        tester.assertEnabled(PATH_DOWNLOAD);
        assertPanelEqualsDownload(tester, MockedDLHL36028.getNrOfFilesPerRow(), MockedDLHL36028.getArchivistExpectation().split("\n"));
    }

    @Test
    public void datasetOwnerFeb2013issue560() throws Exception
    {
        final WicketTester tester = run(new MockedDLHL36028(userService, USER).getList(), USER, true, false);
        tester.clickLink("panel:choiceForm:submitLink");
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertInvisible(PATH_DOWNLOAD);
        tester.assertEnabled(PATH_DOWNLOAD);// code smell: invisible but enabled
        assertPanelEqualsDownload(tester, MockedDLHL36028.getNrOfFilesPerRow(), MockedDLHL36028.getDepositorExpectation().split("\n"));
    }

    private void assertPanelEqualsDownload(final WicketTester tester, final Integer[] filesPerRow, final String[] rows) throws Exception
    {
        for (int i = 0, j = 0; j < rows.length; j += filesPerRow[i++])
        {
            final String[] cols = rows[j].split(";");
            final String path = PATH_VIEW + i;
            tester.assertLabel(path + ":downloadTime", cols[0].split("T")[0]);// shown as a date

            tester.assertLabel(path + ":displayName", cols[2].equals("") ? "Anonymous" : cols[2].replaceAll("@.*", "").replaceAll("\\.", " "));
            tester.assertLabel(path + ":organization", cols[3]);
            tester.assertLabel(path + ":function", cols[4]);
            tester.assertLabel(path + ":fileCount", filesPerRow[i] + "");

            // cover both branches for DetailsViewPanel.toggleDisplay()
            tester.clickLink(path + ":detailsLink");
            tester.clickLink(path + ":detailsLink");
        }
    }

    private void assertRows(final WicketTester tester, final int numberOfRows, final Integer... filesPerRow)
    {
        // tester.debugComponentTrees();
        // tester.dumpPage();
        int expectedComponentCount = (filesPerRow.length * 9) + 15;
        for (int i = 0; i < filesPerRow.length; i++)
        {
            final String path = PATH_VIEW + i + ":fileCount";
            if (filesPerRow[i] == null)
                tester.assertInvisible(path);
            else
            {
                tester.assertLabel(path, filesPerRow[i] + "");
                expectedComponentCount++;
            }
        }
        assertThat(WicketTesterHelper.getComponentData(tester.getLastRenderedPage()).size(), is(expectedComponentCount));
        tester.assertLabel("panel:downloadListPanel:downloadCount", numberOfRows + "");
    }

    private WicketTester run(final DownloadList downloadList, final EasyUser sessionUser, final boolean isDepositor, final boolean hasPermissionRestrictedItems)
            throws Exception
    {
        final Session session = mockSessionFor_Component_isActionAuthourized();
        final Dataset dataset = mockDataset(downloadList, sessionUser, isDepositor, hasPermissionRestrictedItems);
        PowerMock.replayAll();

        final WicketTester tester = createWicketTester();
        tester.startPage(new TestPanelPage(new ITestPanelSource()
        {
            private static final long serialVersionUID = 1L;

            public Panel getTestPanel(final String panelId)
            {
                return new ActivityLogPanel(panelId, dataset)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public EasyUser getSessionUser()
                    {
                        return sessionUser;
                    }

                    @Override
                    public Session getSession()
                    {
                        return session;
                    }
                };
            }
        }));
        return tester;
    }
}
