package nl.knaw.dans.easy.domain.dataset.item.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple container for all the item filters. This class is not made to be very dynamic, so be
 * careful when adding filters that you should also update some of the methods: like clear and
 * getFilters.
 */
public class ItemFilters
{
    private static final Logger logger = LoggerFactory.getLogger(ItemFilters.class);

    private static final String SEPARATOR = "[^a-zA-Z_]+";
    private static final String FILTER_SEPARATOR = "[^a-zA-Z_]+";

    private static final List<Role> POWER_USERS =
            Arrays.asList(new Role[] { Role.ARCHIVIST, Role.ADMIN });
    
    public static final ItemFilters FILTERS_FOR_ANONYMUS = new ItemFilters().add(VisibleTo.ANONYMOUS);
    
    public static final ItemFilters FILTERS_FOR_KNOWN =
            new ItemFilters().add(VisibleTo.ANONYMOUS, VisibleTo.KNOWN);
    
    public static final ItemFilters FILTERS_FOR_GRANTED_PERMISSION =
        new ItemFilters().add(VisibleTo.ANONYMOUS, VisibleTo.KNOWN, VisibleTo.RESTRICTED_REQUEST);
    
    public static final ItemFilters FILTERS_FOR_GROUP_MEMBERS =
        new ItemFilters().add(VisibleTo.ANONYMOUS, VisibleTo.KNOWN, VisibleTo.RESTRICTED_GROUP);
    
    public static final ItemFilters FILTERS_FOR_DEPOSITOR =
        null;

    private CreatorRoleFieldFilter creatorRoleFilter = null;
    private VisibleToFieldFilter visibleToFilter = null;
    private AccessibleToFieldFilter accessibleToFilter = null;

	public ItemFilters()
    {

    }

    /**
     * Create a filter from desired values.
     *
     * @param visibleTos comma separated values
     * @param creators comma separated values
     * @param accessibleTos comma separated values
     */
    public ItemFilters(String visibleTos, String creators, String accessibleTos)
    {
        // TODO DRY
        if (creators != null && creators.length() > 0)
            for (final String value : creators.toUpperCase().split(SEPARATOR))
                try
                {
                    add(CreatorRole.valueOf(value));
                }
                catch (IllegalArgumentException exception)
                {
                    logger.error(value + " skipping invalid creator filter value");
                }
        if (visibleTos != null && visibleTos.length() > 0)
            for (final String value : visibleTos.toUpperCase().split(FILTER_SEPARATOR))
                try
                {
                    add(VisibleTo.valueOf(value));
                }
                catch (IllegalArgumentException exception)
                {
                    logger.error(value + " skipping invalid visible to filter value");
                }
        if (accessibleTos != null && accessibleTos.length() > 0)
            for (final String value : accessibleTos.toUpperCase().split(FILTER_SEPARATOR))
                try
                {
                    add(AccessibleTo.valueOf(value));
                }
                catch (IllegalArgumentException exception)
                {
                    logger.error(value + " skipping invalid accessible to filter value");
                }
    }

    public void setVisibleToFilter(VisibleToFieldFilter visibleToFilter)
    {
        this.visibleToFilter = visibleToFilter;
    }

    /**
     * @return the visible to field filter or null if the filter was not set
     */
    public VisibleToFieldFilter getVisibleToFilter()
    {
        return visibleToFilter;
    }

    public AccessibleToFieldFilter getAccessibleToFilter()
	{
		return accessibleToFilter;
	}

	public void setAccessibleToFilter(AccessibleToFieldFilter accessibleToFilter)
	{
		this.accessibleToFilter = accessibleToFilter;
	}
    
    public void setCreatorRoleFilter(CreatorRoleFieldFilter creatorRoleFilter)
    {
        this.creatorRoleFilter = creatorRoleFilter;
    }

    /**
     * @return the creator role field filter or null if the filter was not set
     */
    public CreatorRoleFieldFilter getCreatorRoleFilter()
    {
        return creatorRoleFilter;
    }

    /**
     * @return a list (never null) of available filters
     */
    public List<ItemFilter> getFilters()
    {
        return Arrays.asList(new ItemFilter[] { creatorRoleFilter, visibleToFilter, accessibleToFilter });
    }

    public List<? extends ItemVO> apply(final List<? extends ItemVO> itemList) throws DomainException
    {
        List<ItemFilter> filters = getFilters();
        List<? extends ItemVO> result = new ArrayList<ItemVO>(itemList);
        for (final ItemFilter filter : filters)
        {
            if (filter != null)
                result = filter.apply(result);
        }
        return result;
    }

    private ItemFilters add(VisibleTo... visibleTos)
    {
        if (visibleToFilter == null)
            visibleToFilter = new VisibleToFieldFilter();
        visibleToFilter.addDesiredValues(visibleTos);
        return this;
    }

    private ItemFilters add(AccessibleTo... values)
    {
        if (accessibleToFilter == null)
            accessibleToFilter = new AccessibleToFieldFilter();
        accessibleToFilter.addDesiredValues(values);
        return this;
    }

    private ItemFilters add(CreatorRole... creatorRoles)
    {
        if (creatorRoleFilter == null)
            creatorRoleFilter = new CreatorRoleFieldFilter();
        creatorRoleFilter.addDesiredValues(creatorRoles);
        return this;
    }

    /**
     * @param sessionUser the logged in user or null
     * @param dataset the examined data set or null
     * @return this filter enhanced with business rules
     */
    public static ItemFilters get(EasyUser sessionUser, Dataset dataset,ItemFilters filters)
    {
//        boolean isPublished = dataset != null && // TODO ???
//                //AdministrativeState.valueOf(dataset.getState()) == AdministrativeState.PUBLISHED;
//                AdministrativeState.PUBLISHED.equals(dataset.getAdministrativeState());
        if (sessionUser == null || sessionUser.isAnonymous())
            return FILTERS_FOR_ANONYMUS;
        if (isPowerUser(sessionUser.getRoles()))
            return filters;
        if (dataset != null && dataset.hasDepositor(sessionUser))
            return FILTERS_FOR_DEPOSITOR;
        if (dataset != null && dataset.isPermissionGrantedTo(sessionUser))
            return FILTERS_FOR_GRANTED_PERMISSION;
        if (dataset != null && dataset.isGroupAccessGrantedTo(sessionUser))
            return FILTERS_FOR_GROUP_MEMBERS;
        return FILTERS_FOR_KNOWN;
    }

    private static boolean isPowerUser(Set<Role> roles)
    {
        final Set<Role> intersection = new HashSet<Role>(POWER_USERS);
        intersection.retainAll(roles);
        return !intersection.isEmpty();
    }

	public void clear()
	{
		setCreatorRoleFilter(null);
		setAccessibleToFilter(null);
		setVisibleToFilter(null);	
	}
}
