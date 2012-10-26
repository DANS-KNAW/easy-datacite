package nl.knaw.dans.easy.web.wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.easy.domain.workflow.WorkflowStep;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * &#8853;
 * @author ecco Aug 28, 2009
 *
 */
public class WorkflowProgressPanel extends Panel
{

    private static final long serialVersionUID = -3742642574965755484L;

    private final WorkflowStep workflowStep;
    private final int countRequired;

    private boolean initiated;

    public WorkflowProgressPanel(String id, WorkflowStep workflowStep)
    {
        super(id);
        this.workflowStep = workflowStep;
        countRequired = workflowStep.countRequiredSteps();
        setOutputMarkupId(true);
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
        RefreshingView refreshingImgs = new RefreshingView("refreshingImgs")
        {

            private static final long serialVersionUID = 4122274747093842912L;

            @Override
            protected Iterator getItemModels()
            {
                List<IModel> imgs = new ArrayList<IModel>();
                int countCompleted = workflowStep.countRequiredStepsCompleted();
                for (int i = 0; i < countCompleted; i++)
                {
                    imgs.add(new Model(new Boolean(true)));
                }
                for (int i = 0; i < countRequired - countCompleted; i++)
                {
                    imgs.add(new Model(new Boolean(false)));
                }
                return imgs.iterator();
            }

            @Override
            protected void populateItem(Item item)
            {
                Boolean img = (Boolean) item.getDefaultModelObject();
                WebMarkupContainer on = new WebMarkupContainer("imgOn");
                WebMarkupContainer off = new WebMarkupContainer("imgOff");
                on.setVisible(img.booleanValue());
                off.setVisible(!img.booleanValue());
                item.add(on);
                item.add(off);
            }

        };
        add(refreshingImgs);
    }

}
