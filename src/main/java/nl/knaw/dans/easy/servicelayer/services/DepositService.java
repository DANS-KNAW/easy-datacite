package nl.knaw.dans.easy.servicelayer.services;

import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.jibx.bean.RecursiveList;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;

/**
 * Service for {@link DepositDiscipline}s. Each Discipline obtained from this service is guaranteed to have a
 * {@link FormDefinition} with an id {@link DepositDiscipline#EMD_DEPOSITFORM_WIZARD}.
 * 
 * @author ecco
 */
public interface DepositService extends EasyService
{

    /**
     * Get the Discipline with the given disciplineId.
     * 
     * @param formatId
     *        disciplineId
     * @return Discipline with the given disciplineId
     * @throws ServiceException
     *         wrapper for exceptions
     */
    DepositDiscipline getDiscipline(String formatId) throws ServiceException;

    /**
     * Get the Discipline with the given easy metadata format.
     * 
     * @param disciplineId
     *        disciplineId
     * @return Discipline with the given disciplineId
     * @throws ServiceException
     *         wrapper for exceptions
     */
    DepositDiscipline getDiscipline(MetadataFormat emdFormat) throws ServiceException;

    /**
     * Get a list of known Disciplines. The list ordered according to {@link DepositDiscipline#getOrdinal()}.
     * 
     * @return list of known Disciplines
     * @throws ServiceException
     *         wrapper for exceptions
     */
    List<DepositDiscipline> getDisciplines() throws ServiceException;
    
    ChoiceList getChoices(String listId, Locale locale) throws ServiceException;
    
    byte[] getChoicesAsByteArray(String listId, Locale locale) throws ServiceException;
    
    RecursiveList getRecursiveList(String listId, Locale locale) throws ServiceException;
    
    void getArchisInfo(BasicIdentifier archisIdentifier, EasyMetadata easyMetadata) throws ServiceException;
    
}
