package nl.knaw.dans.easy.domain.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.easy.util.AbstractJibxTest;

import org.jibx.runtime.JiBXException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class FormDescriptorTest extends AbstractJibxTest<FormDescriptor>
{

    public FormDescriptorTest()
    {
        super(FormDescriptor.class);
    }

    @BeforeClass
    public static void testStartInformation()
    {
        before(FormDescriptorTest.class);
    }

    @Test
    public void testClone()
    {
        startOfTest("testClone");
        FormDescriptor formDescriptor = new FormDescriptor("formDescriptor.id");
        formDescriptor.setHelpFile("formDescriptor.helpFile");
        formDescriptor.setHelpItem("formDescriptor.helpItem");
        formDescriptor.setInstructionFile("formDescriptor.instructionFile");
        formDescriptor.setLabelResourceKey("formDescriptor.labelResourceKey");
        formDescriptor.setLicenseFile("formDescriptor.licenseFile");
        formDescriptor.setOrdinal("formDescriptor.ordinal");
        formDescriptor.setShortHelpResourceKey("formDescriptor.shortHelpResourceKey");

        formDescriptor.putCustomProperty("formDescriptor.name", "formDescriptor.value");

        FormDefinition formDefinition = new FormDefinition("formDefinition.id");
        formDefinition.putCustomProperty("formDefinition.name", "formDefinition.value");

        FormPage formPage = new FormPage("formPage.id");
        formPage.getPanelIds().add("standardPanel.id");

        formDefinition.addFormPage(formPage);
        formDescriptor.addFormDefinition(formDefinition);

        StandardPanelDefinition standardPanel = new StandardPanelDefinition("standardPanel.id");
        standardPanel.setDefaultModelClass("modelClass");

        formDescriptor.addPanelDefinition(standardPanel);

        TermPanelDefinition termPanel = new TermPanelDefinition("termPanel.id", "namespacePrefix", "termName");
        termPanel.setLabelResourceKey("termPanel.labelResourceKey");

        formDescriptor.addPanelDefinition(termPanel);

        FormDescriptor descClone = formDescriptor.clone();
        FormDefinition fdClone = descClone.getFormDefinition("formDefinition.id");
        assertEquals(descClone, fdClone.getParent());
        assertEquals("formDefinition.value", fdClone.getCustomProperty("formDefinition.name"));
        assertEquals("formDescriptor.value", fdClone.getCustomProperty("formDescriptor.name"));

        StandardPanelDefinition spdClone = (StandardPanelDefinition) descClone.getPanelDefinition("standardPanel.id");
        assertEquals("formDescriptor.value", spdClone.getCustomProperty("formDescriptor.name"));

    }

    @Test
    public void marshalAndUnmarshalEmpty() throws IOException, XMLException, ValidatorException, SAXException, SchemaCreationException, JiBXException
    {
        startOfTest("marshalAndUnmarshalEmpty");
        FormDescriptor description = new FormDescriptor("UNDECIDED");

        assertTrue(FormDescriptionValidator.instance().validate(description).passed());

        String filename = marshal(description);

        FormDescriptor description2 = unmarshal(filename);
        assertEquals(FormDescriptor.CURRENT_VERSION, description2.getVersion());
        assertTrue(FormDescriptionValidator.instance().validate(description2).passed());

        FormDescriptor description3 = description.clone();

        String descXml = description.asXMLString();
        String desc2Xml = description2.asXMLString();
        String desc3Xml = description3.asXMLString();

        assertEquals("Marshalling/Unmarshaling went wrong", descXml, desc2Xml);
        assertEquals("Cloning went wrong", descXml, desc3Xml);
    }

    @Test
    public void marshalAndUnmarshal() throws IOException, JiBXException, SAXException, SchemaCreationException, XMLException
    {
        startOfTest("marshalAndUnmarshal");
        FormDescriptor desc = new FormDescriptor("SIMPLE");
        desc.setLabelResourceKey("discipline.simple");
        desc.setOrdinal("A");
        desc.putCustomProperty("name", "value");

        TermPanelDefinition tpDef = new TermPanelDefinition("idtp", "prefix", "name");
        tpDef.setPanelClass("TextFieldPanel");
        tpDef.setDefaultModelClass("BasicStringListWrapper");
        tpDef.setLabelResourceKey("labelResourceKey");
        tpDef.setHelpItem("helpItem");
        tpDef.setRepeating(true);
        tpDef.setRequired(true);
        tpDef.setShortHelpResourceKey("shortHelpResourceKey");
        List<ChoiceListDefinition> clDefs = tpDef.getChoiceListDefinitions();
        ChoiceListDefinition clDef = new ChoiceListDefinition("clid");
        clDef.setSchemeName("DCMI_TYPE");
        clDef.setNullValid(true);
        clDef.setLabelResourceKey("schemeLabelResourceKey");
        clDefs.add(clDef);
        desc.addPanelDefinition(tpDef);

        TermPanelDefinition tpDef2 = new TermPanelDefinition("idtp2", "prefix2", "name2");
        tpDef2.setPanelClass("TextFieldPanel");
        tpDef2.setDefaultModelClass("BasicStringListWrapper");
        desc.addPanelDefinition(tpDef2);

        SubHeadingDefinition shDef = new SubHeadingDefinition("idsp");
        shDef.setHelpItem("helpItem");
        shDef.setLabelResourceKey("labelResourceKey");
        shDef.setShortHelpResourceKey("shortHelpResourceKey");
        shDef.getPanelIds().add("idtp");
        shDef.getPanelIds().add("idtp2");
        desc.addPanelDefinition(shDef);

        SubHeadingDefinition shDef2 = new SubHeadingDefinition("idsp2");
        shDef2.setHelpItem("helpItem2");
        shDef2.setLabelResourceKey("labelResourceKey2");
        shDef2.setShortHelpResourceKey("shortHelpResourceKey2");
        shDef2.getPanelIds().add("idtp2");
        desc.addPanelDefinition(shDef2);

        FormDefinition fd1 = new FormDefinition("fd1-id");
        desc.addFormDefinition(fd1);
        fd1.setHelpFile("helpFile1");
        fd1.setLicenseFile("licenseFile1");
        fd1.setInstructionFile("instructionFile");

        List<FormPage> dfps = fd1.getFormPages();
        FormPage dfp = new FormPage("formPage1");
        dfp.setLabelResourceKey("page.labelResourceKey");
        dfp.getPanelIds().add("idtp");
        dfp.getPanelIds().add("idsp");
        dfps.add(dfp);

        dfp = new FormPage("formPage2");
        dfp.setLabelResourceKey("page.labelResourceKey2");
        dfp.getPanelIds().add("idtp2");
        dfp.getPanelIds().add("idsp2");
        dfps.add(dfp);

        FormDefinition fd2 = new FormDefinition("fd2-id");
        desc.addFormDefinition(fd2);
        fd2.setHelpFile("helpFile2");
        fd2.setLicenseFile("licenseFile2");

        dfps = fd2.getFormPages();
        dfp = new FormPage("formPage3");
        dfp.setLabelResourceKey("page.labelResourceKey2e");
        dfp.getPanelIds().add("idtp");
        dfp.getPanelIds().add("idsp");
        dfps.add(dfp);

        dfp = new FormPage("formPage4");
        dfp.setLabelResourceKey("page.labelResourceKey2-2e");
        dfp.getPanelIds().add("idtp2");
        dfp.getPanelIds().add("idsp2");
        dfps.add(dfp);

        assertTrue(FormDescriptionValidator.instance().validate(desc).passed());
        String filename = marshal((FormDescriptor) desc);
        FormDescriptor desc2 = unmarshal(filename);
        assertTrue(FormDescriptionValidator.instance().validate(desc2).passed());

        FormDescriptor cloned = desc.clone();
        assertTrue(FormDescriptionValidator.instance().validate(cloned).passed());

        String d1 = desc.asXMLString();
        String d2 = desc2.asXMLString();
        String d3 = cloned.asXMLString();
        assertEquals("Marshaling/Unmarshaling went wrong", d1, d2);
        assertEquals("Cloning went wrong", d1, d3);

        // test AbstractJiBXObject methods on sub-elements
        TermPanelDefinition tpDefM = desc2.getTermPanelDefinition("idtp");
        assertEquals("Marhaling/Unmarshaling TermPanelDefinition", tpDef.asXMLString(), tpDefM.asXMLString());

        ChoiceListDefinition clDefM = tpDefM.getChoiceListDefinition("clid");
        assertEquals("Marhaling/Unmarshaling ChoiceListDefinition", tpDefM.getChoiceListDefinition("clid").asXMLString(), clDefM.asXMLString());

        SubHeadingDefinition shDefM = desc2.getSubHeadingDefinition("idsp");
        assertEquals("Marhaling/Unmarshaling SubHeadingDefinition", shDef.asXMLString(), shDefM.asXMLString());

        FormDefinition fd1M = desc2.getFormDefinition("fd1-id");
        assertEquals("Marhaling/Unmarshaling FormDefinition", fd1.asXMLString(), fd1M.asXMLString());

        FormPage dfpM = fd1M.getFormPage("formPage1");
        assertEquals("Marhaling/Unmarshaling FormPage", fd1.getFormPage("formPage1").asXMLString(), dfpM.asXMLString());
    }

}
