package nl.knaw.dans.pf.language.ddm.api;

import java.io.File;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.pf.language.xml.validation.AbstractValidator2;

/**
 * DDM validation normally uses the online schemas at "http://easy.dans.knaw.nl/schemas/", but for
 * testing we want to use the schema files locally at "../../schema/" For the files note that schema is
 * without an s!
 * 
 * @author paulboon
 */
public class OfflineDDMValidator extends AbstractValidator2
{
    private static final Logger logger = LoggerFactory.getLogger(OfflineDDMValidator.class);

    // Note that the online url's can be found in nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace
    final static String LOCAL_SCHEMA_DIR = "../../schema/";
    final static String DDM_xsd = "md/2014/09/ddm.xsd";
    final static String DCX_GML_xsd = "dcx/2012/10/dcx-gml.xsd";
    final static String NARCIS_TYPE_xsd = "vocab/2012/10/narcis-type.xsd";
    final static String ABR_xsd = "vocab/2012/10/abr-type.xsd";

    static String getLocal_xsdURLStr(String pathBelowSchemaDir)
    {
        String val = "";
        String path = LOCAL_SCHEMA_DIR + pathBelowSchemaDir;
        try
        {
            val = new File(path).toURI().toURL().toString();
        }
        catch (MalformedURLException e)
        {
            logger.error("Could not find schema at: " + path);
            e.printStackTrace();
        }
        return val;
    }

    public OfflineDDMValidator()
    {
        super(getLocal_xsdURLStr(DDM_xsd), getLocal_xsdURLStr(DCX_GML_xsd), getLocal_xsdURLStr(NARCIS_TYPE_xsd), getLocal_xsdURLStr(ABR_xsd));
    }
}
