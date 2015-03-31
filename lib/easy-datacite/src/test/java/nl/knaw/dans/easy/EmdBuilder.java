package nl.knaw.dans.easy;

import java.io.File;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;

import org.apache.commons.io.FileUtils;

public class EmdBuilder {

    private static final String DEFAULT_EMD = "emd.xml";
    private static final File FOLDER = new File("src/test/resources");
    private String xml;

    public EmdBuilder() throws Exception {
        xml = FileUtils.readFileToString(new File(FOLDER, DEFAULT_EMD), "UTF-8");
    }

    public EmdBuilder(String fileName) throws Exception {
        xml = FileUtils.readFileToString(new File(FOLDER, fileName), "UTF-8");
    }

    public EasyMetadata build() throws Exception {
        return new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
    }

    public EmdBuilder replaceAll(String search, String replace) throws Exception {
        xml = xml.replaceAll(search, replace);
        return this;
    }
}
