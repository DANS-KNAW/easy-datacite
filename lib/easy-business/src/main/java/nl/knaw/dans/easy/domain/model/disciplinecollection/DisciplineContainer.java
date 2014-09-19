package nl.knaw.dans.easy.domain.model.disciplinecollection;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.collections.DmoRecursiveItem;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

public interface DisciplineContainer extends DmoRecursiveItem {
    String CONTENT_MODEL = "easy-model:edm1-discipline";

    DmoNamespace NAMESPACE = new DmoNamespace("easy-discipline");

    String getName();

    void setName(String name);

    List<DisciplineContainer> getSubDisciplines() throws DomainException;

    DisciplineMetadata getDisciplineMetadata();

    void setDisciplineMetadata(DisciplineMetadata dmd);
}
