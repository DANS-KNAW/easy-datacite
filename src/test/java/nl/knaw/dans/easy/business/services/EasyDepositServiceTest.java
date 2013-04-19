package nl.knaw.dans.easy.business.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.util.TestHelper;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;

import org.jibx.runtime.JiBXException;
import org.junit.BeforeClass;
import org.junit.Test;

public class EasyDepositServiceTest extends TestHelper
{

    @BeforeClass
    public static void beforeClass()
    {
        ClassPathHacker.addFile("../../app/easy-webui/src/main/resources");
        before(EasyDepositServiceTest.class);
    }

    @Test
    public void loadFormDescriptions() throws ServiceException, JiBXException
    {
        startOfTest("loadFormDescriptions");
        EasyDepositService eds = new EasyDepositService();
        eds.loadFormDescriptors();
        assertTrue(eds.getDisciplines().size() > 0);

        // test inheritance
        DepositDiscipline discipline = eds.getDiscipline("sociology");

        FormDescriptor fd = discipline.getEmdFormDescriptor();
        //System.out.println(fd.asXMLString(4));
        // element has value: <shortHelpResourceKey>descriptor.shortHelpResourceKey</shortHelpResourceKey>
        //assertEquals("descriptor.shortHelpResourceKey", fd.getShortHelpResourceKey());

        TermPanelDefinition dcCreator = fd.getTermPanelDefinition("dc.creator");
        assertSame(fd, dcCreator.getParent());
        // element is empty: <shortHelpResourceKey></shortHelpResourceKey>
        // assertEquals("", dcCreator.getShortHelpResourceKey());

        //        ChoiceListDefinition clDef = dcCreator.getChoiceListDefinition("listId");
        //        assertSame(dcCreator, clDef.getParent());
        //        // element is not there: inherit from parent.
        //        assertEquals("", clDef.getShortHelpResourceKey());

        // test MetadataFormat
        assertEquals(ApplicationSpecific.MetadataFormat.SOCIOLOGY, discipline.getMetadataFormat());
    }

    @Test(expected = ServiceException.class)
    public void getUnknownDiscipline() throws ServiceException
    {
        startOfTest("getUnknownDiscipline");
        DepositService eds = new EasyDepositService();
        eds.getDiscipline("foo");
    }
}
