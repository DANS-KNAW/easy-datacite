package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

public class CopiedMetadataAlternativeTitleCheckerTask extends AbstractDatasetTask {
    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        /*
         * Filtering the dataset status is done on the xxx-tasks-context.xml The dataset status on this task: - PUBLISHED - MAINTENANCE - SUBMITTED The Emd
         * object, EmdTitle object and dcTitle object is never null for those status. So, no null check is needed for those object.
         */
        Dataset currentDataset = joint.getDataset();
        String currentStoreId = currentDataset.getStoreId();
        EasyMetadata emd = currentDataset.getEasyMetadata();
        List<BasicString> alternativeTitles = emd.getEmdTitle().getTermsAlternative();
        // only archaeology dataset has alternative title
        if (alternativeTitles != null && !alternativeTitles.isEmpty()) {
            // @formatter:off
            /*
             * What is the case? The alternative title sometimes is copied to the title Example:
             * <emd:title> <dc:title>Oude IJsselstreek Engbergen EVZ Oude IJssel Booronderzoek</dc:title>
             * <dc:title>EVZ Oude IJssel, Engbergen, gemeente Oude IJsselstreek</dc:title> <dc:title>Een
             * Bureauonderzoek en Inventariserend Veldonderzoek in de vorm van een verkennend
             * booronderzoek</dc:title> <dcterms:alternative>EVZ Oude IJssel, Engbergen, gemeente Oude
             * IJsselstreek</dcterms:alternative> <dcterms:alternative>Een Bureauonderzoek en
             * Inventariserend Veldonderzoek in de vorm van een verkennend
             * booronderzoek</dcterms:alternative> </emd:title>
             */
            // @formatter:on
            EmdTitle emdTitle = emd.getEmdTitle();
            List<BasicString> dcTitle = emdTitle.getDcTitle();
            // Archaeology has only one title.
            if (dcTitle.size() > 1) {
                String copiedAltTileInTitle = "";
                for (int i = 1; i < dcTitle.size(); i++) {
                    copiedAltTileInTitle += dcTitle.get(i).getValue();
                }
                String altTitle = "";
                for (BasicString alternativeTitle : alternativeTitles) {
                    altTitle += alternativeTitle.getValue();
                }
                if (!copiedAltTileInTitle.equals("") && copiedAltTileInTitle.equals(altTitle)) {
                    IsoDate dateSubmitted = currentDataset.getDateSubmitted();
                    RL.error(new Event(getTaskName(), "Alternative title is copied to the title for the storeId: " + currentStoreId + ". Submitted on "
                            + dateSubmitted.getValueAsString()));
                } else {
                    // Something goes wrong, but others than the jira issue has described.
                    RL.error(new Event(getTaskName(), "The storeId : " + currentStoreId
                            + " 	has error on the title but others that the jira issue has described"));
                }
            } else {
                // for junit purpose
                RL.info(new Event(getTaskName(), "Title is ok."));

            }
        }

    }
}
