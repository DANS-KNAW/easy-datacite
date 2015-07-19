/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree;

import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

import wickettree.content.Folder;
import wickettree.table.HeadersToolbar;
import wickettree.table.NoRecordsToolbar;
import wickettree.theme.WindowsTheme;

/**
 * An implementation of the TableTree that aims to solve the 90% usecase by using {@link Folder}s and by adding headers and no-records-found toolbars to a
 * standard {@link TableTree}.
 * 
 * @param <T>
 *        The model object type
 * @author Sven Meier
 */
public class DefaultTableTree<T> extends TableTree<T> {

    private static final long serialVersionUID = 1L;

    public DefaultTableTree(String id, List<ICellPopulator<T>> columns, ISortableTreeProvider<T> provider, int rowsPerPage) {
        this(id, columns, provider, rowsPerPage, null);

        add(new HeaderContributor(new IHeaderContributor() {

            private static final long serialVersionUID = 1L;

            public void renderHead(IHeaderResponse response) {
                response.renderCSSReference(new WindowsTheme());
            }
        }));
    }

    public DefaultTableTree(String id, List<ICellPopulator<T>> columns, ISortableTreeProvider<T> provider, int rowsPerPage, IModel<Set<T>> state) {
        super(id, columns, provider, rowsPerPage, state);

        addTopToolbar(new HeadersToolbar(this, provider));
        addBottomToolbar(new NoRecordsToolbar(this));
    }

    /*
     * @Override public void renderHead(IHeaderResponse response) { response.renderCSSReference(new WindowsTheme()); }
     */

    @Override
    protected Component newContentComponent(String id, IModel<T> model) {
        return new Folder<T>(id, this, model);
    }

    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> model) {
        return new OddEvenItem<T>(id, index, model);
    }
}
