package nl.knaw.dans.common.fedora;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.relations.Relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.RelationshipTuple;

/**
 * Implements relationship management methods on the APIM interface. The relationship management methods manipulate the content of the RELS-EXT and RELS-INT
 * datastreams. The datastream to be modified is determined from the subject of the relationship, ie a subject of info:fedora/demo:333 will result in changes to
 * RELS-EXT in demo:333, and a subject of info:fedora/demo:333/DS1 will result in changes to RELS-INT in demo:333. These modifications will also be propagated
 * to the Resource Index if it is enabled.
 * <p/>
 * In defiance of http://fedora-commons.org/confluence/display/FCR30/API-M#API-M-RelationshipManagement
 * only valid form of subject is storeId of existing object i.e.: <code>test:123</code>.
 * 
 * @author ecco
 */
public class RelationshipManager
{

    private static final Logger logger = LoggerFactory.getLogger(RelationshipManager.class);

    private final Repository repository;

    /**
     * Constructs a new RelationshipManager with the given Repository as target.
     * 
     * @param repository
     *        Repository to manage
     */
    public RelationshipManager(Repository repository)
    {
        this.repository = repository;
    }

    /**
     * Creates a new relationship in the object. Adds the specified relationship to the object's RELS-EXT or RELS-INT Datastream. If the Resource Index is
     * enabled, the relationship will be added to the Resource Index.
     * 
     * @param subject
     *        The subject. (Only valid form: storeId of existing object i.e. test:123)
     * @param relationship
     *        The predicate
     * @param object
     *        The object (target)
     * @param isLiteral
     *        A boolean value indicating whether the object is a literal
     * @param dataType
     *        The datatype of the literal. Optional
     * @return True if and only if the relationship was added
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public boolean addRelationship(String subject, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException
    {
        boolean success = false;
        try
        {
            success = repository.getFedoraAPIM().addRelationship(subject, relationship, object, isLiteral, dataType);
            if (logger.isDebugEnabled())
            {
                logger.debug("Added relationship: subject=" + subject + " relationship=" + relationship + " object=" + object);
            }
        }
        catch (RemoteException e)
        {
            String msg = "Unable to add a relationship: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return success;
    }

    /**
     * Get the relationships asserted in the object's RELS-EXT or RELS-INT Datastream that match the given criteria.
     * 
     * @param subject
     *        The subject. (Only valid form: storeId of existing object i.e. test:123)
     * @param relationship
     *        The predicate to match. A null value matches all predicates.
     * @return RelationshipTuple[]
     *         <ul>
     *         <li>String subject - The subject of the relation. Either a Fedora object URI (eg info:fedora/demo:333) or a datastream URI (eg
     *         info:fedora/demo:333/DS1).</li>
     *         <li>String predicate - The predicate relating the subject and the object. Includes the namespace of the relation.</li>
     *         <li>String object - The URI of the object (target) of the relation</li>
     *         <li>boolean isLiteral - If true, the subject should be read as a literal value, not a URI</li>
     *         <li>String datatype - If the subject is a literal, the datatype to parse the value as. Optional.</li>
     *         </ul>
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public RelationshipTuple[] getRelationShips(String subject, String relationship) throws RepositoryException
    {
        RelationshipTuple[] tuples = null;
        try
        {
            tuples = repository.getFedoraAPIM().getRelationships(subject, relationship);
        }
        catch (RemoteException e)
        {
            String msg = "Unable to get relationships: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return tuples;
    }

    public List<Relation> getRelations(String storeId, String relationship) throws RepositoryException
    {
        List<Relation> relations = new ArrayList<Relation>();
        RelationshipTuple[] tuples = getRelationShips(storeId, relationship);
        for (RelationshipTuple tuple : tuples)
        {
            relations.add(new Relation(tuple.getSubject(), tuple.getPredicate(), tuple.getObject(), tuple.isIsLiteral(), tuple.getDatatype()));
        }
        return relations;
    }

    /**
     * Delete the specified relationship. This method will remove the specified relationship(s) from the RELS-EXT or RELS-INT datastream. If the Resource Index
     * is enabled, this will also delete the corresponding triples from the Resource Index.
     * 
     * @param subject
     *        The subject. (Only valid form: storeId of existing object i.e. test:123)
     * @param relationship
     *        The predicate
     * @param object
     *        The object
     * @param isLiteral
     *        A boolean value indicating whether the object is a literal.
     * @param dataType
     *        The datatype of the literal. Optional.
     * @return True if and only if the relationship was purged.
     * @throws RepositoryException
     *         as the common base class for checked exceptions
     */
    public boolean purgeRelationship(String subject, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException
    {
        boolean success = false;
        try
        {
            success = repository.getFedoraAPIM().purgeRelationship(subject, relationship, object, isLiteral, dataType);
            if (logger.isDebugEnabled())
            {
                logger.debug("Purged relationship: subject=" + subject + " relationship=" + relationship + " object=" + object);
            }
        }
        catch (RemoteException e)
        {
            String msg = "Unable to purge a relationship: ";
            logger.debug(msg, e);
            Repository.mapRemoteException(msg, e);
        }
        return success;
    }

}
