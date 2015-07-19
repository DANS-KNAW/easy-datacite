/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.util;

import junit.framework.TestCase;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Test for {@link MultiColumn}.
 */
public class MultiColumnTest extends TestCase {
    private boolean populated = false;

    public void test() throws Exception {
        AbstractColumn<String> column = new AbstractColumn<String>(Model.of("test"), "sortTest") {
            public void populateItem(Item<ICellPopulator<String>> cellItem, String componentId, IModel<String> rowModel) {
                populated = true;
            }
        };

        MultiColumn<String> multi = new MultiColumn<String>(String.class, column);

        assertEquals("test", multi.getDisplayModel().getObject());
        assertEquals("sortTest", multi.getSortProperty());

        multi.populateItem(null, "testId", Model.of(new String()));
        assertTrue(populated);
    }
}
