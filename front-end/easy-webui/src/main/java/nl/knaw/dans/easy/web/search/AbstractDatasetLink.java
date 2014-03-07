package nl.knaw.dans.easy.web.search;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SnippetField;
import nl.knaw.dans.common.lang.search.bean.SearchBeanUtil;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.PropertiesMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public abstract class AbstractDatasetLink<T> extends Link<T>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDatasetLink.class);

    private ArrayList<String> alreadyShownSnippets;

    public AbstractDatasetLink(String id, IModel model)
    {
        super(id, model);
    }

    private static final long serialVersionUID = -574465389734042841L;

    protected void addLabel(Label label)
    {
        addLabel(label, !StringUtils.isBlank((String) label.getDefaultModelObject()));
    }

    protected void addLabel(Label label, boolean visible)
    {
        label.setVisible(visible);
        add(label);
    }

    private String formatStrList(List<String> c)
    {
        String result = "";
        if (c != null && c.size() > 0)
        {
            for (int i = 0; i < c.size(); i++)
            {
                result += c.get(i);
                if (i + 1 < c.size())
                    result += ", ";
            }
        }

        return result;
    }

    protected String getSnippetOrValue(String propertyName)
    {

        SearchHit<? extends DatasetSB> hit = (SearchHit<? extends DatasetSB>) getModelObject();
        DatasetSB datasetHit = hit.getData();

        String fieldName = "";
        Object fieldValue = null;
        try
        {
            fieldName = SearchBeanUtil.getFieldName(datasetHit, propertyName);
            SnippetField snippet = hit.getSnippetByName(fieldName);
            if (snippet != null)
            {
                if (alreadyShownSnippets == null)
                    alreadyShownSnippets = new ArrayList<String>();
                alreadyShownSnippets.add(snippet.getName());
                return formatStrList(snippet.getValue());
            }
            fieldValue = SearchBeanUtil.getFieldValue(datasetHit, propertyName);
        }
        catch (Exception e)
        {
            final String message = new PropertiesMessage("getSnippetOrValue").errorMessage(EasyResources.SHOW_RESULTS);
            LOGGER.error(message, e);
            throw new InternalWebError();
        }

        // assuming string list format!
        return formatStrList((List<String>) fieldValue);
    }

    protected List<SnippetField> getRemainingSnippets()
    {
        SearchHit<? extends DatasetSB> hit = (SearchHit<? extends DatasetSB>) getModelObject();
        List<SnippetField> snippets = new ArrayList<SnippetField>();
        if (hit.getSnippets() == null)
            return snippets;
        for (SnippetField snippet : hit.getSnippets())
        {
            if (alreadyShownSnippets == null || alreadyShownSnippets.indexOf(snippet.getName()) < 0)
                snippets.add(snippet);
        }
        return snippets;
    }

}
