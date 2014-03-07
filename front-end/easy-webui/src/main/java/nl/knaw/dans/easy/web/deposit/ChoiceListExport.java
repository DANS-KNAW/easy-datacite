package nl.knaw.dans.easy.web.deposit;

import java.util.Locale;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves resources from the path {@link #PATH_HINT}
 * <p/>
 * The parameter <code>id</code> in the path is obligatory, parameters <code>l</code> (language-code) and
 * <code>c</code> (country-code) are optional.
 * <p/>
 * For instance:
 * 
 * <pre>
 * http://localhost:8081/resources/easy/discipline.emd.choicelist?id=archaeology.dcterms.temporal
 * http://localhost:8081/resources/easy/discipline.emd.choicelist?id=archaeology.eas.spatial&amp;l=en
 * http://localhost:8081/resources/easy/discipline.emd.choicelist?id=archaeology.eas.spatial&amp;l=en&amp;c=US
 * </pre>
 * 
 * For an up to date URL see {@link EasyWicketApplication#WICKET_APPLICATION_ALIAS}/
 * {@link #RESOURCE_NAME}
 * 
 * @author ecco Nov 27, 2009
 */
public class ChoiceListExport extends DynamicWebResource
{

    public static final String CONTENT_TYPE = "text/xml";

    public static final String PARAM_LIST_ID = "id";
    public static final String PARAM_LANGUAGE = "l";
    public static final String PARAM_COUNTRY = "c";
    public static final String RESOURCE_NAME = "discipline.emd.choicelist";

    private static String PATH_HINT = "host[:port]/[service name]/resources/" //
            + EasyWicketApplication.WICKET_APPLICATION_ALIAS + "/" + RESOURCE_NAME //
            + PARAM_LIST_ID + "=id[&amp;" //
            + PARAM_LANGUAGE + "=language-code[&amp;" //
            + PARAM_COUNTRY + "=country-code]]";

    private static final long serialVersionUID = 7934743786110774390L;

    private static final Logger logger = LoggerFactory.getLogger(ChoiceListExport.class);

    @Override
    protected ResourceState getResourceState()
    {
        logger.debug("GetResourceState");
        return new Data(getParameters());
    }

    private class Data extends DynamicWebResource.ResourceState
    {

        private final ValueMap valueMap;
        private byte[] content;

        public Data(ValueMap valueMap)
        {
            this.valueMap = valueMap;
        }

        @Override
        public String getContentType()
        {
            return CONTENT_TYPE;
        }

        @Override
        public byte[] getData()
        {
            return getContent();
        }

        private byte[] getContent()
        {
            if (content == null)
            {
                content = createContent();
            }
            return content;
        }

        private byte[] createContent()
        {
            String listId = valueMap.getString(PARAM_LIST_ID);
            if (StringUtils.isBlank(listId))
            {
                String msg = "Insufficient parameters: no '" + PARAM_LIST_ID + "'.";
                logger.debug(msg);
                return ("<error>Insufficient parameters: no '" + PARAM_LIST_ID + "'. Path hint: " + PATH_HINT + "</error>").getBytes();
            }
            String language = valueMap.getString(PARAM_LANGUAGE);
            String country = valueMap.getString(PARAM_COUNTRY);
            Locale locale = null;
            if (StringUtils.isNotBlank(language))
            {
                if (StringUtils.isNotBlank(country))
                {
                    locale = new Locale(language, country);
                }
                else
                {
                    locale = new Locale(language);
                }
            }
            byte[] bytes = null;
            try
            {
                bytes = Services.getDepositService().getChoicesAsByteArray(listId, locale);
                logger.debug("Created choicelist content for id=" + listId + ", language=" + language + ", country=" + country);
            }
            catch (ServiceException e)
            {
                logger.error("Unable to create content: ", e);
                bytes = ("<error>" + e.toString() + "</error>").getBytes();
            }
            return bytes;
        }

    }

}
