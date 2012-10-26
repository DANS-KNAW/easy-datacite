package nl.knaw.dans.easy.web.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineStatistics extends StatisticsModel<Dataset>
{
    private static final Logger logger = LoggerFactory.getLogger(DisciplineStatistics.class);

    public DisciplineStatistics(Dataset dataset)
    {
        super(dataset);
    }

    @SuppressWarnings("unchecked")
    private void getDisciplines(List<DisciplineContainer> disciplines, ArrayList<DisciplineContainer> res) throws RepositoryException
    {
        if (disciplines == null || disciplines.size() <= 0)
        {
            return;
        }
        else
        {
            for (DisciplineContainer dc : disciplines)
            {
                if (dc.getLabel() != null)
                {
                    res.add(dc);
                }
                getDisciplines((List<DisciplineContainer>) dc.getParents(), res);
            }
        }
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();
        try
        {
            ArrayList<DisciplineContainer> disciplines = new ArrayList<DisciplineContainer>();
            getDisciplines(getObject().getParentDisciplines(), disciplines);
            // it is possible that a dataset might not be connected with a discipline if it
            // a draft dataset for example
            if (disciplines.size() > 0)
            {
                DisciplineContainer subDiscipline = disciplines.get(0);
                DisciplineContainer topDiscipline = disciplines.get(disciplines.size() - 1);
                res.put("SUB_DISCIPLINE_ID", subDiscipline.getStoreId());
                res.put("SUB_DISCIPLINE_LABEL", subDiscipline.getLabel());
                res.put("TOP_DISCIPLINE_ID", topDiscipline.getStoreId());
                res.put("TOP_DISCIPLINE_LABEL", topDiscipline.getLabel());
            }
        }
        catch (RepositoryException e)
        {
            logger.error(e.getMessage());
        }
        catch (ObjectNotFoundException e)
        {
            logger.error(e.getMessage());
        }
        catch (DomainException e)
        {
            logger.error(e.getMessage());
        }

        return res;
    }

    @Override
    public String getName()
    {
        return "discipline";
    }
}
