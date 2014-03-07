/*
 * Copyright 2009 Sven Meier Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package nl.knaw.dans.common.wicket.components.explorer.content;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import wickettree.AbstractTree;
import wickettree.ITreeProvider;
import wickettree.content.Folder;

/**
 * @author Sven Meier
 */
public class SelectableFolderContent extends Content
{

    private static final long serialVersionUID = 1L;

    private ITreeProvider<ITreeItem> provider;

    private IModel<ITreeItem> selected;

    public SelectableFolderContent(ITreeProvider<ITreeItem> provider)
    {
        this.provider = provider;
    }

    public void detach()
    {
        if (selected != null)
        {
            selected.detach();
        }
    }

    protected boolean isSelected(ITreeItem item)
    {
        IModel<ITreeItem> model = provider.model(item);

        try
        {
            return selected != null && selected.equals(model);
        }
        finally
        {
            model.detach();
        }
    }

    protected void select(ITreeItem item, AbstractTree<ITreeItem> tree, final AjaxRequestTarget target)
    {
        if (selected != null)
        {
            tree.updateNode(selected.getObject(), target);
            selected.detach();
            selected = null;
        }
        selected = provider.model(item);

        tree.updateNode(item, target);

        selectEvent(target);
    }

    /**
     * override this if you want to do extra things when a node is selected
     */
    protected void selectEvent(final AjaxRequestTarget target)
    {
    }

    @Override
    public Component newContentComponent(String id, final AbstractTree<ITreeItem> tree, IModel<ITreeItem> model)
    {
        return new Folder<ITreeItem>(id, tree, model)
        {
            private static final long serialVersionUID = 1L;

            /**
             * Always clickable.
             */
            @Override
            protected boolean isClickable()
            {
                return true;
            }

            @Override
            protected void onClick(AjaxRequestTarget target)
            {
                SelectableFolderContent.this.select(getModelObject(), tree, target);
            }

            @Override
            protected boolean isSelected()
            {
                return SelectableFolderContent.this.isSelected(getModelObject());
            }
        };
    }

    public IModel<ITreeItem> getSelected()
    {
        return selected;
    }

    public void setSelected(IModel<ITreeItem> selected)
    {
        this.selected = selected;
    }

    public void setSelectedAndUpdate(IModel<ITreeItem> selected, AbstractTree<ITreeItem> tree, AjaxRequestTarget target)
    {
        this.selected = selected;
        SelectableFolderContent.this.select(this.selected.getObject(), tree, target);
    }
}
