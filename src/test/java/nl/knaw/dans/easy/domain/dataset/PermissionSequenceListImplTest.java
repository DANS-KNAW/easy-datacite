package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionSequenceListImplTest
{

    private static final Logger logger = LoggerFactory.getLogger(PermissionSequenceListImplTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void testMarshalAndUnmarshal() throws XMLException
    {
        PermissionSequenceListImpl psl = new PermissionSequenceListImpl();
        psl.addSequence(PermissionSequenceImplTest.getRequestSequence("Piet II"));
        psl.addSequence(PermissionSequenceImplTest.getReplySequence("Magdalena III"));

        requestAgain(psl.getSequenceFor("Magdalena III"));

        replyAgain(psl.getSequenceFor("Magdalena III"));

        if (verbose)
            logger.debug("\n" + psl.asXMLString(4) + "\n");

        byte[] objectXML = psl.asObjectXML();
        PermissionSequenceList psl2 = (PermissionSequenceList) JiBXObjectFactory.unmarshal(PermissionSequenceListImpl.class, objectXML);
        assertEquals(psl.asXMLString(), psl2.asXMLString());
    }

    private void requestAgain(PermissionSequence sequence)
    {
        PermissionRequestModel request = sequence.getRequestModel();
        request.setRequestTheme("I am pleeing. Please grant me permission.");
        ((PermissionSequenceImpl) sequence).updateRequest(request);
    }

    private void replyAgain(PermissionSequence sequence)
    {
        PermissionReplyModel reply = sequence.getReplyModel();
        reply.setExplanation("No. You cannot have my data!");
        ((PermissionSequenceImpl) sequence).updateReply(reply);
    }

}
