package nl.knaw.dans.easy.domain.form;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;

import org.junit.Test;

public class TermPanelDefinitionTest
{
    @Test
    public void testClone() throws XMLSerializationException
    {
        TermPanelDefinition tpDef = new TermPanelDefinition("panelId", "namespacePrefix", "termName");
        tpDef.setHelpFile("helpFile");
        tpDef.setHelpItem("helpItem");
        tpDef.setInstructionFile("instructionFile");
        tpDef.setLabelResourceKey("labelResourceKey");
        tpDef.setLicenseFile("licenseFile");
        tpDef.setDefaultModelClass("modelClass");
        tpDef.setPanelClass("panelClass");
        tpDef.setRepeating(true);
        tpDef.setRequired(true);
        tpDef.setShortHelpResourceKey("shortHelpResourceKey");
        
        TermPanelDefinition clone = tpDef.clone();
        //System.out.println(clone.asXMLString(4));
        assertEquals(tpDef.asXMLString(), clone.asXMLString());
    }

}
