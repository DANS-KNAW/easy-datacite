package nl.knaw.dans.easy.rest.resources;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleFieldSet;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.rest.util.SearchHitConverter;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * This class provides methods to access the advanced search resource.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
@Path("advsearch")
public class AdvancedSearchResource extends AuthenticatedResource
{

    private static final String QUERY_SEPERATOR = " OR ";

    /**
     * Executes an advanced search on the metadata using the given search
     * parameters.
     * 
     * @param offset
     *            Offset index of result list.
     * @param limit
     *            Max number of hits to be returned.
     * @return Response containing the hits to the search query.
     */
    @GET
    public Response search(@DefaultValue("0")
    @QueryParam(value = "offset")
    int offset, @DefaultValue("10")
    @QueryParam(value = "limit")
    int limit)
    {
        try
        {
            SearchRequest request = new SimpleSearchRequest();
            request.setFilterQueries(constructFieldSet());
            request.setOffset(offset);
            request.setLimit(limit);
            SearchResult<? extends DatasetSB> result = Services.getSearchService().searchPublished(request, authenticate());
            return responseXmlOrJson(SearchHitConverter.convert(result.getHits()));
        }
        catch (ServiceException e)
        {
            return internalServerError(e);
        }
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    private FieldSet constructFieldSet()
    {
        MultivaluedMap<String, String> parameters = getQueryParameters();
        FieldSet fields = new SimpleFieldSet();

        for (String key : parameters.keySet())
        {
            String translatedKey = translateKey(key);
            if (translatedKey != null)
            {
                fields.add(new SimpleField(translatedKey, parseValues(parameters.get(key))));
            }
        }

        return fields;
    }

    private String parseValues(List<String> values)
    {
        if (values.size() == 1)
        {
            return values.get(0);
        }
        else
        {
            String result = "(";
            for (String value : values)
            {
                result += value + QUERY_SEPERATOR;
            }
            return result.substring(0, result.length() - QUERY_SEPERATOR.length()) + ")";
        }
    }

    private String translateKey(String key)
    {
        key = key.toLowerCase();
        if (key.equals("title"))
        {
            return DatasetSB.DC_TITLE_FIELD;
        }
        else if (key.equals("creator"))
        {
            return DatasetSB.DC_CREATOR_FIELD;
        }
        else if (key.equals("description"))
        {
            return DatasetSB.DC_DESCRIPTION_FIELD;
        }
        else if (key.equals("subject"))
        {
            return DatasetSB.DC_SUBJECT_FIELD;
        }
        else if (key.equals("coverage"))
        {
            return DatasetSB.DC_COVERAGE_FIELD;
        }
        else if (key.equals("identifier"))
        {
            return DatasetSB.DC_IDENTIFIER_FIELD;
        }
        else if (key.equals("date"))
        {
            return DatasetSB.DC_DATE_FIELD;
        }
        else if (key.equals("language"))
        {
            return DatasetSB.DC_LANGUAGE_FIELD;
        }
        else if (key.equals("format"))
        {
            return DatasetSB.DC_FORMAT_FIELD;
        }
        else if (key.equals("access"))
        {
            return DatasetSB.DS_ACCESSCATEGORY_FIELD;
        }
        return null;
    }

}
