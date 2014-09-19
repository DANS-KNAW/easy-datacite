package nl.knaw.dans.easy.business.item.zip;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipOutputStream;

import nl.knaw.dans.common.lang.collect.Collector;
import nl.knaw.dans.common.lang.collect.CollectorException;
import nl.knaw.dans.common.lang.file.ZipItem;
import nl.knaw.dans.common.lang.file.ZipUtil;
import nl.knaw.dans.common.lang.progress.ProgressSubject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;

public class ContentCollector extends ProgressSubject implements Collector<List<? extends ItemVO>> {

    private final Collector<List<? extends ItemVO>> collector;
    private final ZipOutputStream zipOut;

    public ContentCollector(Collector<List<? extends ItemVO>> collector, ZipOutputStream zipOut) {
        this.collector = collector;
        this.zipOut = zipOut;
    }

    @Override
    public List<? extends ItemVO> collect() throws CollectorException {
        List<? extends ItemVO> itemVOList = collector.collect();

        onStartProcess();
        int totalItems = itemVOList.size();
        int currentItem = 0;

        for (ItemVO itemVO : itemVOList) {
            ZipItem zipItem = new ZipItem(itemVO.getPath());
            if (itemVO instanceof FileItemVO) {
                URL url = Data.getEasyStore().getFileURL(new DmoStoreId(itemVO.getSid()));
                zipItem.setStreamUrl(url);
            }
            try {
                ZipUtil.addZipEntry(zipOut, zipItem);
                onProgress(totalItems, ++currentItem);
            }
            catch (IOException e) {
                throw new CollectorException("While adding zip content: ", e);
            }
        }

        onEndProcess();
        return itemVOList;
    }

}
