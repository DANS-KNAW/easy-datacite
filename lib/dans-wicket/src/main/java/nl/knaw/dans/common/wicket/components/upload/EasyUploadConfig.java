package nl.knaw.dans.common.wicket.components.upload;

import java.io.Serializable;

/**
 * For configuring the Easy upload component.
 * 
 * @author lobo
 */
public class EasyUploadConfig implements Serializable {
    private static final long serialVersionUID = -8131205692377372623L;

    private String basePath;

    private boolean autoRemoveMessages = false;

    private boolean autoRemoveFiles = false;

    public EasyUploadConfig() {
        this.basePath = System.getProperty("java.io.tmpdir");
    }

    public EasyUploadConfig(String basePath) {
        this.basePath = basePath;
    }

    /**
     * If set to true the 'uploaded file' messages under the upload component disappear automatically when a file has been uploaded.
     */
    public void setAutoRemoveMessages(boolean autoRemoveMessages) {
        this.autoRemoveMessages = autoRemoveMessages;
    }

    /**
     * If set to true the 'uploaded file' messages under the upload component disappear automatically when a file has been uploaded. Defaults to false.
     */
    public boolean autoRemoveMessages() {
        return autoRemoveMessages;
    }

    /**
     * Sets the path of where uploaded files are uploaded to on the local machine.
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * The path of where uploaded files are uploaded to on the local machine. Defaults to the system temp dir.
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * If set to true the files that were uploaded are immediately removed after they have been uploaded and an onReceivedFiles event has been fired. This is
     * useful for users of the EasyUpload component that immediately process the files and after don't need the files anymore on disk.
     */
    public void setAutoRemoveFiles(boolean autoRemoveFiles) {
        this.autoRemoveFiles = autoRemoveFiles;
    }

    /**
     * If set to true the files that were uploaded are immediately removed after they have been uploaded and an onReceivedFiles event has been fired. This is
     * useful for users of the EasyUpload component that immediately process the files and after don't need the files anymore on disk. Defaults to false.
     */
    public boolean autoRemoveFiles() {
        return autoRemoveFiles;
    }

}
