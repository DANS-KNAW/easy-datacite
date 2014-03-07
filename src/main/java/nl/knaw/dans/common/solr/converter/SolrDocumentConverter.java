package nl.knaw.dans.common.solr.converter;

import java.util.ArrayList;
import java.util.Collection;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.common.solr.SolrUtil;

import org.apache.solr.common.SolrInputDocument;

public class SolrDocumentConverter
{

    static public SolrInputDocument convert(IndexDocument doc)
    {
        SolrInputDocument result = new SolrInputDocument();
        for (Field<?> field : doc.getFields())
        {
            result.addField(field.getName(), SolrUtil.prepareObjectForSolrJ(field.getValue()), 1);
        }

        return result;
    }

    static public Collection<SolrInputDocument> convert(Collection<IndexDocument> indexDocuments)
    {
        Collection<SolrInputDocument> result = new ArrayList<SolrInputDocument>(indexDocuments.size());
        for (IndexDocument indexDoc : indexDocuments)
        {
            result.add(convert(indexDoc));
        }
        return result;
    }

}
