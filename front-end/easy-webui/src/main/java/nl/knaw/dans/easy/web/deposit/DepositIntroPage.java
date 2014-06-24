package nl.knaw.dans.easy.web.deposit;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.Style;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositIntroPage extends AbstractEasyNavPage
{
    private static final Logger logger = LoggerFactory.getLogger(DepositIntroPage.class);
    public static final String EDITABLE_DEPOSIT_INTRO_TEMPLATE = "/pages/DepositIntro.template";

    @SpringBean(name = "staticContentBaseUrl")
    private String staticContentBaseUrl;

    @SpringBean(name = "depositService")
    private DepositService depositService;

    public DepositIntroPage()
    {
        add(Style.DEPOSIT_HEADER_CONTRIBUTION);
        ListView<DepositDiscipline> listView = new ListView<DepositDiscipline>("disciplines", getDisciplines())
        {

            private static final long serialVersionUID = -2578773278751553901L;

            @Override
            protected void populateItem(ListItem<DepositDiscipline> item)
            {
                final DepositDiscipline discipline = (DepositDiscipline) item.getDefaultModelObject();
                final FormDescriptor formDescriptor = discipline.getEmdFormDescriptor();

                item.add(new Label("discipline.name", new ResourceModel(formDescriptor.getLabelResourceKey())));
                item.add(new ExternalLink("instructionLink_EN", staticContentBaseUrl + "/" + formDescriptor.getInstructionFile() + "UK.pdf", "English"));
                item.add(new ExternalLink("instructionLink_NL", staticContentBaseUrl + "/" + formDescriptor.getInstructionFile() + "NL.pdf", "Nederlands"));

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
        add(new EasyEditablePanel("editablePanel", EDITABLE_DEPOSIT_INTRO_TEMPLATE));
    }

    private List<DepositDiscipline> getDisciplines()
    {
        try
        {
            return depositService.getDisciplines();
        }
        catch (ServiceException e)
        {
            errorMessage(EasyResources.DEPOSIT_APPLICATION_ERROR);
            logger.error("Could not start " + this.getClass().getSimpleName() + ": ", e);
            throw new InternalWebError();
        }
    }
}
