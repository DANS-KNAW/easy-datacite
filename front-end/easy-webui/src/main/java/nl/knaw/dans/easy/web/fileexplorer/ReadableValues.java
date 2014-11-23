package nl.knaw.dans.easy.web.fileexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Takes a {@link Set} of values and does the following:
 * <ol>
 * <li>Calls {@link Object#toString()} on each of them</li>
 * <li>Capitalizes the first character of each value and lowercases the rest</li>
 * <li>Sorts the values</li>
 * <li>Replaces all underscore characters with a space</li>
 * <li>Joins all values together with comma's in between</li>
 * </ol>
 */
public class ReadableValues {
    private final String string;

    ReadableValues(final Set<?> values) {
        final List<String> strings = getSortedStrings(values);
        final List<String> readableStrings = new ArrayList<String>(strings.size());
        final Iterator<String> i = strings.iterator();
        while (i.hasNext()) {
            final String s = i.next();
            readableStrings.add(StringUtils.capitalize(s.replace('_', ' ').toLowerCase()));
        }
        string = StringUtils.join(readableStrings, ", ");
    }

    private List<String> getSortedStrings(final Set<?> values) {
        final List<String> strings = new LinkedList<String>();
        for (final Object v : values) {
            strings.add(v.toString());
        }
        Collections.sort(strings);
        return strings;
    }

    @Override
    public String toString() {
        return string;
    }
}
