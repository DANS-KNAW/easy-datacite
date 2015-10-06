package nl.knaw.dans.easy.tools.task.dump;

import java.io.IOException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.knaw.dans.easy.domain.model.FileItemMetadata;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileItemMetadataPrinter {
    private static final Logger log = LoggerFactory.getLogger(FileItemMetadataPrinter.class);
    private static String HASH_ALGORITHM = "SHA-1";
    private final FileItemMetadata metadata;
    private String hash;
    private final boolean includePid;
    private final boolean includeMimeType;
    private final String filePid;

    public FileItemMetadataPrinter(String filePid, FileItemMetadata metadata, URL data, boolean includePid, boolean includeSha1Hash, boolean includeMimeType)
            throws NoSuchAlgorithmException, IOException
    {
        this.filePid = filePid;
        this.metadata = metadata;
        if (includeSha1Hash) {
            this.hash = calculateSha1Hash(data);
        }
        this.includePid = includePid;
        this.includeMimeType = includeMimeType;
    }

    private String calculateSha1Hash(URL data) throws IOException, NoSuchAlgorithmException {
        DigestInputStream stream = null;

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            stream = new DigestInputStream(data.openStream(), digest);
            byte[] dummy = new byte[1024 * 1024];

            while (stream.read(dummy, 0, dummy.length) != -1) {
                // Do nothing, just to update the digest.
            }

            return new String(Hex.encodeHex(digest.digest()));
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private String property(String key, String value) {
        return String.format("FILE[%s]:%s=%s\n", metadata.getPath(), key, new CrlfEscapedString(value));
    }

    public String toString() {
        try {
            // @formatter:off
        return  (includePid ? property("PID", filePid) : "")
                + (includeMimeType ? property("mimeType", metadata.getMimeType()) : "")
                + property("size", Long.toString(metadata.getSize())) 
                + property("name", metadata.getName()) 
                + property("path", metadata.getPath())
                + property("creatorRole", metadata.getCreatorRole().toString()) 
                + property("visibleTo", metadata.getVisibleTo().toString())
                + property("accessibleTo", metadata.getAccessibleTo().toString()) 
                + (hash == null ? "" : property(HASH_ALGORITHM, hash));
        // @formatter:on
        }
        catch (Exception e) {
            log.error("Exception", e);
        }
        return null;
    }

}
