package nl.knaw.dans.easy.business.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListCache;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.DisciplineImpl;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormDescriptorLoader;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.servicelayer.services.DepositService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyDepositService extends AbstractEasyService implements DepositService
{

    //public static final String                CONF_LOCATION     = "easy-business/discipline/emd/form-description";

    private static Logger logger = LoggerFactory.getLogger(EasyDepositService.class);

    private final Map<String, FormDescriptor> formDescriptorMap = Collections.synchronizedMap(new LinkedHashMap<String, FormDescriptor>());

    public String getServiceDescription()
    {
        return "Service for obtaining Disciplines and Discipline-related stuff.";
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException
    {
        loadFormDescriptors();
    }

    public DepositDiscipline getDiscipline(String disciplineId) throws ServiceException
    {
        return new DisciplineImpl(getFormDescriptor(disciplineId));
    }

    public DepositDiscipline getDiscipline(MetadataFormat emd_format) throws ServiceException
    {
        return getDiscipline(emd_format.toString().toLowerCase());
    }

    public List<DepositDiscipline> getDisciplines() throws ServiceException
    {
        List<DepositDiscipline> disciplines = new ArrayList<DepositDiscipline>();
        synchronized (formDescriptorMap)
        {
            for (FormDescriptor formDescriptor : getFormDescriptorMap().values())
            {
                disciplines.add(new DisciplineImpl(formDescriptor.clone()));
            }
        }
        return disciplines;
    }

    public ChoiceList getChoices(String listId, Locale locale) throws ServiceException
    {
        ChoiceList choiceList = null;
        try
        {
            choiceList = ChoiceListGetter.getInstance().getChoiceList(listId, locale);
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
        return choiceList;
    }

    public byte[] getChoicesAsByteArray(String listId, Locale locale) throws ServiceException
    {
        byte[] bytes = null;
        try
        {
            bytes = ChoiceListCache.getInstance().getBytes(listId, locale);
        }
        catch (IOException e)
        {
            throw new ServiceException(e);
        }
        catch (ResourceNotFoundException e)
        {
            throw new ServiceException(e);
        }
        return bytes;
    }

    public void getArchisInfo(BasicIdentifier archisIdentifier, EasyMetadata easyMetadata) throws ServiceException
    {
        ArchisCollector collector = new ArchisCollector(easyMetadata);
        collector.collectInfo(archisIdentifier);
    }

    private FormDescriptor getFormDescriptor(String disciplineId) throws ServiceException
    {
        FormDescriptor descriptor = getFormDescriptorMap().get(disciplineId);
        if (descriptor == null)
        {
            logger.warn("Unknown discipline: " + disciplineId + ". TRYING A RELOAD!!");
            loadFormDescriptors();
            descriptor = getFormDescriptorMap().get(disciplineId);
            if (descriptor == null)
            {
                logger.error("Totaly unknown discipline: " + disciplineId);
                throw new ServiceException("Unknown discipline: " + disciplineId);
            }
        }
        return descriptor.clone();
    }

    private Map<String, FormDescriptor> getFormDescriptorMap() throws ServiceException
    {
        if (formDescriptorMap.isEmpty())
        {
            loadFormDescriptors();
        }
        return formDescriptorMap;
    }

    protected void loadFormDescriptors() throws ServiceException
    {
        try
        {
            loadMap();
        }
        catch (ResourceNotFoundException e)
        {
            throw new ServiceException(e);
        }
    }

    protected void loadMap() throws ResourceNotFoundException
    {
        synchronized (formDescriptorMap)
        {
            FormDescriptorLoader.loadFormDescriptors(formDescriptorMap);
            List<String> invalidDescriptors = new ArrayList<String>();
            for (FormDescriptor descriptor : formDescriptorMap.values())
            {
                if (!descriptor.containsFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD))
                {
                    invalidDescriptors.add(descriptor.getId());
                    logger.warn("FormDescriptor:" + descriptor.getId() + " does not contain a deposit form definition");
                }
            }
            for (String descriptorId : invalidDescriptors)
            {
                formDescriptorMap.remove(descriptorId);
            }
        }
    }

}
