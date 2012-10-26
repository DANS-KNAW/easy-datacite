package nl.knaw.dans.easy.web.wicket;

import nl.knaw.dans.easy.web.EasyResources;

import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class ShortenedCharSequenceModel extends Model<String>
{
    private static final long serialVersionUID = -6097386870538648257L;

    public ShortenedCharSequenceModel(CharSequence str)
    {
        this(str, Integer.parseInt((String) new ResourceModel(EasyResources.SHORTENEDSTRINGMODEL_DEFAULT_SHORTENCHARCOUNT).getObject()));
    }

    public ShortenedCharSequenceModel(CharSequence str, int shortenCharCount)
    {
        this(str, shortenCharCount, (String) new ResourceModel(EasyResources.SHORTENEDSTRINGMODEL_DEFAULT_SHORTENSTRING).getObject());
    }

    public ShortenedCharSequenceModel(CharSequence str, int shortenCharCount, String shortenString)
    {
        super(getShortenedString(str, shortenCharCount, shortenString));
    }

    private static String getShortenedString(CharSequence str, int shortenCharCount, String shortenString)
    {
        if (str.length() > shortenCharCount)
        {
            str = str.subSequence(0, shortenCharCount) + shortenString;
        }

        return str.toString();
    }
}
