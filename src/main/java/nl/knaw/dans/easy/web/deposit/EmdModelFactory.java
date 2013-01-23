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
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeasy.Archis2ListWrapper;
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

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmdModelFactory implements IModelFactory
{

    private static final long serialVersionUID = -6320009217885693076L;

    private static final Logger logger = LoggerFactory.getLogger(EmdModelFactory.class);

    private transient Dataset dataset;

    private DatasetModel datasetModel;

    public EmdModelFactory(DatasetModel datasetModel)
    {
        this.datasetModel = datasetModel;
    }

    public ChoiceList getChoiceList(String listId, Locale locale) throws ServiceException
    {
        return Services.getDepositService().getChoices(listId, locale);
    }

    @SuppressWarnings("rawtypes")
    public IModel createModel(StandardPanelDefinition tpDef) throws ModelFactoryException
    {
        IModel model = null;

        String methodName = "create" + tpDef.getModelClass() + "Model";

        try
        {
            Method method = this.getClass().getMethod(methodName, StandardPanelDefinition.class);
            model = (IModel) method.invoke(this, tpDef);
        }
        catch (SecurityException e)
        {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (NoSuchMethodException e)
        {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (IllegalArgumentException e)
        {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (IllegalAccessException e)
        {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        catch (InvocationTargetException e)
        {
            final String msg = composeErrorMessage(tpDef);
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        //logger.debug("Created model with object " + model.getObject());
        return model;
    }

    private Dataset getDataset()
    {
        if (dataset == null)
        {
            try
            {
                dataset = datasetModel.getObject();
            }
            catch (ServiceRuntimeException e)
            {
                throw new WicketRuntimeException(e);
            }
        }
        return dataset;
    }

    private String composeErrorMessage(StandardPanelDefinition spDef)
    {
        final String msg = "Could not create model for " + TermPanelDefinition.class.getSimpleName() + ":" + spDef.getId() + "\n\t modelClass="
                + spDef.getModelClass();
        return msg;
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createEasyMetadataModel(StandardPanelDefinition definition)
    {
        return new Model(getDataset().getEasyMetadata());
    }

    protected EasyMetadata getEasyMetadata()
    {
        return getDataset().getEasyMetadata();
    }

    public IModel<Archis2ListWrapper> createArchis2ListWrapperModel(StandardPanelDefinition definition)
    {
        Archis2ListWrapper a2lw = new Archis2ListWrapper(getEasyMetadata());
        return new Model<Archis2ListWrapper>(a2lw);
    }

    public IModel<CMDIFormatChoiceWrapper> createCMDIFormatChoiceWrapperModel(StandardPanelDefinition definition)
    {
        CMDIFormatChoiceWrapper cmdi = new CMDIFormatChoiceWrapper(getEasyMetadata());
        return new Model<CMDIFormatChoiceWrapper>(cmdi);
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel<AuthorListWrapper> createAuthorListWrapperModel(StandardPanelDefinition definition)
    {
        AuthorListWrapper alw = new AuthorListWrapper(getEasyMetadataList(definition));
        return new Model(alw);
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createIsoDateListWrapperModel(StandardPanelDefinition definition)
    {
        IsoDateListWrapper dlw = new IsoDateListWrapper(getEasyMetadata().getEmdDate());
        return new Model(dlw);
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createBasicDateListWrapperModel(StandardPanelDefinition definition)
    {
        BasicDateListWrapper bdlw = new BasicDateListWrapper(getEasyMetadata().getEmdDate());
        return new Model(bdlw);
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createSingleISODateWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new SingleISODateWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createAvailableDateWrapperModel(StandardPanelDefinition definition)
    {
        final DatasetState state = getDataset().getAdministrativeState();
        if (state != null && !state.equals(DatasetState.DRAFT))
            return new Model(new SingleISODateWrapper(getEasyMetadataList(definition)));
        else
        {
            final DateTime min = new DateTime(new DateTime().toString(LimitedDateWrapper.DateModel.DATE_FORMAT));
            final DateTime max = min.plusYears(2);
            return new Model(new LimitedDateWrapper(getEasyMetadataList(definition), min, max));
        }
    }

    @SuppressWarnings( {"rawtypes", "unchecked"})
    public IModel createSingleBasicDateWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new SingleBasicDateWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createBasicStringListWrapperModel(StandardPanelDefinition definition)
    {
        String schemeName = null;
        String schemeId = null;
        if (definition.hasChoicelistDefinition())
        {
            schemeName = definition.getChoiceListDefinitions().get(0).getSchemeName();
            schemeId = definition.getChoiceListDefinitions().get(0).getId();
        }
        return new Model(new BasicStringListWrapper(getEasyMetadataList(definition), schemeName, schemeId));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createBasicRemarkListWrapperModel(StandardPanelDefinition definition)
    {
        String schemeName = null;
        String schemeId = null;
        if (definition.hasChoicelistDefinition())
        {
            schemeName = definition.getChoiceListDefinitions().get(0).getSchemeName();
            schemeId = definition.getChoiceListDefinitions().get(0).getId();
        }
        return new Model(new BasicRemarkListWrapper(getEasyMetadataList(definition), schemeName, schemeId));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createSchemedBasicStringListWrapperModel(StandardPanelDefinition definition)
    {
        String schemeName = null;
        String schemeId = null;
        if (definition.hasChoicelistDefinition())
        {
            schemeName = definition.getChoiceListDefinitions().get(0).getSchemeName();
            schemeId = definition.getChoiceListDefinitions().get(0).getId();
        }
        return new Model(new SchemedBasicStringListWrapper(getEasyMetadataList(definition), schemeName, schemeId));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createPointListWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new PointListWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createBoxListWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new BoxListWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createIdentifierListWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new IdentifierListWrapper(getEasyMetadataList(definition)));
    }

    @SuppressWarnings( {"rawtypes", "unchecked"})
    public IModel createRelationListWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new RelationListWrapper(getEasyMetadata().getEmdRelation()));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    public IModel createLicenseWrapperModel(StandardPanelDefinition definition)
    {
        return new Model(new LicenseWrapper(getEasyMetadata().getEmdRights(), getEasyMetadataList(definition)));
    }

    @SuppressWarnings( {"rawtypes"})
    private List getEasyMetadataList(StandardPanelDefinition definition)
    {
        List easyMetadataList = null;
        TermPanelDefinition tpDef = (TermPanelDefinition) definition;
        try
        {
            Term term = new Term(tpDef.getTermName(), tpDef.getNamespacePrefix());
            easyMetadataList = getEasyMetadata().getTerm(term);
        }
        catch (IllegalArgumentException e)
        {
            final String msg = "Could not create Term: namespacePrefix=" + tpDef.getNamespacePrefix() + " termName=" + tpDef.getTermName();
            logger.error(msg, e);
            throw new ModelFactoryException(msg, e);
        }
        return easyMetadataList;
    }

}
