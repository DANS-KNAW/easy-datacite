package nl.knaw.dans.easy.web.deposit;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositIntroPage extends AbstractEasyNavPage
{

    private static final Logger logger = LoggerFactory.getLogger(DepositIntroPage.class);

    public static final String EDITABLE_DEPOSIT_INTRO_TEMPLATE = "/editable/DepositIntro.template";
    
    private final List<DepositDiscipline> disciplines;
    private boolean initiated;

    public DepositIntroPage()
    {
        try
        {
    		disciplines = Services.getDepositService().getDisciplines();
        }
        catch (ServiceException e)
        {
        	errorMessage(EasyResources.DEPOSIT_APPLICATION_ERROR);
            logger.error("Could not start " + this.getClass().getSimpleName() + ": ", e);
            throw new InternalWebError();
        }
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
    	ListView<DepositDiscipline> listView = new ListView<DepositDiscipline>("disciplines", disciplines)
        {

            private static final long serialVersionUID = -2578773278751553901L;

            @Override
            protected void populateItem(ListItem<DepositDiscipline> item)
            {
                final DepositDiscipline discipline = (DepositDiscipline) item.getDefaultModelObject();
                final FormDescriptor formDescriptor = discipline.getEmdFormDescriptor();

                item.add(new Label("discipline.name", new ResourceModel(formDescriptor.getLabelResourceKey())));
                item.add(createInstructionLink(formDescriptor));
                
                Link<DepositDiscipline> startDepositLink = new Link<DepositDiscipline>("startDepositLink", item.getModel())
                {

                    private static final long serialVersionUID = -4209139048992540876L;

                    @Override
                    public void onClick()
                    {
                        setResponsePage(new DepositPage(discipline, DepositDiscipline.EMD_DEPOSITFORM_WIZARD));
                    }
                    
                };
                item.add(startDepositLink);
            }
        };
        add(listView);
        
        add(new EasyEditablePanel("editablePanel", "/editable/DepositIntro.template"));
    }

    public static ExternalLink createInstructionLink(final FormDescriptor formDescriptor)
    {
        final FormDefinition fDef = formDescriptor.getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD);
        final String instructionUrl = fDef.getInstructionFile() == null ? "" : fDef.getInstructionFile();
        ExternalLink instructionLink = new ExternalLink(
                "instructionLink",
                new Model<String>(instructionUrl),
                new ResourceModel(formDescriptor.getLabelResourceKey() + ".instructionLinkText"));
        instructionLink.setVisible(instructionUrl.startsWith("http"));
        return instructionLink;
    }
}
