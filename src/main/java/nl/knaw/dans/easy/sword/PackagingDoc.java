package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.form.AbstractInheritableDefinition;
import nl.knaw.dans.easy.domain.form.ChoiceListDefinition;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.fedora.store.EasyFedoraStore;
import nl.knaw.dans.easy.servicelayer.services.DepositService;

import org.joda.time.DateTime;

public class PackagingDoc
{

    private static final String FEDORA_URL      = "http://localhost:8080/fedora";
    private static final String FEDORA_USER     = "fedoraAdmin";
    private static final String FEDORA_PASSWORD = "fedoraAdmin";
    private static final String EDITABLE_HELP   = "../../easy/trunk/easy/easy-application/easy-home/editable/help/";

    public static void main(final String[] args) throws ServiceException, IOException
    {
        final DepositService depositService = setFedoraContext();
        // TODO get rid of INFO/DEBUG logging
        System.out.print(generate(depositService).toString());
    }

    private static DepositService setFedoraContext()
    {
        final Fedora fedora = new Fedora(FEDORA_URL, FEDORA_USER, FEDORA_PASSWORD);
        new Data().setEasyStore(new EasyFedoraStore("easy", fedora));
        return new EasyDepositService();
    }

    private static StringBuffer generate(final DepositService depositService) throws ServiceException, IOException
    {
        final StringBuffer sb = new StringBuffer();
        final Map<String, ChoiceListDefinition> choiceLists = new HashMap<String, ChoiceListDefinition>();
        final StringBuffer disciplineDetails = parseDisciplines(choiceLists, depositService);
        sb.append(generateHeadSection());
        sb.append("<body>\n  <!-- DO NOT CHANGE\n  generated at " + new DateTime() + " by " + PackagingDoc.class.getName() + " -->\n");
        sb.append(generateIntro());
        sb.append(generateDisciplinesToc(depositService));
        sb.append(generateChoicelistToc(choiceLists));
        sb.append(disciplineDetails);
        sb.append(generateChoicelists(choiceLists, depositService));
        sb.append("</body>\n");
        return sb;
    }

