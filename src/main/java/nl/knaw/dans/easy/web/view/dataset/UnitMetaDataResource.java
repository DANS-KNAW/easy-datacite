/**
 * 
 */
package nl.knaw.dans.easy.web.view.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.common.lang.util.StreamUtil;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.util.time.Time;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitMetaDataResource extends DynamicWebResource
{
	protected static final Logger	logger				= LoggerFactory.getLogger(UnitMetaDataResource.class);
	private static final long		serialVersionUID	= 7094073311242493238L;
	private final UnitMetadata		unitMetaData;
	private final DatasetModel	datasetModel;

	public UnitMetaDataResource(final DatasetModel datasetModel, final UnitMetadata unitMetaData)
	{
		this.datasetModel = datasetModel;
		this.unitMetaData = unitMetaData;
	}

	@Override
	protected void setHeaders(final WebResponse response)
	{
		super.setHeaders(response);
		response.setAttachmentHeader(datasetModel.getObject().getPreferredTitle() + " " + unitMetaData.getLabel());
	}
	
	protected URL getURL() throws ServiceException, CommonSecurityException {
		final URL url = Services.getDatasetService().getUnitMetadataURL(
		        EasySession.getSessionUser(), 
		        datasetModel.getObject(), 
		        unitMetaData);
		return url;
	}
	
	protected DatasetModel getDatasetModel() {
		return datasetModel;
	}

	@Override
	protected ResourceState getResourceState()
	{
		return new DynamicWebResource.ResourceState()
		{
			@Override
			public String getContentType()
			{
				return unitMetaData.getMimeType();
			}

			@Override
			public Time lastModifiedTime()
			{
				try
				{
					final DateTime dateTime = unitMetaData.getCreationDate();
					final String date = dateTime.toString("YYYY.MM.dd");
					final String time = dateTime.toString("hh.mma");
					return Time.valueOf(date + "-" + time);
				}
				catch (final ParseException e)
				{
					return Time.now();
				}
			}

			@Override
			public byte[] getData()
			{
				final byte[] content;
				try
				{
				    final URL url = getURL();
					final InputStream openStream = url.openStream();
					content = StreamUtil.getBytes(openStream);
					openStream.close();
				}
				catch (final IOException e)
				{
					logger.error("Cannot read URL", e);
					throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
                catch (CommonSecurityException e)
                {
                    logger.error("Illegal access to URL", e);
                    throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_FORBIDDEN);
                }
                catch (ServiceException e)
                {
                    logger.error("Cannot get URL", e);
                    throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
				return content;
			}
		};
	}

	
}
