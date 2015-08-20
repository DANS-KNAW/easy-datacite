package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class DatasetLoggingFilter implements DmoFilter<Dataset> {
    private static final Logger log = LoggerFactory.getLogger(DatasetLoggingFilter.class);

    private final int datasets;
    private int datasetCounter = 0;
    private int helper = 1;

    public DatasetLoggingFilter() {
        this(0);
    }

    public DatasetLoggingFilter(int datasets) {
        this.datasets = datasets;
    }

    @Override
    public boolean accept(Dataset dmo) {
        datasetCounter++;
        String datasetId = dmo.getDmoStoreId().getStoreId();
        String rightsHolder = getMetadataFieldListAsString(dmo.getEasyMetadata().getEmdRights().getTermsRightsHolder());
        String dcRights = getMetadataFieldListAsString(dmo.getEasyMetadata().getEmdRights().getDcRights());
        String datasetState = dmo.getAdministrativeState().toString();
        String accessCategory = dmo.getAccessCategory().toString();

        if (datasetCounter == helper) {
            log.info("[{}] Proccessing dataset with ID[{}]; Rightsholder[{}]; DC Rights[{}]; DatasetState[{}]; AccessCategory[{}]", datasetCounter, datasetId,
                    rightsHolder, dcRights, datasetState, accessCategory);
            RL.info(new Event(getClass().getSimpleName(), String.format(
                    "[%d] Proccessing dataset with ID[%s]; Rightsholder[%s]; DC Rights[%s]; DatasetState[%s]; AccessCategory[%s]", datasetCounter, datasetId,
                    rightsHolder, dcRights, datasetState, accessCategory)));
            helper = datasetCounter + datasets + 1;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getName());
        sb.append(" <");
        sb.append(datasetCounter);
        sb.append(">");

        return sb.toString();
    }

    private String getMetadataFieldListAsString(List<BasicString> metadataFieldList) {
        String metadataField = "";
        for (int i = 0; i < metadataFieldList.size(); i++) {
            metadataField += String.format("[%d]'%s';", i + 1, metadataFieldList.get(i).toString());
        }
        return metadataField;
    }
}
