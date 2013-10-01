package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

import org.jibx.runtime.JiBXException;

/**
 * A component with inheritable attributes.
 * 
 * @author ecco Apr 22, 2009
 */
public abstract class AbstractInheritableDefinition<T> extends AbstractJiBXObject<T>
{

    private static final long serialVersionUID = -3247134648110523055L;

    // private static final Logger logger = LoggerFactory
    // .getLogger(AbstractInheritableDefinition.class);

    private String id;
    private AbstractInheritableDefinition<?> parent;
    private final Map<String, String> customPropertiesMap = new HashMap<String, String>();
    // JiBX
    private List<CustomProperty> customProperties;
    private String labelResourceKey;
    private String shortHelpResourceKey;
    private String helpItem;
    private String helpFile;
    private String instructionFile;
    private String licenseFile;

    /**
     * Constructor used by JiBX.
     */
    protected AbstractInheritableDefinition()
    {
        super();
    }

    protected AbstractInheritableDefinition(String id)
    {
        super();
        this.id = id;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the parent
     */
    public AbstractInheritableDefinition<?> getParent()
    {
        return parent;
    }

    /**
     * @param parent
     *        the parent to set
     */
    protected void setParent(final AbstractInheritableDefinition<?> parent)
    {
        this.parent = parent;
    }

    public String getVersion()
    {
        return parent.getVersion();
    }

    /**
     * @return the labelResourceKey
     */
    public String getLabelResourceKey()
    {
        if (labelResourceKey == null && parent != null)
        {
            return parent.getLabelResourceKey();
        }
        else
        {
            return labelResourceKey;
        }
    }

    /**
     * @param labelResourceKey
     *        the labelResourceKey to set
     */
    public void setLabelResourceKey(final String labelResourceKey)
    {
        this.labelResourceKey = labelResourceKey;
    }

    /**
     * @return the shortHelpResourceKey
     */
    public String getShortHelpResourceKey()
    {
        if (shortHelpResourceKey == null && parent != null)
        {
            return parent.getShortHelpResourceKey();
        }
        else
        {
            return shortHelpResourceKey;
        }
    }

    /**
     * @param shortHelpResourceKey
     *        the shortHelpResourceKey to set
     */
    public void setShortHelpResourceKey(final String shortHelpResourceKey)
    {
        this.shortHelpResourceKey = shortHelpResourceKey;
    }

    /**
     * @return the helpItem
     */
    public String getHelpItem()
    {
        if (helpItem == null && parent != null)
        {
            return parent.getHelpItem();
        }
        else
        {
            return helpItem;
        }
    }

    /**
     * @param helpItem
     *        the helpItem to set
     */
    public void setHelpItem(final String helpItem)
    {
        this.helpItem = helpItem;
    }

    /**
     * @return the helpFile
     */
    public String getHelpFile()
    {
        if (helpFile == null && parent != null)
        {
            return parent.getHelpFile();
        }
        else
        {
            return helpFile;
        }
    }

    /**
     * @param helpFile
     *        the helpFile to set
     */
    public void setHelpFile(final String helpFile)
    {
        this.helpFile = helpFile;
    }

    /**
     * @return the instructionFile
     */
    public String getInstructionFile()
    {
        if (instructionFile == null && parent != null)
        {
            return parent.getInstructionFile();
        }
        else
        {
            return instructionFile;
        }
    }

    /**
     * @param instructionFile
     *        the instructionFile to set
     */
    public void setInstructionFile(final String instructionFile)
    {
        this.instructionFile = instructionFile;
    }

    /**
     * @return the licenseFile
     */
    public String getLicenseFile()
    {
        if (licenseFile == null && parent != null)
        {
            return parent.getLicenseFile();
        }
        else
        {
            return licenseFile;
        }
    }

    /**
     * @param licenseFile
     *        the licenseFile to set
     */
    public void setLicenseFile(final String licenseFile)
    {
        this.licenseFile = licenseFile;
    }

    /**
     * Get the custom property by the given <code>name</code> or <code>null</code> if the property could
     * not be found.
     * 
     * @param name
     *        name of the property
     * @return value of the property
     */
    public String getCustomProperty(final String name)
    {
        return getCustomProperty(name, null);
    }

    /**
     * Get the custom property by the given <code>name</code> or the given <code>defaultValue</code> if
     * the property could not be found.
     * 
     * @param name
     *        name of the property
     * @param defaultValue
     *        the default value
     * @return value of the property
     */
    public String getCustomProperty(final String name, final String defaultValue)
    {
        String value = customPropertiesMap.get(name);
        if (value == null && parent != null)
        {
            value = parent.getCustomProperty(name, defaultValue);
        }
        if (value == null)
        {
            value = defaultValue;
        }
        return value;
    }

    public void putCustomProperty(final String name, final String value)
    {
        synchronized (customPropertiesMap)
        {
            customPropertiesMap.put(name, value);
        }
    }

    protected StandardPanelDefinition getStandardPanelDefinition(final String panelId)
    {
        if (parent != null)
        {
            return parent.getStandardPanelDefinition(panelId);
        }
        else
        {
            return null;
        }
    }

    protected TermPanelDefinition getTermPanelDefinition(final String panelId)
    {
        if (parent != null)
        {
            return parent.getTermPanelDefinition(panelId);
        }
        else
        {
            return null;
        }
    }

    protected SubHeadingDefinition getSubHeadingDefinition(final String panelId)
    {
        if (parent != null)
        {
            return parent.getSubHeadingDefinition(panelId);
        }
        else
        {
            return null;
        }
    }

    protected PanelDefinition getPanelDefinition(final String panelId)
    {
        if (parent != null)
        {
            return parent.getPanelDefinition(panelId);
        }
        else
        {
            return null;
        }
    }

    protected abstract AbstractInheritableDefinition<?> clone();

    protected void clone(final AbstractInheritableDefinition<?> clone)
    {
        synchronized (customPropertiesMap)
        {
            clone.customPropertiesMap.putAll(customPropertiesMap);
        }
        clone.helpFile = helpFile;
        clone.helpItem = helpItem;
        clone.instructionFile = instructionFile;
        clone.labelResourceKey = labelResourceKey;
        clone.licenseFile = licenseFile;
        clone.shortHelpResourceKey = shortHelpResourceKey;

    }

    // JiBX
    protected List<CustomProperty> getCustomProperties()
    {
        final List<CustomProperty> customProps = new ArrayList<CustomProperty>();
        synchronized (customPropertiesMap)
        {
            for (final String name : customPropertiesMap.keySet())
            {
                customProps.add(new CustomProperty(name, customPropertiesMap.get(name)));
            }
        }
        return customProps.isEmpty() ? null : customProps;
    }

    // JiBX
    protected void postAbstractJiBXProcess() throws JiBXException
    {
        // logger.debug("Postprocessing (abstract) " + this.getClass().getSimpleName() + ":" + getId()
        // + " after JiBX deserialization.");

        mapCustomProperties();
    }

    private void mapCustomProperties()
    {
        if (customProperties != null)
        {
            synchronized (customPropertiesMap)
            {
                for (final CustomProperty prop : customProperties)
                {
                    customPropertiesMap.put(prop.getName(), prop.getValue());
                }
            }
        }
        customProperties = null;
    }

}
