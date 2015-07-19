/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import wickettree.ITreeProvider;

/**
 * Test for {@link TreeDataProvider}.
 */
public class TreeDataProviderTest extends TestCase {

    public void test() throws Exception {
        TreeDataProvider<String> provider = new TreeDataProvider<String>(new TestProvider()) {
            @Override
            protected boolean iterateChildren(String object) {
                return !object.startsWith("B");
            }
        };

        assertEquals(3 + 2 * (3 + 3 * 3), provider.size());

        Iterator<? extends String> iterator = provider.iterator(0, Integer.MAX_VALUE);
        assertEquals("A", iterator.next());
        assertEquals("AA", iterator.next());
        assertEquals("AAA", iterator.next());
        assertEquals("AAB", iterator.next());
        assertEquals("AAC", iterator.next());
        assertEquals("AB", iterator.next());
        assertEquals("ABA", iterator.next());
        assertEquals("ABB", iterator.next());
        assertEquals("ABC", iterator.next());
        assertEquals("AC", iterator.next());
        assertEquals("ACA", iterator.next());
        assertEquals("ACB", iterator.next());
        assertEquals("ACC", iterator.next());
        assertEquals("B", iterator.next());
        // note: B is not iterated (i.e. collapsed)!
        assertEquals("C", iterator.next());
        assertEquals("CA", iterator.next());
        assertEquals("CAA", iterator.next());
        assertEquals("CAB", iterator.next());
        assertEquals("CAC", iterator.next());
        assertEquals("CB", iterator.next());
        assertEquals("CBA", iterator.next());
        assertEquals("CBB", iterator.next());
        assertEquals("CBC", iterator.next());
        assertEquals("CC", iterator.next());
        assertEquals("CCA", iterator.next());
        assertEquals("CCB", iterator.next());
        assertEquals("CCC", iterator.next());

        assertFalse(iterator.hasNext());
    }

    private static class TestProvider implements ITreeProvider<String> {

        public Iterator<? extends String> getRoots() {
            return Arrays.asList("A", "B", "C").iterator();
        }

        public Iterator<? extends String> getChildren(String object) {
            List<String> children = new ArrayList<String>();

            if (hasChildren(object)) {
                for (int i = 0; i < 3; i++) {
                    children.add(object + (char) ('A' + i));
                }
            }

            return children.iterator();
        }

        public boolean hasChildren(String object) {
            return object.length() < 3;
        }

        public IModel<String> model(String object) {
            return Model.of(object);
        }

        public void detach() {}
    }
}
