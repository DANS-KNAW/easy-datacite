package nl.knaw.dans.easy.domain.emd.validation.base;

public enum EmdXPath
{
    SPATIAL_COVERAGE ("/emd:easymetadata/emd:coverage/eas:spatial/"),
    RIGHTS ("/emd:easymetadata/emd:rights/dcterms:accessRights/"),
    RELATION("/emd:easymetadata/emd:relation/dcterms:relation/"),
    EMBARGO("/emd:easymetadata/emd:date/dcterms:available/");
    /**
     * TODO add validators for the unchecked xml files
     * 
     * <pre>
     *     discipline/emd
     *     |-- choicelist
     *     |   |-- archaeology
     *     |   |   |-- dc
     *     |   |   |   |-- identifier.xml
     *     |   |   |   `-- subject.xml
     *     |   |   |-- dcterms
     *  V  |   |   |   |-- accessrights.xml 
     *     |   |   |   |-- date.xml
     *     |   |   |   `-- temporal.xml
     *     |   |   `-- eas
     *  V  |   |       |-- spatial.xml
     *     |   |       `-- spatial_en.xml
     *     |   `-- common
     *     |       |-- dc
     *     |       |   |-- format.xml
     *     |       |   |-- language.xml
     *     |       |   `-- type.xml
     *     |       `-- dcterms
     *  V  |           |-- accessrights.xml
     *     |           |-- audience.xml                old easy
     *     |           |-- date.xml
     *     |           `-- relation.xml                more like spatial than a simple choice list
     *     `-- recursivelist
     *         `-- archaeology
     *             |-- dc
     *             |   `-- subject.xml
     *             `-- dcterms
     *                 `-- temporal.xml
     * </pre>
     */

    private final String xPath;

    private EmdXPath(String xPath)
    {
        this.xPath = xPath;
    }

    public String getXPath()
    {
        return xPath;
    }
}
