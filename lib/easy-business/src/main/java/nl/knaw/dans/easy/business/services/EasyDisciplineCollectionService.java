package nl.knaw.dans.easy.business.services;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;

public class EasyDisciplineCollectionService implements DisciplineCollectionService {

    public DisciplineContainer getRootDiscipline() throws ServiceException {
        try {
            return DisciplineCollectionImpl.getInstance().getRootDiscipline();
        }
        catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public DisciplineContainer getDisciplineById(DmoStoreId disciplineId) throws ServiceException, ObjectNotFoundException {
        try {
            DisciplineContainer discipline = DisciplineCollectionImpl.getInstance().getDisciplineBySid(disciplineId);
            return discipline;
        }
        catch (DomainException e) {
            throw new ServiceException(e);
        }
    }

    public String getDisciplineName(DmoStoreId disciplineId) throws ServiceException, ObjectNotFoundException {
        try {
            DisciplineContainer discipline = DisciplineCollectionImpl.getInstance().getDisciplineBySid(disciplineId);
            return discipline.getName();
        }
        catch (DomainException e) {
            throw new ServiceException(e);
        }
    }

}
