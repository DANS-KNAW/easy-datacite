package nl.knaw.dans.common.lang.search.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;

/**
 * The bean can have the list (multi valued),
 * while the index has a comma separated string (single value)
 *
 * @author paulboon
 *
 */
public class StringListCollapserConverter implements SearchFieldConverter<List<String>>
{

    public List<String> fromFieldValue(Object in) throws SearchBeanConverterException
    {
        if (!(in instanceof String))
            throw new SearchBeanConverterException("Expected String argument but found " + in.getClass().getSimpleName());

        List<String> list = splitCommaSeparated((String) in);

        // unescape the escaped characters
        for (int i = 0; i < list.size(); i++)
        {

            // unescape ','
            // Note: every ',' must be escaped,
            // otherwise it would have been removed by the splitting
            String unescaped = list.get(i).replace("\\,", ",");

            // unescape '\\'
            unescaped = unescaped.replace("\\\\", "\\");

            list.set(i, unescaped);
        }

        return list;
    }

    public Object toFieldValue(List<String> in) throws SearchBeanConverterException
    {
        String result = "";

        Iterator<String> inIter = in.iterator();
        while (inIter.hasNext())
        {
            String escaped = inIter.next();

            // escape '\'
            escaped = escaped.replace("\\", "\\\\");

            // escape comma's
            escaped = escaped.replace(",", "\\,");

            result += escaped;

            if (inIter.hasNext())
                result += ",";
        }

        return result;
    }

    /* helper functions below, could be moved to some String Util package? */

    /**
     * split, but take care of escaped comma's and even escaped escapes
     */
    public List<String> splitCommaSeparated(final String input)
    {
        List<String> result = new ArrayList<String>();

        if (input == null || input.length() == 0)
        {
            result.add("");
            return result;
        }

        int beginIndex = 0;
        for (int i = 0; i < input.length(); i++)
        {
            if (input.charAt(i) == ',')
            {
                if (isEscaped(input, i))
                    continue;

                result.add(input.substring(beginIndex, i));
                beginIndex = i + 1;
                // handle the case that the last string is empty
                if (i == input.length() - 1) // last index
                    result.add("");
            }
            else if (i == input.length() - 1) // last index
            {
                // remainder
                result.add(input.substring(beginIndex));
            }
        }

        return result;
    }

    /**
     * determine if a character is escaped by '\'
     * because the escape char can be escaped  we
     * determine if the position is preceded by an uneven number of slashes.
     *
     * @param str
     * @param pos
     * @return
     */
    public boolean isEscaped(final String str, final int pos)
    {
        // count backwards from the index the number of escape chars (continues)
        int numEsc = 0;

        while (pos - numEsc > 0 && // avoid out of bounds
                str.charAt(pos - 1 - numEsc) == '\\')
        {
            numEsc++;
        }

        return (numEsc % 2 == 1); // odd
    }
}
