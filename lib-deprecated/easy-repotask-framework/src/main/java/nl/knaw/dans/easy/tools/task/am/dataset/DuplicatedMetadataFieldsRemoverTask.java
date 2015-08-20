package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdTitle;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class DuplicatedMetadataFieldsRemoverTask extends AbstractDatasetTask {
    private int numberOfDatasetsModified;
    private boolean testMode;
    private boolean changedDataset;

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
        UnitOfWork uow = new EasyUnitOfWork(currentDataset.getDepositor());
        String currentStoreId = currentDataset.getStoreId();
        EasyMetadata emd = currentDataset.getEasyMetadata();
        EmdTitle emdTitle = emd.getEmdTitle();
        List<BasicString> alternativeTitles = emdTitle.getTermsAlternative();

        if (alternativeTitles != null && !alternativeTitles.isEmpty()) { // only archaeology dataset has alternative title
            removeCopiedAlternativeTitle(currentDataset, currentStoreId, emdTitle, alternativeTitles);

            if (!isTestMode() && changedDataset) {
                try {
                    uow.attach(currentDataset);
                    uow.commit();
                    RL.info(new Event(getTaskName(), "Saved " + currentStoreId));

                }
                catch (RepositoryException e) {
                    RL.error(new Event(getTaskName(), e));
                }
                catch (UnitOfWorkInterruptException e) {
                    RL.error(new Event(getTaskName(), e));
                }
                finally {
                    changedDataset = false;
                }
            }
        }
    }

    /**
     * @param currentDataset
     * @param currentStoreId
     * @param emd
     * @param alternativeTitles
     */
    private void removeCopiedAlternativeTitle(Dataset currentDataset, String currentStoreId, EmdTitle emdTitle, List<BasicString> alternativeTitles) {
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
        // @formatter: on

        List<BasicString> titles = emdTitle.getDcTitle();
        // Archaeology has only one title.
        if (titles.size() > 1 && alternativeTitles.size() == titles.size() - 1)
        {

            for (int i = 0; i < alternativeTitles.size(); i++)
            {
                String titleElement = titles.get(i + 1).getValue();
                String alternativeTitleElement = alternativeTitles.get(i).getValue();
                if (!alternativeTitleElement.equals(titleElement))
                {
                    if (i > 0)
                    {
                        RL.warn(new Event(getTaskName(), "Alternative titles partially the same as titles, but not up to the end. StoreId: " + currentStoreId));
                    }
                    return;
                }
            }

            RL.info(new Event(getTaskName(), "Removing second and further elements from title, because they are equal to the alternative titles"));
            numberOfDatasetsModified++;
            if (!isTestMode())
            {
                for (int i = titles.size() - 1; i > 0; i--)
                {
                    titles.remove(i);
                    changedDataset = true;
                }
            }
        }
    }

    @Override
    public void close() throws TaskException, TaskCycleException, FatalTaskException
    {
        super.close();

        StringBuffer sb = new StringBuffer("\n===========\nReport\n");
        sb.append("Number of datasets modified: " + numberOfDatasetsModified + "\n");
        sb.append("===================\n");
        RL.info(new Event(getTaskName(), sb.toString()));
    }

    public boolean isTestMode()
    {
        return testMode;
    }

    public void setTestMode(boolean testMode)
    {
        this.testMode = testMode;
    }
}
