package nl.knaw.dans.easy.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleFieldSet;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.joda.time.DateTime;

/**
 * A resource for CMDI type metadata.
 * 
 * @author Georgi Khomeriki
 */
@Path("/cmdi")
public class CmdiResource extends AuthenticatedResource {

	public static final String CMDI_MEDIA_TYPE = "application/x-cmdi+xml";

	/**
	 * Resource to retrieve the latest modification date of CMDI metadata in EASY.
	 * 
	 * @return Date of the last time CMDI metadata was modified in EASY.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GET
	@Path("/last-modified")
	public Response getLastModified() {
		try {
			EasyUser user = authenticate();
			FieldSet fields = new SimpleFieldSet();
			fields.add(new SimpleField(DatasetSB.DC_FORMAT_FIELD,
					CMDI_MEDIA_TYPE));
			SearchRequest request = new SimpleSearchRequest();
			request.setFilterQueries(fields);
			SearchResult<? extends DatasetSB> result = Services
					.getSearchService()
					.searchPublished(request, user);
			DateTime lastDate = null;
			for (Object o : result.getHits()) {
				SimpleSearchHit<?> hit = (SimpleSearchHit<?>) o;
				EasyDatasetSB hitData = (EasyDatasetSB) hit.getData();
				String sid = hitData.getStoreId();
				Dataset dataset = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
				AdministrativeMetadata amd = dataset.getAdministrativeMetadata();
				DateTime date = amd.getDateOfLastChangeTo(amd.getAdministrativeState());
				if(lastDate == null || lastDate.isBefore(date)) {
					lastDate = date;
				}
			}
			return lastDate != null ? simpleResponse(lastDate.toString()) : notFound();
		} catch (ServiceException e) {
			return internalServerError(e);
		}
	}

}
