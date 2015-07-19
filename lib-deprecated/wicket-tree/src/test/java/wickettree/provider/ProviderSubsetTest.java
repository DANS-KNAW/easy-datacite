/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;

import wickettree.ITreeProvider;

/**
 * Test for {@link ProviderSubset}.
 * 
 * @author Sven Meier
 */
public class ProviderSubsetTest extends TestCase {
    private ITreeProvider<String> provider = new EmptyProvider();

    private List<StringModel> models = new ArrayList<StringModel>();

    public void test() throws Exception {
        ProviderSubset<String> subset = new ProviderSubset<String>(provider);

        subset.add("A");
        subset.addAll(Arrays.asList("AA", "AAA"));

        assertEquals(3, subset.size());

        Iterator<String> iterator = subset.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        try {
            iterator.next();
            fail();
        }
        catch (Exception expected) {}

        assertTrue(subset.contains("A"));
        assertTrue(subset.contains("AA"));
        assertTrue(subset.contains("AAA"));

        subset.detach();

        for (StringModel model : models) {
            assertTrue(model.isDetached());
        }

        assertTrue(subset.contains("A"));
        assertTrue(subset.contains("AA"));
        assertTrue(subset.contains("AAA"));
    }

    private class StringModel implements IModel<String> {

        private String string;

        private boolean detached;

        public StringModel(String string) {
            this.string = string;
            models.add(this);
        }

        public String getObject() {
            detached = false;
            return string;
        }

        public void setObject(String string) {
            detached = false;
            this.string = string;
        }

        public void detach() {
            detached = true;
        }

        public boolean isDetached() {
            return detached;
        }

        @Override
        public boolean equals(Object obj) {
            return string == ((StringModel) obj).string;
        }

        @Override
        public int hashCode() {
            return string.hashCode();
        }
    }

    private class EmptyProvider implements ITreeProvider<String> {

        private List<String> EMPTY = new ArrayList<String>();

        public Iterator<String> getRoots() {
            return EMPTY.iterator();
        }

        public boolean hasChildren(String object) {
            return false;
        }

        public Iterator<String> getChildren(String string) {
            throw new UnsupportedOperationException();
        }

        public IModel<String> model(String string) {
            return new StringModel(string);
        }

        public void detach() {}
    }
}
