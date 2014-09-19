package nl.knaw.dans.c.dmo.collections.core;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

import org.dom4j.Element;

public class DmoCollectionFactory extends AbstractDmoFactory<DmoCollection> {

    private final DmoNamespace namespace;

    public DmoCollectionFactory(DmoNamespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public DmoNamespace getNamespace() {
        return namespace;
    }

    @Override
    public DmoCollection newDmo() throws RepositoryException {
        return createDmo(nextSid());
    }

    @Override
    public DmoCollection createDmo(String storeId) {
        if (!namespace.equals(DmoStoreId.getDmoNamespace(storeId))) {
            throw new IllegalArgumentException("Wrong factory: storeId " + storeId + " is not in namespace " + namespace.getValue());
        }
        return new DmoCollectionImpl(new DmoStoreId(storeId));
    }

    @Override
    public void setMetadataUnit(DataModelObject dmo, String unitId, Element element) throws ObjectDeserializationException {
        if (DublinCoreMetadata.UNIT_ID.equals(unitId)) {
            try {
                DmoCollectionImpl collection = (DmoCollectionImpl) dmo;
                JiBXDublinCoreMetadata jdc = (JiBXDublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, element);
                collection.setDcMetadata(jdc);
            }
            catch (XMLDeserializationException e) {
                throw new ObjectDeserializationException(e);
            }
        }
    }

}
