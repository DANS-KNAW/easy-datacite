package nl.knaw.dans.platform.language.pakbon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLDeserializationException;
import nl.knaw.dans.pf.language.xml.transform.XMLTransformer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class Pakbon2EmdTransformer {

    private static final Logger logger = LoggerFactory.getLogger(Pakbon2EmdTransformer.class);

    private static final String XSL_PAKBON2EMD_PATH_DIR = "/res/xslt/pakbon";
    private static final String XSL_PAKBON2EMD_PATH_FILE_PREFIX = "pakbon2emd";
    private static final String XSL_PAKBON2EMD_PATH_FILE_EXT = "xslt";

    public byte[] transform(File pakbonFile) throws TransformerException {
        String version = "";
        PakbonVersionExtractor extractor = new PakbonVersionExtractor();
        FileInputStream fis = null;
        try {
            fis = FileUtils.openInputStream(pakbonFile);
            version = extractor.extract(fis);
        }
        catch (SAXException e) {
            throw new TransformerException(e);
        }
        catch (IOException e) {
            throw new TransformerException(e);
        }
        catch (ParserConfigurationException e) {
            throw new TransformerException(e);
        }
        finally {
            IOUtils.closeQuietly(fis);
        }
        // unfortunately we cannot rewind/reset the stream and reuse it

        return transform(pakbonFile, version);
    }

    public byte[] transform(File pakbonFile, String version) throws TransformerException {
        FileInputStream fis = null;
        XMLTransformer transformer = getTransformer(version);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            fis = FileUtils.openInputStream(pakbonFile);
            transformer.transform(fis, out);
        }
        catch (IOException e) {
            throw new TransformerException(e);
        }
        finally {
            IOUtils.closeQuietly(fis);
        }

        return out.toByteArray();
    }

    public EasyMetadata transformToEmd(File pakbonFile) throws XMLDeserializationException, TransformerException {
        EmdUnmarshaller<EasyMetadata> um = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class);
        EasyMetadata emd = um.unmarshal(transform(pakbonFile));
        return emd;
    }

    private XMLTransformer getTransformer(String version) throws TransformerException {
        XMLTransformer transformer = new XMLTransformer(getStylesheet(version), XMLTransformer.TF_SAXON);
        transformer.getTransformerFactory().setURIResolver(new XsltUriResolver());
        return transformer;
    }

    private URL getStylesheet(String version) throws TransformerException {
        String stylesheetPathName = XSL_PAKBON2EMD_PATH_DIR + "/" + XSL_PAKBON2EMD_PATH_FILE_PREFIX + "_" + version.trim() + "." + XSL_PAKBON2EMD_PATH_FILE_EXT;
        URL url = ResourceLocator.getURL(stylesheetPathName);
        if (url == null) {
            logger.debug("Missing stylesheet on path: '" + stylesheetPathName + "'");
            String msg = "Unsupported version: " + version + " ; Supported versions: " + StringUtils.join(getSupportedVersions().toArray(), ", ");
            logger.info(msg);
            throw new TransformerException(msg);// IllegalStateException(msg);
        }
        return url;
    }

    public List<String> getSupportedVersions() {
        List<String> versions = new ArrayList<String>();
        try {
            File dirFile = ResourceLocator.getFile(XSL_PAKBON2EMD_PATH_DIR);
            List<String> fileNames = getXsltFileNames(dirFile);
            for (String name : fileNames) {
                int beginIndex = name.indexOf('_');
                int endIndex = name.lastIndexOf('.');
                if (beginIndex > 0 && endIndex > 0) {
                    String version = name.substring(beginIndex + 1, endIndex);
                    versions.add(version);
                }
            }
        }
        catch (ResourceNotFoundException e) {
            logger.error("Unable to get filenames for dir: " + XSL_PAKBON2EMD_PATH_DIR);
        }

        return versions;
    }

    private List<String> getXsltFileNames(File dirFile) {
        List<String> fileNames = new ArrayList<String>();

        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(XSL_PAKBON2EMD_PATH_FILE_PREFIX + "_") && name.endsWith("." + XSL_PAKBON2EMD_PATH_FILE_EXT)) {
                    return true;
                }
                return false;
            }
        };

        List<File> files = Arrays.asList(dirFile.listFiles(fileNameFilter));

        for (File file : files)
            fileNames.add(file.getName());

        return fileNames;
    }

}
