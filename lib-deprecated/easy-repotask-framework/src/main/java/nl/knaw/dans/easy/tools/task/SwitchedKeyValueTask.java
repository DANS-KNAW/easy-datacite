package nl.knaw.dans.easy.tools.task;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.AbstractSidSetTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EmdContainer;
import nl.knaw.dans.pf.language.emd.EmdVisitor;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://drivenbydata.onjira.com/browse/EASY-98 second point : for a field with a drop down list keys and values are mixed up.
 */
public class SwitchedKeyValueTask extends AbstractSidSetTask {
    private static final Logger logger = LoggerFactory.getLogger(SwitchedKeyValueTask.class);
    private final String scheme;
    private final String schemeId;

    private final Map<String, String> choices;

    public SwitchedKeyValueTask(final String scheme, final String schemeId) throws FatalTaskException {
        super();
        this.scheme = scheme;
        this.schemeId = schemeId;
        choices = new HashMap<String, String>();
        try {
            for (final KeyValuePair kvp : Services.getDepositService().getChoices(schemeId, null).getChoices())
                choices.put(kvp.getKey(), kvp.getValue());
            logger.info("initialized " + scheme + " " + schemeId);
        }
        catch (final ServiceException e) {
            throw new FatalTaskException(e, this);
        }
    }

    @Override
    public void run(final JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        joint.getDataset().getEasyMetadata().visitChildren(false, new EmdVisitor() {
            @Override
            public Object container(final EmdContainer container) {
                for (final Term term : container.getTerms()) {
                    for (MetadataItem item : container.get(term.getName()))
                        if (wantToValidate(item) && switchedKeyValue((BasicString) item))
                            joint.setCycleSubjectDirty(true);
                }
                return null;
            }
        });
    }

    private boolean wantToValidate(final MetadataItem item) {
        if (schemeId.equals(item.getSchemeId()) && item instanceof BasicString) {
            final BasicString bs = (BasicString) item;
            if (scheme.equals(bs.getScheme()))
                return (true);
        }
        return false;
    }

    private boolean switchedKeyValue(final BasicString bs) {
        final String value = bs.getValue();
        if (!choices.keySet().contains(value)) {
            final String key = findKey(value);
            if (key != null) {
                bs.setValue(key);
                return true;
            }
        }
        return false;
    }

    private String findKey(final String value) {
        for (final String key : choices.keySet()) {
            if (choices.get(key).equals(value)) {
                warn(scheme + " " + schemeId, "change [" + value + "] into [" + key + "]");
                return key;
            }
        }
        warn(scheme + " " + schemeId, "invalid value: " + value);
        return null;
    }

    private void warn(final String... messages) {
        Application.getReporter().warn(new Event(getTaskName(), messages));
    }
}
