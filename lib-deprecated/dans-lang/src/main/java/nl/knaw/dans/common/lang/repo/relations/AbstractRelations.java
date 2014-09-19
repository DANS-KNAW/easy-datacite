package nl.knaw.dans.common.lang.repo.relations;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.AbstractTimestampedObject;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.exception.InvalidRelationshipException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple relations object with setters and getters.
 * 
 * @author lobo
 */
public abstract class AbstractRelations<T extends DataModelObject> extends AbstractTimestampedObject implements Relations {
    private static final long serialVersionUID = 8070049307657543793L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractRelations.class);

    private static RelationsConverter converter;

    private Set<Relation> relations = new HashSet<Relation>();

    private T subject;

    public static void setRelationsConverter(RelationsConverter converter) {
        AbstractRelations.converter = converter;
    }

    public AbstractRelations(T subject) {
        this.subject = subject;
    }

    public T getSubject() {
        return subject;
    }

    public void setSubject(T subject) {
        this.subject = subject;
    }

    public void setRelationships(Set<Relation> relationships) throws InvalidRelationshipException {
        clear();
        for (Relation t : relationships) {
            addRelation(t);
        }
        setDirty(true);
    }

    @Override
    public int size() {
        return relations.size();
    }

    public void addRelation(Relation relation) throws InvalidRelationshipException {
        relations.add(new Relation(subject.getStoreId(), relation.predicate, relation.object, relation.isLiteral, relation.datatype));
        setDirty(true);
    }

    public void addRelation(String predicate, Object object) {
        relations.add(new Relation(subject.getStoreId(), predicate, object, false, null));
    }

    public void addRelation(RelationName relationName, Object object) {
        addRelation(relationName.uri, object);
    }

    public void addRelation(String predicate, String object, String dataType) {
        relations.add(new Relation(subject.getStoreId(), predicate, object, true, dataType));
    }

    public void addRelation(RelationName relationName, String object, String dataType) {
        addRelation(relationName.uri, object, dataType);
    }

    public void removeRelation(String predicate, String object) {
        Set<Relation> foundRelations = getRelation(predicate, object);

        for (Relation removeRelation : foundRelations) {
            relations.remove(removeRelation);
        }
    }

    public void removeRelation(RelationName relationName, String object) {
        removeRelation(relationName.uri, object);
    }

    public Set<Relation> getRelation(String predicate, String object) {
        if (predicate == null && object == null)
            return relations;

        Set<Relation> foundRels = new HashSet<Relation>();

        for (Relation t : relations) {
            if (match(t, predicate, object)) {
                foundRels.add(t);
            }
        }

        return foundRels;
    }

    public boolean match(Relation t, String predicate, String object) {
        int foundCount = 0;
        int notNullCount = 0;
        if (predicate != null) {
            notNullCount++;
            if (predicate.equals(t.predicate) || RelsConstants.stripFedoraUri(predicate).equals(t.predicate)
                    || predicate.equals(RelsConstants.stripFedoraUri(t.predicate)))
                foundCount++;
        }
        if (object != null) {
            notNullCount++;

            if (object.equals(t.object) || RelsConstants.stripFedoraUri(object).equals(t.object) || object.equals(RelsConstants.stripFedoraUri(t.object)))
                foundCount++;
        }
        return notNullCount == foundCount;
    }

    public boolean hasRelation(String predicate, String object) {
        if (predicate == null && object == null)
            return relations.size() > 0;

        for (Relation t : relations) {
            if (match(t, predicate, object)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        if (relations.size() > 0) {
            relations.clear();
            setDirty(true);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relations == null) ? 0 : relations.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractRelations<?> other = (AbstractRelations<?>) obj;
        if (relations == null) {
            if (other.relations != null)
                return false;
        } else if (!relations.equals(other.relations))
            return false;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        return true;
    }

    @Override
    public String getRdf() throws ObjectSerializationException {
        if (converter == null) {
            logger.warn("No RelationsConverter set.");
            throw new ObjectSerializationException("No converter set on " + AbstractRelations.class.getName());
        } else {
            return converter.getRdf(this);
        }
    }
}
