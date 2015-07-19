/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IStyledColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.IItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import wickettree.nested.BranchItem;
import wickettree.table.AbstractToolbar;
import wickettree.table.ITreeColumn;
import wickettree.table.ITreeDataProvider;
import wickettree.table.NodeModel;
import wickettree.table.TreeDataProvider;

/**
 * A tree with tabular markup. If you use one of the provided themes, be sure to add a "tree" class attribute to your table markup.
 * 
 * @author Sven Meier
 */
public abstract class TableTree<T> extends AbstractTree<T> implements IPageable {

    private static final long serialVersionUID = 1L;

    private final DataGridView<T> datagrid;

    private final List<ICellPopulator<T>> columns;

    private final RepeatingView topToolbars;

    private final RepeatingView bottomToolbars;

    /**
     * Constructor
     * 
     * @param id
     *        component id
     * @param columns
     *        list of column definitions
     * @param provider
     *        provider of the tree
     * @param rowsPerPage
     *        number of rows per page
     */
    public TableTree(String id, List<ICellPopulator<T>> columns, ITreeProvider<T> provider, int rowsPerPage) {
        this(id, columns, provider, rowsPerPage, null);
    }

    /**
     * Constructor
     * 
     * @param id
     *        component id
     * @param columns2
     *        list of column definitions
     * @param provider
     *        provider of the tree
     * @param itemsPerPage
     *        number of rows per page
     * @param state
     *        state of nodes
     */
    public TableTree(String id, List<ICellPopulator<T>> columns2, ITreeProvider<T> provider, int itemsPerPage, IModel<Set<T>> state) {
        super(id, provider, state);

        if (columns2 == null || columns2.isEmpty()) {
            throw new IllegalArgumentException("Argument `columns` cannot be null or empty");
        }
        for (ICellPopulator<T> column : columns2) {
            if (column instanceof ITreeColumn<?>) {
                ((ITreeColumn<T>) column).setTree(this);
            }
        }
        this.columns = columns2;

        WebMarkupContainer body = newBodyContainer("body");
        add(body);

        datagrid = new DataGridView<T>("rows", columns2, newDataProvider(provider)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected Item<ICellPopulator<T>> newCellItem(String id, int index, IModel<ICellPopulator<T>> model) {
                Item<ICellPopulator<T>> item = TableTree.this.newCellItem(id, index, model);

                final ICellPopulator<T> column = TableTree.this.columns.get(index);
                if (column instanceof IStyledColumn<?>) {
                    item.add(new AttributeAppender("class", true, Model.of(((IStyledColumn<?>) column).getCssClass()), " "));
                }

                return item;
            }

            @Override
            protected Item<T> newRowItem(String id, int index, IModel<T> model) {
                Item<T> item = TableTree.this.newRowItem(id, index, model);

                // @see #updateNode(T, AjaxRequestTarget)
                item.setOutputMarkupId(true);

                return item;
            }
        };
        datagrid.setRowsPerPage(itemsPerPage);
        datagrid.setItemReuseStrategy(new IItemReuseStrategy() {
            private static final long serialVersionUID = 1L;

            public <S> Iterator<Item<S>> getItems(IItemFactory<S> factory, Iterator<IModel<S>> newModels, Iterator<Item<S>> existingItems) {
                return TableTree.this.getItemReuseStrategy().getItems(factory, newModels, existingItems);
            }
        });
        body.add(datagrid);

        topToolbars = new ToolbarsContainer("topToolbars");
        bottomToolbars = new ToolbarsContainer("bottomToolbars");
        add(topToolbars);
        add(bottomToolbars);
    }

    protected ITreeDataProvider<T> newDataProvider(ITreeProvider<T> provider) {
        return new TreeDataProvider<T>(provider) {
            @Override
            protected boolean iterateChildren(T object) {
                return TableTree.this.getState(object) == State.EXPANDED;
            }
        };
    }

    /**
     * Create the MarkupContainer for the <tbody> tag. Users may subclass it to provide their own (modified) implementation.
     * 
     * @param id
     * @return A new markup container
     */
    protected WebMarkupContainer newBodyContainer(final String id) {
        return new WebMarkupContainer(id);
    }

    public List<ICellPopulator<T>> getColumns() {
        return columns;
    }

    /**
     * @see DataTable
     */
    public void addBottomToolbar(AbstractToolbar toolbar) {
        addToolbar(toolbar, bottomToolbars);
    }

    /**
     * @see DataTable
     */
    public void addTopToolbar(AbstractToolbar toolbar) {
        addToolbar(toolbar, topToolbars);
    }

    private void addToolbar(AbstractToolbar toolbar, RepeatingView container) {
        if (toolbar == null) {
            throw new IllegalArgumentException("argument [toolbar] cannot be null");
        }

        container.add(toolbar);
    }

    public final int getRowCount() {
        return datagrid.getRowCount();
    }

    public int getPageCount() {
        return datagrid.getPageCount();
    }

    public void setCurrentPage(int page) {
        datagrid.setCurrentPage(page);
    }

    public int getCurrentPage() {
        return datagrid.getCurrentPage();
    }

    /**
     * @return number of items per page
     */
    public int getItemsPerPage() {
        return datagrid.getRowsPerPage();
    }

    /**
     * Sets the number of items to be displayed per page
     * 
     * @param items
     *        number of items to display per page
     */
    public void setItemsPerPage(int items) {
        datagrid.setRowsPerPage(items);
    }

    /**
     * @see DataTable
     */
    protected Item<ICellPopulator<T>> newCellItem(final String id, final int index, final IModel<ICellPopulator<T>> model) {
        return new Item<ICellPopulator<T>>(id, index, model);
    }

    /**
     * @see DataTable
     */
    protected Item<T> newRowItem(final String id, int index, final IModel<T> model) {
        Item<T> item = new Item<T>(id, index, model);

        return item;
    }

    /**
     * @see DataTable
     */
    private class ToolbarsContainer extends RepeatingView {
        private static final long serialVersionUID = 1L;

        private ToolbarsContainer(String id) {
            super(id);
        }
    }

    /**
     * Overriden to update the complete row item of the node.
     * 
     * @see #newRowItem(String, int, IModel)
     */
    @Override
    public void updateNode(T t, final AjaxRequestTarget target) {
        if (target != null) {
            final IModel<T> model = getProvider().model(t);
            visitChildren(Item.class, new IVisitor<Component>() {
                public Object component(Component item) {
                    NodeModel<T> nodeModel = (NodeModel<T>) item.getDefaultModel();

                    if (model.equals(nodeModel.getWrappedModel())) {
                        target.addComponent(item);
                        return STOP_TRAVERSAL;
                    }
                    return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
                }
            });
            model.detach();
        }
    }
}
