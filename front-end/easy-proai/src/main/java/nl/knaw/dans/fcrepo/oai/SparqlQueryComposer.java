package nl.knaw.dans.fcrepo.oai;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import proai.error.RepositoryException;

import fedora.common.Constants;
import fedora.services.oaiprovider.FedoraMetadataFormat;
import fedora.services.oaiprovider.FedoraOAIDriver;

public class SparqlQueryComposer implements Constants {

    /**
     * driver.dans.oaiItemIdPredicate = http://www.openarchives.org/OAI/2.0/itemID
     */
    private final String oaiItemIdPredicate;

    /**
     * driver.dans.setSpecPredicate = http://www.openarchives.org/OAI/2.0/setSpec
     */
    private final String setSpecPredicate;

    /**
     * driver.dans.setNamePredicate = http://www.openarchives.org/OAI/2.0/setName
     */
    private final String setNamePredicate;

    /**
     * driver.dans.setMemberPredicate = http://dans.knaw.nl/ontologies/relations#isMemberOfOAISet
     */
    private final String setMemberPredicate;

    /**
     * driver.dans.setInfoDiss = info:fedora/*\/SetInfo.xml
     */
    private final String setInfoDiss;

    private final boolean queryingForSetInfo;

    /**
     * Pairs of prefix/objectId, space separated: driver.dans.exclusiveFormat = carare info:fedora/easy-collection:4
     */
    private final Map<String, String> exclusiveFormatMap = new HashMap<String, String>();

    public SparqlQueryComposer(String oaiItemId, String setSpec, String setName, String setSpecPredicate, String setInfoDiss) {
        this.oaiItemIdPredicate = oaiItemId;
        this.setSpecPredicate = setSpec;
        this.setNamePredicate = setName;
        this.setMemberPredicate = setSpecPredicate;
        this.setInfoDiss = setInfoDiss;
        queryingForSetInfo = !"".equals(setSpecPredicate);
    }

    public SparqlQueryComposer(Properties props) {
        oaiItemIdPredicate = FedoraOAIDriver.getRequired(props, "driver.dans.oaiItemIdPredicate");
        setSpecPredicate = FedoraOAIDriver.getOptional(props, "driver.dans.setSpecPredicate");
        if (!"".equals(setSpecPredicate)) {
            setNamePredicate = FedoraOAIDriver.getRequired(props, "driver.dans.setNamePredicate");
            setMemberPredicate = FedoraOAIDriver.getRequired(props, "driver.dans.setMemberPredicate");
            setInfoDiss = FedoraOAIDriver.getRequired(props, "driver.dans.setInfoDiss");
            queryingForSetInfo = true;
        } else {
            setNamePredicate = null;
            setMemberPredicate = null;
            setInfoDiss = null;
            queryingForSetInfo = false;
        }
        String[] exclusiveFormat = FedoraOAIDriver.getOptional(props, "driver.dans.exclusiveFormat").split(" ");
        if (exclusiveFormat.length % 2 != 0) {
            throw new RepositoryException("Value of property driver.dans.exclusiveFormat should come in pairs.");
        }
        for (int i = 0; i < exclusiveFormat.length; i += 2) {
            addExclusiveFormat(exclusiveFormat[i], exclusiveFormat[i + 1]);
        }
    }

    public void addExclusiveFormat(String prefix, String setId) {
        exclusiveFormatMap.put(prefix, setId);
    }

    public boolean isQueryingForSetInfo() {
        return queryingForSetInfo;
    }

    // SELECT $setObjectPID $setSpec $setName $setDiss $setDissInfo
    // WHERE
    // {
    // ?setObjectPID <http://www.openarchives.org/OAI/2.0/setSpec> ?setSpec .
    // $setObjectPID <http://www.openarchives.org/OAI/2.0/setName> ?setName .
    // OPTIONAL
    // {
    // $setObjectPID <info:fedora/fedora-system:def/view#disseminates> ?setDiss .
    // $setDiss <info:fedora/fedora-system:def/view#disseminationType> ?setDissInfo .
    // FILTER ( $setDissInfo = <info:fedora/*/SetInfo.xml> )
    // }
    // }
    public String getListSetInfoQuery() {
        return new StringBuilder().append(getListSetInfoSelect()).append("WHERE\n").append("{\n").append("\t?setObjectPID <").append(setSpecPredicate)
                .append("> ?setSpec .\n").append(getSetNameClause()).append("\tOPTIONAL\n").append("\t{\n").append("\t\t$setObjectPID <")
                .append(VIEW.DISSEMINATES).append("> ?setDiss .\n").append("\t\t$setDiss <").append(VIEW.DISSEMINATION_TYPE).append("> ?setDissInfo .\n")
                .append("\t\tFILTER ( $setDissInfo = <").append(setInfoDiss).append("> )\n").append("\t}\n").append("}").toString();
    }

