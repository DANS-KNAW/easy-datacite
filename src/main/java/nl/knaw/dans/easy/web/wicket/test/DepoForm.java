package nl.knaw.dans.easy.web.wicket.test;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.web.template.AbstractEasyForm;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class DepoForm extends AbstractEasyForm
{

    private static final long serialVersionUID = 2483055129971445449L;
    
    private boolean initiated;
    private final List<Panel> panelList = new ArrayList<Panel>();

    public DepoForm(String wicketId, IModel model)
    {
        super(wicketId, model);
    }
    
    public void addPanel(Panel panel)
    {
        panelList.add(panel);
    }

    @Override
    protected void onSubmit()
    {
        // TODO Auto-generated method stub

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
        ListView listView = new ListView("panels", panelList)
        {

            private static final long serialVersionUID = -1406027277441728043L;

            @Override
            protected void populateItem(ListItem item)
            {
                Panel panel = (Panel) item.getDefaultModelObject();
                item.add(panel);
            }
            
        };
        add(listView);
        
    }
    

}
