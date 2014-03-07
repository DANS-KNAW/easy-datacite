package nl.knaw.dans.common.fedora.store;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoCollection;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContainer;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContainerItem;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoRecursiveItem;

public class DummyConverter<T extends DataModelObject> extends AbstractDobConverter<T>
{
    private Class<T> clazz;

    public DummyConverter(Class<T> clazz)
    {
        super(new DmoNamespace("dummyConverter"));
        this.clazz = clazz;
    }

    @Override
    public DmoNamespace getObjectNamespace()
    {
        if (clazz.equals(DummyDmoContainer.class))
            return DummyDmoContainer.NAMESPACE;
        else if (clazz.equals(DummyDmoContainerItem.class))
            return DummyDmoContainerItem.NAMESPACE;
        else if (clazz.equals(DummyDmoRecursiveItem.class))
            return DummyDmoRecursiveItem.NAMESPACE;
        else if (clazz.equals(DummyDmoCollection.class))
            return DummyDmoCollection.NAMESPACE;
        else
            throw new RuntimeException("dummy container does not recognize class input");
    }
}
