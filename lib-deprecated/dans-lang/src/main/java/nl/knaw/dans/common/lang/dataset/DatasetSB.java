package nl.knaw.dans.common.lang.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.reposearch.RepoSearchBean;
import nl.knaw.dans.common.lang.search.bean.annotation.CopyField;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchBean;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchField;

@SearchBean(defaultIndex = DatasetsIndex.class, typeIdentifier = DatasetSB.DATASET_TYPE_IDENTIFIER)
public class DatasetSB extends RepoSearchBean
{
    private static final long serialVersionUID = -7886251751049650121L;

    public static final String DATASET_TYPE_IDENTIFIER = "dataset";

    // FIELDS
    public static final String DS_STATE_FIELD = "ds_state";
    @SearchField(name = DS_STATE_FIELD, required = true)
    private DatasetState state;

    public static final String DS_ACCESSCATEGORY_FIELD = "ds_accesscategory";
    @SearchField(name = DS_ACCESSCATEGORY_FIELD, required = true)
    private AccessCategory accessCategory;

    public static final String DC_TITLE_FIELD = "dc_title";
    public static final String DC_TITLE_SORTFIELD = "dc_title_s";
    @SearchField(name = DC_TITLE_FIELD)
    @CopyField(name = DC_TITLE_SORTFIELD, getterPostfix = "Sortable")
    private List<String> dcTitle;

    public static final String DC_CREATOR_FIELD = "dc_creator";
    public static final String DC_CREATOR_SORTFIELD = "dc_creator_s";
    @SearchField(name = DC_CREATOR_FIELD)
    @CopyField(name = DC_CREATOR_SORTFIELD, getterPostfix = "Sortable")
    private List<String> dcCreator;

    public static final String DC_SUBJECT_FIELD = "dc_subject";
    @SearchField(name = DC_SUBJECT_FIELD)
    private List<String> dcSubject;

    public static final String DC_DESCRIPTION_FIELD = "dc_description";
    @SearchField(name = DC_DESCRIPTION_FIELD)
    private List<String> dcDescription;

    public static final String DC_PUBLISHER_FIELD = "dc_publisher";
    public static final String DC_PUBLISHER_SORTFIELD = "dc_publisher_s";
    @SearchField(name = DC_PUBLISHER_FIELD)
    @CopyField(name = DC_PUBLISHER_SORTFIELD, getterPostfix = "Sortable")
    private List<String> dcPublisher;

    public static final String DC_CONTRIBUTOR_FIELD = "dc_contributor";
    public static final String DC_CONTRIBUTOR_SORTFIELD = "dc_contributor_s";
    @SearchField(name = DC_CONTRIBUTOR_FIELD)
    @CopyField(name = DC_CONTRIBUTOR_SORTFIELD, getterPostfix = "Sortable")
    private List<String> dcContributor;

    public static final String DC_DATE_FIELD = "dc_date";
    @SearchField(name = DC_DATE_FIELD)
    private List<String> dcDate;

    public static final String DC_TYPE_FIELD = "dc_type";
    @SearchField(name = DC_TYPE_FIELD)
    private List<String> dcType;

    public static final String DC_FORMAT_FIELD = "dc_format";
    @SearchField(name = DC_FORMAT_FIELD)
    private List<String> dcFormat;

    public static final String DC_IDENTIFIER_FIELD = "dc_identifier";
    @SearchField(name = DC_IDENTIFIER_FIELD)
    private List<String> dcIdentifier;

    public static final String DC_SOURCE_FIELD = "dc_source";
    @SearchField(name = DC_SOURCE_FIELD)
    private List<String> dcSource;

    public static final String DC_LANGUAGE_FIELD = "dc_language";
    @SearchField(name = DC_LANGUAGE_FIELD)
    private List<String> dcLanguage;

    public static final String DC_RELATION_FIELD = "dc_relation";
    @SearchField(name = DC_RELATION_FIELD)
    private List<String> dcRelation;

    public static final String DC_COVERAGE_FIELD = "dc_coverage";
    @SearchField(name = DC_COVERAGE_FIELD)
    private List<String> dcCoverage;

    public static final String DC_RIGHTS_FIELD = "dc_rights";
    @SearchField(name = DC_RIGHTS_FIELD)
    private List<String> dcRights;

