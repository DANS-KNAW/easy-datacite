package nl.knaw.dans.common.lang.repo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBinaryUnit implements BinaryUnit {

    public static final UnitControlGroup DEFAULT_CONTROLGROUP = UnitControlGroup.ManagedContent;

    private static final long serialVersionUID = 5389675522891773571L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractBinaryUnit.class);

    private File file;
    private byte[] binaryContent;
    private boolean fileIsTempFile;
    private String label;
    private long size;
    private String mimeType;
    private boolean versionable;
    private UnitControlGroup unitControlGroup;
    private String location;

    private URL fileURL;

    public AbstractBinaryUnit() {
        this(DEFAULT_CONTROLGROUP);
    }

    public AbstractBinaryUnit(UnitControlGroup unitControlGroup) {
        this.unitControlGroup = unitControlGroup;
    }

    public String getUnitLabel() {
        return label;
    }

    public String getMimeType() {
        if (mimeType == null) {
            return MIMETYPE_UNDEFINED;
        } else {
            return mimeType;
        }
    }

    public boolean hasFile() {
        return file != null;
    }

    @Override
    public boolean hasBinaryContent() {
        return binaryContent != null;
    }

    /**
     * <b>WARNING:</b> binary content can only be part of a datastream when it is part of a new digital object. Fedora allows ingesting foxml with datastreams
     * with inline binary content. It stores the binary content as files. Updating inline binary content cannot be done using any method of the Fedora
     * DatastreamManager. Use {@link #setFileContent(byte[], String, String)} instead.
     */
    @Override
    public void setBinaryContent(byte[] bytes, String label, String mimeType) {
        this.label = label;
        this.mimeType = mimeType;
        this.binaryContent = bytes;
    }

    @Override
    public byte[] getBinaryContent() {
        if (binaryContent == null && fileURL != null) {
            try {
                binaryContent = readURL(fileURL);
            }
            catch (IOException e) {
                throw new ApplicationException("Could not close stream", e);
            }
        }
        return binaryContent;
    }

    private byte[] readURL(URL url) throws IOException {
        InputStream inStream = null;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            inStream = url.openStream();
            BufferedInputStream bis = new BufferedInputStream(inStream);

            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }
        }
        catch (IOException e) {
            throw new ApplicationException("Could not get file content", e);
        }
        finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        return buf.toByteArray();
    }

    public File getFile() {
        return file;
    }

    public long getFileSize() {
        return size;
    }

    public void setFile(File file) throws IOException {
        this.file = file;
        if (file == null) {
            mimeType = null;
            size = 0;
            label = null;
        } else {
            mimeType = FileUtil.getMimeType(file);
            size = file.length();
            label = file.getName();
        }
    }

    @Override
    public void setFileContent(byte[] bytes, String label, String mimeType) throws IOException {
        this.label = label;
        this.mimeType = mimeType;

        FileOutputStream fos = null;
        file = File.createTempFile("binary-", null);
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }
        this.size = file.length();
        fileIsTempFile = true;
    }

    @Override
    public void prepareForStorage() throws IOException {
        // override if necessary.

    }

    @Override
    public void close() {
        if (fileIsTempFile) {
            boolean deleted = file.delete();
            if (!deleted) {
                file.deleteOnExit();
                logger.warn("Could not delete temp file: " + file);
            }
        }
    }

    public boolean isVersionable() {
        return versionable;
    }

    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    public UnitControlGroup getUnitControlGroup() {
        return unitControlGroup;
    }

    public void setUnitControlGroup(UnitControlGroup unitControlGroup) {
        this.unitControlGroup = unitControlGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URL getFileURL() {
        return fileURL;
    }

    public void setFileURL(URL fileURL) {
        this.fileURL = fileURL;
    }

}
