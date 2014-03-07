package nl.knaw.dans.common.fedora.fox;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;

import org.joda.time.DateTime;

public class Datastream extends AbstractTimestampedJiBXObject<Datastream>
{

    private static final long serialVersionUID = -8375891261757581210L;

    public enum State
    {
        /**
         * Indicates a Data Stream is <i>Active</i>.
         */
        A,
        /**
         * Indicates a Data Stream is <i>Inactive</i>.
         */
        I,
        /**
         * Indicates a Data Stream is <i>Deleted</i>.
         */
        D;
    }

    private static final int MAX_ID_LENGTH = 64;

    private String streamId;
    private ControlGroup controlGroup;
    private State state;
    private boolean versionable;
    private URI fedoraUri;
    private Map<String, DatastreamVersion> versions = new HashMap<String, DatastreamVersion>();

    /**
     * Used by JiBX serialization.
     */
    protected Datastream()
    {

    }

    public Datastream(String streamId, ControlGroup controlGroup)
    {
        this.controlGroup = controlGroup;
        setStreamId(streamId);
    }

    /**
     * Returns null. (Method inherited from AbstractJiBXObject.)
     */
    public String getVersion()
    {
        return null;
    }

    public String getStreamId()
    {
        return streamId;
    }

    public void setStreamId(String streamId)
    {
        if (streamId.length() <= MAX_ID_LENGTH)
        {
            this.streamId = streamId;
        }
        else
        {
            throw new IllegalArgumentException("The string '" + streamId + "' is not allowed as Fedora Datastream id.");
        }
    }

    public ControlGroup getControlGroup()
    {
        return controlGroup;
    }

    public void setControlGroup(ControlGroup controlGroup)
    {
        this.controlGroup = controlGroup;
    }

    public State getState()
    {
        return state == null ? State.A : state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public boolean isVersionable()
    {
        return versionable;
    }

    public void setVersionable(boolean versionable)
    {
        this.versionable = versionable;
    }

    public URI getFedoraUri()
    {
        return fedoraUri;
    }

    public void setFedoraUri(URI fedoraUri)
    {
        this.fedoraUri = fedoraUri;
    }

    public DatastreamVersion putDatastreamVersion(final DatastreamVersion version)
    {
        return versions.put(version.getVersionId(), version);
    }

    public DatastreamVersion getDatastreamVersion(final String versionId)
    {
        return versions.get(versionId);
    }

    public DatastreamVersion removeDatastreamVersion(String versionId)
    {
        return versions.remove(versionId);
    }

    public DatastreamVersion getLatestVersion()
    {
        DatastreamVersion version = null;
        DateTime date = new DateTime(0L);
        for (DatastreamVersion dsv : versions.values())
        {
            DateTime created = dsv.getCreated();
            if (created == null && version == null)
            {
                version = dsv;
            }
            else if (created != null && created.isAfter(date))
            {
                version = dsv;
                date = created;
            }
        }
        return version;
    }

    public DatastreamVersion addDatastreamVersion(String versionId, String mimeType)
    {
        if (versionId == null)
        {
            versionId = nextVersionId();
        }
        if (getDatastreamVersion(versionId) != null)
        {
            throw new IllegalArgumentException("A DatastreamVersion with the versionId '" + versionId + "' already exists!");
        }
        DatastreamVersion newVersion = new DatastreamVersion(versionId, mimeType);
        putDatastreamVersion(newVersion);
        return newVersion;
    }

    public List<DatastreamVersion> getDatastreamVersions()
    {
        return new ArrayList<DatastreamVersion>(versions.values());
    }

    /**
     * Used by JiBX deserialization.
     * 
     * @param list
     *        list with DatastreamVersions
     */
    public void setDatastreamVersions(List<DatastreamVersion> list)
    {
        versions.clear();
        if (list != null)
        {
            // the last version in the list is the last created
            DatastreamVersion last = list.get(list.size() - 1);
            DateTime timestamp = last.getCreated();
            setTimestamp(timestamp);
            for (DatastreamVersion version : list)
            {
                version.setTimestamp(timestamp);
                versions.put(version.getVersionId(), version);
            }
        }
    }

    public String nextVersionId()
    {
        return streamId + "." + (getHighestVersionNumber() + 1);
    }

    private int getHighestVersionNumber()
    {
        int highestVersionNumber = -1;
        for (String versionId : versions.keySet())
        {
            if (versionId.length() > streamId.length())
            {
                final String versionNumberString = versionId.substring(streamId.length() + 1, versionId.length());
                try
                {
                    final int versionNumber = Integer.parseInt(versionNumberString);
                    if (highestVersionNumber < versionNumber)
                    {
                        highestVersionNumber = versionNumber;
                    }
                }
                catch (final NumberFormatException e)
                {
                    // OK, not a number
                }
            }
        }
        return highestVersionNumber;
    }

}
