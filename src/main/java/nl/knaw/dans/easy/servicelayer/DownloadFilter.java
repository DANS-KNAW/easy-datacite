package nl.knaw.dans.easy.servicelayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilter;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.AbstractCheck;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.IsDepositorOfDatasetCheck;

public class DownloadFilter implements ItemFilter
{

    private static final AbstractCheck isDepositorCheck = new IsDepositorOfDatasetCheck();
    private static final AbstractCheck isArchivistCheck = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);

    private final Dataset dataset;
    private final EasyUser sessionUser;

    public DownloadFilter(EasyUser sessionUser, Dataset dataset)
    {
        this.dataset = dataset;
        this.sessionUser = sessionUser;
    }

    public List<? extends ItemVO> apply(List<? extends ItemVO> itemList) throws DomainException
    {
        int userProfile = getUserProfile();
        List<ItemVO> filteredItems = new ArrayList<ItemVO>();
        for (ItemVO item : itemList)
        {
            if (item.isAccessibleFor(userProfile) && item.belongsTo(dataset))
            {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public int getUserProfile()
    {
        int userProfile = 0;
        ContextParameters ctxParameters = new ContextParameters(sessionUser, dataset);
        if (isDepositorCheck.evaluate(ctxParameters) || isArchivistCheck.evaluate(ctxParameters))
        {
            userProfile = AccessCategory.MASK_ALL;
        }
        else
        {
            userProfile = dataset.getAccessProfileFor(sessionUser);
        }
        return userProfile;
    }

    public String explain()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("User profile = ").append(Arrays.deepToString(AccessCategory.UTIL.getStates(getUserProfile()).toArray()));
        return sb.toString();
    }

}
