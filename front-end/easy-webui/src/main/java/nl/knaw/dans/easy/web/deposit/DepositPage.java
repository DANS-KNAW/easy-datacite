package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetSpecification;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequireHttps
public class DepositPage extends AbstractEasyNavPage {
    private static final Logger logger = LoggerFactory.getLogger(DepositPage.class);

    public static final String PM_DATASET_ID = "datasetId";
    public static final String PM_FORMDEFINITION_ID = "formDefinitionId";

    private DepositPanel depositPanel;

    @SpringBean(name = "datasetService")
    private DatasetService datasetService;

    @SpringBean(name = "depositService")
    private DepositService depositService;

    /**
     * Constructor used by DepositIntroPage. Creates new a Dataset (and new EasyMetadata).
     * 
     * @param discipline
     * @param formDefinitionId
     *        see formDefintions in src/main/resources/conf/discipline/emd/form-description and DepositDiscipline.EMD_DEPOSITFORM_...
     */
    public DepositPage(final DepositDiscipline discipline, final String formDefinitionId) {
        Dataset dataset;
        try {
            dataset = datasetService.newDataset(discipline.getMetadataFormat());
            super.setDefaultModel(new DatasetModel(dataset));
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.DATASET_CREATION);
            logger.error(message, e);
            throw new RestartResponseException(new ErrorPage());
        }

        // initialize the dataset
        dataset.getAdministrativeMetadata().setDepositor(getSessionUser());

        dataset.getEasyMetadata().getEmdDate().getEasAvailable().add(new IsoDate());

        // do some initialization on the metadata:
        DatasetSpecification.completeEasyMetadata(dataset.getEasyMetadata());

        init(discipline, formDefinitionId);
        if (logger.isDebugEnabled()) {
            logger.debug("Created new EasyMetadata for MetadataFormat " + discipline.getMetadataFormat());
        }
    }

    public DepositPage(final String datasetId) {
        this(datasetId, null);
    }

    public DepositPage(final String datasetId, final String formDefinitionId) {
        try {
            DatasetModel model = new DatasetModel(datasetId);
            super.setDefaultModel(model);
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.DATASET_RETRIEVAL, datasetId);
            logger.error(message, e);
            throw new RestartResponseException(new ErrorPage());
        }

        DepositDiscipline discipline = getDiscipline();
        init(discipline, formDefinitionId);
    }

    public DepositPage(DatasetModel datasetModel, final String formDefinitionId) {
        super(new DatasetModel(datasetModel));
        DepositDiscipline discipline = getDiscipline();
        init(discipline, formDefinitionId);
    }

    public DepositPage(DatasetModel datasetModel) {
        super(new DatasetModel(datasetModel));
        DepositDiscipline discipline = getDiscipline();
        init(discipline, null);
    }

    private DepositDiscipline getDiscipline() {
        EasyMetadata easyMetadata = getDataset().getEasyMetadata();
        MetadataFormat emdFormat = easyMetadata.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        DepositDiscipline discipline;
        try {
            discipline = depositService.getDiscipline(emdFormat);
        }
        catch (ServiceException e) {
            final String message = errorMessage(EasyResources.DISCIPLINE_RETRIEVAL, getDataset().getStoreId());
            logger.error(message, e);
            throw new RestartResponseException(new ErrorPage());
        }
        return discipline;
    }

    private Dataset getDataset() {
        return (Dataset) getDefaultModelObject();
    }

    private void init(DepositDiscipline discipline, String formDefinitionId) {
        String definitionId = formDefinitionId;
        if (definitionId == null) {
            definitionId = DepositDiscipline.EMD_DEPOSITFORM_WIZARD;
        }
        depositPanel = new DepositPanel("depositPanel", discipline, definitionId, (DatasetModel) getDefaultModel());
        add(depositPanel);

        // Disable dynamic reload. We don't want the dataset reloading automatically
        // just before saving. We want to save it in exactly the way it was presented.
        getDatasetModel().setDynamicReload(false);
    }

    protected DatasetModel getDatasetModel() {
        return ((DatasetModel) getDefaultModel());
    }

}
