package nl.knaw.dans.common.fedora.fox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;

import org.joda.time.DateTime;

/**
 * Contains the object properties of a digital object.
 * 
 * @author ecco
 */
public class DigitalObjectProperties extends AbstractTimestampedJiBXObject<DigitalObjectProperties>
{

    public static final String NAME_STATE = "info:fedora/fedora-system:def/model#state";
    public static final String NAME_LABEL = "info:fedora/fedora-system:def/model#label";
    public static final String NAME_OWNERID = "info:fedora/fedora-system:def/model#ownerId";
    public static final String NAME_CREATED_DATE = "info:fedora/fedora-system:def/model#createdDate";
    public static final String NAME_LASTMODIFIED_DATE = "info:fedora/fedora-system:def/view#lastModifiedDate";

    private static final long serialVersionUID = -8693551379186329715L;

    private Map<String, Property> properties = new HashMap<String, Property>();
    private Map<String, Property> extProperties = new HashMap<String, Property>();

    /**
     * Gets the properties as a list.
     * 
     * @return the properties as a list
     */
    public ArrayList<Property> getProperties()
    {
        final ArrayList<Property> props = new ArrayList<Property>();
        props.addAll(properties.values());
        return props;
    }

    /**
     * Set the properties with a list, used for JiBX deserialization.
     * 
     * @param props
     *        a list of properties
     */
    public void setProperties(final ArrayList<Property> props)
    {
        properties.clear();
        if (props != null)
        {
            for (Property prop : props)
            {
                properties.put(prop.name, prop);
                if (NAME_LASTMODIFIED_DATE.equals(prop.name))
                {
                    setTimestamp(prop.value);
                }
            }
        }
    }

    /**
     * Gets the external properties as a list, used for JiBX serialization.
     * 
     * @return the properties as a list
     */
    public ArrayList<Property> getExtProperties()
    {
        final ArrayList<Property> props = new ArrayList<Property>();
        props.addAll(extProperties.values());
        return props;
    }

    /**
     * Set the external properties with a list, used for JiBX serialization.
     * 
     * @param props
     *        a list of properties
     */
    public void setExtProperties(final ArrayList<Property> props)
    {
        extProperties.clear();
        if (props != null)
        {
            for (Property prop : props)
            {
                extProperties.put(prop.name, prop);
            }
        }
    }

    /**
     * Set a property with the given name to the given value.
     * 
     * @param name
     *        name of the property
     * @param value
     *        value of the property
     */
    public void setProperty(final String name, final String value)
    {
        if (value == null)
        {
            properties.remove(name);
        }
        else
        {
            final Property prop = new Property(name, value);
            properties.put(name, prop);
        }
    }

    /**
     * Get the property with the given name.
     * 
     * @param name
     *        name of the property
     * @return value of the property or null
     */
    public String getProperty(final String name)
    {
        final Property prop = properties.get(name);
        return prop == null ? null : prop.value;
    }

    /**
     * Set an external property with the given name to the given value.
     * 
     * @param name
     *        name of the property
     * @param value
     *        value of the property
     */
    public void setExtProperty(final String name, final String value)
    {
        if (value == null)
        {
            extProperties.remove(name);
        }
        else
        {
            final Property prop = new Property(name, value);
            extProperties.put(name, prop);
        }
    }

    /**
     * Get the external property with the given name.
     * 
     * @param name
     *        name of the property
     * @return value of the property or null
     */
    public String getExtProperty(final String name)
    {
        final Property prop = extProperties.get(name);
        return prop == null ? null : prop.value;
    }

    /**
     * Get the state of the digital object.
     * 
     * @return the state of the digital object or <code>null</code> if it is not known
     */
    public DobState getDigitalObjectState()
    {
        final String state = getProperty(DigitalObjectProperties.NAME_STATE);
        return state == null ? null : DobState.valueFor(state);
    }

    /**
     * Set the state of the digital object.
     * 
     * @param state
     *        the state of the digital object
     */
    public void setDigitalObjectState(final DobState state)
    {
        setProperty(DigitalObjectProperties.NAME_STATE, state == null ? null : state.fedoraQuirck);
    }

    public String getStateAsString()
    {
        DobState state = getState();
        return state == null ? null : state.toString();
    }

    /**
     * Get the state of the digital object.
     * 
     * @return the state of the digital object or <code>null</code> if it is not known
     */
    public DobState getState()
    {
        String s = getProperty(DigitalObjectProperties.NAME_STATE);
        return DobState.valueFor(s);
    }

    /**
     * Set the state of the digital object.
     * 
     * @param state
     *        one of the {@link DobState#name()} values
     * @throws IllegalArgumentException
     *         if state is not a {@link DobState#name()} value
     */
    public void setState(String state) throws IllegalArgumentException
    {
        String s = DobState.valueOf(state).fedoraQuirck;
        setProperty(DigitalObjectProperties.NAME_STATE, s);
    }

    /**
     * Get the label of the digital object.
     * 
     * @return the label of the digital object or <code>null</code> if it is not known
     */
    public String getLabel()
    {
        return getProperty(DigitalObjectProperties.NAME_LABEL);
    }

    /**
     * Set the label of the digital object.
     * 
     * @param label
     *        the label of the digital object
     */
    public void setLabel(final String label)
    {
        setProperty(DigitalObjectProperties.NAME_LABEL, label);
    }

    /**
     * Get the ownerId of the digital object.
     * 
     * @return the ownerId of the digital object or <code>null</code> if it is not known
     */
    public String getOwnerId()
    {
        return getProperty(DigitalObjectProperties.NAME_OWNERID);
    }

    /**
     * Set the ownerId of the digital object.
     * 
     * @param ownerId
     *        the ownerId of the digital object
     */
    public void setOwnerId(final String ownerId)
    {
        setProperty(DigitalObjectProperties.NAME_OWNERID, ownerId);
    }

    /**
     * Get the creation date of the digital object.
     * 
     * @return the creation date of the digital object or <code>null</code> if it is not known
     */
    public DateTime getDateCreated()
    {
        final String date = getProperty(DigitalObjectProperties.NAME_CREATED_DATE);
        return date == null ? null : new DateTime(date);
    }

    /**
     * Get the date of last modification of the digital object.
     * 
     * @return the date of last modification of the digital object or <code>null</code> if it is not
     *         known
     */
    public DateTime getLastModified()
    {
        final String date = getProperty(DigitalObjectProperties.NAME_LASTMODIFIED_DATE);
        return date == null ? null : new DateTime(date);
    }

    /**
     * A Property is a name-value pair.
     * 
     * @author ecco
     */
    public static class Property implements Serializable
    {

        private static final long serialVersionUID = 1508487350528087136L;
        private String name;
        private String value;

        Property()
        {

        }

        /**
         * Constructs a new property with the given name and value.
         * 
         * @param name
         *        name of the property
         * @param value
         *        value of the property
         */
        public Property(final String name, final String value)
        {
            this.name = name;
            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

    }

}
