package nl.knaw.dans.easy.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.util.StreamUtil;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmoContentExport extends DynamicWebResource
{

    private static final long   serialVersionUID = 6186922192776184192L;

    private static final Logger logger           = LoggerFactory.getLogger(DmoContentExport.class);

    public static final String  PARAM_STORE_ID   = "sid";
    public static final String  PARAM_UNIT_ID    = "did";
    
    private Map<String, DmoContentExportResponse> responses = new HashMap<String, DmoContentExportResponse>();

    @Override
    protected void setHeaders(WebResponse response)
    {
        String storeId = getParameters().getString(PARAM_STORE_ID);
        String unitId = getParameters().getString(PARAM_UNIT_ID);
        DmoContentExportResponse exportResponse = getExportResponse(storeId, unitId);
        if (exportResponse.hasError())
        {
            sendError(response, exportResponse);
            invalidate();
        }
        else
        {
            super.setHeaders(response);
            UnitMetadata umd = exportResponse.getUnitMetadata();
            
            response.setContentType(umd.getMimeType());
            response.setHeader("Content-Disposition", "filename=" + umd.getLabel());
            response.setHeader("Content-Length", "" + umd.getSize());
        }
    }

    private DmoContentExportResponse getExportResponse(String storeId, String unitId)
    {        
        DmoContentExportResponse exportResponse = responses.get(storeId + unitId);
        if (exportResponse == null)
        {
            exportResponse = new DmoContentExportResponse(storeId, unitId);
            responses.put(storeId + unitId, exportResponse);
            logger.debug("Cached exportResonses size=" + responses.size());
        }
        return exportResponse;
    }

    private void sendError(WebResponse response, DmoContentExportResponse exportResponse)
    {
        try
        {
            response.getHttpServletResponse().sendError(exportResponse.getErrorCode());
        }
        catch (IOException e)
        {
            logger.error("Unable to send error response.", e);
        }
    }
    
    @Override
    protected ResourceState getResourceState()
    {
        String storeId = getParameters().getString(PARAM_STORE_ID);
        String unitId = getParameters().getString(PARAM_UNIT_ID);
        DmoContentExportResponse exportResponse = responses.remove(storeId + unitId);
        return exportResponse;
    }
    
    private static class DmoContentExportResponse extends DynamicWebResource.ResourceState
    {

        private static final long serialVersionUID = 5039616045135391720L;
        
        private UnitMetadata unitMetadata;
        private byte[] data;
        private int errorCode;
        
        public DmoContentExportResponse(String storeId, String unitId)
        {
            DmoStoreId dmoStoreId = null;
            DsUnitId dsUnitId = null;
            try
            {
                dmoStoreId = new DmoStoreId(storeId);
                dsUnitId = new DsUnitId(unitId);
            }
            catch (IllegalArgumentException e)
            {
                errorCode = HttpServletResponse.SC_BAD_REQUEST;
            }
            
            if (errorCode == 0)
                retrieveUnitMetadata(dmoStoreId, dsUnitId);
            
            if (errorCode == 0)
                retrieveData(dmoStoreId, dsUnitId);
        }
        
        public UnitMetadata getUnitMetadata()
        {
            return unitMetadata;
        }

        @Override
        public byte[] getData()
        {
            if (data == null)
            {
                throw new IllegalStateException("No data");
            }
            else
            {
                return data;
            }
        }
        
        @Override
        public String getContentType()
        {
            if (unitMetadata == null)
            {
                return "text/plain";
            }
            else
            {
                return unitMetadata.getMimeType();
            }
        }

        public int getErrorCode()
        {
            return errorCode;
        }
        
        public boolean hasError()
        {
            return errorCode != 0;
        }

        private void checkParameters(String storeId, String unitId)
        {
            if (StringUtils.isBlank(storeId) || StringUtils.isBlank(unitId))
            {
                errorCode = HttpServletResponse.SC_BAD_REQUEST;
            }
        }

        private void retrieveUnitMetadata(DmoStoreId storeId, DsUnitId unitId)
        {
            try
            {
                List<UnitMetadata> umdList = Services.getJumpoffService().getUnitMetadata(EasySession.get().getUser(),storeId, unitId);
                if (umdList.size() > 0)
                {
                    unitMetadata = umdList.get(0);
                }
                else
                {
                    errorCode = HttpServletResponse.SC_NOT_FOUND;
                    logger.info("Returning NOT FOUND for dmo content. storeId=" + storeId + " unitId=" + unitId);
                }
            }
            catch (CommonSecurityException e)
            {
                errorCode = HttpServletResponse.SC_FORBIDDEN;
                logger.error("Illegal access attempt. storeId=" + storeId + " unitId=" + unitId, e);
            }
            catch (ServiceException e)
            {
                errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                logger.error("Unable to serve content. storeId=" + storeId + " unitId=" + unitId, e);
            }
        }
        
        private void retrieveData(DmoStoreId storeId, DsUnitId unitId)
        {
            try
            {
                URL url = Services.getJumpoffService().getURL(storeId, unitId);
                InputStream inStream = url.openStream();
                data = StreamUtil.getBytes(inStream);
                inStream.close();
            }
            catch (CommonSecurityException e)
            {
                errorCode = HttpServletResponse.SC_FORBIDDEN;
                logger.error("Illegal access attempt. storeId=" + storeId + " unitId=" + unitId, e);
            }
            catch (ServiceException e)
            {
                errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                logger.error("Unable to serve content. storeId=" + storeId + " unitId=" + unitId, e);
            }
            catch (IOException e)
            { 
                errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                logger.error("Unable to serve content. storeId=" + storeId + " unitId=" + unitId, e);
            }
        }
        
    }

}
