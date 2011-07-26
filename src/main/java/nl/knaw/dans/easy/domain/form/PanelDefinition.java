package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PanelDefinition extends AbstractInheritableDefinition<PanelDefinition>
{

    private static final long serialVersionUID = -2273513243070254539L;
    
    private List<String> errorMessages = new ArrayList<String>();
    private Map<Integer, List<String>> itemErrorMessages = new LinkedHashMap<Integer, List<String>>();
    
    protected PanelDefinition()
    {
        super();
    }
    
    protected PanelDefinition(String id)
    {
        super(id);
    }
    
    public boolean hasErrors()
    {
        return !errorMessages.isEmpty() || !itemErrorMessages.isEmpty();
    }
    
    public void clearErrorMessages()
    {
        errorMessages.clear();
        itemErrorMessages.clear();
    }
    
    public void addErrorMessage(String msgKey)
    {
        errorMessages.add(msgKey);
    }
    
    public List<String> getErrorMessages()
    {
        return errorMessages;
    }
    
    public void addItemErrorMessage(int index, String msgKey)
    {
        List<String> indexedMessages = itemErrorMessages.get(index);
        if (indexedMessages == null)
        {
            indexedMessages = new ArrayList<String>();
            itemErrorMessages.put(index, indexedMessages);
        }
        indexedMessages.add(msgKey);
    }
    
    public Map<Integer, List<String>> getItemErrorMessages()
    {
        return itemErrorMessages;
    }

}
