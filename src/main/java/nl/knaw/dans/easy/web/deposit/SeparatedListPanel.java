package nl.knaw.dans.easy.web.deposit;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

class SeparatedListPanel extends Panel
{

    private static final long serialVersionUID = -3441453142983333780L;

    /* the first time we don't want to show the separator as it is actually a prefix */
    private boolean showSeparator = false;

    public SeparatedListPanel(final String id, final List<Component> items)
    {
        super(id);
        final ListView<Component> listView = new ListView<Component>("list", items)
        {
            private static final long serialVersionUID = 3720302690110935794L;

            @Override
            protected void populateItem(final ListItem<Component> item)
            {
                item.add(item);
                item.add(new WebMarkupContainer("separator").setVisible(showSeparator));
                showSeparator = true;
            }
        };
        add(listView);

        setVisible(!items.isEmpty());
    }
}