    // SELECT $setObjectPID $setSpec $setName $setDiss $setDissInfo
    // or if no setName
    // SELECT $setObjectPID $setSpec $setDiss $setDissInfo
    private String getListSetInfoSelect() {
        String select = "SELECT $setObjectPID $setSpec $setName $setDiss $setDissInfo\n";
        if (setNamePredicate == null) {
            select = "SELECT $setObjectPID $setSpec $setDiss $setDissInfo\n";
        }
        return select;
    }

    // $setObjectPID <http://www.openarchives.org/OAI/2.0/setName> ?setName .
    // or if no setName
    //
    private String getSetNameClause() {
        String setNameClause = "";
        if (setNamePredicate != null) {
            setNameClause = new StringBuilder().append("\t$setObjectPID <").append(setNamePredicate).append("> ?setName .\n").toString();
        }
        return setNameClause;
    }

    // SELECT $item $itemID $date $state
    // WHERE
    // {
    // ?item <http://www.openarchives.org/OAI/2.0/itemID> ?itemID .
    // $item <info:fedora/fedora-system:def/model#state> ?state .
    // $item <http://dans.knaw.nl/ontologies/relations#isMemberOfOAISet> <info:fedora/easy-collection:4>
    // .
    // $item <info:fedora/fedora-system:def/view#lastModifiedDate> ?date .
    // FILTER ( $date >= xsd:dateTime("2012-12-01T12:04:10.175Z")
    // && $date <= xsd:dateTime("2012-12-03T12:04:10.175Z") )
    // }
    public String getListRecordsPrimaryQuery(String fromUTC, String untilUTC, FedoraMetadataFormat format) {
        return new StringBuilder().append("SELECT $item $itemID $date $state\n").append("WHERE\n").append("{\n").append("\t?item <").append(oaiItemIdPredicate)
                .append("> ?itemID .\n").append("\t$item <").append(MODEL.STATE).append("> ?state .\n").append(getSetMembershipClause(format))
                .append("\t$item <").append(VIEW.LAST_MODIFIED_DATE).append("> ?date .\n").append(getDateFilterClause(fromUTC, untilUTC)).append("}")
                .toString();
    }

    // $item <http://dans.knaw.nl/ontologies/relations#isMemberOfOAISet> <info:fedora/easy-collection:4>
    // .
    private String getSetMembershipClause(FedoraMetadataFormat format) {
        StringBuilder sb = new StringBuilder();
        String setId = exclusiveFormatMap.get(format.getPrefix());
        if (setId != null) {
            sb.append("\t$item <").append(setMemberPredicate).append("> <").append(setId).append("> .\n");
        }
        return sb.toString();
    }

    // FILTER ( $date >= xsd:dateTime("2012-12-01T12:04:10.175Z")
    // && $date <= xsd:dateTime("2012-12-03T12:04:10.175Z") )
    private String getDateFilterClause(String fromUTC, String untilUTC) {
        StringBuilder sb = new StringBuilder();
        if (fromUTC != null || untilUTC != null) {
            sb.append("\tFILTER ( ");
            if (fromUTC != null) {
                sb.append("$date >= xsd:dateTime(\"").append(fromUTC).append("\")");
                if (untilUTC != null) {
                    sb.append("\n\t      && ");
                }
            }
            if (untilUTC != null) {
                sb.append("$date <= xsd:dateTime(\"").append(untilUTC).append("\")");
            }
            sb.append(" )\n");
        }
        return sb.toString();
    }

    // SELECT $setSpec
    // WHERE
    // {
    // <info:fedora/easy-dataset:3028> <http://dans.knaw.nl/ontologies/relations#isMemberOfOAISet> ?set .
    // $set <http://www.openarchives.org/OAI/2.0/setSpec> ?setSpec .
    // }
    public String getSetSpecQuery(String objectId) {
        return new StringBuilder().append("SELECT $setSpec\n").append("WHERE\n").append("{\n").append("\t<").append(objectId).append("> <")
                .append(setMemberPredicate).append("> ?set .\n").append("\t$set <").append(setSpecPredicate).append("> ?setSpec .\n").append("}").toString();
    }

}
