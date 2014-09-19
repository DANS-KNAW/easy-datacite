package nl.knaw.dans.common.fedora.store;

import java.net.URI;
import java.util.Set;

import nl.knaw.dans.common.fedora.fox.ContentDigestType;
import nl.knaw.dans.common.fedora.fox.ContentLocation.Type;
import nl.knaw.dans.common.fedora.fox.ControlGroup;
import nl.knaw.dans.common.fedora.fox.Datastream;
import nl.knaw.dans.common.fedora.fox.Datastream.State;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.FoxConstants;
import nl.knaw.dans.common.fedora.rdf.FedoraRelationsConverter;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.exception.InvalidRelationshipException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;

public abstract class AbstractDobConverter<T extends DataModelObject> implements DobConverter<T> {

    private final DmoNamespace converterId;

    public AbstractDobConverter(DmoNamespace objectNamespace) {
        this.converterId = objectNamespace;
    }

    public DmoNamespace getObjectNamespace() {
        return converterId;
    }

    public DigitalObject serialize(T dataModelObject) throws ObjectSerializationException {
        final DigitalObject dob = new DigitalObject(dataModelObject.getDmoNamespace().getValue());

        // copy sid
        dob.setSid(dataModelObject.getStoreId());

        // properties
        dob.readObjectProperties(dataModelObject);

        // inline xml datastreams
        for (MetadataUnit mdUnit : dataModelObject.getMetadataUnits()) {
            Datastream ds = dob.addDatastream(mdUnit.getUnitId(), ControlGroup.X);
            ds.setFedoraUri(mdUnit.getUnitFormatURI());
            ds.setState(Datastream.State.A);
            ds.setVersionable(mdUnit.isVersionable());

            DatastreamVersion dv = ds.addDatastreamVersion(null, FoxConstants.MIMETYPE_XML);
            dv.setLabel(mdUnit.getUnitLabel());
            try {
                dv.setXmlContent(mdUnit.asObjectXML());
            }
            catch (XMLSerializationException e) {
                throw new ObjectSerializationException("Could not serialize MetadataUnit with unitId " + mdUnit.getUnitId(), e);
            }
            catch (DocumentException e) {
                throw new ObjectSerializationException("Could not serialize MetadataUnit with unitId " + mdUnit.getUnitId(), e);
            }
        }

        // binary content streams
        for (BinaryUnit binUnit : dataModelObject.getBinaryUnits()) {
            Datastream ds = dob.addDatastream(binUnit.getUnitId(), convertControlGroup(binUnit.getUnitControlGroup()));
            ds.setState(State.A);
            ds.setVersionable(binUnit.isVersionable());
            DatastreamVersion dsv = ds.addDatastreamVersion(null, binUnit.getMimeType());
            dsv.setContentDigest(ContentDigestType.DISABLED, null);
            dsv.setLabel(binUnit.getUnitLabel());

            if (binUnit.hasFile()) {
                dsv.setContentLocation(Type.URL, URI.create(binUnit.getLocation()));
            } else if (binUnit.hasBinaryContent()) {
                dsv.setBinaryContent(binUnit.getBinaryContent());
            }
        }

        // relations
        String rdf = FedoraRelationsConverter.generateRdf(dataModelObject);

        if (!StringUtils.isBlank(rdf)) {
            Datastream ds = dob.addDatastream(FoxConstants.STREAM_ID_EXT, ControlGroup.X);
            ds.setFedoraUri(FoxConstants.RELS_EXT_FORMAT_URI_EXT);
            ds.setState(Datastream.State.A);
            ds.setVersionable(false);

            DatastreamVersion dv = ds.addDatastreamVersion(null, FoxConstants.MIMETYPE_XML);
            dv.setLabel("rels-ext");
            try {
                dv.setXmlContent(rdf);
            }
            catch (DocumentException e) {
                throw new ObjectSerializationException(e);
            }
        }
        return dob;
    }

    // override if needed.
    public void prepareForUpdate(T dmo) throws ObjectSerializationException {};

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void deserializeRelationships(DigitalObject dob, DataModelObject dmo) throws ObjectDeserializationException {
        Relations relObj = dmo.getRelations();
        if (!(relObj instanceof AbstractRelations))
            return;

        try {
            String streamId = FoxConstants.STREAM_ID_EXT;
            DatastreamVersion relsVersion = dob.getLatestVersion(streamId);
            if (relsVersion == null)
                return;

            Set<Relation> relations = FedoraRelationsConverter.rdfToRelations(relsVersion.getXmlContentString());
            ((AbstractRelations) relObj).setRelationships(relations);
            ((AbstractRelations) relObj).setDirty(false);

        }
        catch (InvalidRelationshipException e) {
            throw new ObjectDeserializationException(e);
        }
    }

    public void deserialize(DigitalObject dob, T dmo) throws ObjectDeserializationException {
        checkNamespace(dob, dmo);

        dob.writeObjectProperties(dmo);

        deserializeRelationships(dob, dmo);
    }

    protected void checkNamespace(DigitalObject dob, DataModelObject dmo) throws ObjectDeserializationException {
        if (!dob.getSid().startsWith(dmo.getDmoNamespace().getValue())) {
            throw new ObjectDeserializationException("Wrong object. DigitalObject " + dob.getSid() + " does not belong to the DataModelObject namespace "
                    + dmo.getDmoNamespace());
        }
    }

    protected ControlGroup convertControlGroup(BinaryUnit.UnitControlGroup unitControlGroup) {
        return ControlGroup.values()[unitControlGroup.ordinal()];
    }

}
