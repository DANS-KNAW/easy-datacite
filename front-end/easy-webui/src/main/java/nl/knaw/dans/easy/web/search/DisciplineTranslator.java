package nl.knaw.dans.easy.web.search;

import java.util.Locale;

import nl.knaw.dans.common.wicket.components.search.Translator;
import nl.knaw.dans.easy.web.wicket.DisciplineModel;

import org.apache.wicket.model.IModel;

public class DisciplineTranslator implements Translator<String> {
    private static final long serialVersionUID = 2066943547139450059L;

    private static DisciplineTranslator INSTANCE = new DisciplineTranslator();

    public static DisciplineTranslator getInstance() {
        return INSTANCE;
    }

    public IModel<String> getTranslation(String audienceId, Locale locale, boolean fullName) {
        return new DisciplineModel(audienceId);
    }

}
