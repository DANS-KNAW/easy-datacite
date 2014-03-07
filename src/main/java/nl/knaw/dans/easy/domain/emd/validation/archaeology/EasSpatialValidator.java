package nl.knaw.dans.easy.domain.emd.validation.archaeology;

import static nl.knaw.dans.easy.domain.emd.validation.base.EmdXPath.SPATIAL_COVERAGE;

import java.util.List;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReport;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.emd.validation.base.Validator;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdCoverage;
import nl.knaw.dans.pf.language.emd.types.EmdScheme;
import nl.knaw.dans.pf.language.emd.types.Spatial;

public class EasSpatialValidator implements Validator
{

    public static final String LIST_ID = EmdScheme.ARCHAEOLOGY_EAS_SPATIAL.getId();

    private ChoiceList choiceList;

    private int pointCounter;
    private int boxCounter;

    @Override
    public synchronized void validate(final EasyMetadata emd, final ValidationReporter reporter)
    {
        pointCounter = 0;
        boxCounter = 0;
        final EmdCoverage emdCoverage = emd.getEmdCoverage();
        final List<Spatial> spatials = emdCoverage.getEasSpatial();
        for (final Spatial spatial : spatials)
        {
            validate(spatial, reporter);
        }
    }

    private void validate(final Spatial spatial, final ValidationReporter reporter)
    {
        final Spatial.Point point = spatial.getPoint();
        if (point != null)
        {
            final String schemeId = point.getSchemeId();
            final String scheme = point.getScheme();
            validate(reporter, schemeId, scheme, ++pointCounter, "eas:point");
        }

        final Spatial.Box box = spatial.getBox();
        if (box != null)
        {
            final String schemeId = box.getSchemeId();
            final String scheme = box.getScheme();
            validate(reporter, schemeId, scheme, ++boxCounter, "eas:box");
        }
    }

    private void validate(final ValidationReporter reporter, final String schemeId, final String scheme, final int i, final String name)
    {
        final String xPathStub = SPATIAL_COVERAGE.getXPath() + name;
        if (!LIST_ID.equals(schemeId))
        {
            reporter.setMetadataValid(false);
            final String msg = "The value '" + schemeId + "' of the attribute 'schemeId' at " + xPathStub + " is not valid. Expected is '" + LIST_ID + "'";

            final String xpath = xPathStub + "[" + i + "]/" + "@eas:schemeId";
            // /emd:easymetadata/emd:coverage/eas:spatial/eas:point[1]/@eas:schemeId

            reporter.addError(new ValidationReport(msg, xpath, this));
            return;
        }

        if (!choiceListContains(scheme))
        {
            reporter.setMetadataValid(false);
            final String msg = "The value '" + scheme + "' of the attribute 'scheme' at " + xPathStub + " is not a valid key in the list '" + LIST_ID + "'";

            final String xpath = xPathStub + "[" + i + "]/" + "@eas:scheme";
            // /emd:easymetadata/emd:coverage/eas:spatial/eas:point[1]/@eas:scheme

            reporter.addError(new ValidationReport(msg, xpath, this));
        }

    }

    private boolean choiceListContains(final String key)
    {
        final ChoiceList choiceList = getChoiceList();
        return choiceList.getChoices().contains(new KeyValuePair(key, null));
    }

    private ChoiceList getChoiceList()
    {
        if (choiceList == null)
        {
            try
            {
                choiceList = ChoiceListGetter.getInstance().getChoiceList(LIST_ID, null);
            }
            catch (final ObjectNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (final CacheException e)
            {
                throw new ApplicationException(e);
            }
            catch (final ResourceNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (final DomainException e)
            {
                throw new ApplicationException(e);
            }
        }
        return choiceList;
    }

}
