package nl.knaw.dans.easy.mock;

import org.powermock.api.easymock.PowerMock;

class AbstractHelper
{

    public static void verifyAll()
    {
        PowerMock.verifyAll();
    }

    public static void replayAll()
    {
        PowerMock.replayAll();
    }

}
