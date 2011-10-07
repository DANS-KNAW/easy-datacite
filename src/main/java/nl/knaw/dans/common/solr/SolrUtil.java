package nl.knaw.dans.common.solr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import nl.knaw.dans.common.lang.util.Range;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class SolrUtil
{

	public static String escapeColon(String str)
	{
		String result;
		//http://lucene.apache.org/java/2_3_2/queryparsersyntax.html#Escaping%20Special%20Characters
		result =  str.replaceAll(":", "\\\\:");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	static public Object prepareObjectForSolrJ(Object in)
	{
		if (in instanceof Collection)
		{
			Collection<Object> inCollection = (Collection<Object>) in;
			if (((Collection) in).size() == 0) return in;
			Collection<Object> newCollection = new ArrayList<Object>(inCollection.size());
			Iterator<Object> i = inCollection.iterator();
			while(i.hasNext())
			{
				Object nextIn  = i.next();
				newCollection.add(prepareObjectForSolrJ(nextIn) );
			}
			return newCollection;
		}
		if (in instanceof DateTime)
		{
			return toString((DateTime) in);
		}
		else if (in instanceof Date)
		{
			return toString((Date) in);
		}
		else if (in instanceof Range<?>)
        {
            return toString((Range<?>) in);
        }
        
		return in;
	}
	
	public static String toString(final Object o)
	{
		return prepareObjectForSolrJ( o ).toString();
	}
	
	public static String toString(final DateTime d)
	{
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		DateTime dUtc = d.toDateTime(DateTimeZone.UTC);
		return fmt.print(dUtc);
	}
	
	public static String toString(final Date d)
	{
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    format.setTimeZone(TimeZone.getTimeZone("UTC"));
	    return format.format(d);
	}
	
    /**
     * Support for range values (resulting in range queries)
     */
    public static String toString(final Range<?> range)
    {
        String startQueryString = "";
        if (range.getStart() == null)
            startQueryString = "*";
        else
            startQueryString = range.getStart().toString();

        String endQueryString = "";
        if (range.getEnd() == null)
            endQueryString = "*";
        else
            endQueryString = range.getEnd().toString();

        return "[" + startQueryString + " TO " + endQueryString + "]";
    }
}
