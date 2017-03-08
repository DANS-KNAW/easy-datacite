package nl.knaw.dans.easy.sword.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class InMemoryZip {
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream);

    public InMemoryZip add(String entryName, byte[] content) throws IOException {
        zip.putNextEntry(new ZipEntry(entryName));
        zip.write(content);
        zip.closeEntry();
        return this;
    }

    public InputStream toInputStream() throws IOException {
        zip.close();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
