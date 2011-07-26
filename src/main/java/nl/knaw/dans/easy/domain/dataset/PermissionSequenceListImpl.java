package nl.knaw.dans.easy.domain.dataset;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestSearchInfo;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public class PermissionSequenceListImpl extends AbstractTimestampedJiBXObject<PermissionSequenceList> implements
        PermissionSequenceList
{

    private static final long        serialVersionUID = -8323917591473986L;

    private boolean                  versionable;

    private Map<String, PermissionSequence> sequencesMap = new LinkedHashMap<String, PermissionSequence>();
    
    public PermissionSequenceListImpl()
    {

    }

    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

    public boolean isVersionable()
    {
        return versionable;
    }

    public void setVersionable(boolean versionable)
    {
        this.versionable = versionable;
    }
    
    public boolean hasSequenceFor(EasyUser requester)
    {
        return getSequenceFor(requester) != null;
    }
    
    public PermissionSequence getSequenceFor(EasyUser requester)
    {
        return sequencesMap.get(requester.getId());
    }
    
    public PermissionSequence getSequenceFor(String requesterId)
    {
        return sequencesMap.get(requesterId);
    }

    public List<PermissionSequence> getPermissionSequences()
    {
        return new ArrayList<PermissionSequence>(sequencesMap.values());
    }
    
    public DateTime getLastRequestDate()
    {
        DateTime lrd = null;
        for (PermissionSequence permSeq : sequencesMap.values())
        {
            if (permSeq.getLastRequestDate().isAfter(lrd))
            {
                lrd = permSeq.getLastRequestDate();
            }
        }
        return lrd;
    }
    
    public List<PermissionSequence> getPermissionSequences(PermissionSequence.State state)
    {
        ArrayList<PermissionSequence> result = new ArrayList<PermissionSequence>();
        for (PermissionSequence permSeq : sequencesMap.values())
        {
        	if (permSeq.getState().equals(state))
        		result.add(permSeq);
        }
        return result;
    }

    // used by JiBX
    protected void setPermissionSequences(List<PermissionSequence> sequences)
    {
        for (PermissionSequence sequence : sequences)
        {
            sequencesMap.put(sequence.getRequesterId(), sequence);
        }
    }
    
    public void addSequence(PermissionSequence sequence)
    {
        sequencesMap.put(sequence.getRequesterId(), sequence);
    }

    public PermissionSequence removeSequenceFor(EasyUser user)
    {
        return sequencesMap.remove(user.getId());
    }
    
    public boolean hasSequences()
    {
        return !sequencesMap.isEmpty();
    }
    
    public PermissionRequestModel getPermissionRequest(EasyUser requester)
    {
        PermissionRequestModel request;
        PermissionSequence sequence = getSequenceFor(requester);
        if (sequence == null)
        {
            request = new PermissionRequestModel();
        }
        else
        {
            request = sequence.getRequestModel();
        }
        return request;
    }
    
    public PermissionReplyModel getPermissionReply(String requesterId) throws IllegalArgumentException
    {
        PermissionReplyModel reply;
        PermissionSequence sequence = getSequenceFor(requesterId);
        if (sequence == null)
        {
            throw new IllegalArgumentException("No sequence for user with id " + requesterId);
        }
        else
        {
            reply = sequence.getReplyModel();
        }
        return reply;
    }
    
    public boolean isGrantedTo(EasyUser user)
    {
        PermissionSequence sequence = getSequenceFor(user);
        return sequence != null && sequence.isGranted();
    }
    
    public List<PermissionRequestSearchInfo> getSearchInfoList()
    {
        List<PermissionRequestSearchInfo> sbFieldList = new ArrayList<PermissionRequestSearchInfo>();
        for (PermissionSequence sequence : sequencesMap.values())
        {
            sbFieldList.add(sequence.getSearchInfo());
        }
        return sbFieldList;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return new HashCodeBuilder(-1641521, -517070753)
            .appendSuper(super.hashCode())
            .append(this.sequencesMap)
            .append(this.versionable)
            .toHashCode();
    }

    // methods for conversion
    

}
