package nl.knaw.dans.fcrepo.oai;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.trippi.TupleIterator;

import proai.MetadataFormat;
import proai.SetInfo;
import proai.driver.RemoteIterator;
import proai.error.RepositoryException;
import fedora.client.FedoraClient;
import fedora.server.utilities.DateUtility;
import fedora.services.oaiprovider.FedoraMetadataFormat;
import fedora.services.oaiprovider.FedoraRecord;
import fedora.services.oaiprovider.FedoraSetInfoIterator;
import fedora.services.oaiprovider.InvocationSpec;
import fedora.services.oaiprovider.QueryFactory;

public class SparqlQueryFactory implements QueryFactory
{

    private static final Logger logger = Logger.getLogger(SparqlQueryFactory.class);

    private FedoraClient m_fedora;

    private SparqlQueryComposer composer;

    private RiConnector riConnector;

    @Override
    public void init(FedoraClient client, FedoraClient queryClient, Properties props)
    {
        m_fedora = client;
        composer = new SparqlQueryComposer(props);
        riConnector = new RiConnector(queryClient);
    }

    protected SparqlQueryComposer getComposer()
    {
        if (composer == null)
        {
            throw new IllegalStateException(this.getClass().getName() + " was not properly initialized");
        }
        return composer;
    }

    protected RiConnector getRiConnector()
    {
        if (riConnector == null)
        {
            throw new IllegalStateException(this.getClass().getName() + " was not properly initialized");
        }
        return riConnector;
    }

    /**
     * Rather than querying for this information, which can be costly, simply return the current date.
     * 
     * @param formats
     *        iterator over all FedoraMetadataFormats
     * @return current date according to Fedora
     */
    public Date latestRecordDate(Iterator<? extends MetadataFormat> formats) throws RepositoryException
    {
        Date current = new Date();
        logger.info("Latest last-modified date not queried. Current date in UTC is " + DateUtility.convertDateToString(current));
        return current;
    }

    /**
     * Value of property driver.fedora.setSpec.desc.dissType is ignored and assumed to be:
     * info:fedora/{star}/SetInfo.xml
     */
    @Override
    public RemoteIterator<SetInfo> listSetInfo(InvocationSpec setInfoSpec)
    {
        FedoraSetInfoIterator setInfoIterator;
        if (composer.isQueryingForSetInfo())
        {
            logger.info("Starting setInfoQuery. <=== ");
            String listSetInfoQuery = getComposer().getListSetInfoQuery();
            logger.debug("Calling RiConnector.getTuples() with query:\n" + listSetInfoQuery + "\n");
            TupleIterator tuples = getRiConnector().getTuples(listSetInfoQuery);
            setInfoIterator = new FedoraSetInfoIterator(m_fedora, tuples);
        }
        else
        {
            setInfoIterator = new FedoraSetInfoIterator();
        }
        return setInfoIterator;
    }

    @Override
    public RemoteIterator<FedoraRecord> listRecords(Date from, Date until, FedoraMetadataFormat format)
    {
        String fromUTC = getDateString(from);
        String untilUTC = getDateString(until);
        String primaryQuery = getComposer().getListRecordsPrimaryQuery(fromUTC, untilUTC, format);
        logger.info("Starting primaryQuery. <=== " + format.getPrefix() + " " + fromUTC + " - " + untilUTC);
        ListRecordIterator remoteIterator;
        try
        {
            File primaryFile = getRiConnector().getCSVResults(primaryQuery);
            remoteIterator = new ListRecordIterator(getComposer(), getRiConnector(), primaryFile, format);
        }
        catch (RepositoryException e)
        {
            logger.error("Could not list records: ", e);
            remoteIterator = new ListRecordIterator(null, null, null, null);
        }
        catch (IOException e)
        {
            logger.error("Could not close tempFile: ", e);
            remoteIterator = new ListRecordIterator(null, null, null, null);
        }
        return remoteIterator;
    }

    protected String getDateString(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return DateUtility.convertDateToString(date);
    }

}