    public void setDublinCore(DublinCoreMetadata dc)
    {
        setDcTitle(dc.getTitle());
        setDcDescription(dc.getDescription());
        setDcCreator(dc.getCreator());
        setDcSubject(dc.getSubject());
        setDcContributor(dc.getContributor());
        setDcCoverage(dc.getCoverage());
        setDcDate(dc.getDate());
        setDcFormat(dc.getFormat());
        setDcIdentifier(dc.getIdentifier());
        setDcLanguage(dc.getLanguage());
        setDcPublisher(dc.getPublisher());
        setDcRelation(dc.getRelation());
        setDcRights(dc.getRights());
        setDcSource(dc.getSource());
        setDcType(dc.getType());
    }

    private String flatten(List<String> in)
    {
        if (in == null)
            return null;
        String out = "";
        for (String str : in)
            out += str + " ";
        return out;
    }

    public List<String> getDcTitle()
    {
        return dcTitle;
    }

    public String getDcTitleSortable()
    {
        return flatten(dcTitle);
    }

    public void setDcTitle(List<String> dcTitle)
    {
        this.dcTitle = dcTitle;
    }

    public List<String> getDcCreator()
    {
        return dcCreator;
    }

    public String getDcCreatorSortable()
    {
        return flatten(dcCreator);
    }

    public void setDcCreator(List<String> dcCreator)
    {
        this.dcCreator = dcCreator;
    }

    public List<String> getDcSubject()
    {
        return dcSubject;
    }

    public void setDcSubject(List<String> dcSubject)
    {
        this.dcSubject = dcSubject;
    }

    public List<String> getDcDescription()
    {
        return dcDescription;
    }

    public void setDcDescription(List<String> dcDescription)
    {
        this.dcDescription = dcDescription;
    }

    public List<String> getDcPublisher()
    {
        return dcPublisher;
    }

    public String getDcPublisherSortable()
    {
        return flatten(dcPublisher);
    }

    public void setDcPublisher(List<String> dcPublisher)
    {
        this.dcPublisher = dcPublisher;
    }

    public List<String> getDcContributor()
    {
        return dcContributor;
    }

    public String getDcContributorSortable()
    {
        return flatten(dcContributor);
    }

    public void setDcContributor(List<String> dcContributor)
    {
        this.dcContributor = dcContributor;
    }

    public List<String> getDcDate()
    {
        return dcDate;
    }

    public void setDcDate(List<String> dcDate)
    {
        this.dcDate = dcDate;
    }

    public List<String> getDcType()
    {
        return dcType;
    }

    public void setDcType(List<String> dcType)
    {
        this.dcType = dcType;
    }

    public List<String> getDcFormat()
    {
        return dcFormat;
    }

    public void setDcFormat(List<String> dcFormat)
    {
        this.dcFormat = dcFormat;
    }

    public List<String> getDcIdentifier()
    {
        return dcIdentifier;
    }

    public void setDcIdentifier(List<String> dcIdentifier)
    {
        this.dcIdentifier = dcIdentifier;
    }

    public List<String> getDcSource()
    {
        return dcSource;
    }

    public void setDcSource(List<String> dcSource)
    {
        this.dcSource = dcSource;
    }

    public List<String> getDcLanguage()
    {
        return dcLanguage;
    }

    public void setDcLanguage(List<String> dcLanguage)
    {
        this.dcLanguage = dcLanguage;
    }

    public List<String> getDcRelation()
    {
        return dcRelation;
    }

    public void setDcRelation(List<String> dcRelation)
    {
        this.dcRelation = dcRelation;
    }

    public List<String> getDcCoverage()
    {
        return dcCoverage;
    }

    public void setDcCoverage(List<String> dcCoverage)
    {
        this.dcCoverage = dcCoverage;
    }

    public List<String> getDcRights()
    {
        return dcRights;
    }

    public String getDcRightsSortable()
    {
        return flatten(dcRights);
    }

    public void setDcRights(List<String> dcRights)
    {
        this.dcRights = dcRights;
    }

    public void setState(DatasetState datasetState)
    {
        this.state = datasetState;
    }

    public DatasetState getState()
    {
        return this.state;
    }

    public void setAccessCategory(AccessCategory accessCategory)
    {
        this.accessCategory = accessCategory;
    }

    public AccessCategory getAccessCategory()
    {
        return accessCategory;
    }
}
