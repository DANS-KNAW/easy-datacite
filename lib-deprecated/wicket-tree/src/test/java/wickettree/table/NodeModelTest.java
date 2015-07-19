/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;

/**
 * Test for {@link NodeModel}.
 * 
 * @author Sven Meier
 */
public class NodeModelTest extends TestCase {
    public void testEquals() throws Exception {
        NodeModel<String> model = new NodeModel<String>(new StringModel("A"), new boolean[] {true, false});

        assertTrue(model.equals(new NodeModel<String>(new StringModel("A"), new boolean[] {true, false})));

        assertFalse(model.equals(new NodeModel<String>(new StringModel("A"), new boolean[] {true, true})));

        assertFalse(model.equals(new NodeModel<String>(new StringModel("B"), new boolean[] {true, false})));
    }

    private class StringModel implements IModel<String> {

        private String string;

        public StringModel(String string) {
            this.string = string;
        }

        public String getObject() {
            throw new UnsupportedOperationException();
        }

        public void setObject(String object) {
            throw new UnsupportedOperationException();
        }

        public void detach() {}

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof StringModel) {
                return this.string.equals(((StringModel) obj).string);
            }

            return false;
        }
    }
}
