package nl.knaw.dans.easy.domain.model;

import java.net.URI;
import java.util.List;

import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public interface PermissionSequenceList extends MetadataUnitXMLBean
{

    String UNIT_ID = "PRSQL";

    String UNIT_LABEL = "Permission request sequences for this dataset";

    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/permission-sequences/";

    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    /**
     * Get a list of permission sequences for the dataset this PermissionSequenceList belongs to.
     * 
     * @return a list of permission sequences
     */
    List<PermissionSequence> getPermissionSequences();

    /**
     * Get a list of permission sequences for the dataset this PermissionSequenceList belongs to that are
     * of a certain state.
     * 
     * @param state
     *        the state of the permission sequences to be returned
     * @return a list of permission sequences of State state
     */
    List<PermissionSequence> getPermissionSequences(State state);

    /**
     * Get the PermissionSequence for the given requester.
     * 
     * @param requester
     *        the user that asks permission
     * @return PermissionSequence. Can be <code>null</code>
     * @see #hasSequenceFor(EasyUser)
     */
    PermissionSequence getSequenceFor(EasyUser requester);

    PermissionSequence getSequenceFor(String requesterId);

    /**
     * Is there a PermissionSequence for the given requester.
     * 
     * @param requester
     *        the user that asks permission
     * @return <code>true</code> if there is a PermissionSequence, <code>false</code> otherwise
     */
    boolean hasSequenceFor(EasyUser requester);

    boolean hasSequences();

    boolean isGrantedTo(EasyUser user);

    List<PermissionRequestSearchInfo> getSearchInfoList();

    PermissionRequestModel getPermissionRequest(EasyUser requester);

    PermissionReplyModel getPermissionReply(String requesterId);

}
