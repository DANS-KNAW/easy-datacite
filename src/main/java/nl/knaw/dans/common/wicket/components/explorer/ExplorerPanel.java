package nl.knaw.dans.common.wicket.components.explorer;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.wicket.components.explorer.ITreeItem.Type;
import nl.knaw.dans.common.wicket.components.explorer.content.SelectableFolderContent;
import nl.knaw.dans.common.wicket.components.explorer.style.ExplorerTheme;
import nl.knaw.dans.common.wicket.components.explorer.style.WindowsTheme;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import wickettree.AbstractTree;
import wickettree.ITreeProvider;
import wickettree.NestedTree;
import wickettree.provider.InverseSet;
import wickettree.provider.ProviderSubset;

public class ExplorerPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    private AbstractTree<ITreeItem> tree;

    private final ITreeProvider<ITreeItem> treeProvider;

    private Set<ITreeItem> treeState;

    private DefaultDataTable<?> table;

    private TableProvider tableProvider = new TableProvider();

    private SelectableFolderContent content;

    private final BreadcrumbPanel breadcrumbPanel;

    private final ResourceReference theme = new WindowsTheme();
    private final ResourceReference style = new ExplorerTheme();

    @SuppressWarnings("serial")
    public ExplorerPanel(String name, IModel<?> model, final ITreeProvider<ITreeItem> treeProvider)
    {
        super(name, model);

        this.treeProvider = treeProvider;
        treeState = new ProviderSubset<ITreeItem>(treeProvider);

        content = new SelectableFolderContent(treeProvider)
        {
            @Override
            protected void selectEvent(final AjaxRequestTarget target)
            {
                IModel<ITreeItem> selected = content.getSelected();
                if (selected != null)
                {
                    tree.expand(selected.getObject());
                    List<ITreeItem> list = selected.getObject().getChildrenWithFiles();
                    tableProvider.setList(list);
                    selectedFolderChanged(selected);
                    breadcrumbPanel.update(target, selected.getObject());
                    target.addComponent(table);
                }
            }
        };

        tree = createTree(treeProvider, newStateModel());
        tree.add(new AbstractBehavior()
        {
            @Override
            public void renderHead(IHeaderResponse response)
            {
                response.renderCSSReference(theme);
                response.renderCSSReference(style);
            }
        });

        initialExpand(treeProvider.getRoots().next());
        add(tree);

        final Model<IndicatingAjaxLink<Void>> expandModel = new Model<IndicatingAjaxLink<Void>>();
        final Model<IndicatingAjaxLink<Void>> collapseModel = new Model<IndicatingAjaxLink<Void>>();

        // expand all button
        final IndicatingAjaxLink<Void> expand = new IndicatingAjaxLink<Void>("expandAll")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                ((IDetachable) treeState).detach();
                treeState = new InverseSet<ITreeItem>(new ProviderSubset<ITreeItem>(treeProvider));
                this.setVisible(false);
                collapseModel.getObject().setVisible(true);
                target.addComponent(this.getParent());
            }
        };
        expandModel.setObject(expand);
        add(expand);

        // collapse all button
        final IndicatingAjaxLink<Void> collapse = new IndicatingAjaxLink<Void>("collapseAll")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                ((IDetachable) treeState).detach();
                treeState = new ProviderSubset<ITreeItem>(treeProvider);
                this.setVisible(false);
                expandModel.getObject().setVisible(true);
                target.addComponent(this.getParent());
            }
        };
        collapse.setVisible(false);
        collapseModel.setObject(collapse);
        add(collapse);

        // up button
        add(new IndicatingAjaxLink<Void>("up")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                ITreeItem parentItem = content.getSelected().getObject().getParent();
                if (parentItem != null)
                {
                    IModel<ITreeItem> parentModel = treeProvider.model(parentItem);
                    tree.expand(parentItem);
                    List<ITreeItem> list = parentItem.getChildrenWithFiles();
                    tableProvider.setList(list);
                    target.addComponent(table);
                    content.setSelectedAndUpdate(parentModel, tree, target);
                    selectedFolderChanged(parentModel);
                }
            }
        });

        final Model<IndicatingAjaxLink<Void>> selectAllModel = new Model<IndicatingAjaxLink<Void>>();
        final Model<IndicatingAjaxLink<Void>> selectNoneModel = new Model<IndicatingAjaxLink<Void>>();

        // select all button
        IndicatingAjaxLink<Void> selectAll = new IndicatingAjaxLink<Void>("selectAll")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                selectAllClicked(target);
                this.setVisible(false);
                target.addComponent(this);
                selectNoneModel.getObject().setVisible(true);
                target.addComponent(selectNoneModel.getObject());
            }
        };
        selectAllModel.setObject(selectAll);
        add(selectAll);

        // select none button
        IndicatingAjaxLink<Void> selectNone = new IndicatingAjaxLink<Void>("selectNone")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                selectNoneClicked(target);
                this.setVisible(false);
                target.addComponent(this);
                selectAllModel.getObject().setVisible(true);
                target.addComponent(selectAllModel.getObject());
            }
        };
        selectNone.setVisible(false);
        selectNoneModel.setObject(selectNone);
        add(selectNone);

        breadcrumbPanel = new BreadcrumbPanel("breadcrumbPanel", content.getSelected().getObject());
        add(breadcrumbPanel);

        IColumn<?>[] columns = new IColumn[1];
        columns[0] = new PropertyColumn<Object>(new Model<String>("name"), "name", "name");

        // GK: note: couldn't find a function to disable paging therefor the Integer.MAX_VALUE as a
        // workaround
        table = new DefaultDataTable("datatable", columns, tableProvider, Integer.MAX_VALUE);
        table.setOutputMarkupId(true);
        add(table);

    }

    // recursively expands initially to a folder which has a file or more than one folder
    // override if this functionality is unwanted
    public void initialExpand(ITreeItem root)
    {
        content.setSelected(treeProvider.model(root));
        tree.expand(root);
        treeProvider.getChildren(root);
        List<ITreeItem> children = root.getChildrenWithFiles();
        if (children.size() > 1 || children.size() > 0 && children.get(0).getType().equals(Type.FILE))
        {
            tableProvider.setList(children);
            selectedFolderChanged(treeProvider.model(root));
        }
        else if (children.size() > 0)
        {
            initialExpand(children.get(0));
        }
    }

    public void selectedFolderChanged(IModel<ITreeItem> selected)
    {
        // Override in case you want to do some extra work when the current selected folder has changed
    }

    public void selectAllClicked(AjaxRequestTarget target)
    {
        // Override this function and take appropriate actions to 'select all'
    }

    public void selectNoneClicked(AjaxRequestTarget target)
    {
        // Override this function and take appropriate actions to 'deselect all'
    }

    public void setColumns(IColumn[] columns)
    {
        remove(table);
        table = new DefaultDataTable("datatable", columns, tableProvider, Integer.MAX_VALUE);
        table.setOutputMarkupId(true);
        add(table);
    }

    @SuppressWarnings("serial")
    private IModel<Set<ITreeItem>> newStateModel()
    {
        return new AbstractReadOnlyModel<Set<ITreeItem>>()
        {
            @Override
            public Set<ITreeItem> getObject()
            {
                return treeState;
            }

            /**
             * Super class doesn't detach - would be nice though.
             */
            @Override
            public void detach()
            {
                ((IDetachable) treeState).detach();
            }
        };
    }

    protected AbstractTree<ITreeItem> createTree(ITreeProvider<ITreeItem> provider, IModel<Set<ITreeItem>> state)
    {
        tree = new NestedTree<ITreeItem>("tree", provider, state)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected Component newContentComponent(String id, IModel<ITreeItem> model)
            {
                return ExplorerPanel.this.newContentComponent(id, model);
            }
        };
        return tree;
    }

    protected Component newContentComponent(String id, IModel<ITreeItem> model)
    {
        return content.newContentComponent(id, tree, model);
    }

    public SelectableFolderContent getContent()
    {
        return content;
    }

    public AbstractTree<ITreeItem> getTree()
    {
        return tree;
    }

    public DefaultDataTable<?> getTable()
    {
        return table;
    }

    public TableProvider getTableProvider()
    {
        return tableProvider;
    }

    public BreadcrumbPanel getBreadcrumbPanel()
    {
        return breadcrumbPanel;
    }
}
