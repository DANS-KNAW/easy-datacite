package nl.knaw.dans.common.lang.repo.relations;

import java.io.Serializable;

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

/**
 * This class was gotten from the Fedora 3.2.1 source.
 * 
 * A data structure for holding relationships.
 * 
 * AND YOU SHOULD NOT GET CODE OR WRITE CODE WITHOUT TESTING IT THOROUGHLY.
 * ESPECIALLY THE EQUALS AND HASHCODE METHODS! (HB)
 * 
 * @author Robert Haschart
 */
public class Relation implements Serializable
{

    private static final long serialVersionUID = 3072268374381915400L;

    public final String subject;

    public final String predicate;

    public final Object object;

    public final boolean isLiteral;

    public final String datatype;

    public Relation(String subject, String predicate, Object object, boolean isLiteral, String datatype)
    {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.isLiteral = isLiteral;
        this.datatype = datatype;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getPredicate()
    {
        return predicate;
    }

    public Object getObject()
    {
        return object;
    }

    public boolean isLiteral()
    {
        return isLiteral;
    }

    public String getDatatype()
    {
        return datatype;
    }

    @Override
    public String toString()
    {
        String retVal = "Sub: " + subject + "  Pred: " + predicate + "  Obj: [" + object + ", " + isLiteral + ", " + datatype + "]";
        return retVal;
    }

    @Override
    public int hashCode()
    {
        return hc(subject) + hc(predicate) + hc(object) + hc(datatype);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Relation)
        {
            Relation t = (Relation) o;
            return eq(subject, t.subject) && eq(predicate, t.predicate) && eq(object, t.object) && eq(datatype, t.datatype) && isLiteral == t.isLiteral;
        }
        else
        {
            return false;
        }
    }

    // test for equality, accounting for null values
    private static boolean eq(Object a, Object b)
    {
        if (a == null && b == null)
        {
            return true;
        }
        else
        {
            if (a == null)
            {
                return false;
            }
            else if (b == null)
            {
                return false;
            }
            else if ((a instanceof String) && (b instanceof String))
            {
                return RelsConstants.getObjectURI((String) a).equals(RelsConstants.getObjectURI((String) b));
            }
        }
        return a.equals(b);
    }

    // return the hashCode or 0 if null
    private static int hc(Object o)
    {
        if (o == null)
        {
            return 0;
        }
        else
        {
            if (o instanceof String)
            {
                String s = (String) o;
                return RelsConstants.getObjectURI(s).hashCode();
            }
            else
            {
                return o.hashCode();
            }
        }
    }
}
