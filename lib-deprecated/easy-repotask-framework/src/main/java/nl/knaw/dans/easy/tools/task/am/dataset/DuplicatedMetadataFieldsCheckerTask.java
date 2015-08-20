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
import nl.knaw.dans.pf.language.emd.EmdDescription;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

public class DuplicatedMetadataFieldsCheckerTask extends AbstractDatasetTask {
    private static int numberOfCopiedAltTitle;
    private static int numberOfOtherTitleErrorsOfArchTitle;
    private static int numberOfEmptyOrNullTitle;
    private static int numberOfDuplicatedTitle;
    private static int numberOfEmptyOrNullDescription;
    private static int numberOfDuplicatedDescription;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        // @formatter:off
        /*
         * Filtering the dataset status is done on the xxx-tasks-context.xml The dataset status on this
         * task: - PUBLISHED - MAINTENANCE - SUBMITTED The Emd object, EmdTitle object and dcTitle object
         * is never null for those status. So, null check isn't needed for those object.
         */
        // @formatter:on
        Dataset currentDataset = joint.getDataset();
        String currentStoreId = currentDataset.getStoreId();
        EasyMetadata emd = currentDataset.getEasyMetadata();
        EmdTitle emdTitle = emd.getEmdTitle();
        List<BasicString> alternativeTitles = emdTitle.getTermsAlternative();

        if (alternativeTitles != null && !alternativeTitles.isEmpty()) { // only archaeology dataset has alternative title
            checkCopiedAlternativeTitle(currentDataset, currentStoreId, emdTitle, alternativeTitles);
        } else {
            checkDuplicateTitle(currentDataset, currentStoreId, emdTitle);
        }
        checkDuplicateDescription(currentDataset, currentStoreId, emd);

    }

    /**
     * @param currentDataset
     * @param currentStoreId
     * @param emd
     * @param alternativeTitles
     */
    private void checkCopiedAlternativeTitle(Dataset currentDataset, String currentStoreId, EmdTitle emdTitle, List<BasicString> alternativeTitles) {
        // @formatter:off
        /*
         * What is the case? The alternative title sometimes is copied to the title Example: <emd:title>
         * <dc:title>Oude IJsselstreek Engbergen EVZ Oude IJssel Booronderzoek</dc:title> <dc:title>EVZ
         * Oude IJssel, Engbergen, gemeente Oude IJsselstreek</dc:title> <dc:title>Een Bureauonderzoek en
         * Inventariserend Veldonderzoek in de vorm van een verkennend booronderzoek</dc:title>
         * <dcterms:alternative>EVZ Oude IJssel, Engbergen, gemeente Oude
         * IJsselstreek</dcterms:alternative> <dcterms:alternative>Een Bureauonderzoek en Inventariserend
         * Veldonderzoek in de vorm van een verkennend booronderzoek</dcterms:alternative> </emd:title>
         */
        // @formatter:on

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
                RL.error(new Event(getTaskName(), "Copied alternative title is found. StoreId: " + currentStoreId + ". Submitted on "
                        + dateSubmitted.getValueAsString()));
                numberOfCopiedAltTitle++;
            }
        } else {
            // for junit purpose
            RL.info(new Event(getTaskName(), "Title is ok."));

        }
    }

    /**
     * @param currentDataset
     * @param currentStoreId
     * @param emdTitle
     */
    private void checkDuplicateTitle(Dataset currentDataset, String currentStoreId, EmdTitle emdTitle) {
        List<BasicString> dcTitle = emdTitle.getDcTitle();
        if (dcTitle == null || dcTitle.isEmpty()) {
            IsoDate dateSubmitted = currentDataset.getDateSubmitted();
            RL.error(new Event(getTaskName(), "Title is null or empty for the storeId: " + currentStoreId + ". Submitted on "
                    + dateSubmitted.getValueAsString()));
            numberOfEmptyOrNullTitle++;
        } else {
            // Check whether any duplicated title or not.
            for (BasicString bsTitle1 : dcTitle) {
                int numberOfDuplicate = 0;
                String title1 = bsTitle1.getValue();
                for (BasicString bsTitle2 : dcTitle) {
                    String title2 = bsTitle2.getValue();
                    if (title1.equals(title2)) {
                        numberOfDuplicate++;
                    }
                }

                if (numberOfDuplicate > 1) {
                    IsoDate dateSubmitted = currentDataset.getDateSubmitted();
                    RL.error(new Event(getTaskName(), "Duplicated title is found. StoreId: " + currentStoreId + ". Submitted on "
                            + dateSubmitted.getValueAsString()));
                    numberOfDuplicatedTitle++;
                    // The duplicate check will not be continued when one duplicated title is found.
                    break;
                }
            }
        }
    }

    /**
     * @param currentDataset
     * @param currentStoreId
     * @param emd
     */
    private void checkDuplicateDescription(Dataset currentDataset, String currentStoreId, EasyMetadata emd) {
        EmdDescription ed = emd.getEmdDescription();
        List<BasicString> dcDescs = ed.getDcDescription();
        if (dcDescs == null || dcDescs.isEmpty()) {
            IsoDate dateSubmitted = currentDataset.getDateSubmitted();
            RL.error(new Event(getTaskName(), "Description is null or empty for the storeId: " + currentStoreId + ". Submitted on "
                    + dateSubmitted.getValueAsString()));
            numberOfEmptyOrNullDescription++;
        } else {
            if (dcDescs.size() > 1) {
                // Check whether any duplicated description or not.
                for (BasicString bsDescs1 : dcDescs) {
                    int numberOfDuplicate = 0;
                    String title1 = bsDescs1.getValue();
                    for (BasicString bsDescs2 : dcDescs) {
                        String title2 = bsDescs2.getValue();
                        if (title1.equals(title2)) {
                            numberOfDuplicate++;
                        }
                    }

                    if (numberOfDuplicate > 1) {
                        IsoDate dateSubmitted2 = currentDataset.getDateSubmitted();
                        RL.error(new Event(getTaskName(), "Duplicated description is found for the storeId: " + currentStoreId + ". Submitted on "
                                + dateSubmitted2.getValueAsString()));
                        numberOfDuplicatedDescription++;
                        // The duplicate check will not be continued when one duplicated title is found.
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void close() throws TaskException, TaskCycleException, FatalTaskException {
        super.close();

        StringBuffer sb = new StringBuffer("\n===========\nReport\n");
        sb.append("Copied alternative titles: " + numberOfCopiedAltTitle + "\n");
        sb.append("Other title errors in archaelogy metadata: " + numberOfOtherTitleErrorsOfArchTitle + "\n");
        sb.append("Empty or null value title: " + numberOfEmptyOrNullTitle + "\n");
        sb.append("Duplicated title of non archaeology metadata: " + numberOfDuplicatedTitle + "\n");
        sb.append("Empty or null value description: " + numberOfEmptyOrNullDescription + "\n");
        sb.append("Duplicated description: " + numberOfDuplicatedDescription + "\n");
        sb.append("===================\n");
        RL.info(new Event(getTaskName(), sb.toString()));
    }
}
