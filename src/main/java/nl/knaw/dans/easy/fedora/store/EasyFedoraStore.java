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
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.DatasetFactory;
import nl.knaw.dans.easy.domain.dataset.DescriptiveMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.dataset.FileItemFactory;
import nl.knaw.dans.easy.domain.dataset.FolderItemFactory;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadHistoryFactory;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerFactory;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;

import org.joda.time.DateTime;
import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.server.types.gen.MIMETypedStream;

public class EasyFedoraStore extends FedoraDmoStore implements EasyStore
{

    private static final long serialVersionUID = 1288905408847378535L;

    public EasyFedoraStore(String name, final Fedora fedora)
    {
        super(name, fedora);

        AbstractDmoFactory.register(new DatasetFactory());
        addConverter(new DatasetConverter());

        AbstractDmoFactory.register(new FileItemFactory());
        addConverter(new FileItemConverter());

        AbstractDmoFactory.register(new FolderItemFactory());
        addConverter(new FolderItemConverter());

        AbstractDmoFactory.register(new DisciplineContainerFactory());
        addConverter(new DisciplineContainerConverter());

        AbstractDmoFactory.register(new DownloadHistoryFactory());
        addConverter(new DownloadHistoryConverter());
    }

    public URL getFileURL(DmoStoreId dmoStoreId)
    {
        return getStreamURL(dmoStoreId, EasyFile.UNIT_ID);
    }

    @Override
    public URL getStreamURL(DmoStoreId dmoStoreId, String streamId)
    {
        URL url = null;
        final String spec = getFedora().getBaseURL() + "/get/" + dmoStoreId.getStoreId() + "/" + streamId;
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

    public URL getDescriptiveMetadataURL(DmoStoreId dmoStoreId)
    {
        final String spec = getFedora().getBaseURL() + "/get/" + dmoStoreId.getStoreId() + "/" + DescriptiveMetadataImpl.UNIT_ID;
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
        return findDownloadHistoryFor(objectDmo.getDmoStoreId(), period);
    }

    public DownloadHistory findDownloadHistoryFor(DmoStoreId dmoStoreId, String period) throws RepositoryException
    {
        DownloadHistory dlh = null;
        String dmoObjectRef = FedoraURIReference.create(dmoStoreId.getStoreId());
        String query = createDownloadHistoryQuery(dmoObjectRef, period);
        try
        {
            TupleIterator tupleIterator = execSparql(query);
            if (tupleIterator.hasNext())
            {
                Map<String, Node> row = tupleIterator.next();
                String subject = row.get("s").toString();
                String subjectStoreId = FedoraURIReference.strip(subject);
                dlh = (DownloadHistory) retrieve(new DmoStoreId(subjectStoreId));
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
                DownloadHistory dlh = (DownloadHistory) retrieve(new DmoStoreId(storeId));
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

    public EasyMetadata getEasyMetaData(DmoStoreId dmoStoreId, DateTime asOfDateTime) throws RepositoryException
    {
        final MIMETypedStream mts = getFedora().getDatastreamAccessor().getDatastreamDissemination(dmoStoreId.getStoreId(), EasyMetadata.UNIT_ID, asOfDateTime);
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
