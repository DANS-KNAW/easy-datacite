package nl.knaw.dans.easy.web.deposit;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.DepositUploadPanel;

import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPanel extends AbstractCustomPanel
{

    private static final long serialVersionUID = -9132574510082841750L;
    private static Logger logger = LoggerFactory.getLogger(UploadPanel.class);

    private final DatasetModel datasetModel;

    public UploadPanel(String id, DatasetModel datasetModel)
    {
        super(id);
        this.datasetModel = datasetModel;
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        if (isInEditMode())
        {
            return new UploadModePanel();
        }
        else
        {
            DmoStoreId datasetId = datasetModel.getObject().getDmoStoreId();
            List<String> list = new ArrayList<String>();
            try
            {
                list = Services.getItemService().getFilenames(datasetId, true);
                if (list == null || list.isEmpty())
                {
                    super.setVisible(false);
                }
            }
            catch (ServiceException e)
            {
                logger.error(e.getMessage());
            }
            return new ViewModePanel(list);
        }
    }

    class UploadModePanel extends Panel
    {

        private static final long serialVersionUID = -1141097831590702485L;

        public UploadModePanel()
        {
            super(CUSTOM_PANEL_ID);

            this.add(new DepositUploadPanel("uploadPanel", datasetModel));
        }

    }

    class ViewModePanel extends Panel
    {

        private static final long serialVersionUID = -1141097831590702485L;

        public ViewModePanel(List<String> list)
        {
            super(CUSTOM_PANEL_ID);
            String values = "";
            for (String s : list)
            {
                values += s + "\n";
            }
            MultiLineLabel label = new MultiLineLabel("noneditable", values);
            add(label);

        }

    }

}
