package nl.knaw.dans.easy.web.wicket;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineModel extends Model<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisciplineModel.class);
    private static final long serialVersionUID = -1427246145762034962L;

    @SpringBean(name = "disciplineService")
    private DisciplineCollectionService disciplineService;

    public DisciplineModel(String audienceId) {
        InjectorHolder.getInjector().inject(this);
        setObject(audienceId);
    }

    @Override
    public String getObject() {
        String audienceId = (String) super.getObject();
        try {
            return disciplineService.getDisciplineName(new DmoStoreId(audienceId));
        }
        catch (Exception e) {
            LOGGER.error("Unable to convert audienceId " + audienceId + " to discipline name", e);
            throw new InternalWebError();
        }
    }
}
