/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.Model;

import wickettree.TableTree;

/**
 * Copy of {@link org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar}
 */
public class NavigationToolbar extends AbstractToolbar {
    private static final long serialVersionUID = 1L;

    private final TableTree<?> table;

    public NavigationToolbar(final TableTree<?> table) {
        super(table);
        this.table = table;

        WebMarkupContainer span = new WebMarkupContainer("span");
        add(span);
        span.add(new AttributeModifier("colspan", true, new Model<String>(String.valueOf(table.getColumns().size()))));

        span.add(newPagingNavigator("navigator", table));
        span.add(newNavigatorLabel("navigatorLabel", table));
    }

    protected PagingNavigator newPagingNavigator(String navigatorId, final TableTree<?> table) {
        return new PagingNavigator(navigatorId, table);
    }

    protected WebComponent newNavigatorLabel(String navigatorId, final TableTree<?> table) {
        return new NavigatorLabel(navigatorId, table);
    }

    @Override
    public boolean isVisible() {
        return table.getPageCount() > 1;
    }
}
