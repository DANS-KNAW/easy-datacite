package nl.knaw.dans.easy.domain.form;

import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;

import org.jibx.runtime.JiBXException;
import org.junit.Test;
import static org.junit.Assert.*;

public class FormPageTest
{
    
    @Test
    public void getPanelDefinition() throws JiBXException
    {
        FormDescriptor fd = createFormdescriptor();
        FormDefinition fdef = fd.getFormDefinition("formDefinition.id");
        FormPage fpage = fdef.getFormPage("formPage.id");
        
        TermPanelDefinition termPanel1 = fd.getTermPanelDefinition("termPanel.id");
        TermPanelDefinition termPanel2 = fdef.getTermPanelDefinition("termPanel.id");
        TermPanelDefinition termPanel3 = fpage.getTermPanelDefinition("termPanel.id");
        
        assertSame(termPanel1, termPanel2);
        assertSame(termPanel1, termPanel3);
        
        assertEquals("formDescriptor.helpFile", termPanel1.getHelpFile());
        
        assertSame(fd, termPanel1.getParent());
        
        TermPanelDefinition termPanel4 = (TermPanelDefinition) fpage.getPanelDefinitions().get(1);
        assertNotSame(termPanel1, termPanel4);
        assertSame(fpage, termPanel4.getParent());
        assertEquals("formPage.helpFile", termPanel4.getHelpFile());
    }
    
    private FormDescriptor createFormdescriptor()
    {
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
        formPage.getPanelIds().add("termPanel.id");
        formPage.setHelpFile("formPage.helpFile");
        
        formDefinition.addFormPage(formPage);
        formDescriptor.addFormDefinition(formDefinition);
        
        StandardPanelDefinition standardPanel = new StandardPanelDefinition("standardPanel.id");
        standardPanel.setDefaultModelClass("modelClass");
        
        formDescriptor.addPanelDefinition(standardPanel);
        
        TermPanelDefinition termPanel = new TermPanelDefinition("termPanel.id", "namespacePrefix", "termName");
        termPanel.setLabelResourceKey("termPanel.labelResourceKey");
        
        formDescriptor.addPanelDefinition(termPanel);
        return formDescriptor;
    }

}
