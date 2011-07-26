package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

public class RemarksPanel extends AbstractEasyPanel
{

    private static final long serialVersionUID = -4345309701422172351L;
    
    private final List<Remark> remarks;
    
    private boolean initiated;

    public RemarksPanel(String wicketId, RemarksModel remarksModel)
    {
        super(wicketId, remarksModel);
        remarks = remarksModel.getRemarks();
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
        ListView remarksView = new ListView("remarksView", remarks)
        {
            private static final long serialVersionUID = 2247676524537275265L;

            @Override
            protected void populateItem(ListItem item)
            {
                final Remark remark = (Remark) item.getDefaultModelObject();
                populate(item, remark);                               
            }
            
        };
        add(remarksView);
        
        Remark remark = new Remark();
        populate(this, remark);
    }

    private void populate(MarkupContainer container, final Remark remark)
    {
        Label remarkerLabel = new Label("remarkerLabel", new Model()
        {
            private static final long serialVersionUID = 3251891045434565385L;
            
            @Override
            public Serializable getObject()
            {
                EasyUser remarker = remark.getRemarker();
                return remarker == null ? null : remarker.getDisplayName();
            }
            
        });
        container.add(remarkerLabel);
        
        DateLabel remarkDate = DateLabel.forDatePattern("remarkDate", new Model()
        {
            private static final long serialVersionUID = 1139426060975374951L;

            @Override
            public Serializable getObject()
            {
                DateTime dateTime = remark.getRemarkDate();
                return dateTime == null ? null : dateTime.toDate();
            }

        }, "yyyy-MM-dd HH:mm");
        container.add(remarkDate);

        TextArea textArea = new TextArea("textArea", new Model()
        {
            private static final long serialVersionUID = 8412623237717756209L;
            
            @Override
            public Serializable getObject()
            {
                return remark.getText();
            }
            
            @Override
            public void setObject(Serializable object)
            {
                remark.setText((String) object);
            }
            
        });
        container.add(textArea);
    }

}
