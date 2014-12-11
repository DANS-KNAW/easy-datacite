package nl.knaw.dans.easy.web.search.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.solr.exceptions.NullPointerFieldException;
import nl.knaw.dans.common.wicket.components.buttons.CancelLink;
import nl.knaw.dans.common.wicket.components.search.FieldNameResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.FilterCriterium;
import nl.knaw.dans.common.wicket.components.search.criteria.MultiFilterCriterium;
import nl.knaw.dans.common.wicket.components.search.criteria.SearchCriteriaPanel;
import nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.search.AbstractSearchPage;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;
import nl.knaw.dans.easy.web.statistics.AdvancedSearchStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.template.AbstractEasyForm;
import nl.knaw.dans.easy.web.template.Style;
import nl.knaw.dans.easy.web.wicket.AssignToDropChoiceList;
import nl.knaw.dans.easy.web.wicket.UserSelector;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvSearchPage extends AbstractSearchPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvSearchPage.class);

    private Class<? extends AbstractSearchResultPage> resultPage;

    // @formatter:off
    /*
     * How do you want the criteria displayed?
     * 
     * separateFilterCriteria = false:
     *      Criteria: All datasets | Adv. Search: park, nederzetting 
     * 
     * separateFilterCriteria = true:
     *      Criteria: All datasets | Title: park | Subject: nederzetting
     * 
     */
    // @formatter:on
    private boolean separateFilterCriteria = true;

    public AdvSearchPage() {
        this(PublicSearchResultPage.class);
    }

    public AdvSearchPage(Class<? extends AbstractSearchResultPage> resultPage) {
        super(new SearchModel());
        this.resultPage = resultPage;
        init();
    }

    public AdvSearchPage(SearchModel searchModel, Class<? extends AbstractSearchResultPage> resultPage) {
        super(searchModel);
        this.resultPage = resultPage;
        init();
    }

    private void init() {
        add(Style.USER_SELECTOR_HEADER_CONTRIBUTION);
        addCommonFeedbackPanel();

        add(new SearchCriteriaPanel("searchCriteria", getSearchModel()));

        add(new AdvancedSearchForm("advancedSearchForm"));
    }

    public boolean hasSeparateFilterCriteria() {
        return separateFilterCriteria;
    }

    /**
     * How do you want the criteria displayed?
     * <p/>
     * separateFilterCriteria = false:
     * 
     * <pre>
     *      Criteria: All datasets | Adv. Search: park, nederzetting
     * </pre>
     * 
     * separateFilterCriteria = true:
     * 
     * <pre>
     *      Criteria: All datasets | Title: park | Subject: nederzetting
     * </pre>
     * 
     * default value is <code>true</code>
     * 
     * @param separateFilterCriteria
     *        <code>true</code> for separate filter criteria, <code>false</code> for comma-separated list
     */
    public void setSeparateFilterCriteria(boolean separateFilterCriteria) {
        this.separateFilterCriteria = separateFilterCriteria;
    }

    public void onAdvancedSearch(final AdvSearchData searchData) {
        final List<Field<?>> fields = searchData.getFields(isArchivistOrAdmin());

        if (fields.size() == 0 && StringUtils.isBlank(searchData.query)) {
            setResponsePage(AbstractSearchResultPage.instantiate(resultPage, getSearchModel()));
            return;
        } else {
            // logging for statistics
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.ADVANCED_SEARCH_TERM, new AdvancedSearchStatistics(searchData));
        }

        // Any Field box
        if (!StringUtils.isBlank(searchData.query)) {
            getSearchModel().addCriterium(new TextSearchCriterium(searchData.query, new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 1114909631810523718L;

                final String queryStr = searchData.query; // copy the value

                public String getObject() {
                    return CriteriumLabel.createFilterText(AdvSearchPage.this.getString(ADVSEARCH_ANYFIELD_CRITERIUM_PREFIX), queryStr);
                }
            }));
        }

        if (separateFilterCriteria) {
            collectSeparateCriteria(fields);
        } else {
            collectMultiCriterium(fields);
        }

        if (isArchivistOrAdmin())
            resultPage = SearchAllSearchResultPage.class;
        setResponsePage(AbstractSearchResultPage.instantiate(resultPage, getSearchModel()));
    }

    private void collectSeparateCriteria(List<Field<?>> fields) {
        final FieldNameResourceTranslator translator = new FieldNameResourceTranslator();
        for (final Field<?> field : fields) {
            // use a copy of the field
            final SimpleField newField = new SimpleField(field);

            getSearchModel().addCriterium(new FilterCriterium(newField, new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 1L;

                final String fieldValStr = newField.getValue().toString();
                final String fieldName = newField.getName();

                @Override
                public String getObject() {
                    IModel<String> translation = translator.getTranslation(fieldName, getLocale(), false);
                    String prefix = translation.getObject();
                    return CriteriumLabel.createFilterText(prefix, fieldValStr);
                }
            }));
        }
    }

    private void collectMultiCriterium(final List<Field<?>> fields) {
        // produces Adv. Search: value1, value2
        if (fields.size() > 0) {
            getSearchModel().addCriterium(new MultiFilterCriterium(fields, new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 6378460988292127479L;

                @Override
                public String getObject() {
                    String prefix = "";
                    String fieldStr = "";

                    if (fields.size() > 1) {
                        prefix = AdvSearchPage.this.getString(ADVSEARCH_CRITERIUM_PREFIX);

                        Iterator<Field<?>> fieldIt = fields.iterator();
                        while (fieldIt.hasNext()) {
                            Field<?> field = fieldIt.next();
                            if (field.getValue() != null) {
                                fieldStr += field.getValue().toString();
                                if (fieldIt.hasNext())
                                    fieldStr += ", ";
                            }
                        }
                    } else if (fields.size() > 0) {

                        Field<?> field = fields.get(0);

                        FieldNameResourceTranslator translator = new FieldNameResourceTranslator();
                        IModel<String> translation = translator.getTranslation(field.getName(), getLocale(), false);
                        prefix = translation.getObject();

                        if (field.getValue() != null)
                            fieldStr = field.getValue().toString();
                    }
                    return CriteriumLabel.createFilterText(prefix, fieldStr);
                }
            }));
        }
    }

    /**
     * The form with all input options. The markup for this form can currently be found in AdvancedSearchPage.html.
     * 
     * @author lobo
     */
    class AdvancedSearchForm extends AbstractEasyForm<AdvSearchData> {
        private static final long serialVersionUID = -3768697914151647721L;

        public AdvancedSearchForm(String wicketId) {
            this(wicketId, new Model<AdvSearchData>(null));
        }

        public AdvancedSearchForm(String wicketId, IModel<AdvSearchData> model) {
            super(wicketId, model);

            if (getModelObject() == null) {
                model = new Model(new AdvSearchData());
                setDefaultModel(model);
            }

            AdvSearchData data = (AdvSearchData) getModelObject();

            // archivist panel
            initArchivistOptions();

            // general search fields
            add(new TextField<String>("anyField", new PropertyModel(data, "query")));
            add(new TextField<String>("titleField", new SearchFieldModel(data, "title")));
            add(new TextField<String>("creatorField", new SearchFieldModel(data, "creator")));
            add(new TextField<String>("descriptionField", new SearchFieldModel(data, "description")));
            add(new TextField<String>("subjectField", new SearchFieldModel(data, "subject")));
            add(new TextField<String>("coverageField", new SearchFieldModel(data, "coverage")));
            add(new TextField<String>("identifierField", new SearchFieldModel(data, "identifier")));

            add(new SubmitLink("submitButton"));
            add(new CancelLink("cancelButton"));
        }

        private void initArchivistOptions() {
            IModel model = getDefaultModel();

            WebMarkupContainer archivistOptions = new WebMarkupContainer("archivistOptions");
            add(archivistOptions.setVisible(isArchivistOrAdmin()));

            // depositor field
            archivistOptions.add(new UserSelector("depositorField", new PropertyModel(new DepositorModel((AdvSearchData) model.getObject()), "userId")));

            // status checkboxes
            DatasetState[] datasetStates = DatasetState.values();
            List<DatasetState> statusList = new ArrayList<DatasetState>(datasetStates.length);
            for (DatasetState datasetState : datasetStates)
                statusList.add(datasetState);

            ListView<DatasetState> states = new ListView<DatasetState>("states", statusList) {
                private static final long serialVersionUID = 2127442878724476462L;

                @Override
                protected void populateItem(ListItem<DatasetState> item) {
                    DatasetState state = item.getModelObject();
                    item.add(new Check("checkbox", item.getDefaultModel()));
                    item.add(new Label("status", new ResourceModel("fieldvalue." + state.toString())));
                }
            };

            CheckGroup statusGroup = new CheckGroup("statusGroup", new PropertyModel(model, "states"));
            statusGroup.add(states);
            archivistOptions.add(statusGroup);

            // assigned to
            try {
                archivistOptions.add(AssignToDropChoiceList.getDropDownChoice("assignedToField",
                        new PropertyModel(new AssignModel((AdvSearchData) model.getObject()), "userId")));
            }
            catch (ServiceException e) {
                final String message = errorMessage(EasyResources.ERROR_GETTING_ARCHIVISTS_LIST);
                LOGGER.error(message, e);
                throw new RestartResponseException(new ErrorPage());
            }
        }

        @Override
        protected void onSubmit() {
            onAdvancedSearch(getModelObject());
        }

        class AssignModel implements Serializable {
            private static final long serialVersionUID = -6033853618498949502L;

            private final AdvSearchData data;

            protected AssignModel(AdvSearchData data) {
                this.data = data;
            }

            public KeyValuePair getUserId() {
                return new KeyValuePair(data.assignedTo.getValue(), null);
            }

            public void setUserId(KeyValuePair kvp) {
                if (kvp != null)
                    data.assignedTo.setValue(kvp.getKey());
                else
                    data.assignedTo.setValue(null);
            }
        }

        class DepositorModel implements Serializable {
            private static final long serialVersionUID = -6033853668497949502L;

            private final AdvSearchData data;

            protected DepositorModel(AdvSearchData data) {
                this.data = data;
            }

            public String getUserId() {
                return data.depositor.getValue();
            }

            public void setUserId(String user) {
                if (user != null) {
                    int index = user.indexOf(":");
                    if (index > -1)
                        data.depositor.setValue(user.substring(index + 2));
                } else
                    data.depositor.setValue(null);
            }
        }

    }
}
