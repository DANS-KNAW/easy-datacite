package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionSequenceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(PermissionSequenceImplTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void testMarshalAndUnmarshalRequest() throws XMLException {
        PermissionSequence sequence = getRequestSequence("karel1");

        if (verbose)
            logger.debug("\n" + sequence.asXMLString(4));

        byte[] xml = sequence.asObjectXML();
        PermissionSequence sequence2 = (PermissionSequence) JiBXObjectFactory.unmarshal(PermissionSequenceImpl.class, xml);
        assertEquals(sequence.asXMLString(), sequence2.asXMLString());

    }

    @Test
    public void testMarshalAndUnmarshalReply() throws XMLException {
        PermissionSequence sequence = getReplySequence("Alphons2");

        if (verbose)
            logger.debug("\n" + sequence.asXMLString(4));

        byte[] xml = sequence.asObjectXML();
        PermissionSequence sequence2 = (PermissionSequence) JiBXObjectFactory.unmarshal(PermissionSequenceImpl.class, xml);
        assertEquals(sequence.asXMLString(), sequence2.asXMLString());
    }

    public static PermissionSequenceImpl getRequestSequence(String requesterId) {
        EasyUser requester = new EasyUserImpl(requesterId);
        PermissionSequenceImpl sequence = new PermissionSequenceImpl(requester);
        PermissionRequestModel request = sequence.getRequestModel();
        request.setAcceptingConditionsOfUse(true);
        request.setRequestTitle("the request title");
        request.setRequestTheme("The theme of this request");
        sequence.updateRequest(request);
        return sequence;
    }

    public static PermissionSequence getReplySequence(String requesterId) {
        PermissionSequenceImpl sequence = getRequestSequence(requesterId);
        PermissionReplyModel reply = sequence.getReplyModel();
        reply.setExplanation("This is the explanation or reply text");
        reply.setState(State.Denied);
        sequence.updateReply(reply);
        return sequence;
    }

    @Test
    public void testDirty() {
        PermissionSequenceImpl sequence = new PermissionSequenceImpl("requester");
        sequence.setDirty(false);
        sequence.setReplyText("replyText");
        assertTrue(sequence.isDirty());
    }

}
