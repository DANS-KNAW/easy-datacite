package nl.knaw.dans.easy.tools.task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.easymock.Capture;

/**
 * PowerMock extension, copied from http://tech.groups.yahoo.com/group/easymock/message/1467
 */
public class CaptureSet<T> extends Capture<T> {
    private static final long serialVersionUID = 5081794637753112205L;
    private final List<T> capturedValues = new LinkedList<T>();

    @Override
    public void reset() {
        capturedValues.clear();
    }

    @Override
    public void setValue(T value) {
        capturedValues.add(value);
    };

    @Override
    public T getValue() {
        throw new UnsupportedOperationException("use verify() instead");
    }

    /**
     * @return true if the capture contains all the specified items
     */
    public boolean verify(T... items) {
        return capturedValues.size() == items.length && capturedValues.containsAll(Arrays.asList(items));
    }
}
