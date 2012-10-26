package nl.knaw.dans.easy.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.joda.time.DateTime;

/**
 * A Messenger can be used to collect data and express the state of processes.
 * 
 * @author ecco Mar 14, 2009
 * @param <T>
 *        enum expressing the state of the processes this Messenger is involved in
 */
public class Messenger<T extends Enum<T>> implements Serializable
{

    /**
     * Prefix for state key, used in resource lookups.
     * 
     * @see #getStateKey()
     */
    public static final String STATE_KEY_PREFIX = "state.";

    /**
     * Property name: {@value} .
     * 
     * @see #setToken(String)
     * @see #getToken()
     */
    public static final String PROP_TOKEN = "token";

    private static final long serialVersionUID = -8567872685462175167L;

    private final Class<? extends Enum<T>> stateType;

    private final LinkedHashSet<T> accumulatedStates = new LinkedHashSet<T>();

    private final DateTime requestTime;

    private final String randomString;

    private final List<Throwable> exceptions = new ArrayList<Throwable>();

    private T state;

    private String token;

    /**
     * Constructs a new Messenger with it's state set to the first enumConstant of stateType. Also the (final) request
     * time and a (final) random string are instantiated.
     * <p/>
     * In order to let the method {@link #isCompleted()} work properly, the last enumConstant of stateType should also
     * be the last and final state this Messenger can be in.
     * <p/>
     * More formal: the state of this Messenger after instantiation is equal to the enumConstant with ordinal 0.
     * 
     * @param stateType
     *        Class of the enumConstants used
     * @throws ArrayIndexOutOfBoundsException
     *         if T is an empty enum
     */
    public Messenger(final Class<? extends T> stateType)
    {
        super();
        this.stateType = stateType;
        state = stateType.getEnumConstants()[0];
        requestTime = new DateTime();
        randomString = SecurityUtil.getRandomString();
        token = randomString;
    }

    /**
     * Get the Class of the enumConstants used.
     * 
     * @return Class of the enumConstants
     */
    public Class<? extends Enum<T>> getStateType()
    {
        return stateType;
    }

    /**
     * Set the state of this Messenger and accumulate the state.
     * 
     * @param state
     *        enumConstant of stateType
     * @throws IllegalArgumentException
     *         if state == null
     * @see #getAccumelatedStates()
     */
    protected void setState(final T state)
    {
        if (state == null)
        {
            throw new IllegalArgumentException("State cannot be null.");
        }
        this.state = state;
        accumulatedStates.add(state);
    }

    /**
     * Convenience method to set the state and add an exception; the state is accumulated.
     * 
     * @param state
     *        enumConstant of stateType
     * @param e
     *        throwable to be recorded
     * @throws IllegalArgumentException
     *         if state == null
     * @see #getAccumelatedStates()
     */
    protected void setState(T state, Throwable e)
    {
        setState(state);
        addException(e);
    }

    /**
     * Get the state of this Messenger.
     * 
     * @return enumConstant of stateType
     */
    public T getState()
    {
        return state;
    }

    /**
     * Get the accumulation of states this Messenger was in.
     * 
     * @return the accumulation of states this Messenger was in
     */
    public LinkedHashSet<T> getAccumelatedStates()
    {
        return accumulatedStates;
    }

    /**
     * Returns a state key prefix + the name of this Messengers state.
     * 
     * @return key for resource lookup
     * @see #STATE_KEY_PREFIX
     */
    public String getStateKey()
    {
        return getStateKeyFor(state);
    }

    /**
     * Returns a list of accumulated state keys.
     * 
     * @return a list of accumulated state keys
     */
    public List<String> getAccumulatedStateKeys()
    {
        List<String> stateKeys = new ArrayList<String>();
        for (T accumulatedState : accumulatedStates)
        {
            stateKeys.add(getStateKeyFor(accumulatedState));
        }
        return stateKeys;
    }

    /**
     * Expresses the system time at which this messenger was instantiated.
     * 
     * @return system time at which this messenger was instantiated
     */
    public DateTime getRequestTime()
    {
        return requestTime;
    }

