package nl.knaw.dans.easy.web.download;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.value.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownloadResponse
{
    // key for the downloadParameters in the valueMap
    public static final String PARAMS = "params";

    // keys in the jsonObject
    public static final String DOWNLOAD_TYPE_ZIP = "zip";
    public static final String DOWNLOAD_TYPE = "downloadType";
    public static final String DATASET_ID = "rootSid";
    public static final String SELECTED_ITEM_LIST = "selectedItemList";
    public static final String SELECTED_ITEM = "selectedItem";

    // keys in the "selectedItemList" array
    public static final String ITEM_ID = "sid";

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadResponse.class);

    private final ValueMap valueMap;
    private JSONObject jsonObject;
    private int statusCode;
    private AbstractDownloadHandler downloadHandler;

    public FileDownloadResponse(ValueMap parameters)
    {
        this.valueMap = parameters;
    }

    protected ValueMap getValueMap()
    {
        return valueMap;
    }

    protected JSONObject getClientObject() throws DownloadException
    {
        if (jsonObject == null)
        {
            String downloadParameters = getValueMap().getString(PARAMS);
            try
            {
                jsonObject = new JSONObject(downloadParameters);
            }
            catch (JSONException e)
            {
                String msg = "Unable to read client parameters: ";
                setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                logger.error(msg, e);
                throw new DownloadException(msg, e);
            }
        }
        return jsonObject;
    }

    protected String getDownloadType() throws DownloadException
    {
        String downloadType = DOWNLOAD_TYPE_ZIP;
        try
        {
            downloadType = getClientObject().getString(DOWNLOAD_TYPE);
        }
        catch (JSONException e)
        {
            logger.error("Unable to determine download type: ", e);
            // in this case we default to download type zip.
        }
        return downloadType;
    }

    protected String getMandatoryStringParam(final String key) throws DownloadException
    {
        String value;
        try
        {
            value = getClientObject().getString(key);
            if (StringUtils.isBlank(value))
            {
                String msg = "Unable to read parameter " + key + ": ";
                setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                logger.error(msg);
                throw new DownloadException(msg);
            }
        }
        catch (JSONException e)
        {
            String msg = "Unable to read parameter " + key + ": ";
            setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(msg, e);
            throw new DownloadException(msg, e);
        }
        return value;
    }

    protected List<RequestedItem> getDownloadRequestItems()
    {
        List<RequestedItem> wantedItems = new ArrayList<RequestedItem>();
        try
        {
            JSONArray jsonArray = getClientObject().getJSONArray(SELECTED_ITEM_LIST);

            int numberOfSelectedItems = jsonArray.length();

            for (int i = 0; i < numberOfSelectedItems; i++)
            {
                wantedItems.add(new RequestedItem(jsonArray.getString(i)));
            }
        }
        catch (JSONException e)
        {
            String msg = "Unable to read parameters for zip download: ";
            setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(msg, e);
            throw new DownloadException(msg, e);
        }
        return wantedItems;
    }

    public boolean hasStatusError()
    {
        return statusCode > 0;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public AbstractDownloadHandler getResourceStream()
    {
        if (downloadHandler == null)
        {
            try
            {
                String downloadType = getDownloadType();
                if (downloadType.equals(DOWNLOAD_TYPE_ZIP))
                {
                    downloadHandler = new ZipDownloadHandler(this);
                }
                else
                {
                    downloadHandler = new SingleFileDownloadHandler(this);
                }

            }
            catch (DownloadException e)
            {
                setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                downloadHandler = null;
            }
        }
        return downloadHandler;
    }

}
