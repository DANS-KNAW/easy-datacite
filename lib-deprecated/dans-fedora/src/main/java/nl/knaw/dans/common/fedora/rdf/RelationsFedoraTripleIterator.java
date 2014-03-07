package nl.knaw.dans.common.fedora.rdf;

/*
 * ----------------------------------------------------------------------------- <p><b>License and
 * Copyright: </b>The contents of this file are subject to the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of
 * the License at <a href="http://www.fedora-commons.org/licenses">
 * http://www.fedora-commons.org/licenses.</a></p> <p>Software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under the License.</p> <p>The
 * entire file consists of original code.</p> <p>Copyright &copy; 2008 Fedora Commons, Inc.<br />
 * <p>Copyright &copy; 2002-2007 The Rector and Visitors of the University of Virginia and Cornell
 * University<br /> All rights reserved.</p>
 * -----------------------------------------------------------------------------
 */

/*
 * The contents of this file are subject to the license and copyright terms detailed in the license
 * directory at the root of the source tree (also available online at
 * http://fedora-commons.org/license/).
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;

import org.apache.commons.lang.StringUtils;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

import fedora.common.rdf.SimpleLiteral;
import fedora.common.rdf.SimpleTriple;
import fedora.common.rdf.SimpleURIReference;

public class RelationsFedoraTripleIterator extends TripleIterator
{

    int size = 0;

    int index = 0;

    List<Relation> m_TupleArray = null;

    Map<String, String> m_map = null;

    // 0 references in workspace on 2010-06-01
    public RelationsFedoraTripleIterator(List<Relation> array, Map<String, String> map)
    {
        m_TupleArray = array;
        size = array.size();
        m_map = map;
    }

    public RelationsFedoraTripleIterator(List<Relation> array)
    {
        m_TupleArray = array;
        size = array.size();
        m_map = new HashMap<String, String>();
        m_map.put("rel", "info:fedora/fedora-system:def/relations-external#");
        m_map.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    }

    @Override
    public boolean hasNext() throws TrippiException
    {
        return index < size;
    }

    @Override
    public Triple next() throws TrippiException
    {
        Relation relation = m_TupleArray.get(index++);
        try
        {
            FedoraURIReference subjectRef = new FedoraURIReference(new URI(relation.subject));
            PredicateNode predicateNode = makePredicateResourceFromRel(relation.predicate, m_map);
            ObjectNode object = makeObjectFromURIandLiteral(relation);

            Triple triple = new SimpleTriple(subjectRef, predicateNode, object);
            return triple;
        }
        catch (URISyntaxException e)
        {
            throw new TrippiException("Invalid URI in Triple [relation=" + relation.toString() + "]", e);
        }
    }

    public static ObjectNode makeObjectFromURIandLiteral(Relation relation) throws URISyntaxException, TrippiException
    {
        ObjectNode oNode = null;
        if (relation.isLiteral)
        {
            if (StringUtils.isBlank(relation.datatype))
            {
                String objString = (String) relation.object;
                oNode = new SimpleLiteral(objString, new URI(RelsConstants.RDF_LITERAL));
            }
            else
            {
                String objString = (String) relation.object;
                oNode = new SimpleLiteral(objString, new URI(relation.datatype));
            }
        }
        else if (relation.object instanceof String)
        {
            String objString = (String) relation.object;
            oNode = new FedoraURIReference(new URI(objString));
        }
        else if (relation.object instanceof URI)
        {
            URI uri = (URI) relation.object;
            oNode = new SimpleURIReference(uri);
        }
        return oNode;
    }

    public static PredicateNode makePredicateResourceFromRel(String predicate, Map<String, String> map) throws URISyntaxException
    {
        URI predURI = makePredicateFromRel(predicate, map);
        PredicateNode node = new SimpleURIReference(predURI);
        return node;
    }

    public static URI makePredicateFromRel(String relationship, Map map) throws URISyntaxException
    {
        String predicate = relationship;
        Set keys = map.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext())
        {
            String key = (String) iter.next();
            if (predicate.startsWith(key + ":"))
            {
                predicate = predicate.replaceFirst(key + ":", (String) map.get(key));
            }
        }

        URI retVal = null;
        retVal = new URI(predicate);
        return retVal;
    }

    @Override
    public void close() throws TrippiException
    {

    }
}
