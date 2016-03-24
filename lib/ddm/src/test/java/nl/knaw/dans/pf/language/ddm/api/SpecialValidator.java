package nl.knaw.dans.pf.language.ddm.api;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.pf.language.xml.validation.AbstractValidator2;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * DDM validation normally uses the online schemas at "http://easy.dans.knaw.nl/schemas/" as specified in the namespace locations of the xml files. To test
 * against not yet published schemas we need local copies placed in the target folder by maven. To test against versions not even in the maven repository, first
 * build easy-schema then use a system property to build (or test) ddm or easy-app: mvn -DSNAPSHOT_SCHEMA=true clean install
 */
public class SpecialValidator extends AbstractValidator2 {
    private static final Logger logger = LoggerFactory.getLogger(SpecialValidator.class);

    public final static String LOCAL_SCHEMA_DIR = "target/easy-schema/";

    // Note that the online url's can be found in nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace
    public final static Map<String, File> RECENT_SCHEMAS = getRecentXsds();

    public SpecialValidator() {
        // when offline we get errors on a line in ddm.xsd even when we add local copies of 3rd party XSDs:
        // could not validate against XSD: org.xml.sax.SAXParseException; lineNumber: 97; columnNumber: 90; src-resolve: Cannot resolve the name
        // 'dcterms:created' to a(n) 'element declaration' component.
        super(toLocalUrlOfLastVersion("ddm.xsd"), toLocalUrlOfLastVersion("dcx.xsd"), toLocalUrlOfLastVersion("dc.xsd"),
                toLocalUrlOfLastVersion("dcx-gml.xsd"), toLocalUrlOfLastVersion("dcx-dai.xsd"), toLocalUrlOfLastVersion("narcis-type.xsd"),
                toLocalUrlOfLastVersion("abr-type.xsd"), toLocalUrlOfLastVersion("identifier-type.xsd"));
    }

    private static String toLocalUrlOfLastVersion(String key) {
        File easyFile = RECENT_SCHEMAS.get(key);
        if (easyFile != null)
            return "file://" + easyFile.getAbsolutePath();
        else
            return key;

    }

    private static Map<String, File> getRecentXsds() {
        Map<String, File> fileMap = new HashMap<String, File>();
        addFiles(fileMap, new File(LOCAL_SCHEMA_DIR));
        return fileMap;
    }

    private static void addFiles(Map<String, File> fileMap, File dir) {
        for (String fileName : dir.list()) {
            File file = new File(dir.getPath() + "/" + fileName);
            if (file.isDirectory())
                addFiles(fileMap, file);
            else if (file.toString().toLowerCase().endsWith(".xsd")) {
                String name = file.getName();
                if (!fileMap.containsKey(name) || numAfterAlpha(fileMap.get(name)).compareTo(numAfterAlpha(file)) < 0)
                    fileMap.put(name, file);
            }
        }
    }

    private static String numAfterAlpha(File file) {
        return file.toString().replaceAll("/([0-9])", "/~$1");
    }
}
