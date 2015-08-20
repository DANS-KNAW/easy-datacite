package nl.knaw.dans.easy.tools.util;

import java.lang.reflect.Field;

import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

/**
 * @param <O>
 *        the object containing F
 * @param <F>
 *        a field of O, the field might be inaccessible at compile time
 */
public class FieldAccess<O, F> {
    private final Field field;
    private final AbstractTask reportingClass;

    public FieldAccess(final Class<? extends O> fieldContainerClass, final String fieldName, final AbstractTask reportingClass) throws TaskException {
        this.reportingClass = reportingClass;
        try {
            this.field = fieldContainerClass.getDeclaredField(fieldName);
            this.field.setAccessible(true);
        }
        catch (final SecurityException e) {
            throw new TaskException(e, reportingClass);
        }
        catch (final NullPointerException e) {
            throw new TaskException(e, reportingClass);
        }
        catch (NoSuchFieldException e) {
            throw new TaskException(e, reportingClass);
        }
    }

    public void set(final O object, final F value) throws TaskException {
        try {
            field.set(object, value);
        }
        catch (final IllegalArgumentException e) {
            throw new TaskException(e, reportingClass);
        }
        catch (final IllegalAccessException e) {
            throw new TaskException(e, reportingClass);
        }
    }

    /**
     * If a field only lacks a setter, using the reflected getter too makes sure you get and set the same field.
     */
    @SuppressWarnings("unchecked")
    public F get(final O object) throws TaskException {
        // fields may have getters, but now we can make sure to get and set the same field
        try {
            return (F) field.get(object);
        }
        catch (final IllegalArgumentException e) {
            throw new TaskException(e, reportingClass);
        }
        catch (final IllegalAccessException e) {
            throw new TaskException(e, reportingClass);
        }
        catch (final ClassCastException e) {
            throw new TaskException(e, reportingClass);
        }
    }
}
