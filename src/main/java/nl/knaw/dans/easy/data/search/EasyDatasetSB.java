package nl.knaw.dans.easy.data.search;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetsIndex;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchBean;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchField;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionRequestSearchInfo;
import nl.knaw.dans.easy.domain.model.PermissionRequestSearchInfoConverter;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SearchBean(
		defaultIndex = DatasetsIndex.class, 
		typeIdentifier = Dataset.NAME_SPACE_VALUE)
public class EasyDatasetSB extends DatasetSB
{
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(EasyDatasetSB.class);
    
	private static final long serialVersionUID = 6823227197181816448L;

	public static final String DEFAULT_DATASET_INDEX = "datasets";

	// FIELDS
	public static final String DATE_DRAFT_SAVED_FIELD = "emd_date_draft_saved";
	@SearchField(name = DATE_DRAFT_SAVED_FIELD)
	private DateTime dateDraftSaved;
	
	public static final String DATE_CREATED_FIELD = "emd_date_created";
	@SearchField(name = DATE_CREATED_FIELD)
	private DateTime dateCreated;
	
	public static final String DATE_CREATED_FORMATTED_FIELD = "emd_date_created_formatted";
    @SearchField(name = DATE_CREATED_FORMATTED_FIELD)
    private String dateCreatedFormatted;
	
	public static final String DATE_AVAILABLE_FIELD = "emd_date_available";
	@SearchField(name = DATE_AVAILABLE_FIELD)
	private DateTime dateAvailable;
	
	public static final String DATE_AVAILABLE_FORMATTED_FIELD = "emd_date_available_formatted";
    @SearchField(name = DATE_AVAILABLE_FORMATTED_FIELD)
    private String dateAvailableFormatted;

	public static final String DATE_SUBMITTED_FIELD = "emd_date_submitted";
	@SearchField(name = DATE_SUBMITTED_FIELD)
	private DateTime dateSubmitted;

	public static final String DATE_PUBLISHED_FIELD = "emd_date_published";
	@SearchField(name = DATE_PUBLISHED_FIELD)
	private DateTime datePublished;

	public static final String DATE_DELETED_FIELD = "emd_date_deleted";
	@SearchField(name = DATE_DELETED_FIELD)
	private DateTime dateDeleted;
	
	public static final String ARCHAEOLOGY_DC_SUBJECT = "archaeology_dc_subject";
	@SearchField(name = ARCHAEOLOGY_DC_SUBJECT)
	private List<String> archaeologyDcSubject;
	
	public static final String ARCHAEOLOGY_DCTERMS_TEMPORAL = "archaeology_dcterms_temporal";
	@SearchField(name = ARCHAEOLOGY_DCTERMS_TEMPORAL)
	private List<String> archaeologyDctermsTemporal;
	
	public static final String DEPOSITOR_ID_FIELD = "amd_depositor_id";
	//TODO: should be required=true!
	@SearchField(name = DEPOSITOR_ID_FIELD)
	private String depositorId; 

	public static final String WORKFLOW_STEPS_COMPLETED_FIELD = "amd_workflow_progress";
	@SearchField(name = WORKFLOW_STEPS_COMPLETED_FIELD)
	private Integer workflowProgress;

	public static final String ASSIGNEE_ID_FIELD = "amd_assignee_id";
	@SearchField(name = ASSIGNEE_ID_FIELD)
	private String assigneeId;
	
	public static final String AUDIENCE_FIELD = "emd_audience";
	@SearchField(name = AUDIENCE_FIELD)
	private List<String> audience;
	
	public static final String EASY_COLLECTIONS_FIELD = "easy_collections";
	@SearchField(name = EASY_COLLECTIONS_FIELD)
	private List<String> collections;
	
	public static final String PERMISSION_STATUS_FIELD = "psl_permission_status";
	@SearchField(name = PERMISSION_STATUS_FIELD, converter=PermissionRequestSearchInfoConverter.class)
	private List<PermissionRequestSearchInfo> permissionStatusList;

	public String getDepositorId()
	{
		return depositorId;
	}

	public void setDepositorId(String depositorId)
	{
		this.depositorId = depositorId;
	}

	public Integer getWorkflowProgress()
	{
		return workflowProgress;
	}

	public void setWorkflowProgress(Integer workflowStepsCompleted)
	{
		this.workflowProgress = workflowStepsCompleted;
	}

	public void setAssigneeId(String assigneeId)
	{
		this.assigneeId = assigneeId;
	}

	public String getAssigneeId()
	{
		return assigneeId;
	}
	
	public DateTime getDateDraftSaved()
	{
		return dateDraftSaved;
	}

	public void setDateDraftSaved(DateTime dateDraftSaved)
	{
		this.dateDraftSaved = dateDraftSaved;
	}

	public DateTime getDateCreated()
	{
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated)
	{
		this.dateCreated = dateCreated;
	}

    public String getDateCreatedFormatted()
    {
        return dateCreatedFormatted;
    }

    public void setDateCreatedFormatted(String dateCreatedFormatted)
    {
        this.dateCreatedFormatted = dateCreatedFormatted;
    }

    public DateTime getDateAvailable()
	{
		return dateAvailable;
	}

	public void setDateAvailable(DateTime dateAvailable)
	{
		this.dateAvailable = dateAvailable;
	}

	public String getDateAvailableFormatted()
    {
        return dateAvailableFormatted;
    }

    public void setDateAvailableFormatted(String dateAvailableFormatted)
    {
        this.dateAvailableFormatted = dateAvailableFormatted;
    }

    public DateTime getDateSubmitted()
	{
		return dateSubmitted;
	}

	public void setDateSubmitted(DateTime dateSubmitted)
	{
		this.dateSubmitted = dateSubmitted;
	}

	public DateTime getDatePublished()
	{
		return datePublished;
	}

	public void setDatePublished(DateTime datePublished)
	{
		this.datePublished = datePublished;
	}

	public DateTime getDateDeleted()
	{
		return dateDeleted;
	}

	public void setDateDeleted(DateTime dateDeleted)
	{
		this.dateDeleted = dateDeleted;
	}

	public void setAudience(List<String> audience)
	{
		this.audience = audience;
	}

	public List<String> getAudience()
	{
		return audience;
	}

    public List<String> getCollections()
    {
        return collections;
    }

    public void setCollections(List<String> collections)
    {
        this.collections = collections;
    }

    public List<PermissionRequestSearchInfo> getPermissionStatusList()
    {
        return permissionStatusList;
    }

    public void setPermissionStatusList(List<PermissionRequestSearchInfo> permissionStatusList)
    {
        this.permissionStatusList = permissionStatusList;
    }

    public List<String> getArchaeologyDcSubject()
    {
        return archaeologyDcSubject;
    }

    public void setArchaeologyDcSubject(List<String> archaeologyDcSubject)
    {
        this.archaeologyDcSubject = archaeologyDcSubject;
    }

    public List<String> getArchaeologyDctermsTemporal()
    {
        return archaeologyDctermsTemporal;
    }

    public void setArchaeologyDctermsTemporal(List<String> archaeologyDctermsTemporal)
    {
        this.archaeologyDctermsTemporal = archaeologyDctermsTemporal;
    }
}
