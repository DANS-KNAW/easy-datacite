package nl.knaw.dans.common.lang.repo;

import java.io.File;
import java.io.IOException;

public interface BinaryUnit extends Unit {

    public enum UnitControlGroup {
        ExternallyReferencedContent, ManagedContent, RedirectedContent, InlineXML
    }

    // Fedora cannot handle mimetype "undefined"
    // String MIMETYPE_UNDEFINED = "undefined";
    String MIMETYPE_UNDEFINED = "application/octet-stream";

    UnitControlGroup getUnitControlGroup();

    void setUnitControlGroup(UnitControlGroup unitControlGroup);

    boolean hasFile();

    boolean hasBinaryContent();

    File getFile();

    void setFile(File file) throws IOException;

    void setFileContent(byte[] bytes, String label, String mimeType) throws IOException;

    String getFileSha1Checksum();

    /**
     * Set the given byte array as inline content. Careful! Only works on ingest! Not on update.
     * 
     * @param bytes
     *        the byte array to be stored as managed content
     * @param label
     *        the label for the unit
     * @param mimeType
     *        the mime type of the content
     */
    void setBinaryContent(byte[] bytes, String label, String mimeType);

    /**
     * The binary content as set by {@link #setBinaryContent(byte[], String, String)}. Always returns <code>null</code> after an object is retrieved from store.
     * 
     * @return The binary content as set by setBinaryContent, <code>null</code> after retrieval of an object
     */
    byte[] getBinaryContent();

    /**
     * Prepare for storage, like saving any content of this BinaryUnit to file.
     * 
     * @see #setFileContent(byte[], String, String)
     * @throws IOException
     *         if preparing could not be done
     */
    void prepareForStorage() throws IOException;

    /**
     * Releases any system resources associated with this Unit.
     */
    void close();

    String getMimeType();

    long getFileSize();

    String getLocation();

    void setLocation(String location);

}
