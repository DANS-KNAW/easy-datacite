package nl.knaw.dans.common.lang.collect;

/**
 * Generic interface for decorating a {@link Collector}.
 *
 * @param <T> the type or container type for collected things.
 */
public interface CollectorDecorator<T> extends Collector<T>
{

}
