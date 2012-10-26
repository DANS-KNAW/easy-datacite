package nl.knaw.dans.easy.web.permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.easy.domain.model.PermissionSequence;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class RequestDataTableProvider extends SortableDataProvider
{
    private static final long serialVersionUID = 1L;
    final List<PermissionSequence> requests;

    public RequestDataTableProvider(List<PermissionSequence> requests)
    {
        this.requests = requests;

        // The default sorting
        setSort("lastStateChange", true);
    }

    class SortableDataProviderComparator implements Comparator<PermissionSequence>, Serializable
    {
        public int compare(final PermissionSequence o1, final PermissionSequence o2)
        {
            PropertyModel<Comparable> model1 = new PropertyModel<Comparable>(o1, getSort().getProperty());
            PropertyModel<Comparable> model2 = new PropertyModel<Comparable>(o2, getSort().getProperty());

            int result = 0;
            if (model1.getObject() != null && model2.getObject() != null)
                result = model1.getObject().compareTo(model2.getObject());

            if (!getSort().isAscending())
            {
                result = -result;
            }

            return result;
        }
    }

    private SortableDataProviderComparator comparator = new SortableDataProviderComparator();

    @Override
    public Iterator<? extends PermissionSequence> iterator(int first, int count)
    {
        // Get the data
        List<PermissionSequence> newList = new ArrayList<PermissionSequence>(requests);

        // Sort the data
        Collections.sort(newList, comparator);

        // Return the data for the current page - this can be determined only after sorting
        return newList.subList(first, first + count).iterator();
    }

    @Override
    public int size()
    {
        return requests.size();
    }

    @Override
    public IModel model(Object object)
    {
        return new Model<PermissionSequence>((PermissionSequence) object);
    }

}
