package nl.knaw.dans.c.dmo.collections.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import nl.knaw.dans.c.dmo.collections.core.DmoCollectionImpl;
import nl.knaw.dans.c.dmo.collections.store.Store;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

public class JiBXCollectionConverter
{

    public static JiBXCollection convert(DmoCollection collection, boolean isRoot)
    {
        JiBXCollection jibxCollection = new JiBXCollection();
        jibxCollection.setNamespace(collection.getDmoNamespace().getValue());
        if (isRoot)
        {
            jibxCollection.setId(DmoCollection.ROOT_ID);
        }
        else
        {
            if (collection.getDmoStoreId() != null)
            {
                jibxCollection.setId(collection.getDmoStoreId().getId());
            }
        }
        jibxCollection.setLabel(collection.getLabel());
        jibxCollection.setShortName(collection.getShortName());
        jibxCollection.setPublishedAsOAISet(collection.isPublishedAsOAISet());
        jibxCollection.setDcMetadata((JiBXDublinCoreMetadata) collection.getDcMetadata());
        addChildrenToJiBX(jibxCollection, collection);
        return jibxCollection;
    }
    
    public static DmoCollectionImpl convert(JiBXCollection jibRoot, boolean idFromStore) throws XMLDeserializationException, RepositoryException, CollectionsException
    {
        DmoCollectionImpl root = new DmoCollectionImpl(getRootId(jibRoot));
        root.setLabel("Root of " + root.getDmoNamespace().getValue());
        root.setShortName(jibRoot.getShortName());
        if (jibRoot.isPublishedAsOAISet())
        {
            root.publishAsOAISet();
        }
        setDcMetadata(jibRoot, root);
        addChildrenToDmo(root, jibRoot, idFromStore);
        return root;
    }
    
    public static DmoCollectionImpl convert(URL url, boolean idFromStore) throws IOException, XMLDeserializationException, RepositoryException, CollectionsException
    {
        if (url == null)
        {
            throw new IOException("Not found. url == null.");
        }
        XMLErrorHandler handler = validateXml(url);
        if (!handler.passed())
        {
            throw new XMLDeserializationException(handler.getMessages());
        }
        JiBXCollection jibRoot;
        InputStream inStream = null;
        try
        {
            inStream = url.openStream();
            jibRoot = (JiBXCollection) JiBXObjectFactory.unmarshal(JiBXCollection.class, inStream);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return convert(jibRoot, idFromStore);
    }

    private static void addChildrenToJiBX(JiBXCollection jibxParent, DmoCollection dmoParent)
    {
        for (DmoCollection dmoKid : dmoParent.getChildren())
        {
            JiBXCollection jibxKid = new JiBXCollection();
            if (dmoKid.getDmoStoreId() != null)
            {
                jibxKid.setId(dmoKid.getDmoStoreId().getId());
            }
            jibxKid.setLabel(dmoKid.getLabel());
            jibxKid.setShortName(dmoKid.getShortName());
            jibxKid.setPublishedAsOAISet(dmoKid.isPublishedAsOAISet());
            jibxKid.setDcMetadata((JiBXDublinCoreMetadata) dmoKid.getDcMetadata());
            jibxParent.addChild(jibxKid);
            addChildrenToJiBX(jibxKid, dmoKid);
        }
    }

    private static void addChildrenToDmo(DmoCollectionImpl dmoParent, JiBXCollection jibxParent, boolean idFromStore) throws RepositoryException,
            XMLDeserializationException, CollectionsException
    {
        for (JiBXCollection jibKid : jibxParent.getChildren())
        {
            DmoStoreId dmoStoreId = getDmoStoreId(dmoParent.getDmoNamespace(), jibKid.getId(), idFromStore);
            DmoCollectionImpl dmoKid = new DmoCollectionImpl(dmoStoreId);
            dmoParent.addChild(dmoKid);
            setDcMetadata(jibKid, dmoKid);
            
            if (StringUtils.isBlank(jibKid.getLabel()))
            {
                throw new XMLDeserializationException("Element label cannot be blank: " + jibKid.getId() + " " + jibKid.getShortName());
            }
            dmoKid.setLabel(jibKid.getLabel());
            if (StringUtils.isBlank(jibKid.getShortName()))
            {
                throw new XMLDeserializationException("Element short-name cannot be blank: " + jibKid.getId() + " " + jibKid.getLabel());
            }
            dmoKid.setShortName(jibKid.getShortName());
            if (jibKid.isPublishedAsOAISet())
            {
                dmoKid.publishAsOAISet();
            }
            
            addChildrenToDmo(dmoKid, jibKid, idFromStore);
        }
    }

    private static void setDcMetadata(JiBXCollection jibCol, DmoCollectionImpl dmoCol)
    {
        JiBXDublinCoreMetadata dcMetadata = jibCol.getDcMetadata();
        if (dcMetadata != null)
        {
            dmoCol.setDcMetadata(dcMetadata);
        }
    }

    private static DmoStoreId getRootId(JiBXCollection jibRoot) throws XMLDeserializationException
    {
        String namespace = jibRoot.getNamespace();
        if (StringUtils.isBlank(namespace))
        {
            throw new XMLDeserializationException("Attribute dmo-namespace of root-element cannot be blank.");
        }
        DmoStoreId rootId = new DmoStoreId(new DmoNamespace(namespace), DmoCollection.ROOT_ID);
        return rootId;
    }

    private static DmoStoreId getDmoStoreId(DmoNamespace dmoNamespace, String id, boolean idFromStore) throws RepositoryException,
            XMLDeserializationException
    {
        DmoStoreId dmoStoreId;
        if (idFromStore)
        {
            dmoStoreId = Store.getStoreManager().nextDmoStoreId(dmoNamespace);
        }
        else
        {
            dmoStoreId = getDmoStoreId(dmoNamespace, id);
        }
        return dmoStoreId;
    }

    private static DmoStoreId getDmoStoreId(DmoNamespace dmoNamespace, String id) throws XMLDeserializationException
    {
        if (StringUtils.isBlank(id))
        {
            throw new XMLDeserializationException("Attribute id of element dmo-collections is blank and idFromStore is false.");
        }
        return new DmoStoreId(dmoNamespace, id);
    }
    
    private static XMLErrorHandler validateXml(URL url) throws XMLDeserializationException
    {
        XMLErrorHandler handler;
        try
        {
            handler = CollectionTreeValidator.instance().validate(url.openStream(), null);
        }
        catch (ValidatorException e)
        {
            throw new XMLDeserializationException(e);
        }
        catch (SAXException e)
        {
            throw new XMLDeserializationException(e);
        }
        catch (SchemaCreationException e)
        {
            throw new XMLDeserializationException(e);
        }
        catch (IOException e)
        {
            throw new XMLDeserializationException(e);
        }
        return handler;
    }

}
