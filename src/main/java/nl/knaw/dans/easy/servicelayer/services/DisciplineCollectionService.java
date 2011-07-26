package nl.knaw.dans.easy.servicelayer.services;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;


public interface DisciplineCollectionService
{
	DisciplineContainer getRootDiscipline() throws ServiceException, ObjectNotFoundException;
	
	DisciplineContainer getDisciplineById(String disciplineId) throws ServiceException, ObjectNotFoundException;
	
	String getDisciplineName(String disciplineId) throws ServiceException, ObjectNotFoundException;
}
