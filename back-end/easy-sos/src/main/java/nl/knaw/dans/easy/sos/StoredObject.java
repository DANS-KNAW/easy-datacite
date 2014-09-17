package nl.knaw.dans.easy.sos;

import java.util.UUID;

/**
 * Represents a stored object.
 */
public class StoredObject
{
    private final int componentLength = 4;
    private final String uuid;
    private final String relativePathString;

    public StoredObject()
    {
        this(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public StoredObject(String uuid)
    {
        this.uuid = uuid;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i + componentLength < uuid.length() + 1; i += componentLength)
        {
            b.append(uuid.substring(i, i + componentLength));
            if (!atLastComponent(i))
                b.append('/');
        }
        relativePathString = b.toString();
    }

    private boolean atLastComponent(int i)
    {
        return i + componentLength + 1 > uuid.length();
    }

    public String getRelativePath()
    {
        return relativePathString;
    }

    public String getUuid()
    {
        return uuid;
    }
}
