package nl.knaw.dans.common.wicket.components.editablepanel;

import org.apache.wicket.model.IModel;

/**
 * An <code>IModel</code> implementation that forwards all calls to a decorated <code>IModel</code>. Intended to be subclassed by decorators that need to
 * override only part of the IModel interface.
 * 
 * @param <T>
 *        the type parameter of the decorated <code>IModel</code> object
 */
public class DefaultIModelDecorator<T> implements IModel<T> {
    private static final long serialVersionUID = -9222209948850020288L;

    protected final IModel<T> decoratee;

    /**
     * Constructs a new {@link DefaultIModelDecorator}.
     * 
     * @param decoratee
     *        the <code>IModel</code> object being decorated
     */
    protected DefaultIModelDecorator(IModel<T> decoratee) {
        this.decoratee = decoratee;
    }

    @Override
    public void detach() {
        decoratee.detach();
    }

    @Override
    public T getObject() {
        return decoratee.getObject();
    }

    @Override
    public void setObject(T object) {
        decoratee.setObject(object);
    }
}