    /**
     * Expresses the system time at which this messenger was instantiated as a String.
     * 
     * @return the system time at which this messenger was instantiated as a String
     */
    public String getRequestTimeAsString()
    {
        return Long.valueOf(requestTime.getMillis()).toString();
    }

    /**
     * Returns a random string composed at instantiation time.
     * 
     * @return a random string composed at instantiation time
     * @see #setToken(String)
     * @see #getToken()
     * @see #verifyToken()
     */
    public String getRandomString()
    {
        return randomString;
    }

    /**
     * Get the token set on this messenger. A random string, composed at instantiation time, can make a round trip via a
     * hidden field on a html form and, on submit of the form, be picked up by the {@link #setToken(String)} method.
     * Token and random string can than be verified to be equal.
     * 
     * @return token set on this Messenger
     * @see #getRandomString()
     * @see #setToken(String)
     * @see #verifyToken()
     */
    public String getToken()
    {
        return token;
    }

    /**
     * Set the token on this Messenger, used for html form submit.
     * 
     * @param token
     *        the random string composed at instantiation time
     * @see #getRandomString()
     * @see #getToken()
     * @see #verifyToken()
     */
    public void setToken(String token)
    {
        this.token = token;
    }

    /**
     * Verify the integrity of a html form submit.
     * 
     * @return <code>true</code> if this random string is equal to this token, <code>false</code> otherwise
     * @see #getRandomString()
     * @see #setToken(String)
     * @see #getToken()
     * @deprecated Only works the way it should after refresh of page in browser. (Otherwise we get a cache.)
     */
    public boolean verifyToken()
    {
        return randomString.equals(token);
    }

    /**
     * Record a Throwable during processing this Messenger partakes in.
     * 
     * @param e
     *        Throwable to be recorded
     */
    public void addException(Throwable e)
    {
        exceptions.add(e);
    }

    /**
     * Get a list of Throwables recorded during processing this Messenger partakes in.
     * 
     * @return a list of Throwables
     */
    public List<Throwable> getExceptions()
    {
        return exceptions;
    }

    /**
     * Get recorded Throwables as a String.
     * 
     * @return recorded Throwables as a String
     */
    public String getExceptionsAsString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception.count=" + exceptions.size());
        for (Throwable t : exceptions)
        {
            sb.append("\n\t");
            sb.append(t.getClass().getName() + " message=" + t.getMessage());
        }
        return sb.toString();
    }

    /**
     * A list of all states this Messenger can be in.
     * 
     * @return list of all states
     */
    public List<? extends Enum<T>> getAllStates()
    {
        return Arrays.asList(stateType.getEnumConstants());
    }

    /**
     * A list of all state keys.
     * 
     * @return list of all state keys
     */
    public List<String> getAllStateKeys()
    {
        List<String> allStateKeys = new ArrayList<String>();
        for (Enum<T> st : getAllStates())
        {
            allStateKeys.add(getStateKeyFor(st));
        }
        return allStateKeys;
    }

    /**
     * Prints all state keys to System.out.
     */
    public void dumpAllStateKeys()
    {
        for (String stateKey : getAllStateKeys())
        {
            System.out.println(stateKey);
        }
    }

    /**
     * Get the state key for the given state.
     * 
     * @param state
     *        enumConstant of stateType
     * @return state key for the given state
     */
    public String getStateKeyFor(Enum<T> state)
    {
        return STATE_KEY_PREFIX + state.name();
    }

    /**
     * Has this Messenger reached it's last state?
     * 
     * @return <code>true</code> the state of this Messenger is equal to the enumConstant with the highest ordinal,
     *         <code>false</code> otherwise.
     */
    public boolean isCompleted()
    {
        return stateType.getEnumConstants()[stateType.getEnumConstants().length - 1].equals(state);
    }

    /**
     * Create a unique token that can be used in mail authentication.
     * 
     * @param id
     *        the id of a domain object, i.e. userId
     * @return a unique token that can be used in mail authentication
     */
    public String createMailToken(String id)
    {
        return Integer.valueOf(SecurityUtil.generateHashCode(id, getRequestTimeAsString(), getRandomString())).toString();
    }

}
