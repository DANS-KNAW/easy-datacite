package nl.knaw.dans.c.dmo.collections;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.c.dmo.collections.core.Settings;
import nl.knaw.dans.c.dmo.collections.xml.CollectionTreeValidator;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.DmoCollections;
import nl.knaw.dans.i.dmo.collections.config.Configuration;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.dmo.collections.exceptions.NoSuchCollectionException;
import nl.knaw.dans.i.security.SecurityAgent;
import nl.knaw.dans.i.security.annotations.SecuredOperationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DmoCollectionsImpl implements DmoCollections
{

    private static final Logger logger = LoggerFactory.getLogger(DmoCollectionsImpl.class);

    public DmoCollectionsImpl()
    {
        logger.info("Instantiated " + this.getClass().getName());
    }

    @Override
    public Set<String> getSecuredOperationIds()
    {
        Set<String> securityIds = new HashSet<String>(SecuredOperationUtil.getInterfaceSecurityIds(CollectionManagerImpl.class));
        return securityIds;
    }

    @Override
    public void setConfiguration(Configuration configuration)
    {
        Settings.instance().configure(configuration);
    }

    @Override
    public CollectionManager newManager(String ownerId)
    {
        return new CollectionManagerImpl(ownerId);
    }

    @Override
    public XMLErrorHandler validateXml(URL xmlTreeUrl) throws ValidatorException
    {
        if (xmlTreeUrl == null)
        {
            throw new IllegalArgumentException("The given URL is null.");
        }
        try
        {
            return CollectionTreeValidator.instance().validate(xmlTreeUrl.openStream(), null);
        }
        catch (SAXException e)
        {
            throw new ValidatorException(e);
        }
        catch (SchemaCreationException e)
        {
            throw new ValidatorException(e);
        }
        catch (IOException e)
        {
            throw new ValidatorException(e);
        }
    }

    @Override
    public void registerNamespace(DmoNamespace namespace)
    {
        Settings.instance().registerDmoNamespace(namespace);
    }

    @Override
    public void setContentModelOAISet(DmoStoreId dmoStoreId)
    {
        Settings.instance().setContentModelOAISet(dmoStoreId);
    }

    @Override
    public void setSecurityAgents(List<SecurityAgent> agents)
    {
        Settings.instance().putSecurityAgents(agents);
    }

    @Override
    public Set<DmoStoreId> filterOAIEndNodes(Set<DmoStoreId> memberIds) throws NoSuchCollectionException, CollectionsException
    {
        Set<DmoStoreId> endNodes = new HashSet<DmoStoreId>();
        Set<DmoCollection> memberCollections = new HashSet<DmoCollection>();
        CollectionManager manager = newManager(null);
        for (DmoStoreId storeId : memberIds)
        {
            DmoCollection collection = manager.getCollection(storeId);
            if (collection.isPublishedAsOAISet())
            {
                memberCollections.add(collection);
            }
        }

        for (DmoCollection collection : memberCollections)
        {
            if (collection.isOAIendNode(memberIds))
            {
                endNodes.add(collection.getDmoStoreId());
            }
        }
        return endNodes;
    }

}
