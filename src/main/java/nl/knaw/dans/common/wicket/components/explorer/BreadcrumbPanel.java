package nl.knaw.dans.common.wicket.components.explorer;

import java.util.LinkedList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class BreadcrumbPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    private ListView<String> breadcrumbs;

    public BreadcrumbPanel(String id, ITreeItem selected)
    {
        super(id);

        breadcrumbs = new ListView<String>("path", getBreadcrumbs(selected))
        {
            private static final long serialVersionUID = 1L;

            protected void populateItem(ListItem<String> item)
            {
                item.add(new Label("breadcrumb", item.getModel()));
            }
        };
        breadcrumbs.setOutputMarkupId(true);
        add(breadcrumbs);

        this.setOutputMarkupId(true);
    }

    public void update(AjaxRequestTarget target, ITreeItem selected)
    {
        breadcrumbs.setList(getBreadcrumbs(selected));
        target.addComponent(this);
    }

    // get breadcrumb path to current select folder
    private LinkedList<String> getBreadcrumbs(ITreeItem selected)
    {
        LinkedList<String> path = new LinkedList<String>();
        ITreeItem breadcrumb = selected;
        while (breadcrumb != null)
        {
            path.addFirst(breadcrumb.getName());
            breadcrumb = breadcrumb.getParent();
        }

        return path;
    }
}
