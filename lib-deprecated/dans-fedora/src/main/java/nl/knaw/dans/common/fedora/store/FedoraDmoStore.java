package nl.knaw.dans.common.fedora.store;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.fedora.fox.ControlGroup;
import nl.knaw.dans.common.fedora.fox.Datastream;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.FoxConstants;
import nl.knaw.dans.common.fedora.rdf.FedoraRelationsConverter;
import nl.knaw.dans.common.fedora.rdf.FedoraURIReference;
import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.AbstractDmoStore;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoFactory;
import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.repo.relations.RelationsConverter;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.joda.time.DateTime;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.client.FedoraClient;
import fedora.common.Constants;
import fedora.server.types.gen.DatastreamDef;

/**
 * A {@link DmoStore} for the Fedora Repository.
 * 
 * @see Fedora
 * @author ecco Nov 29, 2009
 */
public class FedoraDmoStore extends AbstractDmoStore {
    private static final Logger logger = LoggerFactory.getLogger(FedoraDmoStore.class);

    private static DobConverter<?> DEFAULT_DOBCONVERTER = new DefaultDobConverter();

    private final Fedora fedora;

    private final Map<DmoNamespace, DobConverter<?>> converters = Collections.synchronizedMap(new HashMap<DmoNamespace, DobConverter<?>>());

    /**
     * Create a new FedoraStore.
     */
    public FedoraDmoStore(final String name, final Fedora fedora)// , final DmoContext context, final
                                                                 // DmoFactory factory)
    {
        super(name); // , context, factory);
        this.fedora = fedora;
        addConverters();
    }

