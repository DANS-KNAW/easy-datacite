package nl.knaw.dans.easy.tools.task.am.permissions;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceImpl;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceListImpl;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionRequestReader {

    public static final String XPATH_USER = "/dmsaa/users/user";
    public static final String XPATH_PERMISSION = "/dmsaa/users/user/permissions/permission";

    private static DecimalFormat D_FORMAT = new DecimalFormat("000");

    private static final Logger logger = LoggerFactory.getLogger(PermissionRequestReader.class);

    private final String userPermissionFile;
    private Map<String, PermissionSequenceList> aipId_permissionSequenceList_map;

    public PermissionRequestReader(String dmsaaLocation) {
        userPermissionFile = dmsaaLocation;
    }

    public PermissionSequenceList getSequenceListFor(String aipId) throws TaskExecutionException {
        return getSequenceListMap().get(aipId);
    }

    public Map<String, PermissionSequenceList> getSequenceListMap() throws TaskExecutionException {
        if (aipId_permissionSequenceList_map == null) {
            aipId_permissionSequenceList_map = new HashMap<String, PermissionSequenceList>();
            try {
                readPermissionRequests();
            }
            catch (DocumentException e) {
                throw new TaskExecutionException("Cannot read permissions: ", e);
            }
            for (PermissionSequenceList psl : aipId_permissionSequenceList_map.values()) {
                for (PermissionSequence ps : psl.getPermissionSequences()) {
                    ((PermissionSequenceImpl) ps).stripDates();
                }
            }
        }
        return aipId_permissionSequenceList_map;
    }

    public String getUserPermissionFile() {
        return userPermissionFile;
    }

    private void readPermissionRequests() throws DocumentException, TaskExecutionException {
        File permissionFile = new File(getUserPermissionFile());
        logger.debug("Gathering permission data from file " + permissionFile.getAbsolutePath());
        Dom4jReader reader = new Dom4jReader(permissionFile);
        List<Node> users = reader.getNodes(XPATH_USER);
        for (Node user : users) {
            updateSequenceLists(user);
        }
    }

    private void updateSequenceLists(Node user) throws TaskExecutionException {
        String userId = user.selectSingleNode("username").getText();
        List<Node> permissions = getSortedPermissionList(user);
        for (Node permission : permissions) {

            updateRequestAndReply(userId, permission);
        }
    }

    private void updateRequestAndReply(String userId, Node permission) throws TaskExecutionException {
        String seq = permission.selectSingleNode("seq").getText();
        String aipId = permission.selectSingleNode("aipId").getText();
        // System.err.println(aipId + " " + seq);
        String status = permission.selectSingleNode("status").getText();
        String title = permission.selectSingleNode("title").getText();
        String theme = permission.selectSingleNode("theme").getText();
        String comment = permission.selectSingleNode("comment").getText();
        boolean totallyBlank = true;

        PermissionSequenceListImpl sequenceList = (PermissionSequenceListImpl) getSequenceList(aipId);
        PermissionSequenceImpl sequence = (PermissionSequenceImpl) sequenceList.getSequenceFor(userId);
        if (sequence == null) {
            sequence = new PermissionSequenceImpl(userId);
        }

        if (!StringUtils.isBlank(title + theme)) {
            totallyBlank = false;
            sequence.setState(getStateFor(status));

            PermissionRequestModel request = sequence.getRequestModel();
            request.setAcceptingConditionsOfUse(true); // no accept in original
            request.setRequestTitle(title);
            request.setRequestTheme(theme);
            sequence.updateRequest(request);
            sequence.setState(getStateFor(status));
        }

        if (!StringUtils.isBlank(comment)) {
            totallyBlank = false;
            PermissionReplyModel reply = sequence.getReplyModel();
            reply.setExplanation(comment);
            reply.setState(getStateFor(status));

            sequence.updateReply(reply);
        }
        if (!totallyBlank) {
            sequenceList.addSequence(sequence);
        }

    }

    private List<Node> getSortedPermissionList(Node user) {
        List<Node> permissions = user.selectNodes("permissions/permission");
        Collections.sort(permissions, new Comparator<Node>() {

            public int compare(Node n1, Node n2) {
                String aipId1 = n1.selectSingleNode("aipId").getText();
                String aipId2 = n2.selectSingleNode("aipId").getText();

                String seq1 = n1.selectSingleNode("seq").getText();
                String seq2 = n2.selectSingleNode("seq").getText();

                String digitSeq1 = convertToDigitString(seq1);
                String digitSeq2 = convertToDigitString(seq2);

                // System.err.println(aipId1 + digitSeq1 + " | " + aipId2 + digitSeq2);

                return (aipId1 + digitSeq1).compareTo((aipId2 + digitSeq2));
            }

            private String convertToDigitString(String seq) {
                String digitString = "000";
                try {
                    int digits = Integer.parseInt(seq);
                    digitString = D_FORMAT.format(digits);
                }
                catch (NumberFormatException e) {
                    // so be it
                }
                return digitString;
            }

        });
        return permissions;
    }

    protected PermissionSequenceList getSequenceList(String aipId) throws TaskExecutionException {
        PermissionSequenceList sequenceList = getSequenceListMap().get(aipId);
        if (sequenceList == null) {
            sequenceList = new PermissionSequenceListImpl();
            getSequenceListMap().put(aipId, sequenceList);
        }
        return sequenceList;
    }

    private PermissionSequence.State getStateFor(String status) {
        PermissionSequence.State state = PermissionSequence.State.Submitted;
        if ("granted".equals(status)) {
            state = PermissionSequence.State.Granted;
        } else if ("returned".equals(status)) {
            state = PermissionSequence.State.Returned;
        } else if ("denied".equals(status)) {
            state = PermissionSequence.State.Denied;
        }
        return state;
    }

}
