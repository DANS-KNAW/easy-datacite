package nl.knaw.dans.easy.web.fileexplorer;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModalFileDetails extends Panel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ModalFileDetails.class);

    public ModalFileDetails(final ModalWindow window, final List<TreeItem> items, final DatasetModel dataset)
    {
        super(window.getContentId());

        Label message = new Label("message", new ResourceModel("details.message"));
        message.setVisible(items.size() <= 0);
        add(message);

        buildList(items, dataset);

        add(new IndicatingAjaxLink<Void>("close")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }

        });
    }

    private void buildList(final List<TreeItem> items, final DatasetModel dataset)
    {
        add(new ListView<TreeItem>("outerList", items)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<TreeItem> item)
            {
                TreeItem treeItem = (TreeItem) item.getDefaultModelObject();
                List<KeyValuePair> metadata = getFileItemMetaData(treeItem, dataset);

                item.add(new Label("filename", treeItem.getName()));

                item.add(new ListView<KeyValuePair>("innerList", metadata)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(final ListItem<KeyValuePair> item)
                    {
                        final KeyValuePair kvp = (KeyValuePair) item.getDefaultModelObject();
                        item.add(new Label("key", kvp.getKey()));
                        item.add(new Label("value", kvp.getValue()));
                    }
                });

            }
        });
    }

    private List<KeyValuePair> getFileItemMetaData(TreeItem item, DatasetModel dataset)
    {
        try
        {
            FileItemDescription description = Services.getItemService().getFileItemDescription(EasySession.getSessionUser(), dataset.getObject(),
                    new DmoStoreId(item.getId()));
            if (EasySession.getSessionUser().hasRole(Role.ARCHIVIST) || dataset.getObject().hasDepositor(EasySession.getSessionUser()))
            {
                // metadata for Arch/Depo
                List<KeyValuePair> metadata = description.getMetadataForArchDepo();
                for (KeyValuePair kvp : metadata)
                {
                    if (kvp.getKey().toLowerCase().equals("creator"))
                    {
                        kvp.setValue(new StringResourceModel("Creator." + kvp.getValue(), this, null).getString());
                    }
                    if (kvp.getKey().toLowerCase().equals("visible to"))
                    {
                        kvp.setValue(new StringResourceModel("Rights." + kvp.getValue(), this, null).getString());
                    }
                    if (kvp.getKey().toLowerCase().equals("accessible to"))
                    {
                        kvp.setValue(new StringResourceModel("Rights." + kvp.getValue(), this, null).getString());
                    }
                }
                return metadata;
            }
            else
            {
                // metadata for all other users
                List<KeyValuePair> metadata = description.getMetadataForAnonKnown();
                AuthzStrategy strategy = item.getItemVO().getAuthzStrategy();

                for (KeyValuePair kvp : metadata)
                {
                    if (kvp.getKey().toLowerCase().equals("creator"))
                    {
                        kvp.setValue(new StringResourceModel("Creator." + kvp.getValue(), this, null).getString());
                    }
                    if (kvp.getKey().toLowerCase().equals("accessible"))
                    {
                        kvp.setValue(new StringResourceModel(strategy.getSingleReadMessage().getMessageCode(), this, null).getString());
                    }
                }
                return metadata;
            }
        }
        catch (ServiceException e)
        {
            logger.error("Problem getting the metadata for sid: " + item.getId(), e);
        }
        return new ArrayList<KeyValuePair>();
    }

}
