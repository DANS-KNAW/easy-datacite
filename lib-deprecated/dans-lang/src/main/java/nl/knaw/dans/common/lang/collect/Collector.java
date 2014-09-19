package nl.knaw.dans.common.lang.collect;

/**
 * Generic interface for collecting things.
 * 
 * @param <T>
 *        the type or container type for collected things.
 */
public interface Collector<T> {

    T collect() throws CollectorException;

}
