/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import wickettree.TableTree;

/**
 * {@link TableTree}'s toolbars are very similar to the toolbars of the {@link DataTable}. Regretfully the latter take the {@link DataTable} as an constructor
 * argument - maybe these class hierarchies could be merged.
 */
public abstract class AbstractToolbar extends Panel {
    private static final long serialVersionUID = 1L;

    private static int counter = 0;

    private final TableTree<?> tree;

    public AbstractToolbar(IModel<?> model, TableTree<?> tree) {
        super("" + (counter++), model);

        this.tree = tree;
    }

    public AbstractToolbar(TableTree<?> tree) {
        this(null, tree);
    }

    protected TableTree<?> getTree() {
        return tree;
    }
}
