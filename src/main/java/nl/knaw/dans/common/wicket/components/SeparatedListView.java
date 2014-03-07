package nl.knaw.dans.common.wicket.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * <p>
 * Where {@link ListView<T>} rather works with a TABLE, UL or OL, this repeater uses a prefix as
 * separator. The first occurrence of the prefix is not rendered. Though the repeater is not restricted
 * to links, a typical example would be:
 * </p>
 * HTML
 * 
 * <pre>
 *     &lt;span wicket:id="list"&gt;
 *         &lt;span wicket:id="separator"&gt;|&lt;/span&gt; 
 *         &lt;a href="#" wicket:id="link"&gt;
 *             &lt;span wicket:id="label"&gt; &lt;/span&gt;
 *         &lt;/a&gt;
 *     &lt;/span&gt;
 * </pre>
 * 
 * java
 * 
 * <pre>
 *     SeparatedListView createListView(final String[] labels)
 *     {
 *         final List&lt;Component&gt; links = new ArrayList&lt;Component&gt;();
 *         for (String label : labels)
 *             links.add(new MyLink(new Model&lt;String&gt;(label)));
 *         return new SeparatedListView(&quot;list&quot;, &quot;separator&quot;, links);
 *     }
 *     class MyLink extends Link&lt;String&gt;
 *     {
 *         public MyLink(final IModel&lt;String&gt; model)
 *         {
 *             super(&quot;link&quot;, model);
 *             add(new Label(&quot;label&quot;, model.getObject()));
 *         }
 *     }
 * }
 * </pre>
 */
public class SeparatedListView extends ListView<Component>
{
    // TODO move to commons?

    private static final long serialVersionUID = 3720302690110935794L;

    private final String separatorId;

    public SeparatedListView(final String id, final String separatorId, final List<? extends Component> list)
    {
        super(id, list);
        this.separatorId = separatorId;
    }

    @Override
    protected void populateItem(final ListItem<Component> item)
    {
        final boolean showSeparator = item.getIndex() != 0;
        item.add(item.getModelObject());
        item.add(new WebMarkupContainer(separatorId).setVisible(showSeparator));
    }

}
