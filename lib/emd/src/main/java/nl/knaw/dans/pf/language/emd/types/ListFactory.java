package nl.knaw.dans.pf.language.emd.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for lists of emd types.
 * 
 * @author ecco
 */
public final class ListFactory
{

    private ListFactory()
    {
        // never instantiate.
    }

    // ecco: CHECKSTYLE: OFF
    // Method used by JiBX serialization.
    static synchronized List<Author> authorList()
    {
        return new ArrayList<Author>();
    }

    // Method used by JiBX serialization.
    static synchronized List<BasicDate> basicDateList()
    {
        return new ArrayList<BasicDate>();
    }

    // Method used by JiBX serialization.
    static synchronized List<BasicIdentifier> basicIdentifierList()
    {
        return new ArrayList<BasicIdentifier>();
    }

    // Method used by JiBX serialization.
    static synchronized List<BasicString> basicStringList()
    {
        return new ArrayList<BasicString>();
    }

    // Method used by JiBX serialization.
    static synchronized List<IsoDate> isoDateList()
    {
        return new ArrayList<IsoDate>();
    }

    // Method used by JiBX serialization.
    static synchronized List<Relation> relationList()
    {
        return new ArrayList<Relation>();
    }

    // Method used by JiBX serialization.
    static synchronized List<Spatial> spatialList()
    {
        return new ArrayList<Spatial>();
    }

    // Method used by JiBX serialization.
    static synchronized List<BasicRemark> basicRemarkList()
    {
        return new ArrayList<BasicRemark>();
    }

}
