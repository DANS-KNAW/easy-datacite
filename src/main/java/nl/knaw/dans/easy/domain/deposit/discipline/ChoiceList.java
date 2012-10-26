package nl.knaw.dans.easy.domain.deposit.discipline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class ChoiceList extends AbstractJiBXObject<ChoiceList>
{

    public static final String LID_ARCHAEOLOGY_DC_SUBJECT = "archaeology.dc.subject";
    public static final String LID_ARCHAEOLOGY_DCTERMS_TEMPORAL = "archaeology.dcterms.temporal";

    private static final long serialVersionUID = -168278539121079593L;

    private String comment;
    private List<KeyValuePair> choices = new ArrayList<KeyValuePair>();
    private Map<String, String> choiceMap;

    protected ChoiceList()
    {

    }

    public ChoiceList(List<KeyValuePair> choices)
    {
        this.choices = choices;
    }

    public List<KeyValuePair> getChoices()
    {
        return Collections.unmodifiableList(choices);
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getComment()
    {
        return comment;
    }

    public String getValue(String key)
    {
        return getChoiceMap().get(key);
    }

    private Map<String, String> getChoiceMap()
    {
        if (choiceMap == null)
        {
            choiceMap = new HashMap<String, String>();
            for (KeyValuePair kvp : choices)
            {
                choiceMap.put(kvp.getKey(), kvp.getValue());
            }
        }
        return choiceMap;
    }

    public byte[] toBytes()
    {
        return null;
    }
}
