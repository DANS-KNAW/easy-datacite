package nl.knaw.dans.easy.domain.deposit.discipline;

import java.util.List;

public class KvpPrinter
{

    /**
     * <pre>
     * <xs:enumeration value="PALEO">
     *  <xs:annotation>
     *      <xs:documentation xml:lang="nl">Paleolithicum: tot 8800 vC</xs:documentation>
     *  </xs:annotation>
     * </xs:enumeration>
     * </pre>
     * 
     * @param kvpList
     * @return
     */
    public String printXsEnumeration(List<KeyValuePair> kvpList)
    {
        StringBuilder sb = new StringBuilder("\n");
        for (KeyValuePair kvp : kvpList)
        {
            sb.append("<xs:enumeration value=\"" + kvp.getKey() + "\">\n");
            sb.append("\t<xs:annotation>\n");
            sb.append("\t\t<xs:documentation xml:lang=\"nl\">\n\t\t\t" + kvp.getValue() + "\n\t\t</xs:documentation>\n");
            sb.append("\t</xs:annotation>\n");
            sb.append("</xs:enumeration>\n");
        }
        return sb.toString();
    }

}
