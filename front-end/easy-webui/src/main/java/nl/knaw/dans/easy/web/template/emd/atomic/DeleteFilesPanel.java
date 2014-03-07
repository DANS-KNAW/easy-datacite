package nl.knaw.dans.easy.web.template.emd.atomic;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteFilesPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DeleteFilesPanel.class);

    public DeleteFilesPanel(final ModalWindow window, final DatasetModel datasetModel)
    {
        super(window.getContentId());

        add(new IndicatingAjaxLink<Void>("ok")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                try
                {
                    List<DmoStoreId> sids = getSids(Services.getItemService().getFilesAndFolders(EasySession.getSessionUser(), datasetModel.getObject(),
                            datasetModel.getObject().getDmoStoreId(), -1, -1, null, null));
                    Services.getItemService().updateObjects(EasySession.getSessionUser(), datasetModel.getObject(), sids,
                            new UpdateInfo(null, null, null, true), null);
                    // Remove all messages from the uploadpanel
                    target.appendJavascript("removeMessages();");
                }
                catch (ServiceException e)
                {
                    logger.error("Error while trying to delete files.");
                }
                window.close(target);
            }
        });

        add(new IndicatingAjaxLink<Void>("cancel")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }
        });
    }

    private List<DmoStoreId> getSids(List<ItemVO> items)
    {
        List<DmoStoreId> result = new ArrayList<DmoStoreId>();

        for (ItemVO item : items)
        {
            result.add(new DmoStoreId(item.getSid()));
        }

        return result;
    }

}
