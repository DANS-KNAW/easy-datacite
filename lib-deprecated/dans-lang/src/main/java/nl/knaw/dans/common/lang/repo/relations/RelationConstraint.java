package nl.knaw.dans.common.lang.repo.relations;

import nl.knaw.dans.common.lang.repo.collections.DmoCollectionMember;

/**
 * Describes the constraints between relations subject /object. The
 * predicate is currently not stored in here, because it is assumed
 * the relation constraints are used for specific kinds of relations.
 * Thus the predicate should be clear. Maybe lateron it might proof
 * useful to store the predicate in here as well.
 *  
 * @author lobo
 */
public interface RelationConstraint
{

    public abstract Class<? extends DmoCollectionMember> getSubject();

    public abstract Class<? extends DmoCollectionMember> getObject();

    public abstract int getObjectCardinality();

    public abstract int getSubjectCardinality();

}
