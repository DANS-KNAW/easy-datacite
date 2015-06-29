package nl.knaw.dans.easy.web.deposit;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.DepositUploadPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPanel extends AbstractCustomPanel {

    private static final long serialVersionUID = -9132574510082841750L;
    private static Logger logger = LoggerFactory.getLogger(UploadPanel.class);

    private final DatasetModel datasetModel;

    @SpringBean(name = "itemService")
    private ItemService itemService;

    public UploadPanel(String id, DatasetModel datasetModel) {
        super(id);
        this.datasetModel = datasetModel;
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel() {
        if (isInEditMode()) {
            return new UploadModePanel();
        } else {
            DmoStoreId datasetId = datasetModel.getObject().getDmoStoreId();
            List<FileItemVO> fileItemVOs = new ArrayList<FileItemVO>();
            try {
                EasyUser currentUser = ((EasySession) getSession()).getUser();
                itemService.getFileItemsRecursively(currentUser, datasetModel.getObject(), fileItemVOs, null, datasetId);
            }
            catch (ServiceException e) {
                logger.error(e.getMessage());
            }
            if (fileItemVOs.isEmpty()) {
                super.setVisible(false);
            }
            return new ViewModePanel(fileItemVOs);
        }
    }

    class UploadModePanel extends Panel {

        private static final long serialVersionUID = -1141097831590702485L;

        public UploadModePanel() {
            super(CUSTOM_PANEL_ID);

            this.add(new DepositUploadPanel("uploadPanel", datasetModel));
        }

    }

    class ViewModePanel extends Panel {

        private static final long serialVersionUID = -1141097831590702485L;

        public ViewModePanel(List<FileItemVO> fileItemVOs) {
            super(CUSTOM_PANEL_ID);

            ListView<FileItemVO> listView = new ListView<FileItemVO>("uploadedfiles", fileItemVOs) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(ListItem<FileItemVO> item) {
                    FileItemVO fileItemVO = item.getModelObject();
                    item.add(new Label("path", fileItemVO.getPath()));
                    item.add(new Label("checksum", fileItemVO.getSha1Checksum()));
                }
            };
            add(listView);
        }
    }

}
