/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IStyledColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import wickettree.TableTree;

/**
 * Copy of {@link org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar}
 */
public class HeadersToolbar extends AbstractToolbar {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param tree
     *        tree this toolbar will be attached to
     */
    public HeadersToolbar(final TableTree<?> tree) {
        this(tree, null);
    }

    /**
     * Constructor
     * 
     * @param tree
     *        tree this toolbar will be attached to
     */
    public HeadersToolbar(final TableTree<?> tree, final ISortStateLocator stateLocator) {
        super(tree);

        RepeatingView headers = new RepeatingView("headers");
        add(headers);

        for (final ICellPopulator<?> column : tree.getColumns()) {
            WebMarkupContainer item = new WebMarkupContainer(headers.newChildId());
            headers.add(item);

            WebMarkupContainer header = null;
            if (((IColumn<?>) column).isSortable()) {
                header = newSortableHeader("header", ((IColumn<?>) column).getSortProperty(), stateLocator);
            } else {
                header = new WebMarkupContainer("header");
            }

            if (column instanceof IStyledColumn<?>) {
                header.add(new AttributeAppender("class", true, Model.of(((IStyledColumn<?>) column).getCssClass()), " "));
            }

            item.add(header);
            item.setRenderBodyOnly(true);
            header.add(((IColumn<?>) column).getHeader("label"));

        }
    }

    /**
     * Factory method for sortable header components. A sortable header component must have id of <code>headerId</code> and conform to markup specified in
     * <code>HeadersToolbar.html</code>
     * 
     * @param headerId
     *        header component id
     * @param property
     *        property this header represents
     * @param locator
     *        sort state locator
     * @return created header component
     */
    protected WebMarkupContainer newSortableHeader(String headerId, String property, ISortStateLocator locator) {
        return new OrderByBorder(headerId, property, locator) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged() {
                getTree().setCurrentPage(0);
            }
        };
    }
}
