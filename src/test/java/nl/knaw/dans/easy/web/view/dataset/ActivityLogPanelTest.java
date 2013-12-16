package nl.knaw.dans.easy.web.view.dataset;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.DefaultDobConverter;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadHistoryFactory;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.fedora.store.DownloadHistoryConverter;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.TestPanelPage;

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
        final WicketTester tester = run(new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 0, new Integer[0]);
    }

    @Test
    public void singleRow() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);
        final WicketTester tester = run(new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 1, (Integer) null);
    }

    @Test
    public void twoDetailRows() throws Exception
    {
        // TODO rather use the result of an actual DownloadHistory
//        final DigitalObject dob = new DigitalObject(DownloadHistory.NAMESPACE.toString()).putDatastream(datastream);// TODO read XML
//        final DownloadHistory dlh = (DownloadHistory) DownloadHistoryFactory.dmoInstance(DownloadHistory.NAMESPACE+":1");
//        new DownloadHistoryConverter().deserialize(dob, dlh);
//        dlh.getDownloadList();

        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);

        // TODO is this a workaround for bug?
        downloadList.getRecords().get(0).setFileItemId("fileItem:0");
        downloadList.getRecords().get(0).setPath("path0");
        downloadList.getRecords().get(1).setFileItemId("fileItem:1");
        downloadList.getRecords().get(1).setPath("path1");
        final WicketTester tester = run(new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 2, 2);
    }

    @Test
    public void twoRows() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME.minusDays(2));
        final WicketTester tester = run(new EasyUserImpl(Role.ARCHIVIST));
        assertRows(tester, 2, (Integer) null, (Integer) null);
    }

    private void assertRows(final WicketTester tester, final int totalNumberOfFiles, final Integer... filesPerRow)
    {
        // tester.debugComponentTrees();
        // tester.dumpPage();
        int expectedComponentCount = (filesPerRow.length * 9) + 15;
        for (int i = 0; i < filesPerRow.length; i++)
        {
            String path = "panel:downloadListPanel:timeViewContainer:timeView:" + i + ":fileCount";
            if (filesPerRow[i] == null)
                tester.assertInvisible(path);
            else
            {
                tester.assertLabel(path, filesPerRow[i] + "");
                expectedComponentCount++;
            }
        }
        assertThat(WicketTesterHelper.getComponentData(tester.getLastRenderedPage()).size(), is(expectedComponentCount));
        tester.assertLabel("panel:downloadListPanel:downloadCount", totalNumberOfFiles + "");
    }

    private WicketTester run(final EasyUser easyUser) throws Exception
    {
        final Dataset dataset = mockDataset();

        PowerMock.replayAll();
        final EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);

        final WicketTester tester = new WicketTester(application);
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
                };
            }
        }));
        return tester;
    }
}