    private void addConverters() {
        AbstractDmoFactory.register(new JumpoffDmoFactory());
        addConverter(new JumpoffDmoConverter(this));

        AbstractRelations.setRelationsConverter(new RelationsConverter() {

            @Override
            public String getRdf(Relations relations) throws ObjectSerializationException {
                return FedoraRelationsConverter.relationsToRdf(relations);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getObjectXML(final DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException {
        return getFedora().getObjectManager().getObjectXML(dmoStoreId.getStoreId());
    }

    public boolean exists(DmoStoreId dmoStoreId) throws RepositoryException {
        boolean exists;
        try {
            getObjectXML(dmoStoreId);
            exists = true;
        }
        catch (ObjectNotInStoreException e) {
            exists = false;
        }
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    public String nextSid(final DmoNamespace objectNamespace) throws RepositoryException {
        return getFedora().getObjectManager().nextSid(objectNamespace.getValue());
    }

    @SuppressWarnings("unchecked")
    public String doIngest(final DataModelObject dmo, final String logMessage) throws RepositoryException, ObjectSerializationException,
            DmoStoreEventListenerException, ObjectExistsException
    {
        for (final BinaryUnit binUnit : dmo.getBinaryUnits()) {
            uploadFile(binUnit);
        }

        final DigitalObject dob = getConverter(dmo.getDmoNamespace()).serialize(dmo);

        final String returnId = getFedora().getObjectManager().ingest(dob, logMessage);

        return returnId;
    }

    @SuppressWarnings("unchecked")
    public DateTime doUpdate(final DataModelObject dmo, final boolean skipChangeChecking, final String logMessage) throws DmoStoreEventListenerException,
            RepositoryException
    {
        DateTime updateTime = null;
        int updateCount = 0;

        try {
            // properties
            if (skipChangeChecking || dmo.isDirty()) {
                updateTime = getFedora().getObjectManager().modifyObjectProperties(dmo, logMessage);
            }

            final List<String> existingUnits = listUnits(dmo.getDmoStoreId());
            // binary units
            final List<BinaryUnit> binUnits = dmo.getBinaryUnits();
            for (final BinaryUnit binUnit : binUnits) {
                if (binUnit.hasFile() || binUnit.hasBinaryContent()) {

                    if (updateCount == 0) {
                        beforeUpdate(dmo);
                    }

                    DateTime timestamp = addOrUpdateBinaryUnit(dmo.getDmoStoreId(), logMessage, existingUnits, binUnit);

                    updateCount++;
                    if (timestamp != null) {
                        updateTime = timestamp;
                    }
                }
            }

            // metadata units
            for (final MetadataUnit mdUnit : dmo.getMetadataUnits()) {
                if (skipChangeChecking || mdUnit.isDirty()) {
                    if (updateCount == 0) {
                        beforeUpdate(dmo);
                    }

                    DateTime timestamp = addOrUpdateMetadataUnit(dmo.getDmoStoreId(), logMessage, existingUnits, mdUnit);

                    updateCount++;
                    if (timestamp != null) {
                        updateTime = timestamp;
                    }
                }
            }

            // relations

            // hack: result of business logic implemented in data conversion
            // BEWARE!!! this will remove all DansOntologyNamespace.IS_MEMBER_OF relations,
            // that are not in DmoContainerItemRelations.
            // TODO So get rid of this stupid multi inheritance DisciplineContainer.
            getConverter(dmo.getDmoNamespace()).prepareForUpdate(dmo);
            // /

            final Relations relations = dmo.getRelations();
            if (relations != null) {
                if (skipChangeChecking || relations.isDirty()) {

                    final String rdf = FedoraRelationsConverter.generateRdf(dmo);
                    // System.err.println("this is rdf generated\n" + rdf);

                    if (updateCount == 0)
                        beforeUpdate(dmo);
                    final DateTime timestamp = getFedora().getDatastreamManager().modifyDatastreamByValue(dmo.getStoreId(), FoxConstants.STREAM_ID_EXT,
                            "rels-ext", FoxConstants.RELS_EXT_FORMAT_URI_EXT.toString(), relations, rdf.getBytes(), logMessage);
                    updateCount++;
                    if (timestamp != null) {
                        updateTime = timestamp;
                    }
                }
            }
        }
        catch (final RepositoryException e) {
            informPartialUpdated(dmo);
            throw e;
        }
        catch (XMLSerializationException e) {
            informPartialUpdated(dmo);
            throw new RepositoryException(e);
        }

        return updateTime;
    }

    protected static void printRelations(Relations relations, String when) {
        System.err.println(when);
        System.err.println(relations.size() + " relations " + relations + "\n");
        for (Relation r : relations.getRelation(null, null)) {
            System.err.println(r.toString());
        }
    }

    @Override
    public void addOrUpdateMetadataUnit(DmoStoreId dmoStoreId, MetadataUnit metadataUnit, String logMessage) throws RepositoryException {
        final List<String> existingUnits = listUnits(dmoStoreId);
        try {
            addOrUpdateMetadataUnit(dmoStoreId, logMessage, existingUnits, metadataUnit);
        }
        catch (XMLSerializationException e) {
            throw new RepositoryException(e);
        }
    }

    private DateTime addOrUpdateMetadataUnit(DmoStoreId dmoStoreId, String logMessage, List<String> existingUnits, MetadataUnit mdUnit)
            throws RepositoryException, XMLSerializationException
    {
        DateTime timestamp = null;
        if (existingUnits.contains(mdUnit.getUnitId())) {
            timestamp = getFedora().getDatastreamManager().modifyDatastreamByValue(dmoStoreId.getStoreId(), mdUnit.getUnitId(), mdUnit.getUnitLabel(),
                    mdUnit.getUnitFormat(), mdUnit, logMessage);
        } else {
            getFedora().getDatastreamManager().addDatastream(dmoStoreId.getStoreId(), mdUnit.getUnitId(), null, mdUnit.getUnitLabel(), mdUnit.isVersionable(),
                    FoxConstants.MIMETYPE_XML, mdUnit.getUnitFormat(), new ByteArrayInputStream(mdUnit.asObjectXML()), ControlGroup.X, Datastream.State.A,
                    DatastreamVersion.CONTENT_DIGEST_TYPE.code, null, logMessage);
        }
        return timestamp;
    }

    @Override
    public void addOrUpdateBinaryUnit(DmoStoreId dmoStoreId, BinaryUnit binUnit, String logMessage) throws RepositoryException {
        final List<String> existingUnits = listUnits(dmoStoreId);
        addOrUpdateBinaryUnit(dmoStoreId, logMessage, existingUnits, binUnit);
    }

    private DateTime addOrUpdateBinaryUnit(DmoStoreId dmoStoreId, final String logMessage, final List<String> existingUnits, final BinaryUnit binUnit)
            throws RepositoryException
    {
        uploadFile(binUnit);
        DateTime timestamp = null;

        if (existingUnits.contains(binUnit.getUnitId())) {
            timestamp = getFedora().getDatastreamManager().modifyDatastreamByReference(dmoStoreId.getStoreId(), binUnit.getUnitId(), null,
                    binUnit.getUnitLabel(), binUnit.getMimeType(), null, binUnit.getLocation(), DatastreamVersion.CONTENT_DIGEST_TYPE.code, null, logMessage,
                    false);
        } else {
            getFedora().getDatastreamManager().addDatastream(dmoStoreId.getStoreId(), binUnit.getUnitId(), null, binUnit.getUnitLabel(),
                    binUnit.isVersionable(), binUnit.getMimeType(), null, binUnit.getLocation(),
                    ControlGroup.values()[binUnit.getUnitControlGroup().ordinal()], Datastream.State.A, DatastreamVersion.CONTENT_DIGEST_TYPE.code, null,
                    logMessage);
        }
        return timestamp;
    }

    @SuppressWarnings("unchecked")
    public DataModelObject doRetrieve(final DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException, ObjectDeserializationException {
        final DigitalObject dob = getFedora().getObjectManager().getDigitalObject(dmoStoreId.getStoreId());
        final DataModelObject dmo = AbstractDmoFactory.dmoInstance(dmoStoreId.getStoreId());

        getConverter(new DmoNamespace(dob.getObjectNamespace())).deserialize(dob, dmo);

        return dmo;
    }

    public DateTime doPurge(final DataModelObject dmo, final boolean force, final String logMessage) throws DmoStoreEventListenerException, RepositoryException
    {
        final DateTime purgeTime = getFedora().getObjectManager().purgeObject(dmo.getStoreId(), force, logMessage);

        return purgeTime;
    }

    public JumpoffDmo findJumpoffDmoFor(DataModelObject dmo) throws ObjectNotInStoreException, RepositoryException {
        return findJumpoffDmoFor(dmo.getDmoStoreId());
    }

    public JumpoffDmo findJumpoffDmoFor(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException {
        JumpoffDmo jumpoffDmo = null;
        String dmoObjectRef = FedoraURIReference.create(dmoStoreId.getStoreId());
        String query = createJumpoffQuery(dmoObjectRef);
        try {
            TupleIterator tupleIterator = execSparql(query);
            if (tupleIterator.hasNext()) {
                Map<String, Node> row = tupleIterator.next();
                String subject = row.get("s").toString();
                String subjectStoreId = FedoraURIReference.strip(subject);
                jumpoffDmo = (JumpoffDmo) retrieve(new DmoStoreId(subjectStoreId));
            }
        }
        catch (IOException e) {
            throw new RepositoryException(e);
        }
        catch (TrippiException e) {
            throw new RepositoryException(e);
        }

        return jumpoffDmo;
    }

    protected List<String> listUnits(final DmoStoreId dmoStoreId) throws RepositoryException {
        final List<String> unitIds = new ArrayList<String>();
        final DatastreamDef[] defs = getFedora().getDatastreamAccessor().listDatastreams(dmoStoreId.getStoreId(), null);
        for (final DatastreamDef def : defs) {
            unitIds.add(def.getID());
        }
        return unitIds;
    }

    protected byte[] getBinaryContent(DmoStoreId dmoStoreId, DsUnitId unitId) throws RepositoryException, IOException {
        URL url = getFileURL(dmoStoreId, unitId);
        InputStream inStream = null;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            inStream = url.openStream();
            BufferedInputStream bis = new BufferedInputStream(inStream);

            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }
        }
        catch (IOException e) {
            throw new RepositoryException(e);
        }
        finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        return buf.toByteArray();
    }

    @Override
    public URL getFileURL(final DmoStoreId dmoStoreId, final DsUnitId unitId) {
        return toFedoraURL(dmoStoreId + "/" + unitId.getUnitId());
    }

    @Override
    public URL getFileURL(final DmoStoreId dmoStoreId, final DsUnitId unitId, final DateTime dateTime) {
        final String date = dateTime.toString("YYYY-MM-dd");
        final String time = dateTime.toString("HH:mm:ss.SSS");
        return toFedoraURL(dmoStoreId.getStoreId() + "/" + unitId.getUnitId() + "/" + date + "T" + time);
    }

    private URL toFedoraURL(final String spec) {
        try {
            return new URL(fedora.getBaseURL() + "/get/" + spec);
        }
        catch (final MalformedURLException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * Register the Converters listed in <code>converters</code> to the converters of this Store. Use this method for dependency injection by a framework.
     * 
     * @param converters
     *        converters to register
     */
    public void setConverters(final List<DobConverter<?>> converters) {
        for (final DobConverter<?> converter : converters) {
            addConverter(converter);
        }
    }

    /**
     * Add a converter capable of conversion to and from {@link DigitalObject} and a {@link DataModelObject} type.
     * 
     * @param converter
     *        a converter who's converterId equals the objectNamespace of the DataModelObject type it is converting
     */
    public void addConverter(final DobConverter<?> converter) {
        converters.put(converter.getObjectNamespace(), converter);
        logger.info("Added converter for namespace '" + converter.getObjectNamespace().getValue() + "': " + converter);
    }

    /**
     * Get the converter for the {@link DataModelObject} type of the given objectNamespace.
     * 
     * @param objectNamespace
     *        objectNamespace of DataModelObject corresponds to converterId of converter
     * @return converter for the DataModelObject type
     * @throws RepositoryException
     *         if a converter with a converterId corresponding to the given objectNamespace is not available
     */
    @SuppressWarnings("rawtypes")
    public DobConverter getConverter(final DmoNamespace objectNamespace) throws RepositoryException {
        DobConverter<?> converter;
        converter = converters.get(objectNamespace);
        if (converter == null) {
            logger.debug("No converter for the objectNamespace " + objectNamespace);
            converter = DEFAULT_DOBCONVERTER;;
        }
        return converter;
    }

    /**
     * Get the Fedora of this FedoraStore.
     * 
     * @return Fedora
     */
    protected Fedora getFedora() {
        return fedora;
    }

    protected void uploadFile(final BinaryUnit binUnit) throws RepositoryException {
        try {
            binUnit.prepareForStorage();
        }
        catch (IOException e) {
            throw new RepositoryException(e);
        }
        if (binUnit.hasFile()) {
            final String tmpId = getFedora().getRepository().upload(binUnit.getFile());
            binUnit.setLocation(tmpId);
            binUnit.close();
        }
    }

    public List<DmoStoreId> findSubordinates(DmoStoreId dmoStoreId) throws RepositoryException {
        List<DmoStoreId> subordinates = new ArrayList<DmoStoreId>();
        String dmoObjectRef = FedoraURIReference.create(dmoStoreId.getStoreId());
        String query = createSubordinateQuery(dmoObjectRef);
        try {
            TupleIterator tupleIterator = execSparql(query);
            while (tupleIterator.hasNext()) {
                Map<String, Node> row = tupleIterator.next();
                String subject = row.get("s").toString();
                String subordinateId = FedoraURIReference.strip(subject);
                subordinates.add(new DmoStoreId(subordinateId));
            }
        }
        catch (IOException e) {
            throw new RepositoryException(e);
        }
        catch (TrippiException e) {
            throw new RepositoryException(e);
        }
        return subordinates;
    }

    protected static String createSubordinateQuery(String dmoObjectRef) {
        return new StringBuilder("select ?s from <#ri> where {?s <")//
                .append(RelsConstants.DANS_NS.IS_SUBORDINATE_TO.stringValue())//
                .append("> <")//
                .append(dmoObjectRef)//
                .append("> . }")//
                .toString();
    }

    protected static String createJumpoffQuery(String dmoObject) {
        return new StringBuilder("select ?s from <#ri> where {?s <")//
                .append(RelsConstants.DANS_NS.IS_JUMPOFF_PAGE_FOR.stringValue())//
                .append("> <")//
                .append(dmoObject).append("> . }")//
                .toString();
    }

    protected TupleIterator execSparql(final String query) throws RepositoryException, IOException {
        logger.debug("FedoraStore executing SparQL: " + query);

        final FedoraClient fc = fedora.getRepository().getFedoraClient();

        final Map<String, String> params = new HashMap<String, String>();
        params.put("lang", "sparql");
        params.put("query", query);

        return fc.getTuples(params);
    }

    public List<Relation> getRelations(final String subject, final String predicate, final String object) throws RepositoryException {
        final String query = createRelationQuery(subject, predicate, object);

        TupleIterator tuples = null;
        try {
            tuples = execSparql(query);

            final List<Relation> relations = convertToRelations(subject, predicate, object, tuples);

            logger.debug("getRelations() returning " + relations.size() + " relations");

            return relations;
        }
        catch (final TrippiException e) {
            throw new RepositoryException(e);
        }
        catch (final IOException e) {
            throw new RepositoryException(e);
        }
        finally {
            closeTupleIterator(tuples);
        }
    }

    @Override
    public boolean addRelationship(final DmoStoreId dmoStoreId, final String relationship, final String object, final boolean isLiteral, final String dataType)
            throws RepositoryException
    {
        return getFedora().getRelationshipManager().addRelationship(dmoStoreId.getStoreId(), relationship, object, isLiteral, dataType);
    }

    @Override
    public boolean purgeRelationship(final DmoStoreId dmoStoreId, final String relationship, final String object, final boolean isLiteral, final String dataType)
            throws RepositoryException
    {
        return getFedora().getRelationshipManager().purgeRelationship(dmoStoreId.getStoreId(), relationship, object, isLiteral, dataType);
    }

    @Override
    public List<Relation> getRelations(DmoStoreId dmoStoreId, String predicate) throws RepositoryException {
        return getFedora().getRelationshipManager().getRelations(dmoStoreId.getStoreId(), predicate);
    }

    private List<Relation> convertToRelations(final String subject, final String predicate, final String object, final TupleIterator tuples)
            throws TrippiException
    {
        final List<Relation> relations = new ArrayList<Relation>();
        String resultSubject;
        String resultObject;
        String resultPredicate;
        String resultDatatype;
        boolean resultIsLiteral;

        while (tuples.hasNext()) {
            final Map<String, Node> row = tuples.next();

            resultSubject = subject != null ? subject : FedoraURIReference.strip(row.get("s").toString());
            resultPredicate = predicate != null ? predicate : FedoraURIReference.strip(row.get("p").toString());

            resultIsLiteral = false;
            if (object == null) {
                final Node objectNode = row.get("o");
                resultObject = FedoraURIReference.strip(objectNode.toString());
                resultIsLiteral = objectNode instanceof Literal;
                if (resultIsLiteral)
                    resultDatatype = ((Literal) objectNode).getDatatypeValue().toString();
            } else
                resultObject = object;
            resultDatatype = null;

            relations.add(new Relation(resultSubject, resultPredicate, resultObject, resultIsLiteral, resultDatatype));
        }
        return relations;
    }

    private String createRelationQuery(String subject, String predicate, String object) {
        String query = "select " + (subject == null ? "?s " : "") + (predicate == null ? "?p " : "") + (object == null ? "?o " : "") + "from <#ri> where {"
                + (subject == null ? "?s " : "<" + FedoraURIReference.create(subject) + "> ") + (predicate == null ? "?p " : "<" + predicate + "> ")
                + (object == null ? "?o" : "<" + FedoraURIReference.create(object) + ">") + "}";
        return query;
    }

    public List<DmoStoreId> getSidsByContentModel(DmoStoreId dmoStoreId) throws RepositoryException {
        String query = "select ?s from <#ri> where " + "{ ?s <" + Constants.MODEL.HAS_MODEL.toString() + "> <"
                + FedoraURIReference.create(dmoStoreId.getStoreId()) + "> }";
        TupleIterator tuples = null;
        try {
            tuples = execSparql(query);

            List<DmoStoreId> sids = new ArrayList<DmoStoreId>();

            while (tuples.hasNext()) {
                Map<String, Node> row = tuples.next();
                String sid = row.get("s").toString();
                sid = FedoraURIReference.strip(sid);
                sids.add(new DmoStoreId(sid));
            }

            return sids;
        }
        catch (TrippiException e) {
            throw new RepositoryException(e);
        }
        catch (IOException e) {
            throw new RepositoryException(e);
        }
        finally {
            closeTupleIterator(tuples);
        }
    }

    private void closeTupleIterator(TupleIterator tuples) {
        if (tuples != null) {
            try {
                tuples.close();
            }
            catch (TrippiException e) {
                logger.error("An error occured while closing a TupleIterator. Application will continue operation.", e);
            }
        }
    }

    public DateTime getLastModified(DmoStoreId dmoStoreId) throws RepositoryException {
        return getFedora().getRepository().getLastModified(dmoStoreId.getStoreId());
    }

    @Override
    public List<UnitMetadata> getUnitMetadata(final DmoStoreId dmoStoreId, final DsUnitId unitId) throws RepositoryException {
        return getFedora().getDatastreamManager().getDatastreamMetadata(dmoStoreId.getStoreId(), unitId.getUnitId());
    }

    @Override
    public List<UnitMetadata> getUnitMetadata(DmoStoreId dmoStoreId) throws RepositoryException {
        return getFedora().getDatastreamManager().getDatastreamMetadata(dmoStoreId.getStoreId());
    }

    @Override
    public DateTime purgeUnit(DmoStoreId dmoStoreId, DsUnitId unitId, DateTime creationDate, String logMessage) throws RepositoryException {
        return getFedora().getDatastreamManager().purgeDatastream(dmoStoreId.getStoreId(), unitId.getUnitId(), creationDate, creationDate, false, logMessage);
    }

}
