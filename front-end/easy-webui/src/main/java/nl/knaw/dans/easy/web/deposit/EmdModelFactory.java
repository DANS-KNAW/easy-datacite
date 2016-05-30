package nl.knaw.dans.easy.web.deposit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeasy.ArchisListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.AuthorListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.BasicDateListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.BasicRemarkListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.BasicStringListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.BoxListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.CMDIFormatChoiceWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.IdentifierListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.IsoDateListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.LicenseWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.LimitedDateWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.PointListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.RelationListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.SchemedBasicStringListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.SingleBasicDateWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.SingleISODateWrapper;
import nl.knaw.dans.easy.web.wicket.IModelFactory;
import nl.knaw.dans.easy.web.wicket.ModelFactoryException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmdModelFactory implements IModelFactory {

    private static final long serialVersionUID = -6320009217885693076L;

    private static final Logger logger = LoggerFactory.getLogger(EmdModelFactory.class);

    private transient Dataset dataset;

    private DatasetModel datasetModel;

    @SpringBean(name = "depositService")
    private DepositService depositService;

    public EmdModelFactory(DatasetModel datasetModel) {
        this.datasetModel = datasetModel;
        InjectorHolder.getInjector().inject(this);
    }

    public ChoiceList getChoiceList(String listId, Locale locale) throws ServiceException {
        return depositService.getChoices(listId, locale);
    }

    @SuppressWarnings("rawtypes")
    public IModel createModel(StandardPanelDefinition tpDef) throws ModelFactoryException {
        IModel model = null;

        String methodName = "create" + tpDef.getModelClass() + "Model";

        try {
            Method method = this.getClass().getMethod(methodName, StandardPanelDefinition.class);
            model = (IModel) method.invoke(this, tpDef);
        }
        catch (SecurityException e) {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (NoSuchMethodException e) {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (IllegalArgumentException e) {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (IllegalAccessException e) {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (InvocationTargetException e) {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        return model;
    }

    private Dataset getDataset() {
        if (dataset == null) {
            try {
                dataset = datasetModel.getObject();
            }
            catch (ServiceRuntimeException e) {
                throw new WicketRuntimeException(e);
            }
        }
        return dataset;
    }

    private String composeErrorMessage(StandardPanelDefinition spDef) {
        final String msg = "Could not create model for " + TermPanelDefinition.class.getSimpleName() + ":" + spDef.getId() + "\n\t modelClass="
                + spDef.getModelClass();
        return msg;
    }

    public IModel<EasyMetadata> createEasyMetadataModel(StandardPanelDefinition definition) {
        return new Model<EasyMetadata>(getDataset().getEasyMetadata());
    }

    protected EasyMetadata getEasyMetadata() {
        return getDataset().getEasyMetadata();
    }

    public IModel<ArchisListWrapper> createArchisListWrapperModel(StandardPanelDefinition definition) {
        ArchisListWrapper alw = new ArchisListWrapper(getEasyMetadata());
        return new Model<ArchisListWrapper>(alw);
    }

    public IModel<CMDIFormatChoiceWrapper> createCMDIFormatChoiceWrapperModel(StandardPanelDefinition definition) {
        CMDIFormatChoiceWrapper cmdi = new CMDIFormatChoiceWrapper(getEasyMetadata());
        return new Model<CMDIFormatChoiceWrapper>(cmdi);
    }

    @SuppressWarnings({"unchecked"})
    public IModel<AuthorListWrapper> createAuthorListWrapperModel(StandardPanelDefinition definition) {
        AuthorListWrapper alw = new AuthorListWrapper(getEasyMetadataList(definition));
        return new Model<AuthorListWrapper>(alw);
    }

    public IModel<IsoDateListWrapper> createIsoDateListWrapperModel(StandardPanelDefinition definition) {
        IsoDateListWrapper dlw = new IsoDateListWrapper(getEasyMetadata().getEmdDate());
        return new Model<IsoDateListWrapper>(dlw);
    }

    public IModel<BasicDateListWrapper> createBasicDateListWrapperModel(StandardPanelDefinition definition) {
        BasicDateListWrapper bdlw = new BasicDateListWrapper(getEasyMetadata().getEmdDate());
        return new Model<BasicDateListWrapper>(bdlw);
    }

    @SuppressWarnings({"unchecked"})
    public IModel<SingleISODateWrapper> createSingleISODateWrapperModel(StandardPanelDefinition definition) {
        return new Model<SingleISODateWrapper>(new SingleISODateWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<? extends SingleISODateWrapper> createAvailableDateWrapperModel(StandardPanelDefinition definition) {
        final DatasetState state = getDataset().getAdministrativeState();
        if (state != null && !state.equals(DatasetState.DRAFT))
            return new Model<SingleISODateWrapper>(new SingleISODateWrapper(getEasyMetadataList(definition)));
        else {
            final DateTime min = new DateTime(new DateTime().toString(LimitedDateWrapper.DateModel.DATE_FORMAT));
            final DateTime max = min.plusYears(2);
            return new Model<LimitedDateWrapper>(new LimitedDateWrapper(getEasyMetadataList(definition), min, max));
        }
    }

    @SuppressWarnings({"unchecked"})
    public IModel<SingleBasicDateWrapper> createSingleBasicDateWrapperModel(StandardPanelDefinition definition) {
        return new Model<SingleBasicDateWrapper>(new SingleBasicDateWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<BasicStringListWrapper> createBasicStringListWrapperModel(StandardPanelDefinition definition) {
        String schemeName = null;
        String schemeId = null;
        if (definition.hasChoicelistDefinition()) {
            schemeName = definition.getChoiceListDefinitions().get(0).getSchemeName();
            schemeId = definition.getChoiceListDefinitions().get(0).getId();
        }
        return new Model<BasicStringListWrapper>(new BasicStringListWrapper(getEasyMetadataList(definition), schemeName, schemeId));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<BasicRemarkListWrapper> createBasicRemarkListWrapperModel(StandardPanelDefinition definition) {
        String schemeName = null;
        String schemeId = null;
        if (definition.hasChoicelistDefinition()) {
            schemeName = definition.getChoiceListDefinitions().get(0).getSchemeName();
            schemeId = definition.getChoiceListDefinitions().get(0).getId();
        }
        return new Model<BasicRemarkListWrapper>(new BasicRemarkListWrapper(getEasyMetadataList(definition), schemeName, schemeId));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<SchemedBasicStringListWrapper> createSchemedBasicStringListWrapperModel(StandardPanelDefinition definition) {
        String schemeName = null;
        String schemeId = null;
        if (definition.hasChoicelistDefinition()) {
            schemeName = definition.getChoiceListDefinitions().get(0).getSchemeName();
            schemeId = definition.getChoiceListDefinitions().get(0).getId();
        }
        return new Model<SchemedBasicStringListWrapper>(new SchemedBasicStringListWrapper(getEasyMetadataList(definition), schemeName, schemeId));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<PointListWrapper> createPointListWrapperModel(StandardPanelDefinition definition) {
        return new Model<PointListWrapper>(new PointListWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<BoxListWrapper> createBoxListWrapperModel(StandardPanelDefinition definition) {
        return new Model<BoxListWrapper>(new BoxListWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings("unchecked")
    public IModel<IdentifierListWrapper> createIdentifierListWrapperModel(StandardPanelDefinition definition) {
        return new Model<IdentifierListWrapper>(new IdentifierListWrapper(getEasyMetadataList(definition)));
    }

    public IModel<RelationListWrapper> createRelationListWrapperModel(StandardPanelDefinition definition) {
        return new Model<RelationListWrapper>(new RelationListWrapper(getEasyMetadata().getEmdRelation()));
    }

    @SuppressWarnings({"unchecked"})
    public IModel<LicenseWrapper> createLicenseWrapperModel(StandardPanelDefinition definition) {
        return new Model<LicenseWrapper>(new LicenseWrapper(getEasyMetadata().getEmdRights(), getEasyMetadataList(definition)));
    }

    @SuppressWarnings({"rawtypes"})
    private List getEasyMetadataList(StandardPanelDefinition definition) {
        List<MetadataItem> easyMetadataList = null;
        TermPanelDefinition tpDef = (TermPanelDefinition) definition;
        try {
            Term term = new Term(tpDef.getTermName(), tpDef.getNamespacePrefix());
            easyMetadataList = getEasyMetadata().getTerm(term);
        }
        catch (IllegalArgumentException e) {
            final String msg = "Could not create Term: namespacePrefix=" + tpDef.getNamespacePrefix() + " termName=" + tpDef.getTermName();
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        return easyMetadataList;
    }

}
