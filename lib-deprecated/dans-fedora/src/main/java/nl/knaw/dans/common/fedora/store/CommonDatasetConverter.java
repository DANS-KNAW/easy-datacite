package nl.knaw.dans.common.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.dataset.CommonDataset;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;

import org.dom4j.Element;

public class CommonDatasetConverter extends AbstractDobConverter<CommonDataset> {
    public CommonDatasetConverter() {
        super(new DmoNamespace("dccd"));
    }

    @Override
    public void deserialize(DigitalObject digitalObject, CommonDataset dmo) throws ObjectDeserializationException {
        super.deserialize(digitalObject, dmo);

        try {
            DatastreamVersion dcVersion = digitalObject.getLatestVersion(DublinCoreMetadata.UNIT_ID);
            if (dcVersion != null) {
                Element element = dcVersion.getXmlContentElement();
                DublinCoreMetadata dc = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, element);
                dc.setTimestamp(dcVersion.getTimestamp());
                dc.setDirty(false);
                dmo.setDublinCoreMetadata(dc);
            } else {
                throw new XMLDeserializationException("No dublin core found on retrieved digital object. sid=" + digitalObject.getSid());
            }
        }
        catch (XMLDeserializationException e) {
            throw new ObjectDeserializationException(e);
        }
    }

}
