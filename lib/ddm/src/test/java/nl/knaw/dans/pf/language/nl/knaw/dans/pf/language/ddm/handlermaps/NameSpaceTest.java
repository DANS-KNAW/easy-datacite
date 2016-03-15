package nl.knaw.dans.pf.language.nl.knaw.dans.pf.language.ddm.handlermaps;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import org.junit.Test;

import java.io.File;

import static nl.knaw.dans.pf.language.ddm.api.SpecialValidator.LOCAL_SCHEMA_DIR;
import static nl.knaw.dans.pf.language.ddm.api.SpecialValidator.RECENT_SCHEMAS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NameSpaceTest {
    @Test
    public void usesLatestVersions() throws Exception {
        for (NameSpace ns : NameSpace.values()) {
            if (ns.xsd != null && ns.xsd.contains("easy.dans.knaw.nl/schemas")) {
                String name = new File(ns.xsd).getName();
                String localPath = RECENT_SCHEMAS.get(name).toString();
                String expectedRelativePath = localPath.replace(LOCAL_SCHEMA_DIR, "");
                String implementedRelativePath = ns.xsd.replace("http://easy.dans.knaw.nl/schemas/", "");
                assertThat("latset xsd for NameSpace " + ns, implementedRelativePath, is(expectedRelativePath));
            }
        }
    }
}
