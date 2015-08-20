package nl.knaw.dans.easy.tools.task.dump;

import java.io.IOException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.LicenseUnit;

public class LicenseReader {
    private static String HASH_ALGORITHM = "SHA-1";

    private final String sid;

    LicenseReader(String sid) {
        this.sid = sid;
    }

    List<LicenseData> read() {
        List<UnitMetadata> meta;
        try {
            meta = Data.getEasyStore().getUnitMetadata(new DmoStoreId(sid), new DsUnitId(LicenseUnit.UNIT_ID));
        }
        catch (RepositoryException e1) {
            throw new RuntimeException(e1);
        }

        List<LicenseData> licenses = new LinkedList<LicenseData>();

        if (meta == null || meta.size() == 0) {
            return licenses;
        }

        try {
            Iterator<UnitMetadata> iterator = meta.iterator();

            while (iterator.hasNext()) {
                UnitMetadata umd = iterator.next();
                LicenseData data = new LicenseData();
                data.setLabel(umd.getLabel());
                data.setSha1Hash(calculateSha1Hash(Data.getEasyStore()
                        .getFileURL(new DmoStoreId(sid), new DsUnitId(LicenseUnit.UNIT_ID), umd.getCreationDate())));
                licenses.add(data);
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return licenses;
    }

    private String calculateSha1Hash(URL data) throws NoSuchAlgorithmException, IOException {
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
        catch (IOException e) {

        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }

        return null;
    }
}
