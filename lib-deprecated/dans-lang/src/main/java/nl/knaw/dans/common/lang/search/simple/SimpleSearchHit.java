package nl.knaw.dans.common.lang.search.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SnippetField;

public class SimpleSearchHit<T> implements SearchHit<T>
{
    private static final long serialVersionUID = -7069145763070657957L;

    private T data;
    private float score;
    private Map<String, SnippetField> snippets;

    public SimpleSearchHit(T data)
    {
        setData(data);
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public float getRelevanceScore()
    {
        return score;
    }

    public void setRelevanceScore(float score)
    {
        this.score = score;
    }

    public void setSnippets(List<SnippetField> highlightSnippets)
    {
        if (highlightSnippets == null)
        {
            snippets = new HashMap<String, SnippetField>(0);
            return;
        }

        snippets = new HashMap<String, SnippetField>(highlightSnippets.size());
        for (SnippetField snippet : highlightSnippets)
            snippets.put(snippet.getName(), snippet);
    }

    public SnippetField getSnippetByName(String fieldName)
    {
        return snippets != null ? snippets.get(fieldName) : null;
    }

    @SuppressWarnings("unchecked")
    public List<SnippetField> getSnippets()
    {
        return snippets != null ? new ArrayList<SnippetField>(snippets.values()) : Collections.EMPTY_LIST;
    }

}
