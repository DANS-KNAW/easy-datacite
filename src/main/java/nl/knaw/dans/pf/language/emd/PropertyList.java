package nl.knaw.dans.pf.language.emd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertyList implements Serializable
{

    private static final long serialVersionUID = 442071239235827438L;
    private String comment;
    private List<Property> properties = new ArrayList<Property>();;

    public PropertyList()
    {

    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public List<Property> getProperties()
    {
        return properties;
    }

    public void addProperty(String key, String value)
    {
        properties.add(new Property(key, value));
    }

    public void setProperties(List<Property> properties)
    {
        this.properties = properties;
    }

    public String getValue(String key, String defaultValue)
    {
        String value = defaultValue;
        for (Property prop : properties)
        {
            if (prop.getKey().equals(key))
            {
                value = prop.getValue();
                break;
            }
        }
        return value;
    }

    public static class Property implements Serializable
    {

        private static final long serialVersionUID = 1L;
        private String key;
        private String value;

        @SuppressWarnings("unused")
        private Property()
        {

        }

        public Property(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
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
