/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package wickettree.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import wickettree.AbstractTree;
import wickettree.ITreeProvider;

/**
 * A provider wrapping a Swing {@link TreeModel}. EXPERIMENTAL !
 * 
 * @author Sven Meier
 */
public abstract class TreeModelProvider<T> implements ITreeProvider<T> {

    private TreeModel treeModel;

    private boolean rootVisible;

    private Listener listener;

    private boolean completeUpdate;

    private List<T> nodeUpdates;

    private List<T> branchUpdates;

    public TreeModelProvider(TreeModel treeModel) {
        this(treeModel, true);
    }

    public TreeModelProvider(TreeModel treeModel, boolean rootVisible) {
        this.treeModel = treeModel;
        this.rootVisible = rootVisible;

        treeModel.addTreeModelListener(listener);
    }

    public Iterator<T> getRoots() {
        if (rootVisible) {
            return new Iterator<T>() {
                boolean next = true;

                public boolean hasNext() {
                    return next;
                }

                public T next() {
                    next = false;
                    return cast(treeModel.getRoot());
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            return getChildren(cast(treeModel.getRoot()));
        }
    }

    public boolean hasChildren(T object) {
        return !treeModel.isLeaf(object);
    }

    public Iterator<T> getChildren(final T object) {
        return new Iterator<T>() {
            private int size = treeModel.getChildCount(object);
            private int index = -1;

            public boolean hasNext() {
                return index < size - 1;
            }

            public T next() {
                index++;
                return cast(treeModel.getChild(object, index));
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected T cast(Object object) {
        return (T) object;
    }

    public abstract IModel<T> model(T object);

    public void detach() {
        completeUpdate = false;
        nodeUpdates = null;
        branchUpdates = null;
    }

    public void update(AbstractTree<T> tree, AjaxRequestTarget target) {
        if (completeUpdate) {
            target.addComponent(tree);
        } else {
            for (T object : nodeUpdates) {
                tree.updateNode(object, target);
            }

            for (T object : branchUpdates) {
                tree.updateBranch(object, target);
            }
        }

        detach();
    }

    protected void nodeUpdate(Object[] nodes) {
        if (nodeUpdates == null) {
            nodeUpdates = new ArrayList<T>();
        }

        for (Object node : nodes) {
            nodeUpdates.add(cast(node));
        }
    }

    protected void branchUpdate(Object branch) {
        if (branchUpdates == null) {
            branchUpdates = new ArrayList<T>();
        }

        branchUpdates.add(cast(branch));
    }

    private class Listener implements TreeModelListener, Serializable {
        public void treeNodesChanged(TreeModelEvent e) {
            if (e.getChildIndices() == null) {
                completeUpdate = true;
            } else {
                nodeUpdate(e.getChildren());
            }
        }

        public void treeNodesInserted(TreeModelEvent e) {
            branchUpdate(e.getTreePath().getLastPathComponent());
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            branchUpdate(e.getTreePath().getLastPathComponent());
        }

        public void treeStructureChanged(TreeModelEvent e) {
            if (e.getTreePath().getPathCount() == 1) {
                completeUpdate = true;
            } else {
                branchUpdate(e.getTreePath().getLastPathComponent());
            }
        }
    }
}
