package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.collections.SimpleCollectionImpl;

public class SimpleCollectionConverter extends AbstractDobConverter<SimpleCollectionImpl>
{

    public SimpleCollectionConverter()
    {
        super(SimpleCollection.NAMESPACE);
    }

}
