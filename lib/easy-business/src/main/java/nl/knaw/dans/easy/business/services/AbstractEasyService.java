package nl.knaw.dans.easy.business.services;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.EasyService;

/**
 * Class implementing general methods of Easy services.
 * 
 * @author ecco
 */
public abstract class AbstractEasyService implements EasyService {
    /**
     * {@inheritDoc}
     */
    public String getServiceTypeName() {
        return this.getClass().getName();
    }

    /**
     * Get a short description of this service. Implementers of this abstract class should override the method 'getServiceDescription:String'.
     * 
     * @return a dummy description
     */
    public String getServiceDescription() {
        return "This is an abstract service. Implementers of this abstract class should override the method 'getServiceDescription:String'.";
    }

    protected static String getUserId(EasyUser user) {
        return user.isAnonymous() ? "[unknown user]" : user.getId();
    }

    /**
     * Convenience method
     */
    public static String getStoreId(DataModelObject dmo) {
        return dmo == null ? "[null]" : dmo.getStoreId();
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException {}
}
