/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.util;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.util.time.Duration;

import wickettree.ITreeProvider;
import wickettree.nested.BranchItem;
import wickettree.provider.ProviderSubset;

/**
 * Wrapper of a ITreeProvider handling intermediate childrens.
 * 
 * @see #getChildren(Object)
 * @see #intermediate(Iterator)
 */
public class IntermediateTreeProvider<T> implements ITreeProvider<T> {

    private ITreeProvider<T> provider;

    private Duration delay;

    /**
     * All nodes with intermediate children.
     */
    private ProviderSubset<T> intermediates;

    /**
     * Wrap the given provider.
     * 
     * @param provider
     *        provider to wrap
     * @param delay
     *        delay after which to update branches for nodes with intermediate children
     */
    public IntermediateTreeProvider(ITreeProvider<T> provider, Duration delay) {
        this.provider = provider;
        this.delay = delay;

        intermediates = new ProviderSubset<T>(provider);
    }

    /**
     * Does the given node have intermediate children.
     */
    public boolean hasIntermediateChildren(T t) {
        return intermediates.contains(t);
    }

    public Iterator<? extends T> getRoots() {
        return provider.getRoots();
    }

    public boolean hasChildren(T object) {
        return provider.hasChildren(object);
    }

    /**
     * Delegate to the wrapped {@link ITreeProvider}, remembering nodes with intermediate children.
     * 
     * @see #intermediate(Iterator)
     */
    public Iterator<? extends T> getChildren(T t) {
        Iterator<? extends T> iterator = provider.getChildren(t);

        if (iterator instanceof IntermediateIterator<?>) {
            intermediates.add(t);
        } else {
            intermediates.remove(t);
        }

        return iterator;
    }

    public IModel<T> model(T object) {
        return new BehaviorWrapper(provider.model(object));
    }

    public void detach() {
        provider.detach();

        intermediates.detach();
    }

    /**
     * A wrapper which adds an Ajax behavior on {@link BranchItem}s.
     */
    private class BehaviorWrapper implements IComponentAssignedModel<T>, IWrapModel<T> {
        private IModel<T> model;

        public BehaviorWrapper(IModel<T> model) {
            this.model = model;
        }

        public T getObject() {
            return model.getObject();
        }

        public void setObject(T object) {
            throw new UnsupportedOperationException();
        }

        public void detach() {
            model.detach();
        }

        @Override
        public int hashCode() {
            return model.hashCode();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof IntermediateTreeProvider.BehaviorWrapper) {
                BehaviorWrapper other = (BehaviorWrapper) obj;

                return this.model.equals(other.model);
            }
            return false;
        }

        public IWrapModel<T> wrapOnAssignment(final Component component) {
            if (component instanceof BranchItem<?>) {
                component.add(new AbstractDefaultAjaxBehavior() {
                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);

                        T t = model.getObject();
                        if (hasIntermediateChildren(t)) {
                            response.renderOnLoadJavascript(getJsTimeoutCall());
                        }
                    }

                    protected final String getJsTimeoutCall() {
                        return "setTimeout(\"" + getCallbackScript() + "\", " + delay.getMilliseconds() + ");";
                    }

                    @Override
                    protected void respond(AjaxRequestTarget target) {
                        T t = model.getObject();

                        intermediates.remove(t);

                        target.addComponent(component);
                    }
                });
            }

            return this;
        }

        public IModel<?> getWrappedModel() {
            return model;
        }
    }

    /**
     * Mark children as intermediate. Call this method from your {@link ITreeProvider#getChildren(Object)} implementation as needed.
     * 
     * @param children
     *        intermediate children
     * @see ITreeProvider#getChildren(Object)
     */
    public static <S> Iterator<S> intermediate(Iterator<S> children) {
        return new IntermediateIterator<S>(children);
    }

    private static class IntermediateIterator<S> implements Iterator<S> {
        private Iterator<S> iterator;

        public IntermediateIterator(Iterator<S> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public S next() {
            return iterator.next();
        }

        public void remove() {
            iterator.remove();
        }
    }
}
