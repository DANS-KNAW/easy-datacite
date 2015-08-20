package nl.knaw.dans.easy.tools.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

public class TestServicesTask extends AbstractTask {

    private static final String DATASET_ID = "easy-dataset:1"; // Staat van het land : GPD enquête 1998
    private static final Logger logger = LoggerFactory.getLogger(TestServicesTask.class);

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        try {
            List<ItemVO> items = Services.getItemService().getFilesAndFolders(null, null, new DmoStoreId(DATASET_ID));
            for (ItemVO itemVO : items) {
                logger.debug(itemVO.getSid());
            }

        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }

    }

}
