package nl.knaw.dans.easy.web.fileexplorer;

import java.util.Collections;
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
    private String string;

    ReadableValues(Set<?> values) {
        List<String> strings = getSortedStrings(values);
        String[] ss = new String[strings.size()];
        int i = 0;
        for (String s : strings) {
            ss[i++] = StringUtils.capitalize(s.toString().replace('_', ' ').toLowerCase());
        }
        string = StringUtils.join(ss, ", ");
    }

    private List<String> getSortedStrings(Set<?> values) {
        List<String> strings = new LinkedList<String>();
        for (Object v : values) {
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
