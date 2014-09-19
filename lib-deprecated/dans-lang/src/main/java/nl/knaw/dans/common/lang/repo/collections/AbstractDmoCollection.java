package nl.knaw.dans.common.lang.repo.collections;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.relations.RelationConstraint;

public abstract class AbstractDmoCollection extends AbstractDataModelObject implements DmoCollection {
    private static final long serialVersionUID = 8459311783779671871L;

    private List<RelationConstraint> constraints = new ArrayList<RelationConstraint>();

    /*
     * public AbstractDmoCollection() { super(); }
     */

    public AbstractDmoCollection(String storeId) {
        super(storeId);
    }

    protected void addRelationConstraint(int sCardinality, Class<? extends DmoCollectionMember> subject, int oCardinality,
            Class<? extends DmoCollectionMember> object)
    {
        constraints.add(new DmoCollectionMemberConstraint(sCardinality, subject, oCardinality, object));
    }

    public RelationConstraint getRelationConstraint(Class<? extends DmoCollectionMember> subject, Class<? extends DmoCollectionMember> object) {

        for (RelationConstraint constraint : constraints) {
            if (ClassUtil.instanceOf(subject, constraint.getSubject()) && ClassUtil.instanceOf(object, constraint.getObject()))
                return constraint;
            else if (ClassUtil.instanceOf(object, constraint.getSubject()) && ClassUtil.instanceOf(subject, constraint.getObject())) {
                // return the object in reverse order
                return new DmoCollectionMemberConstraint(constraint.getObjectCardinality(), constraint.getObject(), constraint.getSubjectCardinality(), subject);
            }

        }

        return null;
    }

}
