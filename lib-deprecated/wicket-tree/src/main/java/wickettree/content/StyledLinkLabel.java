/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.content;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * A styled link with a label.
 * 
 * @see #newLinkComponent(String, IModel)
 * @see #getStyleClass()
 * @author Sven Meier
 */
public abstract class StyledLinkLabel<T> extends Panel {
    private static final StyleBehavior STYLE_CLASS = new StyleBehavior();

    private static final long serialVersionUID = 1L;

    public StyledLinkLabel(String id, IModel<T> model) {
        super(id, model);

        MarkupContainer link = newLinkComponent("link", model);
        link.add(STYLE_CLASS);
        add(link);

        link.add(newLabelComponent("label", model));
    }

    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    public T getModelObject() {
        return getModel().getObject();
    }

    /**
     * Hook method to create a new link component. This default implementation returns an {@link AjaxFallbackLink} which invokes
     * {@link #onClick(AjaxRequestTarget)} only if {@link #isClickable()} returns <code>true</code>.
     * 
     * @see #isClickable()
     * @see #onClick(AjaxRequestTarget)
     */
    protected MarkupContainer newLinkComponent(String id, IModel<T> model) {
        return new IndicatingAjaxLink<Void>(id) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isEnabled() {
                return StyledLinkLabel.this.isClickable();
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                StyledLinkLabel.this.onClick(target);
            }
        };
    }

    /**
     * Hook method to create a new label component.
     * 
     * @param id
     * @param model
     * @return created component
     * @see #newLabelModel(IModel)
     */
    protected Component newLabelComponent(String id, IModel<T> model) {
        return new Label(id, newLabelModel(model));
    }

    /**
     * Create the model for the label, defaults to the model itself.
     * 
     * @param model
     * @return wrapping model
     */
    protected IModel<?> newLabelModel(IModel<T> model) {
        return model;
    }

    /**
     * Get a style class for the link.
     */
    protected abstract String getStyleClass();

    /**
     * Clicking is disabled by default, override this method if you want your link to be enabled.
     * 
     * @see #newLinkComponent(String, IModel)
     * @see #isClickable()
     */
    protected boolean isClickable() {
        return false;
    }

    /**
     * Hook method to be notified of a click on the link.
     * 
     * @param target
     * @see #newLinkComponent(String, IModel)
     * @see #isClickable()
     */
    protected void onClick(AjaxRequestTarget target) {}

    /**
     * Behavior to add a style class attribute to a contained link.
     */
    private static class StyleBehavior extends AbstractBehavior {
        private static final long serialVersionUID = 1L;

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            StyledLinkLabel<?> parent = (StyledLinkLabel<?>) component.getParent();

            String styleClass = parent.getStyleClass();
            if (styleClass != null) {
                tag.put("class", styleClass);
            }
        }
    }
}
