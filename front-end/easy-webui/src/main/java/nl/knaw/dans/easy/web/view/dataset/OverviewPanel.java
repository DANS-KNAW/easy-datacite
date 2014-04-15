package nl.knaw.dans.easy.web.view.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.jumpoff.JumpoffPanel;
import nl.knaw.dans.common.wicket.components.jumpoff.ResourceRef;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.JumpoffService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.RelationInfoPanel;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverviewPanel extends AbstractEasyPanel
{

    public static final String SUMMARY = "summary";

    private static final long serialVersionUID = 737305119989771486L;

    private static final Logger logger = LoggerFactory.getLogger(OverviewPanel.class);

    private final Dataset dataset;

    @SpringBean(name = "jumpoffService")
    private JumpoffService jumpoffService;

    public OverviewPanel(String wicketId, Dataset dataset)
    {
        super(wicketId);
        this.dataset = dataset;
        init();
    }

    private void init()
    {
        add(new SummaryPanel(SUMMARY, dataset));
        add(new RelationInfoPanel("relationInfo", dataset.getEasyMetadata().getEmdRelation()));
        add(new JumpoffPanel("jumpoffPanel", dataset, EasyWicketApplication.WICKET_APPLICATION_ALIAS)
        {

            private static final long serialVersionUID = -1007009673871749198L;

            @Override
            public void deleteJumpoffDmo(DataModelObject targetDmo, JumpoffDmo jumpoffDmo)
            {
                try
                {
                    Services.getJumpoffService().deleteJumpoff(getSessionUser(), targetDmo, jumpoffDmo);
                }
                catch (ServiceException e)
                {
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new RestartResponseException(ErrorPage.class);
                }
            }

            @Override
            public void deleteResource(DataModelObject targetDmo, ResourceRef resourceRef)
            {
                try
                {
                    Services.getJumpoffService().deleteMetadataUnit(getSessionUser(), new DmoStoreId(resourceRef.getContainerId()),
                            new DsUnitId(resourceRef.getUnitId()));
                }
                catch (ServiceException e)
                {
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new RestartResponseException(ErrorPage.class);
                }
            }

            @Override
            public JumpoffDmo getJumpoffDmoFor(DataModelObject targetDmo)
            {
                try
                {
                    return jumpoffService.getJumpoffDmoFor(getSessionUser(), targetDmo.getDmoStoreId());
                }
                catch (ServiceException e)
                {
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new RestartResponseException(ErrorPage.class);
                }
            }

            @Override
            public List<UnitMetadata> getUnitMetadata(JumpoffDmo jumpoffDmo)
            {
                try
                {
                    return Services.getJumpoffService().getUnitMetadata(getSessionUser(), jumpoffDmo);
                }
                catch (ServiceException e)
                {
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new RestartResponseException(ErrorPage.class);
                }
            }

            @Override
            public void saveJumpoffDmo(DataModelObject targetDmo, JumpoffDmo jumpoffDmo)
            {
                try
                {
                    Services.getJumpoffService().saveJumpoffDmo(getSessionUser(), targetDmo, jumpoffDmo);
                }
                catch (ServiceException e)
                {
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new RestartResponseException(ErrorPage.class);
                }

            }

            @Override
            public void toggleEditorMode(JumpoffDmo jumpoffDmo)
            {
                try
                {
                    Services.getJumpoffService().toggleEditorMode(getSessionUser(), jumpoffDmo);
                }
                catch (ServiceException e)
                {
                    final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                    logger.error(message, e);
                    throw new RestartResponseException(ErrorPage.class);
                }

            }

        });
    }
}
