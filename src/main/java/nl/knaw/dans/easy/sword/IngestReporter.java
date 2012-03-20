package nl.knaw.dans.easy.sword;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.worker.WorkReporter;

import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IngestReporter extends WorkReporter
{

    List<Throwable>       reportedExceptions = new ArrayList<Throwable>();
    private final String  message;
    private static Logger logger             = LoggerFactory.getLogger(IngestReporter.class);

    IngestReporter(final String message)
    {
        this.message = message;
    }

    @Override
    public boolean onIngest(final DataModelObject dmo)
    {
        super.onIngest(dmo);
        logger.debug("ingesting " + dmo.getLabel() + " " + dmo.getStoreId() + " " + message);
        return false;
    }

    @Override
    public boolean onUpdate(final DataModelObject dmo)
    {
        logger.debug("updating " + dmo.getLabel() + " " + dmo.getStoreId() + " " + message);
        return false;
    }

    @Override
    public void onException(final Throwable t)
    {
        super.onException(t);
        logger.error("problem with " + message, t);
        reportedExceptions.add(t);
    }

    public boolean checkOK() throws SWORDException
    {
        logger.debug(" exceptions: \n" + reportedExceptions.size() + "\n" + super.toString());
        return reportedExceptions.size() == 0;
    }
}
