package nl.knaw.dans.easy.security;

import java.util.Formatter;

import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;

public class HasPermissionRestrictedItemsCheck extends AbstractCheck {

    public String getProposition() {
        return "[Dataset has items that require permission]";
    }

    public boolean evaluate(ContextParameters ctxParameters) {
        Dataset dataset = ctxParameters.getDataset();
        if (dataset != null) {
            try {
                return datasetHasPermissionRestrictedItems(dataset);
            }
            catch (StoreAccessException e) {
                return false;
            }
        }
        return false;
    }

    private boolean datasetHasPermissionRestrictedItems(Dataset dataset) throws StoreAccessException {
        return Data.getFileStoreAccess().hasMember(dataset.getDmoStoreId(), FileItemVO.class, AccessibleTo.RESTRICTED_REQUEST);
    }

    @Override
    protected String explain(ContextParameters ctxParameters) {
        StringBuilder sb = super.startExplain(ctxParameters);

        Dataset dataset = ctxParameters.getDataset();

        if (dataset == null) {
            sb.append(", dataset = null");
        } else {
            try {
                sb.append(new Formatter().format(", dataset {} hasPermissionRestrictedItems = ", dataset.getStoreId(),
                        datasetHasPermissionRestrictedItems(dataset)));
            }
            catch (StoreAccessException e) {
                sb.append(new Formatter().format(", dataset {} might have PermissionRestrictedItems. Got: {} {}", dataset.getStoreId(), e.getClass()
                        .getCanonicalName(), e.getMessage()));
            }
        }

        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }
}
