package nl.knaw.dans.common.wicket.components.explorer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem.Type;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class TableProvider extends SortableDataProvider
{
    private static final long serialVersionUID = 1L;

    class SortableDataProviderComparator implements Comparator<ITreeItem>, Serializable
    {
        private static final long serialVersionUID = 1L;

        public int compare(final ITreeItem o1, final ITreeItem o2)
        {
            PropertyModel<Comparable> model1 = new PropertyModel<Comparable>(o1, getSort().getProperty());
            PropertyModel<Comparable> model2 = new PropertyModel<Comparable>(o2, getSort().getProperty());

            if (o1.getType().equals(Type.FOLDER) && !o2.getType().equals(Type.FOLDER))
            {
                return -1;
            }
            else if (!o1.getType().equals(Type.FOLDER) && o2.getType().equals(Type.FOLDER))
            {
                return 1;
            }
            else
            {
                int result = model1.getObject().compareTo(model2.getObject());

                if (!getSort().isAscending())
                {
                    result = -result;
                }

                return result;
            }
        }
    }

    private List<ITreeItem> list = new ArrayList<ITreeItem>();
    private SortableDataProviderComparator comparator = new SortableDataProviderComparator();

    public TableProvider()
    {
        // The default sorting
        setSort("name", true);
    }

    public Iterator iterator(int first, int count)
    {
        // Get the data
        List<ITreeItem> newList = new ArrayList<ITreeItem>(list);

        // Sort the data
        Collections.sort(newList, comparator);

        // Return the data for the current page - this can be determined only after sorting
        return newList.subList(first, first + count).iterator();
    }

    public int size()
    {
        return list.size();
    }

    public IModel<ITreeItem> model(final Object object)
    {
        return new AbstractReadOnlyModel<ITreeItem>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public ITreeItem getObject()
            {
                return (ITreeItem) object;
            }
        };
    }

    public List<ITreeItem> getList()
    {
        return list;
    }

    public void setList(List<ITreeItem> list)
    {
        this.list = list;
    }
}
