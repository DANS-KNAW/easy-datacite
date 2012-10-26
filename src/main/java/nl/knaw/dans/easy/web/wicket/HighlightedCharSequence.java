package nl.knaw.dans.easy.web.wicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link CharSequence} implementation that may contain HTML &lt;em&gt; tags. The tags are <i>not</i>
 * considered part of the char sequence. However, when getting a subsequence the &lt;em&gt; tags are
 * always properly closed.  For instance:
 * 
 * <pre>
 * CharSequence hs = new HighlightedString("This is <em>important</em> so remember it")
 * assert hs.getSubsequence(5, 10).equals("is <em>im</em>");
 * assert hs.length() == 5;
 * assert hs.charAt(11) == 'o'; 
 * </pre>
 */
public class HighlightedCharSequence implements CharSequence
{
    private String markedUpString;
    private ArrayList<String> parts = new ArrayList<String>();
    private Map<Integer, Boolean> isEmPart = new HashMap<Integer, Boolean>();

    public HighlightedCharSequence(String markedUpString)
    {
        this.markedUpString = markedUpString;
        final Pattern emElement = Pattern.compile("<em>(.*?)</em>");
        final Matcher emMatcher = emElement.matcher(markedUpString);

        int i = 0;
        int j = 0;
        while (emMatcher.find(i) && i < markedUpString.length())
        {
            if (emMatcher.start() > i)
            {
                String part = markedUpString.substring(i, emMatcher.start());
                parts.add(part);
                isEmPart.put(j++, false);
            }

            String part = emMatcher.group(1);
            parts.add(part);
            isEmPart.put(j++, true);

            i = emMatcher.end();
        }

        if (i < markedUpString.length())
        {
            String part = markedUpString.substring(i);
            parts.add(part);
            isEmPart.put(j++, false);
        }
    }

    @Override
    public char charAt(int index)
    {
        int partIndex = charIndexToPartIndex(index);
        int offset = charIndexToOffsetInPart(index);

        return parts.get(partIndex).charAt(offset);
    }

    @Override
    public int length()
    {
        int length = 0;

        for (String part : parts)
        {
            length += part.length();
        }

        return length;
    }

    @Override
    public CharSequence subSequence(int begin, int end)
    {
        int beginPartIndex = charIndexToPartIndex(begin);
        int offsetInBeginPart = charIndexToOffsetInPart(begin);
        int endPartIndex = charIndexToPartIndex(end - 1);
        int offsetInEndPart = charIndexToOffsetInPart(end - 1);

        if (beginPartIndex == endPartIndex)
        {
            String result = parts.get(beginPartIndex).substring(offsetInBeginPart, offsetInEndPart + 1);
            return isEmPart.get(beginPartIndex) ? addEmTags(result) : result;
        }

        String beginPart = parts.get(beginPartIndex).substring(offsetInBeginPart);
        String endPart = parts.get(endPartIndex).substring(0, offsetInEndPart + 1);

        String result = isEmPart.get(beginPartIndex) ? addEmTags(beginPart) : beginPart;

        for (int i = beginPartIndex + 1; i < endPartIndex; ++i)
        {
            result += isEmPart.get(i) ? addEmTags(parts.get(i)) : parts.get(i);
        }

        result += isEmPart.get(endPartIndex) ? addEmTags(endPart) : endPart;

        return result;
    }

    private static String addEmTags(String content)
    {
        return "<em>" + content + "</em>";
    }

    private int charIndexToPartIndex(int charIndex)
    {
        int c = parts.get(0).length() - 1;
        int i = 0;

        while (c < charIndex)
        {
            if (i == parts.size() - 1)
            {
                throw new IndexOutOfBoundsException();
            }

            c += parts.get(++i).length();
        }

        return i;
    }

    private int charIndexToOffsetInPart(int charIndex)
    {
        int c = charIndex;
        int i = 0;

        while (i < parts.size() && c - parts.get(i).length() >= 0)
        {
            c -= parts.get(i).length();
            ++i;
        }

        return c;
    }

    @Override
    public String toString()
    {
        return markedUpString;
    }

}
