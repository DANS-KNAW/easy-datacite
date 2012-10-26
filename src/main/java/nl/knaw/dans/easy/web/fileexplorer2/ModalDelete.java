package nl.knaw.dans.easy.web.fileexplorer2;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModalDelete extends Panel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ModalDelete.class);

    public ModalDelete(final ModalWindow window, final List<TreeItem> items, final DatasetModel dataset)
    {
        super(window.getContentId());

        String message = "";

        if (items.size() == 0)
        {
            message = "Please use the check boxes first, to select the files and/or folders you want to delete.";
        }
        else if (items.size() == 1)
        {
            message = "Are you sure you want to delete '" + items.get(0).getName() + "'?";
        }
        else
        {
            message = "Are you sure you want to delete " + items.size() + " files/folders?";
        }

        add(new Label("message", message));

        add(new IndicatingAjaxLink<Void>("yesLink")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                List<DmoStoreId> sidList = new ArrayList<DmoStoreId>();
                for (TreeItem item : items)
                {
                    sidList.add(new DmoStoreId(item.getId()));
                }
                try
                {
                    Services.getItemService().updateObjects(EasySession.getSessionUser(), dataset.getObject(), sidList, new UpdateInfo(null, null, null, true),
                            null);
                    updateAfterDelete(target);
                    window.close(target);
                }
                catch (ServiceRuntimeException e)
                {
                    logger.error("Error while trying to delete object.", e);
                }
                catch (ServiceException e)
                {
                    logger.error("Error while trying to delete object.", e);
                }
            }

            @Override
            public boolean isVisible()
            {
                return items.size() > 0;
            }

        });

        add(new AjaxLink<Void>("noLink")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }

        });

        add(new IndicatingAjaxLink<Void>("close")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }

            @Override
            public boolean isVisible()
            {
                return items.size() <= 0;
            }
        });
    }

    public void updateAfterDelete(AjaxRequestTarget target)
    {
        // Override if you want to update something after deletion
    }
}
