package nl.knaw.dans.common.fedora.store;

import nl.knaw.dans.common.fedora.fox.Datastream;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoFactory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;

import org.dom4j.Element;

public class DefaultDobConverter extends AbstractDobConverter<DataModelObject> {

    public static String NO_NAMESPACE = "no-namespace";

    public DefaultDobConverter() {
        super(new DmoNamespace(NO_NAMESPACE));
    }

    @Override
    public void deserialize(DigitalObject dob, DataModelObject dmo) throws ObjectDeserializationException {
        super.deserialize(dob, dmo);
        DmoFactory<?> factory = AbstractDmoFactory.factoryFor(dmo.getDmoNamespace());
        for (Datastream datastream : dob.getDatastreams()) {
            String mdUnitId = datastream.getStreamId();
            DatastreamVersion version = datastream.getLatestVersion();
            Element element = version.getXmlContentElement();
            factory.setMetadataUnit(dmo, mdUnitId, element);
        }
    }

}
