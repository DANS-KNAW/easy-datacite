package nl.knaw.dans.easy.web.view.dataset;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.web.template.TestPanelPage;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class ActivityLogPanelTest extends ActivityLogFixture implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Test
    public void noRows() throws Exception
    {
        final WicketTester tester = run(createDownloadList(), new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 0, new Integer[0]);
    }

    @Test
    public void singleRow() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);

        final WicketTester tester = run(downloadList, new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 1, (Integer) null);
    }

    @Test
    public void twoDetailRows() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);

        downloadList.getRecords().get(0).setFileItemId("fileItem:0");
        downloadList.getRecords().get(0).setPath("path0");
        downloadList.getRecords().get(1).setFileItemId("fileItem:1");
        downloadList.getRecords().get(1).setPath("path1");

        final WicketTester tester = run(downloadList, new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 2, 2);
    }

    @Test
    public void twoRows() throws Exception
    {
        final DownloadList downloadList = createDownloadList();
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME.minusDays(2));

        final WicketTester tester = run(downloadList, new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 2, (Integer) null, (Integer) null);
    }

    @Test
    public void feb2013issue560() throws Exception
    {
        final WicketTester tester = run(MockDLH36028.getList(userService), new EasyUserImpl(Role.ARCHIVIST));
        assertPanelEqualsDownload(tester, MockDLH36028.NR_OF_FILE_PER_ROW, MockDLH36028.EXPECTED_DOWNLOAD.split("\n"));
    }

    private void assertPanelEqualsDownload(final WicketTester tester, final Integer[] filesPerRow, final String[] rows)
    {
        for (int i = 0, j = 0; j < rows.length; j += filesPerRow[i++])
        {
            final String[] cols = rows[j].replace(";anonymous; ; ; ;", ";;;;;").replace(";null;null;null;null;", ";;;;;").split(";");
            final String path = "panel:downloadListPanel:timeViewContainer:timeView:" + i;
            tester.assertLabel(path + ":downloadTime", cols[0].split("T")[0]);//shown as a date
            tester.assertLabel(path + ":displayName", cols[1]);
            tester.assertLabel(path + ":organization", cols[2]);
            tester.assertLabel(path + ":function", cols[3]);
            tester.assertLabel(path + ":fileCount", filesPerRow[i] + "");
            // cover both branches for DetailsViewPanel.toggleDisplay()
            tester.clickLink(path + ":detailsLink");
            tester.clickLink(path + ":detailsLink");
        }
    }

    private void assertRows(final WicketTester tester, final int numberOfRows, final Integer... filesPerRow)
    {
        tester.debugComponentTrees();
        tester.dumpPage();
        int expectedComponentCount = (filesPerRow.length * 9) + 15;
        for (int i = 0; i < filesPerRow.length; i++)
        {
            final String path = "panel:downloadListPanel:timeViewContainer:timeView:" + i + ":fileCount";
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

    private WicketTester run(final DownloadList downloadList, final EasyUser easyUser) throws Exception
    {
        final Dataset dataset = mockDataset(downloadList);
        final Session session = mockSessionFor_Component_isActionAuthourized();
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
                        return easyUser;
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
