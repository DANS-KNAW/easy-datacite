package nl.knaw.dans.easy;

import java.io.File;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;

import org.apache.commons.io.FileUtils;

public class EmdBuilder {

    private static final String DEFAULT_EMD = "emd.xml";
    private String xml;

    public EmdBuilder() throws Exception {
        this(DEFAULT_EMD);
    }

    public EmdBuilder(String fileName) throws Exception {
        xml = FileUtils.readFileToString(new File(EmdBuilder.class.getResource("/" + fileName).toURI()), "UTF-8");
    }

    public EasyMetadata build() throws Exception {
        return new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
    }

    public EmdBuilder replaceAll(String search, String replace) throws Exception {
        xml = xml.replaceAll(search, replace);
        return this;
    }
}
