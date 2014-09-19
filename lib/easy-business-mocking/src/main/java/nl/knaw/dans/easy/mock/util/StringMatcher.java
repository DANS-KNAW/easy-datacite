package nl.knaw.dans.easy.mock.util;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

public class StringMatcher implements IArgumentMatcher {
    private final Object expected;

    public StringMatcher(final Object expected) {
        this.expected = expected;
    }

    /**
     * Matches if both are null objects or the toString values of the objects are equal. If both toString values start with the result of
     * {@link Object#toString()}, that part is ignored in the comparison.
     * 
     * @param expected
     * @return
     */
    public static <T extends Object> T eq(final T expected) {
        EasyMock.reportMatcher(new StringMatcher(expected));
        return null;
    }

    @Override
    public void appendTo(final StringBuffer buffer) {
        buffer.append("toStringEq(" + expected + ")");
    }

    @Override
    public boolean matches(final Object actual) {
        if (expected == null || actual == null)
            return (expected == null) && (actual == null);
        if (startsWithClass(expected) && startsWithClass(actual))
            return toString(expected).equals(toString(actual));
        return expected.toString().equals(actual.toString());
    }

    private String toString(final Object object) {
        return object.toString().replaceFirst("[^ ]+", "");
    }

    private boolean startsWithClass(final Object object) {
        return object.toString().startsWith(object.getClass().getName() + "@");
    }
}
