package nl.knaw.dans.common.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Locale sensitive cache for temporary in-memory storage of objects. Once an object (<code>V</code>) is
 * stored under key (<code>K</code> ), it is kept in cache for at least <code>maxAge</code>. Each time
 * the object is drawn from cache, it's age is reset.
 * <p/>
 * Cache cleaning takes place at fixed intervals, know as <code>probeInterval</code>.
 * <p/>
 * AbstractCache can handle localized objects.
 * <p/>
 * 
 * @author ecco Apr 29, 2009
 * @param <K>
 *        key to get cached objects
 * @param <V>
 *        the cached object
 */
public abstract class AbstractCache<K, V>
{

    /**
     * The default probe interval is {@value} minutes.
     */
    public static final long DEFAULT_PROBE_INTERVAL = 5L;

    /**
     * The default minimum period a cached object is not removed from cache is {@value} minutes.
     */
    public static final long DEFAULT_MAX_AGE = 30L;

    private static final long MINUTES_TO_MILLIS = 60000L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);

    private final String cacheName;

    private final Map<String, Cached<V>> cache = Collections.synchronizedMap(new HashMap<String, Cached<V>>());

    private final Timer timer;

    private final long probeInterval;

    private long maxAge;

    /**
     * Construct a new AbstractCache, with default probe interval and default max age.
     */
    protected AbstractCache()
    {
        this(DEFAULT_PROBE_INTERVAL, DEFAULT_MAX_AGE);
    }

    /**
     * Construct a new AbstractCache, with default max age.
     * 
     * @param probeMinutes
     *        probe interval in minutes
     */
    protected AbstractCache(final long probeMinutes)
    {
        this(probeMinutes, DEFAULT_MAX_AGE);
    }

    /**
     * Construct a new AbstractCache.
     * 
     * @param probeMinutes
     *        probe interval in minutes
     * @param maxAgeMinutes
     *        max age in minutes
     */
    protected AbstractCache(final long probeMinutes, final long maxAgeMinutes)
    {
        if (probeMinutes < 1L)
        {
            throw new IllegalArgumentException("Cannot set probe interval to less than 1 minute.");
        }
        probeInterval = probeMinutes * MINUTES_TO_MILLIS;
        setMaxAge(maxAgeMinutes);
        cacheName = this.getClass().getName();
        timer = new Timer(this.getClass().getSimpleName(), true);
        timer.schedule(new CacheCleaner(), probeInterval, probeInterval);
        LOGGER.debug("Started timer " + timer);
    }

    /**
     * Cancel the {@link Timer} associated with this AbstractCache. In general, caches that live shorter
     * than the live time of the application should call this method at the end of their live cycle.
     */
    public void destroy()
    {
        timer.cancel();
        LOGGER.debug("Canceled timer " + timer);
    }

    /**
     * Get cached object from cache.
     * 
     * @param key
     *        key for cached object
     * @param locale
     *        Locale for the cached object or <code>null</code>
     * @return cached object or <code>null</code> if the object for the given key is not available
     * @throws CacheException
     *         wrapper for exceptions while obtaining the object
     */
    public V getCachedObject(final K key, final Locale locale) throws CacheException
    {
        V retVal = null;
        Cached<V> cached = null;
        final String cacheKey = getLocaleKey(key, locale);
        synchronized (cache)
        {
            cached = cache.get(cacheKey);
            if (cached == null)
            {
                cached = getItFromSubClass(key, locale, cacheKey);
            }
        }
        if (cached != null)
        {
            retVal = cached.getObject();
        }
        return retVal;
    }

    private Cached<V> getItFromSubClass(final K key, final Locale locale, final String cacheKey) throws CacheException
    {
        Cached<V> cached = null;
        final V object = getObject(key, locale);
        if (object != null)
        {
            cached = new Cached<V>(object);
            cache.put(cacheKey, cached);
            LOGGER.debug("Cached '" + cacheKey + "' for " + cacheName);
        }
        else
        {
            LOGGER.debug("Object with id '" + cacheKey + "' not in cache and not available from " + cacheName);
        }
        return cached;
    }

    /**
     * Subclasses are asked to provide the object for given key (if it was not available yet). Only if
     * the returned object is not null, the object is stored in cache.
     * 
     * @param key
     *        key for cached object
     * @param locale
     *        Locale of the object, may be <code>null</code>
     * @return object to cache or <code>null</code> if, for the given key, no object should be cached
     * @throws CacheException
     *         wrapper for exceptions while obtaining the object
     */
    protected abstract V getObject(K key, Locale locale) throws CacheException;

    /**
     * Put the given object in cache under the given locale-specific key.
     * 
     * @param key
     *        the key for the cached object
     * @param locale
     *        the locale of the object, can be <code>null</code>
     * @param object
     *        the object to cache
     */
    public void putObject(final K key, final Locale locale, final V object)
    {
        if (object != null)
        {
            final String cacheKey = getLocaleKey(key, locale);
            final Cached<V> cached = new Cached<V>(object);
            cache.put(cacheKey, cached);
            LOGGER.debug("Cached '" + cacheKey + "' for " + cacheName);
        }
    }

    /**
     * Get the maximum age of cached objects in minutes.
     * 
     * @return the maximum age of cached objects in minutes
     */
    public long getMaxAge()
    {
        return maxAge / MINUTES_TO_MILLIS;
    }

    /**
     * Set the maximum age of cached objects in minutes.
     * 
     * @param maxAgeMinutes
     *        maximum age of cached objects in minutes
     */
    protected final void setMaxAge(final long maxAgeMinutes)
    {
        if (maxAgeMinutes < 1L)
        {
            throw new IllegalArgumentException("Cannot set max age to less than 1 minute.");
        }
        this.maxAge = maxAgeMinutes * MINUTES_TO_MILLIS;
    }

    /**
     * Get the probe interval in minutes.
     * 
     * @return the probeInterval
     */
    public long getProbeInterval()
    {
        return probeInterval / MINUTES_TO_MILLIS;
    }

    /**
     * Get the momentary size of the cache in amount of cached objects.
     * 
     * @return momentary size of the cache
     */
    public int size()
    {
        synchronized (cache)
        {
            return cache.size();
        }
    }

    private String getLocaleKey(final K key, final Locale locale)
    {
        final StringBuilder sb = new StringBuilder(key.toString());
        if (locale != null && StringUtils.isNotBlank(locale.getLanguage()))
        {
            sb.append("_");
            sb.append(locale.getLanguage());
            if (StringUtils.isNotBlank(locale.getCountry()))
            {
                sb.append("_");
                sb.append(locale.getCountry());
            }
        }
        return sb.toString();
    }

    private class CacheCleaner extends TimerTask
    {

        public void run()
        {
            final List<String> expiredObjects = new ArrayList<String>();
            final long probeTime = System.currentTimeMillis() - maxAge;
            synchronized (cache)
            {
                // LOGGER.debug("Start. Clean cache for " + cacheName + ". Size before = " +
                // cache.size());
                for (final String cacheKey : cache.keySet())
                {
                    if (cache.get(cacheKey).isExpired(probeTime))
                    {
                        expiredObjects.add(cacheKey);
                    }
                }

                for (final String expired : expiredObjects)
                {
                    cache.remove(expired);
                }
                // LOGGER.debug("Done. Cleaned cache for " + cacheName + ". Size after = " +
                // cache.size());
            }
        }

    }

    private static class Cached<V>
    {

        private final V object;
        private long dateUsed;

        protected Cached(final V object)
        {
            this.object = object;
            dateUsed = System.currentTimeMillis();
        }

        protected V getObject()
        {
            dateUsed = System.currentTimeMillis();
            return object;
        }

        protected boolean isExpired(final long probeTime)
        {
            return dateUsed < probeTime;
        }

    }

}
