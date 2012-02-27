package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jibx.runtime.JiBXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormDescriptor extends AbstractInheritableDefinition<FormDescriptor>
{

    public static final String                         CURRENT_VERSION   = "0.1";

    private static final long                          serialVersionUID  = -8504692470376524874L;

    private static final Logger                        logger            = LoggerFactory
                                                                                 .getLogger(FormDescriptor.class);

    private String                                     version           = CURRENT_VERSION;
    private String                                     ordinal;

    private final Map<String, StandardPanelDefinition> standardPanelMap  = Collections
                                                                                 .synchronizedMap(new HashMap<String, StandardPanelDefinition>());
    private final Map<String, TermPanelDefinition>     termPanelMap      = Collections
                                                                                 .synchronizedMap(new HashMap<String, TermPanelDefinition>());
    private final Map<String, SubHeadingDefinition>    subHeadingMap     = Collections
                                                                                 .synchronizedMap(new HashMap<String, SubHeadingDefinition>());
    private final Map<String, FormDefinition>          formDefinitionMap = Collections
                                                                                 .synchronizedMap(new HashMap<String, FormDefinition>());

    // JiBX
    private List<StandardPanelDefinition>              standardPanelDefinitions;
    // JiBX
    private List<TermPanelDefinition>                  termPanelDefinitions;
    // JiBX
    private List<SubHeadingDefinition>                 subHeadingDefinitions;
    // JiBX
    private List<FormDefinition>                       formDefinitions;

    // JiBX
    protected FormDescriptor()
    {
        super();
    }

    public FormDescriptor(final String id)
    {
        super(id);
    }

    public String getVersion()
    {
        return version;
    }

    public String getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(final String ordinal)
    {
        this.ordinal = ordinal;
    }

    public StandardPanelDefinition getStandardPanelDefinition(final String panelId)
    {
        synchronized (standardPanelMap)
        {
            return standardPanelMap.get(panelId);
        }
    }

    public TermPanelDefinition getTermPanelDefinition(final String panelId)
    {
        synchronized (termPanelMap)
        {
            return termPanelMap.get(panelId);
        }
    }

    public SubHeadingDefinition getSubHeadingDefinition(final String panelId)
    {
        synchronized (subHeadingMap)
        {
            return subHeadingMap.get(panelId);
        }
    }

    public PanelDefinition getPanelDefinition(final String panelId)
    {
        PanelDefinition def = getStandardPanelDefinition(panelId);
        if (def == null)
        {
            def = getTermPanelDefinition(panelId);
            if (def == null)
            {
                def = getSubHeadingDefinition(panelId);
            }
        }
        return def;
    }

    public FormDefinition getFormDefinition(final String formDefinitionId)
    {
        synchronized (formDefinitionMap)
        {
            return formDefinitionMap.get(formDefinitionId);
        }
    }

    public synchronized FormDescriptor clone()
    {
        final FormDescriptor clone = new FormDescriptor(getId());
        super.clone(clone);
        clone.version = version;
        clone.ordinal = ordinal;

        synchronized (standardPanelMap)
        {
            for (final StandardPanelDefinition spDef : standardPanelMap.values())
            {   
                clone.addPanelDefinition(spDef.clone());
            }
        }

        synchronized (termPanelMap)
        {
            for (final TermPanelDefinition tpDef : termPanelMap.values())
            {
                clone.addPanelDefinition(tpDef.clone());
            }
        }

        synchronized (subHeadingMap)
        {
            for (final SubHeadingDefinition shDef : subHeadingMap.values())
            {
                clone.addPanelDefinition(shDef.clone());
            }
        }

        synchronized (formDefinitionMap)
        {
            for (final FormDefinition formDefinition : formDefinitionMap.values())
            {
                clone.addFormDefinition(formDefinition.clone());
            }
        }
        return clone;
    }

    public void addPanelDefinition(final StandardPanelDefinition def)
    {
        synchronized (standardPanelMap)
        {
            def.setParent(this);
            standardPanelMap.put(def.getId(), def);
        }
    }

    public void addPanelDefinition(final TermPanelDefinition def)
    {
        synchronized (termPanelMap)
        {
            def.setParent(this);
            termPanelMap.put(def.getId(), def);
        }
    }

    public void addPanelDefinition(final SubHeadingDefinition def)
    {
        synchronized (subHeadingMap)
        {
            def.setParent(this);
            subHeadingMap.put(def.getId(), def);
        }
    }

    public void addFormDefinition(final FormDefinition def)
    {
        synchronized (formDefinitionMap)
        {
            def.setParent(this);
            formDefinitionMap.put(def.getId(), def);
        }
    }

    public boolean containsFormDefinition(final String formDefinitionId)
    {
        return formDefinitionMap.containsKey(formDefinitionId);
    }

    // JiBX
    protected List<StandardPanelDefinition> getStandardPanelDefinitions()
    {
        synchronized (standardPanelMap)
        {
            return new ArrayList<StandardPanelDefinition>(standardPanelMap.values());
        }
    }

    // JiBX
    public List<TermPanelDefinition> getTermPanelDefinitions()
    {
        synchronized (termPanelMap)
        {
            return new ArrayList<TermPanelDefinition>(termPanelMap.values());
        }
    }

    // JiBX
    protected List<SubHeadingDefinition> getSubHeadingDefinitions()
    {
        synchronized (subHeadingMap)
        {
            return new ArrayList<SubHeadingDefinition>(subHeadingMap.values());
        }
    }

    // JiBX
    protected List<FormDefinition> getFormDefinitions()
    {
        synchronized (formDefinitionMap)
        {
            return new ArrayList<FormDefinition>(formDefinitionMap.values());
        }
    }

    // JiBX
    protected void postJiBXProcess() throws JiBXException
    {
        logger.debug("Postprocessing " + this.getClass().getSimpleName() + ":" + getId()
                + " after JiBX deserialization.");
        final StringBuilder errorCollector = new StringBuilder();

        mapStandardPanelDefinitions();
        mapTermPanelDefinitions();
        validateAndmapSubHeadingDefinitions(errorCollector);
        validateAndMapFormDefinitions(errorCollector);

        if (errorCollector.length() > 0)
        {
            logger.debug(errorCollector.toString());
            final String msg = "Errors(s) while unmarshalling " + this.getClass().getSimpleName() + ":" + getId();
            throw new JiBXException(msg, new FormConfigurationException(errorCollector.toString()));
        }
    }

    // JiBX
    private void mapStandardPanelDefinitions()
    {
        if (standardPanelDefinitions != null)
        {
            for (final StandardPanelDefinition def : standardPanelDefinitions)
            {
                addPanelDefinition(def);
            }
        }
        standardPanelDefinitions = null;
    }

    // JiBX
    private void mapTermPanelDefinitions()
    {
        if (termPanelDefinitions != null)
        {
            for (final TermPanelDefinition def : termPanelDefinitions)
            {
                addPanelDefinition(def);
            }
        }
        termPanelDefinitions = null;
    }

    // JiBX
    private void validateAndmapSubHeadingDefinitions(final StringBuilder errorCollector)
    {
        if (subHeadingDefinitions != null)
        {
            for (final SubHeadingDefinition def : subHeadingDefinitions)
            {
                boolean validDefinition = true;
                for (final String panelId : def.getPanelIds())
                {
                    final PanelDefinition panel = getPanelDefinition(panelId);
                    if (panel == null)
                    {
                        validDefinition = false;
                        final String msg = "Missing PanelDefinition: '" + panelId + "'"
                                + "\n\tThis means that you referenced '" + panelId + "' from subHeading:" + def.getId()
                                + ", but did not define the child panel '" + panelId + "' in edd:panelDefinitions.";
                        errorCollector.append("\n" + msg);
                    }
                }
                if (validDefinition)
                {
                    addPanelDefinition(def);
                }
            }
        }
        subHeadingDefinitions = null;
    }

    // JiBX
    private void validateAndMapFormDefinitions(final StringBuilder errorCollector)
    {
        if (formDefinitions != null)
        {
            for (final FormDefinition formDefinition : formDefinitions)
            {
                if (validateFormPages(formDefinition, errorCollector))
                {
                    addFormDefinition(formDefinition);
                }
            }
        }
    }

    // JiBX
    private boolean validateFormPages(final FormDefinition formDefinition, final StringBuilder errorCollector)
    {
        boolean allPagesValid = true;
        for (final FormPage page : formDefinition.getFormPages())
        {
            for (final String panelId : page.getPanelIds())
            {
                final PanelDefinition pDef = getPanelDefinition(panelId);
                if (pDef == null)
                {
                    allPagesValid = false;
                    final String msg = "Missing PanelDefinition: '" + panelId + "'"
                            + "\n\tThis means that you referenced '" + panelId + "' from FormPage:" + page.getId()
                            + " in formDefinition: " + formDefinition.getId() + ", but did not define the panel:"
                            + panelId + " in edd:panelDefinitions.";
                    errorCollector.append("\n" + msg);
                }
            }
        }
        return allPagesValid;
    }

    // Factory methods for JiBX
    @SuppressWarnings("rawtypes")
    protected static List arrayList()
    {
        return new ArrayList();
    }

}
