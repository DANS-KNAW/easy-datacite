package nl.knaw.dans.easy.domain.emd.validation.archaeology;

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
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EmdCoverage;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial;

public class EasSpatialValidator implements Validator
{
    
    public static final String LIST_ID = "archaeology.eas.spatial";
    
    public static final String X_PATH_STUB = "/emd:easymetadata/emd:coverage/eas:spatial/";
    
    private ChoiceList choiceList;
    
    private int pointCounter;
    private int boxCounter;

    @Override
    public synchronized void validate(EasyMetadata emd, ValidationReporter reporter)
    {
        pointCounter = 0;
        boxCounter = 0;
        EmdCoverage emdCoverage = emd.getEmdCoverage();
        List<Spatial> spatials = emdCoverage.getEasSpatial();
        for (Spatial spatial : spatials)
        {
            validate(spatial, reporter);
        }
    }

    private void validate(Spatial spatial, ValidationReporter reporter)
    {
        Spatial.Point point = spatial.getPoint();
        if (point != null)
        {
            String schemeId = point.getSchemeId();
            String scheme = point.getScheme();
            validate(reporter, schemeId, scheme, ++pointCounter, "eas:point");
        }
        
        Spatial.Box box = spatial.getBox();
        if (box != null)
        {
            String schemeId = box.getSchemeId();
            String scheme = box.getScheme();
            validate(reporter, schemeId, scheme, ++boxCounter, "eas:box");
        }
    }
    
    private void validate(ValidationReporter reporter, String schemeId, String scheme, int i, String name)
    {
        if (!LIST_ID.equals(schemeId))
        {
            reporter.setMetadataValid(false);
            String msg = "The value '" + schemeId + "' of the attribute 'schemeId' at " 
                + X_PATH_STUB + name + " is not valid. Expected is '" + LIST_ID + "'";
            
            String xpath = X_PATH_STUB + name + "[" + i + "]/" + "@eas:schemeId";
            // /emd:easymetadata/emd:coverage/eas:spatial/eas:point[1]/@eas:schemeId
            
            reporter.addError(new ValidationReport(msg, xpath, this));
            return;
        }
        
        if (!choiceListContains(scheme))
        {
            reporter.setMetadataValid(false);
            String msg = "The value '" + scheme + "' of the attribute 'scheme' at " 
                + X_PATH_STUB + name + " is not a valid key in the list '" + LIST_ID + "'";
            
            String xpath = X_PATH_STUB + name + "[" + i + "]/" + "@eas:scheme";
            // /emd:easymetadata/emd:coverage/eas:spatial/eas:point[1]/@eas:scheme
            
            reporter.addError(new ValidationReport(msg, xpath, this));
        }
        
    }
    
    private boolean choiceListContains(String key)
    {
        ChoiceList choiceList = getChoiceList();
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
            catch (ObjectNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (CacheException e)
            {
                throw new ApplicationException(e);
            }
            catch (ResourceNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (DomainException e)
            {
                throw new ApplicationException(e);
            }
        }
        return choiceList;
    }

}
