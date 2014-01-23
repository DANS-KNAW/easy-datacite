package nl.knaw.dans.easy.web.template.emd.atomic;

import java.io.File;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.emd.atomic.DepositUploadPanel;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DepositUploadPanelTest
{
    private static final String ACCENT_XML = // [m, e, t, -, a, c, c, é, n, t, ., x, m, l]
    new String(new byte[] {109, 101, 116, 45, 97, 99, 99, -61, -87, 110, 116, 46, 120, 109, 108});
    private static final String DIACRITIC_ACCENT_XML = // [m, e, t, -, a, c, c, e, ́, n, t, ., x, m, l]
    new String(new byte[] {109, 101, 116, 45, 97, 99, 99, 101, -52, -127, 110, 116, 46, 120, 109, 108});

    @Test
    public void smokeTest() throws Exception
    {
        final String storeId = Dataset.NAME_SPACE_VALUE + ":mocked";
        final DmoStoreId dmoStoreId = new DmoStoreId(storeId);
        final Dataset dataset = PowerMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getDmoStoreId()).andStubReturn(dmoStoreId);
        EasyMock.expect(dataset.getStoreId()).andStubReturn(storeId);
        EasyMock.expect(dataset.isInvalidated()).andStubReturn(false);

        final ItemService itemService = PowerMock.createMock(ItemService.class);
        new Services().setItemService(itemService);
        EasyMock.expect(itemService.hasChildItems(EasyMock.isA(DmoStoreId.class))).andStubReturn(false);

        PowerMock.replayAll();

        final WicketTester tester = new WicketTester();
        tester.getApplication().getResourceSettings().addResourceFolder("src/main/java/");
        tester.startPanel(new ITestPanelSource()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getTestPanel(final String panelId)
            {
                return new DepositUploadPanel(panelId, new DatasetModel(dataset));
            }
        });
        tester.debugComponentTrees();
        tester.assertVisible("panel:uploadPanel:uploadIframe");
        tester.assertVisible("panel:uploadPanel:uploadProgress");
        InlineFrame iFrame = (InlineFrame)tester.getComponentFromLastRenderedPage("panel:uploadPanel:uploadIframe");
        Component component = iFrame.get("uploadForm:file");
        FileUtils.write(new File("target/DepositUploadPanel-smokeTest.html"),tester.getServletResponse().getDocument());

        // How to get into the IFrameto hit the submit button?
        
        // rendered as test:
        // src="?wicket:interface=:1:panel:uploadPanel:uploadIframe::ILinkListener::"
        // in situ:
        // src="?wicket:interface=:2:depositPanel:depositForm:recursivePanel:levelContainer:recursivePanelContainer:recursivePanels:6:recursivePanel:customPanel:uploadPanel:uploadPanel:uploadIframe::ILinkListener::"
        
        // FormTester formTester = iframeTester.newFormTester("uploadForm");
        // formTester.setValue("file", ACCENT_XML);
        // formTester.setValue("uploadId", "123");
        // formTester.submit();
    }
}
