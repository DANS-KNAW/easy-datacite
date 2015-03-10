package nl.knaw.dans.easy;

import java.util.List;

import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;

public class DatasetProxy extends DatasetImpl {
    private static final long serialVersionUID = 1L;
    private List<DisciplineContainer> parentDisciplines;

    public DatasetProxy(String storeId, final EasyUserImpl depositor, final State state, final List<DisciplineContainer> parentDisciplines) {
        super(storeId);
        this.parentDisciplines = parentDisciplines;
        setState(state.toString());
        setAuthzStrategy(new EasyItemContainerAuthzStrategy() {
            // need a subclass because the constructors are protected
            private static final long serialVersionUID = 1L;
        });

        // needed twice because considered dirty
        getAdministrativeMetadata().setDepositor(depositor);
        getAdministrativeMetadata().setDepositor(depositor);
    }

    @Override
    public List<DisciplineContainer> getParentDisciplines() {
        return parentDisciplines;
    }
}
