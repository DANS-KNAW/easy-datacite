//package nl.knaw.dans.easy.web.wicket;
//
//
//import static org.easymock.EasyMock.expect;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import nl.knaw.dans.common.lang.ResourceNotFoundException;
//import nl.knaw.dans.common.lang.dataset.AccessCategory;
//import nl.knaw.dans.common.lang.test.ClassPathHacker;
//import nl.knaw.dans.easy.business.dataset.LicenseComposer;
//import nl.knaw.dans.easy.business.dataset.LicenseComposer.LicenseComposerException;
//import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
//import nl.knaw.dans.easy.domain.form.FormDefinition;
//import nl.knaw.dans.easy.domain.form.FormPage;
//import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
//import nl.knaw.dans.easy.servicelayer.exceptions.ServiceException;
//import nl.knaw.dans.easy.web.DatasetFixture;
//import nl.knaw.dans.easy.web.bean.Services;
//import nl.knaw.dans.easy.web.common.DatasetModel;
//import nl.knaw.dans.easy.web.deposit.EmdPanelFactory;
//
//import org.apache.wicket.markup.html.panel.Panel;
//import org.apache.wicket.util.tester.TestPanelSource;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import com.lowagie.text.Document;
//import com.lowagie.text.Element;
//import com.lowagie.text.Paragraph;
//
//public class RecusivePanelTest extends DatasetFixture
//{
//    @BeforeClass
//    public static void morePathHacking() throws Exception
//    {
//        ClassPathHacker.addFile("../easy-home/license");
//    }
//    
//    @Ignore("RecursivePanel is generic stuff. It does not have properties of it's own."
//            + "It can only be tested within the context of another page or panel that does have properties.")
//    @Test
//    public void isNotEditable() throws Exception
//    {
//        final String filename = "isNotEditable";
//
//        resetMocks();
//        expectDataset(false);
//        expect(getDataset().getEasyMetadata()).andReturn(getMetadata("nl/knaw/dans/easy/business/dataset/SampleMetaData.xml")).anyTimes();
//        expectDiscipline("Humanities", "easy-discipline:2");
//        expect(getDataset().getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS).anyTimes();
//        replayMocks();
//        
//        final TestPanelSource panel = createRecursivePanel(MetadataFormat.SOCIOLOGY, false);
//        final String renderedHtml = captureHtmlOf(panel, filename,-1,0);
//        
//        writeHtml(filename + ".tidy", renderedHtml);
//        //proofOfConcept(renderedHtml);
//    }
//
//    /** Converts HTML rendered by Wicket with iText to a section of the license PDF (issues issues #203, #175, #141) */
//    private void proofOfConcept(final String renderedHtml) throws LicenseComposerException, FileNotFoundException, IOException
//    {
//        // TODO how to set following JVM argument for nightly build? See for example LicenseComposerTest 
//        // -Deasy.home="/mnt/hgfs/joke/easy-data/SVN/eof/trunk/easy/easy-application/easy-home"
//        final LicenseComposer licenseComposer = new LicenseComposer(getSessionUser(), getDataset(), true){
//
//            @Override
//            protected Element formatMetaData(final Document document) throws LicenseComposerException
//            {
//                copyHtml(document, renderedHtml);
//                return new Paragraph("");
//            }
//        };
//
//        final FileOutputStream outputStream = new FileOutputStream(captureFolder + "license.pdf");
//        licenseComposer.createPdf(outputStream);
//        outputStream.flush();
//        outputStream.close();
//        // TODO assert that PDF file is readable
//    }
//
//    @Ignore("problem with pluslink, perhaps EmdPanelFactory should specify a form as parent (AbstractRepeaterPanel item.add(buttonsHolder))")
//    @Test
//    public void isEditable() throws Exception
//    {
//        final String filename = "isEditable";
//
//        resetMocks();
//        expectDataset(false);
//        expectDiscipline("Humanities", "easy-discipline:2");
//        replayMocks();
//        final TestPanelSource panel = createRecursivePanel(MetadataFormat.SOCIOLOGY, true);
//
//        writeHtml(filename + ".tidy", captureHtmlOf(panel, filename,-1,0));
//    }
//
//    private static TestPanelSource createRecursivePanel(final MetadataFormat metadataFormat, final boolean editable)
//    {
//        return new TestPanelSource()
//        {
//            private static final long serialVersionUID = -3830462644372837295L;
//
//            public Panel getTestPanel(final String panelId)
//            {
//                try
//                {
//                    return new RecursivePanel(panelId, getEmdPanelFactory(), getFormPage(metadataFormat, editable));
//                }
//                catch (final Exception exception)
//                {
//                    throw new RuntimeException(exception);
//                }
//            }
//        };
//    }
//
//    private static EmdPanelFactory getEmdPanelFactory()
//    {
//        return new EmdPanelFactory(RecursivePanel.PANEL_WICKET_ID, null, new DatasetModel(getDataset()));
//    }
//
//    private static FormPage getFormPage(final MetadataFormat metadataFormat, final boolean editable) throws ResourceNotFoundException, ServiceException
//    {
//        final String disciplineId = metadataFormat.toString().toLowerCase();
//        final DepositDiscipline depoDiscipline = Services.getDepositService().getDiscipline(disciplineId);
//        final FormDefinition formDefinition = depoDiscipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_VIEW_DEFINITION);
//        final FormPage formPage = formDefinition.getFormPage(DepositDiscipline.EMD_VIEW_DEFINITION + ".1");
//        formPage.setEditable(editable);
//        return formPage;
//    }
//}
