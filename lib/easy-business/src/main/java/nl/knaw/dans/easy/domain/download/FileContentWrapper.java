package nl.knaw.dans.easy.domain.download;

import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;

public class FileContentWrapper implements Serializable, DownloadInfo {

    private static final long serialVersionUID = 677620380637265652L;

    private final String datasetId;

    private final String fileItemId;

    private FileItemVO fileItemVO;

    private String fileName;

    private URL url;

    public FileContentWrapper(String fileItemId, String datasetId) {
        this.fileItemId = fileItemId;
        this.datasetId = datasetId;
    }

    public String getFileItemId() {
        return fileItemId;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public void setFileItemVO(FileItemVO fileItemVO) {
        this.fileItemVO = fileItemVO;
        this.fileName = fileItemVO.getName();
    }

    public FileItemVO getFileItemVO() {
        return fileItemVO;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName()).append(" fileItemId=").append(fileItemId);
        if (url == null) {
            sb.append(" url=").append("null");
        } else {
            sb.append(" url=").append(url.toString());
        }
        return sb.toString();
    }

    @Override
    public List<String> getFileNames() {
        return Arrays.asList(fileItemVO.getPath());
    }

}