    private static StringBuffer parseDisciplines(final Map<String, ChoiceListDefinition> choiceLists, final DepositService depositService)
            throws ServiceException, IOException
    {
        final StringBuffer sb = new StringBuffer();
        for (final DepositDiscipline discipline : depositService.getDisciplines())
        {
            final FormDefinition formDef = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD);
            sb.append("<h2>discipline '<a name='" + discipline.getDepositDisciplineId() + "'>" + discipline.getDepositDisciplineId() + "</a>'</h2>\n");
            sb.append(generateElements(choiceLists, formDef.getFormPages(),discipline.getDepositDisciplineId()));
        }
        return sb;
    }

    private static StringBuffer generateHeadSection()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("<head>\n");
        sb.append("<script language='javascript' type='text/javascript'>\n");
        sb.append("function showHide(shID) {\n");
        sb.append("   if (document.getElementById(shID)) {\n");
        sb.append("      if (document.getElementById(shID).style.display != 'block') {\n");
        sb.append("         document.getElementById(shID).style.display = 'block';\n");
        sb.append("      }\n");
        sb.append("      else {\n");
        sb.append("         document.getElementById(shID).style.display = 'none';\n");
        sb.append("      }\n");
        sb.append("   }\n");
        sb.append("}\n");
        sb.append("</script>\n");
        sb.append("<style type='text/css'>\n");
        sb.append("   .help {\n");
        sb.append("      display: none;\n");
        sb.append("      padding: 4px;\n");
        sb.append("      margin-top: 4px;\n");
        sb.append("      border: 1px solid #666; }\n");
        sb.append("   a.toggleLink { font-size: 70%; text-decoration: none;}\n");
        sb.append("   a.toggleLink:hover { border: 1px dotted #36f; }\n");
        sb.append("   th {background-color: #DDD;\n");
        sb.append("   td {vertical-align: top; }\n");// TODO alignment does not work, at least not
                                                     // FireFox
        sb.append("</style>\n");
        sb.append("</head>\n");
        return sb;
    }

    private static StringBuffer generateIntro()
    {
        final StringBuffer sb = new StringBuffer();

        sb.append("<h1>Sword Packaging</h1>\n");
        sb.append("<p>The zip file for a sword deposit should contain</p>\n");
        sb.append("<ul>\n");
        sb.append("  <li>a single file called 'easyMetadata.xml'<BR>\n");
        sb.append("    Name and format are subject to change. A description of the current format follows below.\n");
        sb.append("  </li>\n");
        sb.append("  <li>a single folder called 'data'<BR>\n");
        sb.append("    The full path name of datafiles should not exceed 252 characters.\n");
        sb.append("    Desired file formats are documented in the \n");
        sb.append("    <a href='http://www.dans.knaw.nl/en/content/data-archive/depositing-data'>general instructions</a>\n");
        sb.append("  </li>\n");
        sb.append("</ul>\n");
        sb.append("<h1>metadata format</h1>\n");
        sb.append("<p>\n");
        sb.append("  This <a href='http://eof12.dans.knaw.nl/schemas/md/emd/2012/easymetadata.xsd'>XSD</a>\n");
        sb.append("  can be used to create / validate metadata instances.\n");
        sb.append("  The <a href='http://eof12.dans.knaw.nl/schemas/docs/emd/emd.html'>schema documentation</a> \n");
        sb.append("  is a human readable version.\n");
        sb.append("</p>\n");
        sb.append("<p>\n");
        sb.append("  It is important to note that additional constraints apply for the different disciplines.\n");
        sb.append("  The discipline ID is stored in &lt;eas:metadataformat&gt;, \n");
        sb.append("  and the constraints per desciplines can be found in the following table.</p>\n");
        sb.append("</p>\n");
        return sb;
    }

    private static StringBuffer generateDisciplinesToc(final DepositService depositService) throws ServiceException
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("<table style='margin-left:2em'>\n");
        sb.append("  <tr><th>Discipline ID</td><th colspan='2'>details</td></tr>\n");
        for (final DepositDiscipline discipline : depositService.getDisciplines())
        {
            final String id = discipline.getDepositDisciplineId();
            final FormDefinition formDef = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD);
            sb.append("  <tr><td>" + id + "\n");
            sb.append("  </td><td><a href='" + formDef.getInstructionFile() + "'>instructions</a>\n");
            sb.append("  </td><td><a href='#" + id + "'>elements</a>\n");
            sb.append("  </td></tr>\n");
        }
        sb.append("</table>\n");
        return sb;
    }

    private static StringBuffer generateChoicelistToc(final Map<String, ChoiceListDefinition> choiceLists)
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("<p>In addition, some elements require entries from a controlled vocabulary. \n");
        sb.append("Where applicable the elements descriptions refer to the appropriate one. \n");
        sb.append("The following list is an overview.</p>\n");

        sb.append("<ul>\n");
        for (final ChoiceListDefinition clDef : choiceLists.values())
            sb.append("  <li><a href='#" + clDef.getId() + "'>" + clDef.getId() + "</a></li>\n");
        sb.append("</ul>\n");
        return sb;
    }

    private static StringBuffer generateChoicelists(final Map<String, ChoiceListDefinition> choiceLists, final DepositService depositService)
            throws ServiceException
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("<table><tr>\n");
        for (final ChoiceListDefinition clDef : choiceLists.values())
        {
            sb.append("<tr><td colspan='2'><h2><a name='" + clDef.getId() + "'>" + clDef.getId() + "</a></h2></td></tr>\n");
            sb.append("<tr><th>value</th><th>description</th>\n");
            final ChoiceList choiceList = depositService.getChoices(clDef.getId(), null);
            for (final KeyValuePair kvp : choiceList.getChoices()){
                String indent = "";
                for (int i=0;i<kvp.getIndent();i++)
                    indent += " . . ";
                sb.append("<tr><td>" + kvp.getKey() + "</td><td>" + indent + kvp.getValue() + "</td></tr>\n");
            }
        }
        sb.append("</table>\n");
        return sb;
    }

    private static StringBuffer generateElements(final Map<String, ChoiceListDefinition> choiceLists, final List<FormPage> formPages, String disciplineId) throws IOException,
            ServiceException
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("<table>\n<tr><th>element</th><th>notes</th></tr>");
        for (final FormPage formPage : formPages)
        {
            sb.append("<tr><td colspan='2'>&nbsp;</td></td></tr>\n");
            final List<PanelDefinition> panels = formPage.getPanelDefinitions();
            for (final PanelDefinition panel : panels)
            {
                sb.append("<tr><td>" + panel.getId() + "</td><td>" + helpInfo(panel, disciplineId) + "</td></tr>\n");
                collectChoiceLists(panel, choiceLists);
            }
        }
        sb.append("</table>\n");
        return sb;
    }

    private static void collectChoiceLists(final PanelDefinition panel, final Map<String, ChoiceListDefinition> choiceLists) throws ServiceException
    {
        if (panel instanceof SubHeadingDefinition)
        {
            final SubHeadingDefinition shDef = (SubHeadingDefinition) panel;
            for (final PanelDefinition spd : shDef.getPanelDefinitions())
                collectChoiceLists(spd, choiceLists);
        }
        else if (panel instanceof StandardPanelDefinition)
        {
            final StandardPanelDefinition spDef = (StandardPanelDefinition) panel;
            for (final ChoiceListDefinition clDef : spDef.getChoiceListDefinitions())
                choiceLists.put(clDef.getId(), clDef);
        }
    }

    private static String helpInfo(final AbstractInheritableDefinition<?> panel, String disciplineId) throws IOException
    {
        String s = "";
        if (panel instanceof StandardPanelDefinition)
        {
            final StandardPanelDefinition sp = (StandardPanelDefinition) panel;
            if (sp.isRequired())
                s += " mandatory ";
            if (sp.isRepeating())
                s += " repeating ";
            if (sp.hasChoicelistDefinition())
                for (final ChoiceListDefinition cld : sp.getChoiceListDefinitions())
                    s += " <a href='#" + cld.getId() + "'>allowed values</a> ";
        }
        s += getShortHelp(panel, s);
        if (panel.getHelpItem() == null)
            return s;
        final File file = new File(EDITABLE_HELP + panel.getHelpItem() + ".template");
        final String help = new String(FileUtil.readFile(file)).replaceAll("<hr />", " ").replaceAll("h2>", "h4>");
        final String id = disciplineId+"_"+panel.getId();
        return s + " <a href='#' id='" + id + "-show' class='toggleLink' onclick='showHide(\"" + id + "\");return false;'>Show/hide help</a>"
                + "<table class='help' id='" + id + "'><tr><td>" + help + "</td></tr></table>";
    }

    private static String getShortHelp(final AbstractInheritableDefinition<?> panel, final String s)
    {
        final String key = panel.getShortHelpResourceKey();
        if (key != null)
        {
            // TODO return DepositPage.class.getResource(key); without dependency on the web-ui layer
            return " ";
        }
        return "";
    }
}
