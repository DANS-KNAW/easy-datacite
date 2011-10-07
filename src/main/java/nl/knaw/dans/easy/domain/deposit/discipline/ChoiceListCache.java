package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.AbstractCache;
import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache and Locale-sensitive locator of ChoiceLists.
 * 
 * @see AbstractCache
 * @see ResourceLocator
 * @author ecco Apr 30, 2009
 */
public class ChoiceListCache extends AbstractListCache<ChoiceList>
{

    public static final String     BASE_FOLDER = "easy-business/discipline/emd/choicelist/";

    private static final Logger    logger      = LoggerFactory.getLogger(ChoiceListCache.class);

    private static ChoiceListCache instance    = new ChoiceListCache();

    private ChoiceListCache()
    {
        super();
    }

    /**
     * Get the singleton instance of ChoiceListCache.
     * 
     * @return singleton instance of ChoiceListCache
     */
    public static ChoiceListCache getInstance()
    {
        return instance;
    }

//    /**
//     * Get a choice list as a Properties object for the default Locale.
//     * 
//     * @param listId
//     *        id of the choice list
//     * @return choice list as Properties
//     * @throws IOException
//     *         if such mishap occurs
//     * @throws ResourceNotFoundException
//     *         if not found
//     */
//    public Properties getProperties(String listId) throws IOException, ResourceNotFoundException
//    {
//        return getProperties(listId, null);
//    }
//
//    /**
//     * Get a choice list as a Properties object. Properties inherit from HashTable and therefore the sequence of choices
//     * between calls is not stable or conforming to any order.
//     * 
//     * @param listId
//     *        id of the choice list
//     * @param locale
//     *        Locale, may be <code>null</code>
//     * @return choice list as Properties
//     * @throws IOException
//     *         if that occurs
//     * @throws ResourceNotFoundException
//     *         if not found
//     * @see ResourceLocator#getURL(String, Locale, String)
//     */
//    public Properties getProperties(String listId, Locale locale) throws IOException, ResourceNotFoundException
//    {
//        Properties props = new Properties();
//        InputStream inStream = null;
//        try
//        {
//            inStream = getInputStream(listId, locale);
//            props.loadFromXML(inStream);
//        }
//        finally
//        {
//            if (inStream != null)
//            {
//                inStream.close();
//            }
//        }
//        return props;
//    }

    @Override
    protected String getBaseFolder()
    {
        return BASE_FOLDER;
    }

    protected ChoiceList getObjectForCache(String key, Locale locale) throws CacheException, IOException
    {
        ChoiceList choiceList = null;
        InputStream inStream = null;
        try
        {
            inStream = getInputStream(key, locale);
            choiceList = (ChoiceList) JiBXObjectFactory.unmarshal(ChoiceList.class, inStream);
        }
        catch (IOException e)
        {
            String msg = "Unable to get 'ChoiceList:" + key + "': ";
            logger.error(msg, e);
            throw new CacheException(msg, e);
        }
        catch (ResourceNotFoundException e)
        {
            String msg = "Resource for 'ChoiceList:" + key + "' not found: ";
            logger.error(msg, e);
            throw new CacheException(msg, e);
        }
        catch (XMLDeserializationException e)
        {
            String msg = "Unable to unmarshal 'ChoiceList:" + key + "': ";
            logger.error(msg, e);
            throw new CacheException(msg, e);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return choiceList;
    }

}
