package nl.knaw.dans.easy.web.wicket;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class KvpChoiceRenderer extends ChoiceRenderer
{
    private static final String IDENT_STRING = "---";
    private static final long serialVersionUID = -1055346602839443523L;

    public KvpChoiceRenderer()
    {
        super(KeyValuePair.PROP_VALUE, KeyValuePair.PROP_KEY);
    }

    @Override
    public Object getDisplayValue(Object object)
    {
        String result = (String) super.getDisplayValue(object);

        KeyValuePair kvp = (KeyValuePair) object;
        int indent = kvp.getIndent();
        if (indent > 0)
        {
            String dashes = IDENT_STRING;
            for (int i = 1; i < indent; i++)
            {
                dashes += IDENT_STRING;
            }
            result = dashes + " " + result;
        }

        return result;
    }

}
