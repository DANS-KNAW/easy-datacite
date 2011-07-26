package nl.knaw.dans.easy.domain.form;

public class CustomProperty
{
    private String name;
    private String value;
    
    // JiBX
    protected CustomProperty()
    {
        
    }
    
    public CustomProperty(String name, String value)
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
