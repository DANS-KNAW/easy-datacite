package nl.knaw.dans.easy.tools.collector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.collect.Collector;
import nl.knaw.dans.common.lang.collect.CollectorDecorator;
import nl.knaw.dans.common.lang.collect.CollectorException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.data.store.StoreException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CollectorDecorator that adorns a list of itemIds with corresponding datasetIds.
 */
public class DatasetIdCollectorDecorator implements CollectorDecorator<Map<String, Set<String>>> {

    private static final Logger logger = LoggerFactory.getLogger(DatasetIdCollectorDecorator.class);

    private final Collector<List<String>> collector;

    public DatasetIdCollectorDecorator(Collector<List<String>> collector) {
        this.collector = collector;
    }

    @Override
    public Map<String, Set<String>> collect() throws CollectorException {
        Map<String, Set<String>> idCollection = new HashMap<String, Set<String>>();
        List<String> entries = collector.collect();
        for (String entry : entries) {
            collect(idCollection, entry);
        }
        return idCollection;
    }

    private void collect(Map<String, Set<String>> idCollection, String entry) {
        if (entry.startsWith(Dataset.NAMESPACE.getValue())) {
            addAsDataset(idCollection, entry);
        } else if (entry.startsWith(FolderItem.NAMESPACE.getValue()) || entry.startsWith(FileItem.NAMESPACE.getValue())) {
            addAsItem(idCollection, entry);
        } else {
            logger.warn("Entry with unknown namespace: '" + entry + "'");
        }
    }

    private void addAsItem(Map<String, Set<String>> idCollection, String itemId) {
        String datasetId = getDatasetId(itemId);
        if (datasetId == null) {
            logger.warn("No dataset found for item: '" + itemId + "'");
        } else {
            Set<String> children = idCollection.get(datasetId);
            if (children == null) {
                children = new HashSet<String>();
                idCollection.put(datasetId, children);
            }
            children.add(itemId);
        }
    }

    private void addAsDataset(Map<String, Set<String>> idCollection, String datasetId) {
        Set<String> children = idCollection.get(datasetId);
        if (children == null) {
            children = new HashSet<String>();
            idCollection.put(datasetId, children);
        }

        children.clear();
        children.add(datasetId);
    }

    private String getDatasetId(String itemId) {
        String datasetId;
        try {
            datasetId = Data.getFileStoreAccess().getDatasetId(new DmoStoreId(itemId));
        }
        catch (StoreException e) {
            logger.error("Unable to retrieve a datasetId", e);
            throw new ApplicationException(e);
        }
        catch (StoreAccessException e) {
            logger.error("Unable to retrieve a datasetId", e);
            throw new ApplicationException(e);
        }
        return datasetId;
    }

}
