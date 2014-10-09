package nl.knaw.dans.easy.security.authz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;

public abstract class AbstractItemContainerAuthzStrategy extends AbstractDatasetAutzStrategy {

    private static final long serialVersionUID = 1L;
    protected static Logger logger = LoggerFactory.getLogger(AbstractItemContainerAuthzStrategy.class);
    private int discoveryProfile = NOT_EVALUATED;
    private int readProfile = NOT_EVALUATED;

    public AbstractItemContainerAuthzStrategy(final User user, final Object... contextObjects) {
        super(user, contextObjects);
    }

    public AbstractItemContainerAuthzStrategy() {
        super();
    }

    abstract DmoStoreId getTargetDmoStoreId();

    @Override
    protected int getResourceDiscoveryProfile() {
        if (discoveryProfile == NOT_EVALUATED) {
            try {
                final List<AccessCategory> accessibilityCategories = new ArrayList<AccessCategory>();
                for (final AccessibleTo at : Data.getFileStoreAccess().getValuesFor(getTargetDmoStoreId(), AccessibleTo.class)) {
                    accessibilityCategories.add(AccessibleTo.translate(at));
                }
                discoveryProfile = AccessCategory.UTIL.getBitMask(accessibilityCategories);
            }
            catch (final StoreAccessException e) {
                logger.error(e.getMessage(), e);
                readProfile = AccessCategory.UTIL.getBitMask(AccessCategory.NO_ACCESS);
            }
        }
        return discoveryProfile;
    }

    @Override
    protected int getResourceReadProfile() {
        if (readProfile == NOT_EVALUATED) {
            try {
                final List<AccessCategory> accessibilityCategories = new ArrayList<AccessCategory>();
                for (final VisibleTo at : Data.getFileStoreAccess().getValuesFor(getTargetDmoStoreId(), VisibleTo.class)) {
                    accessibilityCategories.add(VisibleTo.translate(at));
                }
                readProfile = AccessCategory.UTIL.getBitMask(accessibilityCategories);
            }
            catch (final StoreAccessException e) {
                logger.error(e.getMessage(), e);
                readProfile = AccessCategory.UTIL.getBitMask(AccessCategory.NO_ACCESS);
            }
        }
        return readProfile;
    }
}
