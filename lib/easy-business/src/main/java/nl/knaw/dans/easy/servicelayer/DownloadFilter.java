package nl.knaw.dans.easy.servicelayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.AbstractCheck;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.IsDepositorOfDatasetCheck;

public class DownloadFilter {

    private static final AbstractCheck isDepositorCheck = new IsDepositorOfDatasetCheck();
    private static final AbstractCheck isArchivistCheck = new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN);

    private final Dataset dataset;
    private final boolean isPowerUser;
    private final Set<AccessibleTo> accessibleToSet;

    public DownloadFilter(EasyUser sessionUser, Dataset dataset) {
        final ContextParameters ctxParameters = new ContextParameters(sessionUser, dataset);
        this.dataset = dataset;
        accessibleToSet = dataset.getAccessibleToSetFor(sessionUser);
        isPowerUser = isDepositorCheck.evaluate(ctxParameters) || isArchivistCheck.evaluate(ctxParameters);
    }

    public List<? extends ItemVO> apply(final List<? extends ItemVO> itemList) throws DomainException {
        final List<ItemVO> filteredItems = new ArrayList<ItemVO>();
        for (final ItemVO item : itemList) {
            if (item.belongsTo(dataset)) {
                if (isPowerUser)
                    filteredItems.add(item);
                else if (item instanceof FileItemVO) {
                    if (isAccessible((FileItemVO) item))
                        filteredItems.add(item);
                } else if (isAccessible((FolderItemVO) item))
                    filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    private boolean isAccessible(FileItemVO item) {
        return accessibleToSet.contains(item.getAccessibleTo());
    }

    private boolean isAccessible(final FolderItemVO item) throws DomainException {
        final DmoStoreId dmoStoreId = new DmoStoreId(item.getSid());
        try {
            final Set<AccessibleTo> values = Data.getFileStoreAccess().getValuesFor(dmoStoreId, AccessibleTo.class);
            values.retainAll(accessibleToSet);
            return values.size() > 0;
        }
        catch (final StoreAccessException e) {
            throw new DomainException(e.getMessage(), e);
        }
    }
}
