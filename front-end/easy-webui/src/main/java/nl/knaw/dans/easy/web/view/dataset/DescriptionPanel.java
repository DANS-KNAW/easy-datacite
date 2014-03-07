package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.web.RedirectData;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.DepositPage;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.view.dataset.emd.EasyMetadataViewPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescriptionPanel extends AbstractDatasetModelPanel
{

    private static final long serialVersionUID = 3936840351761057765L;

    private static final Logger logger = LoggerFactory.getLogger(DescriptionPanel.class);

    private boolean initiated;

    public DescriptionPanel(String id, DatasetModel model)
    {
        super(id, model);
    }

    public boolean isInitiated()
    {
        return initiated;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        final EasyMetadata emd = getDataset().getEasyMetadata();

        add(new EasyMetadataViewPanel("easyMetadataPanel", getDatasetModel()));

        add(new DownloadPanel("downloadPanel", emd));

        Link editLink = new Link("editLink")
        {

            private static final long serialVersionUID = -475314441520496889L;

            @Override
            public void onClick()
            {
                logger.debug("editLink clicked");
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(DatasetViewPage.PM_DATASET_ID, getDataset().getStoreId());
                getEasySession().setRedirectData(DepositPage.class, new RedirectData(DatasetViewPage.class, pageParameters));
                Page depositPage = new DepositPage(getDataset().getStoreId(), DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST);
                setResponsePage(depositPage);
            }

        };
        editLink.setVisible(!DatasetState.PUBLISHED.equals(getDataset().getAdministrativeState()));
        add(editLink);
    }
}
