/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.provider;

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Test for {@link TreeModelProvider}.
 * 
 * @author Sven Meier
 */
public class TreeModelProviderTest extends TestCase {
    private DefaultTreeModel treeModel;

    public TreeModelProviderTest() {
        treeModel = getDefaultTreeModel();
    }

    public void test() throws Exception {
        TreeModelProvider<DefaultMutableTreeNode> provider = new TreeModelProvider<DefaultMutableTreeNode>(treeModel) {
            @Override
            public IModel<DefaultMutableTreeNode> model(DefaultMutableTreeNode object) {
                return Model.of(object);
            }
        };

        Iterator<DefaultMutableTreeNode> roots = provider.getRoots();
        assertTrue(roots.hasNext());
        DefaultMutableTreeNode root = roots.next();
        assertEquals("JTree", root.getUserObject());
        assertFalse(roots.hasNext());

        Iterator<DefaultMutableTreeNode> children = provider.getChildren(root);
        assertTrue(children.hasNext());
        assertEquals("colors", children.next().getUserObject());
        assertTrue(children.hasNext());
        assertEquals("sports", children.next().getUserObject());
        assertTrue(children.hasNext());
        assertEquals("food", children.next().getUserObject());
        assertFalse(roots.hasNext());

        treeModel.nodeChanged(root);
    }

    protected static DefaultTreeModel getDefaultTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");
        DefaultMutableTreeNode parent;

        parent = new DefaultMutableTreeNode("colors");
        root.add(parent);
        parent.add(new DefaultMutableTreeNode("blue"));
        parent.add(new DefaultMutableTreeNode("violet"));
        parent.add(new DefaultMutableTreeNode("red"));
        parent.add(new DefaultMutableTreeNode("yellow"));

        parent = new DefaultMutableTreeNode("sports");
        root.add(parent);
        parent.add(new DefaultMutableTreeNode("basketball"));
        parent.add(new DefaultMutableTreeNode("soccer"));
        parent.add(new DefaultMutableTreeNode("football"));
        parent.add(new DefaultMutableTreeNode("hockey"));

        parent = new DefaultMutableTreeNode("food");
        root.add(parent);
        parent.add(new DefaultMutableTreeNode("hot dogs"));
        parent.add(new DefaultMutableTreeNode("pizza"));
        parent.add(new DefaultMutableTreeNode("ravioli"));
        parent.add(new DefaultMutableTreeNode("bananas"));
        return new DefaultTreeModel(root);
    }

}
