package nl.knaw.dans.easy.tools.jumpoff.twips;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.MarkupUnit;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalRuntimeException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Stroll through
 */
public class TwipsJumpoffMigrationCheckerTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(TwipsJumpoffMigrationCheckerTask.class);

    public static final String MGMDATA_XML = "/mgmdata/mgmdata.xml";
    public final String ROOT_OVERVIEW_AIP = "/twips.dans.knaw.nl--5762177928943175215-1238574089841";
    public static final String XPATH_ROOT_OVERVIEW_AIPS_LIST = "//appHTML/div/table/tr/td/table/*/td[2]/a[@href]";

    public static final String XPATH_LEAF_AIPS_LIST = "//appHTML/div/table/tr/td/table/*/td[1]/a[@href]";

    public static final String ROOT_OVERVIEW_DATASET = "easy-dataset:42";
    public static final String XPATH_ROOT_OVERVIEW_PIDS_LIST = "//div[@class='jumpoffpage']//a[@href]";

    private final String repositoryRoot;
    private final String rootOverviewMgmdata;

    private Document xmlDocument;
    private XPath xPath;
    private Map<String, List<String>> overviewToLeaf;
    private Map<String, List<String>> oldJumpoffPidMap;
    private Map<String, List<String>> currentOverviewToLeaf;
    private List<String> currentOverviewAipIds;

    public TwipsJumpoffMigrationCheckerTask(String repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
        this.rootOverviewMgmdata = repositoryRoot + ROOT_OVERVIEW_AIP + MGMDATA_XML;
        if (!new File(rootOverviewMgmdata).exists()) {
            throw new FatalRuntimeException("Root overview mgmdata.xml file not found: " + rootOverviewMgmdata);
        }
        overviewToLeaf = new HashMap<String, List<String>>();
        oldJumpoffPidMap = new HashMap<String, List<String>>();
        currentOverviewToLeaf = new HashMap<String, List<String>>();
        currentOverviewAipIds = new ArrayList<String>();
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        fillOverviewToLeafMap();
        processCurrentJumpOffPage();
        compareOldAndCurrentJumOff();
    }

    private void fillOverviewToLeafMap() {
        parseXMLFile(rootOverviewMgmdata);
        initXPath();
        NodeList overviewAipNodes = (NodeList) read(XPATH_ROOT_OVERVIEW_AIPS_LIST, XPathConstants.NODESET);

        List<String> overviewAips = getAipIdList(overviewAipNodes);

        for (String overviewAip : overviewAips) {
            String mgmdataFile = repositoryRoot + "/" + overviewAip + MGMDATA_XML;
            if (!new File(mgmdataFile).exists()) {
                RL.error(new Event(getTaskName(), "File not found: " + mgmdataFile));
            } else {
                overviewToLeaf.put(overviewAip, getLeafAips(mgmdataFile));
            }

        }
    }

    private void parseXMLFile(String filePath) {
        try {
            xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
        }
        catch (IOException ex) {
            logger.error("Exception: " + filePath, ex);
            RL.error(new Event(filePath, ex));
        }
        catch (SAXException ex) {
            logger.error("Exception: " + filePath, ex);
            RL.error(new Event(filePath, ex));
        }
        catch (ParserConfigurationException ex) {
            logger.error("Exception: " + filePath, ex);
            RL.error(new Event(filePath, ex));
        }
    }

    private void initXPath() {
        xPath = XPathFactory.newInstance().newXPath();
    }

    private List<String> getAipIdList(NodeList refRefPageList) {
        List<String> aipIdList = new ArrayList<String>();
        int len = refRefPageList.getLength();
        for (int index = 0; index < len; index++) {
            Node aNode = refRefPageList.item(index);
            if (aNode.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = (NamedNodeMap) aNode.getAttributes();
                // To get a xml attribute.
                for (int g = 0; g < attributes.getLength(); g++) {
                    Attr attribute = (Attr) attributes.item(g);
                    // fill in the VVP list
                    String hrefVal = attribute.getValue();
                    int aipIdPos = hrefVal.indexOf("twips.dans.knaw.nl-");
                    if (aipIdPos > -1) {
                        String aipId = hrefVal.substring(aipIdPos);
                        aipIdList.add(aipId);
                    }

                }
            }
        }
        return aipIdList;
    }

    private List<String> getLeafAips(String overviewAipMgmdata) {
        parseXMLFile(overviewAipMgmdata);
        NodeList leafAipNodes = (NodeList) read(XPATH_LEAF_AIPS_LIST, XPathConstants.NODESET);
        List<String> leafAips = getAipIdList(leafAipNodes);
        return leafAips;
    }

    private String getPidByAIpId(MigrationRepo repo, String aipId) throws RepositoryException {
        String pid = null;
        List<IdMap> idMap = repo.findByAipId(aipId);
        if (idMap.size() != 1) {
            logger.error("Found multiple IdMaps for aip ID: " + aipId);
            RL.error(new Event(getTaskName(), "Found multiple IdMaps for aip ID: " + aipId));
        } else {
            pid = idMap.get(0).getPersistentIdentifier();
        }
        return pid;
    }

    private void processCurrentJumpOffPage() {
        MigrationRepo repo = Data.getMigrationRepo();
        try {
            JumpoffDmo rootCurrentOverviewJumpoff = Data.getEasyStore().findJumpoffDmoFor(new DmoStoreId(ROOT_OVERVIEW_DATASET)); // 1.
            List<String> overviewPids = getPidsFromJumpOffPage(rootCurrentOverviewJumpoff);// 2
            for (String overviewPid : overviewPids) {

                List<IdMap> idMaps = repo.findByPersistentIdentifier(overviewPid);
                if (idMaps.size() != 1) {
                    logger.error("Found multiple IdMaps for PID: " + overviewPid);
                    RL.error(new Event(getTaskName(), "Found multiple IdMaps for PID: " + overviewPid));
                } else {
                    String currentAipId = idMaps.get(0).getAipId();// 3(a) --> 3(f)
                    currentOverviewAipIds.add(currentAipId);
                    String overviewStoreId = idMaps.get(0).getStoreId();// 3(b)
                    JumpoffDmo overviewJumpoff = Data.getEasyStore().findJumpoffDmoFor(new DmoStoreId(overviewStoreId));// 3(c)
                    org.dom4j.Document jumpOffDocument = overviewJumpoff.getJumpoffDmoMetadata().asDocument();
                    convertDom4JToW3CDocument(jumpOffDocument);
                    NodeList pidNodes = (NodeList) read(XPATH_ROOT_OVERVIEW_PIDS_LIST, XPathConstants.NODESET);
                    List<String> pidsLeaf = getPidList(pidNodes);// 3(d)
                    currentOverviewToLeaf.put(currentAipId, getCurrentAipIds(pidsLeaf, repo));// 3(e)
                }

            }
        }
        catch (RepositoryException e) {
            logger.error("Exception: ", e);
            RL.error(new Event("Exception", e));
        }
        catch (XMLSerializationException e) {
            logger.error("Exception: ", e);
            RL.error(new Event("Exception", e));
        }
        catch (DocumentException e) {
            logger.error("Exception: ", e);
            RL.error(new Event("Exception", e));
        }
    }

    private List<String> getPidsFromJumpOffPage(JumpoffDmo rootOverviewJumpoff) {
        List<String> pids = null;
        try {
            MarkupUnit jumpOffPageMarkupUnit = rootOverviewJumpoff.getHtmlMarkup();
            String divJumpOffPage = jumpOffPageMarkupUnit.getHtml();
            xmlDocument = stringToDom(divJumpOffPage);
            NodeList overviewPidNodes = (NodeList) read(XPATH_ROOT_OVERVIEW_PIDS_LIST, XPathConstants.NODESET);
            pids = getPidList(overviewPidNodes);
        }
        catch (SAXException e) {
            logger.error("Exception: ", e);
            RL.error(new Event("Exception", e));
        }
        catch (ParserConfigurationException e) {
            logger.error("Exception: ", e);
            RL.error(new Event("Exception", e));
        }
        catch (IOException e) {
            logger.error("Exception: ", e);
            RL.error(new Event("Exception", e));
        }

        return pids;
    }

    private Document stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    private void convertDom4JToW3CDocument(org.dom4j.Document jumpOffDocument) throws DocumentException {
        DOMWriter writer = new DOMWriter();
        xmlDocument = writer.write(jumpOffDocument);
    }

    private List<String> getPidList(NodeList overviewPidNodes) {
        List<String> pidList = new ArrayList<String>();
        int len = overviewPidNodes.getLength();
        for (int index = 0; index < len; index++) {
            Node aNode = overviewPidNodes.item(index);
            if (aNode.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = (NamedNodeMap) aNode.getAttributes();
                for (int g = 0; g < attributes.getLength(); g++) {
                    Attr attribute = (Attr) attributes.item(g);
                    String hrefVal = attribute.getValue();
                    int aipIdPos = hrefVal.indexOf("http://persistent-identifier.nl/?identifier=");
                    if (aipIdPos > -1) {
                        String aipId = hrefVal.substring(aipIdPos);
                        pidList.add(aipId);
                    }

                }
            }
        }
        return pidList;
    }

    private List<String> getCurrentAipIds(List<String> pidsLeaf, MigrationRepo repo) throws RepositoryException {
        List<String> currentAipIds = new ArrayList<String>();
        for (String pid : pidsLeaf) {
            currentAipIds.add(getPidByAIpId(repo, pid));
        }
        return currentAipIds;
    }

    private void compareOldAndCurrentJumOff() { // 3(f) & 3(g)
        for (String currentOverviewAip : currentOverviewAipIds) {
            List<String> oldJumpOffAipIds = overviewToLeaf.get(currentOverviewAip);
            List<String> currentJumpOffAipIds = currentOverviewToLeaf.get(currentOverviewAip);
            List<String> aipIdsNotInCurrent = getMissingJumpOff(oldJumpOffAipIds, currentJumpOffAipIds);
            for (String missingAip : aipIdsNotInCurrent) {
                RL.error(new Event(getTaskName(), "AIP: " + missingAip + " is missing."));
            }
        }

    }

    private List<String> getMissingJumpOff(List<String> oldJumpOffAipIds, List<String> currentJumpOffAipIds) {
        List<String> aipIdsNotInCurrent = new ArrayList<String>();
        for (String oldJumpOfAipId : oldJumpOffAipIds) {
            if (!currentJumpOffAipIds.contains(oldJumpOfAipId)) {
                aipIdsNotInCurrent.add(oldJumpOfAipId);
            }
        }
        return aipIdsNotInCurrent;
    }

    private void fillInOldJumpoffPidMap() {
        MigrationRepo repo = Data.getMigrationRepo();
        Set<String> overviewAips = overviewToLeaf.keySet();
        for (String overviewAip : overviewAips) {
            try {
                String overviewAipPid = getPidByAIpId(repo, overviewAip);
                if (overviewAipPid == null) {
                    continue;
                }

                List<String> leafAips = overviewToLeaf.get(overviewAip);
                List<String> leafAipPids = new ArrayList<String>();
                for (String leafAip : leafAips) {
                    leafAipPids.add(getPidByAIpId(repo, leafAip));
                }
                oldJumpoffPidMap.put(overviewAipPid, leafAipPids);
            }
            catch (ObjectNotInStoreException e) {
                logger.error("Exception: " + overviewAip, e);
                RL.error(new Event(overviewAip, e));
            }
            catch (RepositoryException e) {
                logger.error("Exception: " + overviewAip, e);
                RL.error(new Event(overviewAip, e));
            }

        }
    }

    private Object read(String expression, QName returnType) {
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            return xPathExpression.evaluate(xmlDocument, returnType);
        }
        catch (XPathExpressionException ex) {
            logger.error("Exception: " + ex);
            RL.error(new Event(ex));
            throw new FatalRuntimeException(ex);
        }
    }
}
