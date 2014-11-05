package nl.knaw.dans.easy.security.authz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    abstract Set<AccessibleTo> getAccessibilities();

    abstract Set<VisibleTo> getVisibilities();

    @Override
    protected int getResourceDiscoveryProfile() {
        if (discoveryProfile == NOT_EVALUATED) {
            final List<AccessCategory> accessibilityCategories = new ArrayList<AccessCategory>();
            for (final VisibleTo at : getVisibilities()) {
                accessibilityCategories.add(VisibleTo.translate(at));
            }
            discoveryProfile = AccessCategory.UTIL.getBitMask(accessibilityCategories);
        }
        return discoveryProfile;
    }

    @Override
    protected int getResourceReadProfile() {
        if (readProfile == NOT_EVALUATED) {
            final List<AccessCategory> accessibilityCategories = new ArrayList<AccessCategory>();
            for (final AccessibleTo at : getAccessibilities()) {
                accessibilityCategories.add(AccessibleTo.translate(at));
            }
            readProfile = AccessCategory.UTIL.getBitMask(accessibilityCategories);
        }
        return readProfile;
    }
}
