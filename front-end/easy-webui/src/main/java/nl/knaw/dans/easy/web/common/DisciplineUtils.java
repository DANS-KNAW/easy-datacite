package nl.knaw.dans.easy.web.common;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.DepositService;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class DisciplineUtils {

    @SpringBean(name = "depositService")
    private static DepositService depositService;

    public static ChoiceList getDisciplinesChoiceList() {
        if (depositService == null)
            InjectorHolder.getInjector().inject(new DisciplineUtils());
        try {
            return depositService.getChoices("custom.disciplines", null);
        }
        catch (ServiceException e) {
            throw new InternalWebError();
        }
    }

    public static KeyValuePair getDisciplineItemById(String disciplineId) {
        final ChoiceList choices = getDisciplinesChoiceList();

        for (KeyValuePair kvp : choices.getChoices()) {
            if (kvp.getKey().equals(disciplineId)) {
                return kvp;
            }
        }

        return null;
    }

    public void setDepositService(DepositService depositService) {
        DisciplineUtils.depositService = depositService;
    }
}
