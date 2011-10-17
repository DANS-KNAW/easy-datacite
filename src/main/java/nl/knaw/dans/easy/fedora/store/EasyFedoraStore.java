package nl.knaw.dans.easy.fedora.store;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.fedora.rdf.FedoraURIReference;
import nl.knaw.dans.common.fedora.store.FedoraDmoStore;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.reposearch.RepoSearchListener;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.data.store.EasyDmoContext;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.DescriptiveMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;

import org.joda.time.DateTime;
import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.server.types.gen.MIMETypedStream;

public class EasyFedoraStore extends FedoraDmoStore implements EasyStore
{

    private static final long      serialVersionUID = 1288905408847378535L;
    

    public EasyFedoraStore(String name, final Fedora fedora)
    {
        this(name, fedora, null);
    }

    public EasyFedoraStore(String name, final Fedora fedora, final SearchEngine searchEngine)
    {
        super(name, fedora, new EasyDmoContext());

        addConverter(new DatasetConverter());
        addConverter(new FolderItemConverter());
        addConverter(new FileItemConverter());
        addConverter(new DisciplineContainerConverter());
        addConverter(new DownloadHistoryConverter());
        addConverter(new CommonDatasetConverter());
        

        if (searchEngine != null)
            addEventListener(new RepoSearchListener(searchEngine));

    }
    
    public URL getFileURL(String storeId)
    {
        return getStreamURL(storeId, EasyFile.UNIT_ID);
    }
    
    @Override
    public URL getStreamURL(String storeId, String streamId)
    {
        URL url = null;
        final String spec = getFedora().getBaseURL() + "/get/" + storeId + "/" + streamId;
        try
        {
            url = new URL(spec);
        }
        catch (MalformedURLException e)
        {
            throw new ApplicationException(e);
        }
        return url;
    }

    public URL getDescriptiveMetadataURL(String storeId)
    {
        final String spec = getFedora().getBaseURL() + "/get/" + storeId + "/" + DescriptiveMetadataImpl.UNIT_ID;
        try
        {
            return new URL(spec);
        }
        catch (MalformedURLException e)
        {
            throw new ApplicationException(e);
        }
    }

    public DownloadHistory findDownloadHistoryFor(DataModelObject objectDmo, String period) throws RepositoryException
    {
        return findDownloadHistoryFor(objectDmo.getStoreId(), period);
    }

    public DownloadHistory findDownloadHistoryFor(String objectStoreId, String period) throws RepositoryException
    {
        DownloadHistory dlh = null;
        String dmoObjectRef = FedoraURIReference.create(objectStoreId);
        String query = createDownloadHistoryQuery(dmoObjectRef, period);
        try
        {
            TupleIterator tupleIterator = execSparql(query);
            if (tupleIterator.hasNext())
            {
                Map<String, Node> row = tupleIterator.next();
                String subject = row.get("s").toString();
                String subjectStoreId = FedoraURIReference.strip(subject);
                dlh = (DownloadHistory) retrieve(subjectStoreId);
            }
        }
        catch (IOException e)
        {
            throw new RepositoryException(e);
        }
        catch (TrippiException e)
        {
            throw new RepositoryException(e);
        }
        return dlh;
    }

    public List<DownloadHistory> findDownloadHistoryFor(DataModelObject dmo) throws RepositoryException
    {
        List<DownloadHistory> dlhList = new ArrayList<DownloadHistory>();
        String dmoObjectRef = FedoraURIReference.create(dmo.getStoreId());
        String query = createDownloadHistoryQuery(dmoObjectRef);
        try
        {
            TupleIterator tupleIterator = execSparql(query);
            while (tupleIterator.hasNext())
            {
                Map<String, Node> row = tupleIterator.next();
                String subject = row.get("s").toString();
                String storeId = FedoraURIReference.strip(subject);
                DownloadHistory dlh = (DownloadHistory) retrieve(storeId);
                dlhList.add(dlh);
            }
        }
        catch (IOException e)
        {
            throw new RepositoryException(e);
        }
        catch (TrippiException e)
        {
            throw new RepositoryException(e);
        }
        return dlhList;
    }

    public EasyMetadata getEasyMetaData(String storeId, DateTime asOfDateTime) throws RepositoryException
    {
        final MIMETypedStream mts = getFedora().getDatastreamAccessor().getDatastreamDissemination(storeId, EasyMetadata.UNIT_ID, asOfDateTime);
        try
        {
            return (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, mts.getStream());
        }
        catch (final XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }
    
    protected static String createDownloadHistoryQuery(String dmoObjectRef, String period)
    {
        return new StringBuilder("select ?s from <#ri> where {?s <")//
                .append(RelsConstants.DANS_NS.HAS_DOWNLOAD_HISTORY_OF.stringValue())//
                .append("> <")//
                .append(dmoObjectRef)//
                .append("> . ")//
                .append("?s <")//
                .append(RelsConstants.DANS_NS.HAS_PERIOD.stringValue())//
                .append("> \"")//
                .append(period)//
                .append("\"^^<")//
                .append(RelsConstants.RDF_LITERAL)//
                .append("> . }")//
                .toString();

    }

    protected static String createDownloadHistoryQuery(String dmoObject)
    {
        return new StringBuilder("select ?s from <#ri> where {?s <")//
                .append(RelsConstants.DANS_NS.HAS_DOWNLOAD_HISTORY_OF.stringValue())//
                .append("> <")//
                .append(dmoObject)//
                .append("> . }")//
                .toString();
    }

}
