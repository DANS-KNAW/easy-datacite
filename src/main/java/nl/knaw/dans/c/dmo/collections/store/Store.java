package nl.knaw.dans.c.dmo.collections.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.i.store.StoreManager;
import nl.knaw.dans.i.store.StoreSession;

public abstract class Store
{
    
    private static final Logger logger = LoggerFactory.getLogger(Store.class);
    
    
    private static StoreManager storeManager;
    
    public static StoreManager getStoreManager()
    {
        if (storeManager == null)
        {
            throw new IllegalStateException("No StoreManager set. Make sure your binding is properly configured.");
        }
        return storeManager;
    }
    
    public static StoreSession newStoreSession(String ownerId)
    {
        return getStoreManager().newStoreSession(ownerId);
    }
    
    public static void register(StoreManager storeManager)
    {
        Store.storeManager = storeManager;
        logger.info("Registered storeManager: " + storeManager.getClass().getName());
    }
    
    public static class Registrator
    {
        
        public void setStoreManager(StoreManager storeManager)
        {
            Store.register(storeManager);
        }
    }

}
