package nl.knaw.dans.common.lang.repo.relations;

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

import org.jrdf.graph.TypedNodeVisitor;
import org.jrdf.graph.URIReference;

/**
 *  This class was gotten from the Fedora 3.2.1 source.
 *  
 * A URIReference from a known namespace.
 */
public class RelationName implements URIReference
{

    private static final long serialVersionUID = 1L;

    public final OntologyNamespace namespace;

    public final String localName;

    public final String uri;

    public final String qName;

    private URI m_uri;

    public RelationName(OntologyNamespace namespace, String localName)
    {
        try
        {
            this.namespace = namespace;
            this.localName = localName;
            uri = namespace.uri + localName;
            qName = namespace.prefix + ":" + this.localName;
            m_uri = new URI(uri);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Bad URI Syntax", e);
        }
    }

    /**
     * Does the given string loosely match this name? Either: 1) It matches
     * localName (case insensitive) 2) It matches uri (case sensitive) if
     * (firstLocalNameChar == true): 3) It is one character long, and that
     * character matches the first character of localName (case insensitive)
     */
    public boolean looselyMatches(String in, boolean tryFirstLocalNameChar)
    {
        if (in == null || in.length() == 0)
        {
            return false;
        }
        if (in.equalsIgnoreCase(localName))
        {
            return true;
        }
        if (in.indexOf(localName) != -1)
        {
            return true;
        }
        if (in.equals(uri))
        {
            return true;
        }
        if (tryFirstLocalNameChar && in.length() == 1 && in.toUpperCase().charAt(0) == localName.toUpperCase().charAt(0))
        {
            return true;
        }
        return false;
    }

    //
    // Implementation of the URIReference interface
    //

    public void accept(TypedNodeVisitor visitor)
    {
        visitor.visitURIReference(this);
    }

    public URI getURI()
    {
        return m_uri;
    }

    @Override
    public String toString()
    {
        return uri;
    }

    public String stringValue()
    {
        return toString();
    }

    public String getLocalName()
    {
        return localName;
    }

    public String getNamespace()
    {
        return namespace.toString();
    }

}
