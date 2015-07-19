/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import org.apache.wicket.IClusterable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import wickettree.TableTree;

/**
 * Copy of {@link org.apache.wicket.extensions.markup.html.repeater.data.table.NavigatorLabel}
 */
public class NavigatorLabel extends Label {

    private static final long serialVersionUID = 1L;

    public NavigatorLabel(final String id, final TableTree<?> table) {
        super(id);
        setDefaultModel(new StringResourceModel("NavigatorLabel", this, new Model<LabelModelObject>(new LabelModelObject(table)),
                "Showing ${from} to ${to} of ${of}"));
    }

    private class LabelModelObject implements IClusterable {
        private static final long serialVersionUID = 1L;

        private final TableTree<?> table;

        public LabelModelObject(TableTree<?> table) {
            this.table = table;
        }

        public int getOf() {
            return table.getRowCount();
        }

        public int getFrom() {
            if (getOf() == 0) {
                return 0;
            }
            return (table.getCurrentPage() * table.getItemsPerPage()) + 1;
        }

        public int getTo() {
            if (getOf() == 0) {
                return 0;
            }
            return Math.min(getOf(), getFrom() + table.getItemsPerPage() - 1);
        }
    }
}
