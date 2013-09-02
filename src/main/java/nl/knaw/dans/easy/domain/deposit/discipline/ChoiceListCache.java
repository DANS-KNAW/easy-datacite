package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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

    public static final String BASE_FOLDER = "easy-business/discipline/emd/choicelist/";

    private static final Logger logger = LoggerFactory.getLogger(ChoiceListCache.class);

    private static ChoiceListCache instance = new ChoiceListCache();

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
