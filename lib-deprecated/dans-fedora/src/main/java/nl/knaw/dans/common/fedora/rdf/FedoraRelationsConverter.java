package nl.knaw.dans.common.fedora.rdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.fedora.fox.FoxConstants;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;

import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

public class FedoraRelationsConverter {

    // WARNING: will return an empty String ("") if no relations are present in the dmo (anymore).
    // Fedora ignores the empty String when calling modifyDatastreamByValue, leaving already persisted
    // relations intact.
    public static String generateRdf(DataModelObject dataModelObject) throws ObjectSerializationException {
        AbstractRelations<?> dmoRelations = (AbstractRelations<?>) dataModelObject.getRelations();
        List<Relation> relationList = new ArrayList<Relation>();

        Set<String> contentModels = dataModelObject.getContentModels();
        if (contentModels != null) {
            for (String contentModel : contentModels) {
                if (dmoRelations == null) {
                    Relation t = new Relation(dataModelObject.getStoreId(), FoxConstants.MODEL_ONTOLOGY.HAS_MODEL.toString(), contentModel, false, null);
                    relationList.add(t);
                } else {
                    dmoRelations.addRelation(FoxConstants.MODEL_ONTOLOGY.HAS_MODEL.toString(), RelsConstants.getObjectURI(contentModel));
                }
            }
        }

        if (dmoRelations != null) {
            // make shallow copy of the relations
            relationList.addAll(dmoRelations.getRelation(null, null));
        }

        return relationsToRdf(relationList);
    }

    public static String relationsToRdf(Relations relations) throws ObjectSerializationException {
        List<Relation> relList = new ArrayList<Relation>(relations.getRelation(null, null));
        return relationsToRdf(relList);
    }

    public static String relationsToRdf(List<Relation> relationList) throws ObjectSerializationException {
        String rdf = "";
        if (relationList.size() > 0) {
            try {
                RelationsFedoraTripleIterator iter = new RelationsFedoraTripleIterator(relationList);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                iter.toStream(os, RDFFormat.RDF_XML, false);
                rdf = new String(os.toByteArray());
            }
            catch (TrippiException e) {
                throw new ObjectSerializationException(e);
            }
        }
        return rdf;
    }

    public static Set<Relation> rdfToRelations(String rdf) throws ObjectDeserializationException {
        ByteArrayInputStream xmlContentsStream = new ByteArrayInputStream(rdf.getBytes());

        Set<Relation> result = new HashSet<Relation>();
        try {
            TripleIterator iter = TripleIterator.fromStream(xmlContentsStream, RDFFormat.RDF_XML);

            for (int i = 0; iter.hasNext(); i++) {
                Triple triple = iter.next();

                ObjectNode oNode = triple.getObject();
                String subject = FedoraURIReference.strip(triple.getSubject().toString());
                String predicate = triple.getPredicate().toString();
                if (oNode instanceof Literal) {
                    Literal literal = (Literal) oNode;
                    URI typeURI = literal.getDatatypeURI();
                    String datatype = typeURI == null ? RelsConstants.RDF_LITERAL : typeURI.toString();
                    Object object = literal.getValue();
                    result.add(new Relation(subject, predicate, (String) object, true, datatype));
                } else {
                    String object = oNode.toString();
                    result.add(new Relation(FedoraURIReference.strip(subject), predicate, FedoraURIReference.strip(object), false, null));
                }
            }
        }
        catch (TrippiException e) {
            throw new ObjectDeserializationException(e);
        }
        return result;
    }

}
