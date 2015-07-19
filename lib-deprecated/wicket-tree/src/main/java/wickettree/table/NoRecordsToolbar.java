/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import wickettree.TableTree;

/**
 * Copy of {@link org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar}
 */
public class NoRecordsToolbar extends AbstractToolbar {
    private static final long serialVersionUID = 1L;

    private static final IModel<String> DEFAULT_MESSAGE_MODEL = new ResourceModel("datatable.no-records-found", "No Records Found");

    public NoRecordsToolbar(final TableTree<?> table) {
        this(table, DEFAULT_MESSAGE_MODEL);
    }

    public NoRecordsToolbar(final TableTree<?> table, IModel<String> messageModel) {
        super(table);
        WebMarkupContainer td = new WebMarkupContainer("td");
        add(td);

        td.add(new AttributeModifier("colspan", true, new Model<String>(String.valueOf(table.getColumns().size()))));
        td.add(new Label("msg", messageModel));
    }

    @Override
    public boolean isVisible() {
        return getTree().getRowCount() == 0;
    }

}
